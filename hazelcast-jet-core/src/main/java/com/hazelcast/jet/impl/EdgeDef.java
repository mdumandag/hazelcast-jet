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

package com.hazelcast.jet.impl;

import com.hazelcast.jet.Edge;
import com.hazelcast.jet.Partitioner;

import java.io.Serializable;

class EdgeDef implements Serializable {

    private final int otherEndId;
    private final int otherEndOrdinal;
    private final int ordinal;
    private final int priority;
    private final boolean isDistributed;
    private final Edge.ForwardingPattern forwardingPattern;
    private final Partitioner partitioner;

    EdgeDef(int otherEndId, int ordinal, int otherEndOrdinal, int priority,
                   boolean isDistributed, Edge.ForwardingPattern forwardingPattern, Partitioner partitioner) {
        this.otherEndId = otherEndId;
        this.otherEndOrdinal = otherEndOrdinal;
        this.ordinal = ordinal;
        this.priority = priority;
        this.isDistributed = isDistributed;
        this.forwardingPattern = forwardingPattern;
        this.partitioner = partitioner;
    }

    int getOtherEndId() {
        return otherEndId;
    }

    int getOrdinal() {
        return ordinal;
    }

    Edge.ForwardingPattern getForwardingPattern() {
        return forwardingPattern;
    }

    Partitioner getPartitioner() {
        return partitioner;
    }

    int getPriority() {
        return priority;
    }

    int getOtherEndOrdinal() {
        return otherEndOrdinal;
    }

    boolean isDistributed() {
        return isDistributed;
    }
}