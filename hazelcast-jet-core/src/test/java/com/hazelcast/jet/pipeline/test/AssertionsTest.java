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

package com.hazelcast.jet.pipeline.test;

import com.hazelcast.jet.aggregate.AggregateOperations;
import com.hazelcast.jet.pipeline.PipelineTestSupport;
import com.hazelcast.jet.pipeline.Sinks;
import com.hazelcast.jet.pipeline.Sources;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static com.hazelcast.jet.Traversers.traverseArray;
import static com.hazelcast.jet.Util.entry;
import static com.hazelcast.jet.function.Functions.wholeItem;
import static com.hazelcast.jet.pipeline.test.Assertions.assertCollected;
import static com.hazelcast.jet.pipeline.test.Assertions.assertCollectedEventually;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class AssertionsTest extends PipelineTestSupport {

    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    @Test
    public void test_assertOrdered() {
        p.drawFrom(TestSources.items(1, 2, 3, 4))
         .apply(Assertions.assertOrdered(Arrays.asList(1, 2, 3, 4)));

        execute();
    }

    @Test
    public void test_assertOrdered_should_fail() throws Throwable {
        p.drawFrom(TestSources.items(4, 3, 2, 1))
         .apply(Assertions.assertOrdered(Arrays.asList(1, 2, 3, 4)));

        expectedException.expect(AssertionError.class);
        executeAndPeel();
    }

    @Test
    public void test_assertOrdered_not_terminal() {
        List<Integer> assertionSink = jet().getList(sinkName);
        assertTrue(assertionSink.isEmpty());

        p.drawFrom(TestSources.items(1, 2, 3, 4))
                .apply(Assertions.assertOrdered(Arrays.asList(1, 2, 3, 4)))
                .drainTo(sinkList());

        execute();

        assertEquals(4, assertionSink.size());
        assertEquals(1, (int) assertionSink.get(0));
        assertEquals(2, (int) assertionSink.get(1));
        assertEquals(3, (int) assertionSink.get(2));
        assertEquals(4, (int) assertionSink.get(3));
    }

    @Test
    public void test_assertOrdered_not_terminal_should_fail() throws Throwable {
        List<Integer> assertionSink = jet().getList(sinkName);
        assertTrue(assertionSink.isEmpty());

        p.drawFrom(TestSources.items(1, 2, 4, 3))
                .apply(Assertions.assertOrdered(Arrays.asList(1, 2, 3, 4)))
                .drainTo(sinkList());

        expectedException.expect(AssertionError.class);
        executeAndPeel();

        assertEquals(4, assertionSink.size());
        assertEquals(1, (int) assertionSink.get(0));
        assertEquals(2, (int) assertionSink.get(1));
    }

    @Test
    public void test_assertAnyOrder() {
        p.drawFrom(TestSources.items(4, 3, 2, 1))
         .apply(Assertions.assertAnyOrder(Arrays.asList(1, 2, 3, 4)));

        execute();
    }

    @Test
    public void test_assertAnyOrder_distributedSource() {
        List<Integer> input = IntStream.range(0, itemCount).boxed().collect(Collectors.toList());
        putToBatchSrcMap(input);

        List<Entry<String, Integer>> expected = input.stream()
                                                     .map(i -> entry(String.valueOf(i), i))
                                                     .collect(Collectors.toList());

        p.drawFrom(Sources.map(srcMap))
         .apply(Assertions.assertAnyOrder(expected));

        execute();
    }

    @Test
    public void test_assertAnyOrder_should_fail() throws Throwable {
        p.drawFrom(TestSources.items(3, 2, 1))
         .apply(Assertions.assertAnyOrder(Arrays.asList(1, 2, 3, 4)));

        expectedException.expect(AssertionError.class);
        executeAndPeel();
    }

    @Test
    public void test_assertAnyOrder_duplicate_entry() throws Throwable {
        p.drawFrom(TestSources.items(1, 3, 2, 3))
                .apply(Assertions.assertAnyOrder(Arrays.asList(1, 2, 3, 3)));

        execute();
    }

    @Test
    public void test_assertAnyOrder_duplicate_entry_only_in_source_should_fail() throws Throwable {
        p.drawFrom(TestSources.items(1, 3, 2, 3))
                .apply(Assertions.assertAnyOrder(Arrays.asList(1, 2, 3)));

        expectedException.expect(AssertionError.class);
        executeAndPeel();
    }

    @Test
    public void test_assertAnyOrder_duplicate_entry_only_in_assert_should_fail() throws Throwable {
        p.drawFrom(TestSources.items(1, 2, 3))
                .apply(Assertions.assertAnyOrder(Arrays.asList(1, 2, 3, 3)));

        expectedException.expect(AssertionError.class);
        executeAndPeel();
    }

    @Test
    public void test_assertAnyOrder_not_terminal() {
        List<Integer> assertionSink = jet().getList(sinkName);
        assertTrue(assertionSink.isEmpty());

        p.drawFrom(TestSources.items(4, 3, 2, 1))
                .apply(Assertions.assertAnyOrder(Arrays.asList(1, 2, 3, 4)))
                .drainTo(sinkList());

        execute();

        assertEquals(4, assertionSink.size());
        assertEquals(4, (int) assertionSink.get(0));
        assertEquals(3, (int) assertionSink.get(1));
        assertEquals(2, (int) assertionSink.get(2));
        assertEquals(1, (int) assertionSink.get(3));
    }

    @Test
    public void test_assertAnyOrder_not_terminal_should_fail() throws Throwable {
        List<Integer> assertionSink = jet().getList(sinkName);
        assertTrue(assertionSink.isEmpty());

        p.drawFrom(TestSources.items(3, 2, 1))
                .apply(Assertions.assertAnyOrder(Arrays.asList(1, 2, 3, 4)))
                .drainTo(sinkList());

        expectedException.expect(AssertionError.class);
        executeAndPeel();

        assertEquals(4, assertionSink.size());
        assertEquals(3, (int) assertionSink.get(0));
        assertEquals(2, (int) assertionSink.get(1));
        assertEquals(1, (int) assertionSink.get(2));
    }

    @Test
    public void test_assertContains() {
        p.drawFrom(TestSources.items(4, 3, 2, 1))
         .apply(Assertions.assertContains(Arrays.asList(1, 3)));

        execute();
    }

    @Test
    public void test_assertContains_should_fail() throws Throwable {
        p.drawFrom(TestSources.items(4, 1, 2, 3))
         .apply(Assertions.assertContains(Arrays.asList(1, 3, 5)));

        expectedException.expect(AssertionError.class);
        executeAndPeel();
    }

    @Test
    public void test_assertContains_not_terminal() {
        List<Integer> assertionSink = jet().getList(sinkName);
        assertTrue(assertionSink.isEmpty());

        p.drawFrom(TestSources.items(4, 3, 2, 1))
                .apply(Assertions.assertContains(Arrays.asList(1, 3)))
                .drainTo(sinkList());

        execute();

        assertEquals(4, assertionSink.size());
        assertEquals(4, (int) assertionSink.get(0));
        assertEquals(3, (int) assertionSink.get(1));
        assertEquals(2, (int) assertionSink.get(2));
        assertEquals(1, (int) assertionSink.get(3));
    }

    @Test
    public void test_assertContains_not_terminal_should_fail() throws Throwable {
        List<Integer> assertionSink = jet().getList(sinkName);
        assertTrue(assertionSink.isEmpty());

        p.drawFrom(TestSources.items(4, 1, 2, 3))
                .apply(Assertions.assertContains(Arrays.asList(1, 3, 5)))
                .drainTo(sinkList());

        expectedException.expect(AssertionError.class);
        executeAndPeel();

        assertEquals(4, assertionSink.size());
        assertEquals(4, (int) assertionSink.get(0));
        assertEquals(1, (int) assertionSink.get(1));
        assertEquals(2, (int) assertionSink.get(2));
        assertEquals(3, (int) assertionSink.get(3));
    }

    @Test
    public void test_assertCollected() {
        p.drawFrom(TestSources.items(4, 3, 2, 1))
         .apply(assertCollected(c -> assertTrue("list size must be at least 4", c.size() >= 4)));

        execute();
    }

    @Test
    public void test_assertCollected_should_fail() throws Throwable {
        p.drawFrom(TestSources.items(1))
         .apply(assertCollected(c -> assertTrue("list size must be at least 4", c.size() >= 4)));

        expectedException.expect(AssertionError.class);
        executeAndPeel();
    }

    @Test
    public void test_assertCollected_not_terminal() {
        List<Integer> assertionSink = jet().getList(sinkName);
        assertTrue(assertionSink.isEmpty());

        p.drawFrom(TestSources.items(4, 3, 2, 1))
                .apply(assertCollected(c -> assertTrue("list size must be at least 4", c.size() >= 4)))
                .drainTo(sinkList());

        execute();

        assertEquals(4, assertionSink.size());
        assertEquals(4, (int) assertionSink.get(0));
        assertEquals(3, (int) assertionSink.get(1));
        assertEquals(2, (int) assertionSink.get(2));
        assertEquals(1, (int) assertionSink.get(3));
    }

    @Test
    public void test_assertCollected_not_terminal_should_fail() throws Throwable {
        List<Integer> assertionSink = jet().getList(sinkName);
        assertTrue(assertionSink.isEmpty());

        p.drawFrom(TestSources.items(1))
                .apply(assertCollected(c -> assertTrue("list size must be at least 4", c.size() >= 4)))
                .drainTo(sinkList());

        expectedException.expect(AssertionError.class);
        executeAndPeel();

        assertEquals(1, assertionSink.size());
        assertEquals(1, (int) assertionSink.get(0));
    }

    @Test
    public void test_assertCollectedEventually() throws Throwable {
        p.drawFrom(TestSources.itemStream(1, (ts, seq) -> 0L))
         .withoutTimestamps()
         .apply(assertCollectedEventually(5, c -> assertTrue("did not receive item '0'", c.contains(0L))));

        expectedException.expect(AssertionCompletedException.class);
        executeAndPeel();
    }

    @Test
    public void test_assertCollectedEventually_should_fail() throws Throwable {
        p.drawFrom(TestSources.itemStream(1, (ts, seq) -> 0L))
         .withoutTimestamps()
         .apply(assertCollectedEventually(5, c -> assertTrue("did not receive item '1'", c.contains(1L))));

        expectedException.expect(AssertionError.class);
        executeAndPeel();
    }

    @Test
    public void test_assertCollectedEventuallynot_terminal() throws Throwable {
        List<Integer> assertionSink = jet().getList(sinkName);
        assertTrue(assertionSink.isEmpty());

        p.drawFrom(TestSources.itemStream(1, (ts, seq) -> 0L))
                .withoutTimestamps()
                .apply(assertCollectedEventually(5, c -> assertTrue("did not receive item '0'", c.contains(0L))))
                .drainTo(sinkList());

        expectedException.expect(AssertionCompletedException.class);
        executeAndPeel();

        assertFalse(assertionSink.isEmpty());
    }

    @Test
    public void test_assertCollectedEventuallynot_terminal_should_fail() throws Throwable {
        List<Integer> assertionSink = jet().getList(sinkName);
        assertTrue(assertionSink.isEmpty());

        p.drawFrom(TestSources.itemStream(1, (ts, seq) -> 0L))
                .withoutTimestamps()
                .apply(assertCollectedEventually(5, c -> assertTrue("did not receive item '1'", c.contains(1L))))
                .drainTo(sinkList());

        expectedException.expect(AssertionError.class);
        executeAndPeel();

        assertFalse(assertionSink.isEmpty());
    }

    @Test
    public void test_multiple_assertions_in_pipeline() throws Throwable {
        Map<String, Long> assertionSink = jet().getMap(sinkName);
        assertTrue(assertionSink.isEmpty());

        p.drawFrom(TestSources.items("some text here and here and some here"))
                .apply(Assertions.assertOrdered(Arrays.asList("some text here and here and some here")))
                .flatMap(line -> traverseArray(line.toLowerCase().split("\\W+")))
                .apply(Assertions.assertAnyOrder(
                        Arrays.asList("some", "text", "here", "and", "here", "and", "some", "here")))
                .filter(word -> !word.equals("and"))
                .apply(Assertions.assertContains(Arrays.asList("some", "text")))
                .apply(Assertions.assertContains(Arrays.asList("some", "here")))
                .groupingKey(wholeItem())
                .aggregate(AggregateOperations.counting())
                .drainTo(Sinks.map(sinkName));

        execute();

        assertEquals(3, assertionSink.size());
        assertEquals(2, (long) assertionSink.get("some"));
        assertEquals(1, (long) assertionSink.get("text"));
        assertEquals(3, (long) assertionSink.get("here"));
    }
}
