/*
 * Copyright (c) 2008-2019, Hazelcast, Inc. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.hazelcast.jet.impl.connector;

import com.hazelcast.core.IMap;
import com.hazelcast.jet.JetInstance;
import com.hazelcast.jet.core.JetTestSupport;
import com.hazelcast.jet.core.processor.SourceProcessors;
import com.hazelcast.jet.core.test.TestSupport;
import com.hazelcast.jet.function.FunctionEx;
import com.hazelcast.projection.Projection;
import com.hazelcast.query.Predicate;
import com.hazelcast.test.HazelcastParallelClassRunner;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;

import static com.hazelcast.jet.Util.entry;
import static java.util.Collections.emptyList;

@RunWith(HazelcastParallelClassRunner.class)
public class ReadMapOrCachePTest extends JetTestSupport {

    private JetInstance jet;

    @Before
    public void setUp() {
        jet = createJetMember();
    }

    @Test
    public void test_whenEmpty() {
        TestSupport
                .verifyProcessor(SourceProcessors.readMapP("map"))
                .jetInstance(jet)
                .disableSnapshots()
                .disableProgressAssertion()
                .expectOutput(emptyList());
    }

    @Test
    public void test_whenNoPredicateAndNoProjection() {
        IMap<Integer, String> map = jet.getMap("map");
        List<Entry<Integer, String>> expected = new ArrayList<>();
        for (int i = 0; i < 1000; i++) {
            map.put(i, "value-" + i);
            expected.add(entry(i, "value-" + i));
        }

        TestSupport
            .verifyProcessor(SourceProcessors.readMapP("map"))
            .jetInstance(jet)
            .disableSnapshots()
            .disableProgressAssertion()
            .outputChecker(TestSupport.SAME_ITEMS_ANY_ORDER)
            .expectOutput(expected);
    }

    @Test
    public void test_whenPredicateAndProjectionSet() {
        IMap<Integer, String> map = jet.getMap("map");
        List<String> expected = new ArrayList<>();
        for (int i = 0; i < 1000; i++) {
            map.put(i, "value-" + i);
            if (i % 2 == 0) {
                expected.add("value-" + i);
            }
        }

        Predicate<Integer, String> predicate = entry -> entry.getKey() % 2 == 0;
        Projection<Entry<Integer, String>, String> projection = toProjection(Entry::getValue);
        TestSupport
            .verifyProcessor(SourceProcessors.readMapP("map", predicate, projection))
            .jetInstance(jet)
            .disableSnapshots()
            .disableProgressAssertion()
            .outputChecker(TestSupport.SAME_ITEMS_ANY_ORDER)
            .expectOutput(expected);
    }

    private static <I, O> Projection<I, O> toProjection(FunctionEx<I, O> projectionFn) {
        return new Projection<I, O>() {
            @Override public O transform(I input) {
                return projectionFn.apply(input);
            }
        };
    }
}
