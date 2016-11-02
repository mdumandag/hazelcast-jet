/*
 * Copyright (c) 2008-2016, Hazelcast, Inc. All Rights Reserved.
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

package com.hazelcast.jet2;

import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.nio.Address;

import java.io.Serializable;
import java.util.stream.Stream;

import static java.util.stream.Collectors.toList;

public interface ProcessorMetaSupplier extends Serializable {

    default void init(Context context) {
    }

    ProcessorSupplier get(Address address);

    static ProcessorMetaSupplier of(final ProcessorSupplier listSupplier) {
      return address -> listSupplier;
    }

    static ProcessorMetaSupplier of(final SimpleProcessorSupplier processorSupplier) {
        return address -> count -> Stream.generate(processorSupplier::get).limit(count).collect(toList());
    }

    interface Context {

        HazelcastInstance getHazelcastInstance();

        int totalParallelism();

        int perNodeParallelism();

        static Context of(HazelcastInstance instance, int totalParallelism, int perNodeParallelism) {
            return new Context() {
                @Override
                public HazelcastInstance getHazelcastInstance() {
                    return instance;
                }
                @Override
                public int totalParallelism() {
                    return totalParallelism;
                }
                @Override
                public int perNodeParallelism() {
                    return perNodeParallelism;
                }
            };
        }
    }
}
