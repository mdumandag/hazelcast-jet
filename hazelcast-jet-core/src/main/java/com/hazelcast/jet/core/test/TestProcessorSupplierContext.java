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

package com.hazelcast.jet.core.test;

import com.hazelcast.jet.JetInstance;
import com.hazelcast.jet.config.ProcessingGuarantee;
import com.hazelcast.jet.core.ProcessorSupplier;
import com.hazelcast.logging.ILogger;

import javax.annotation.Nonnull;

/**
 * {@link ProcessorSupplier.Context} implementation suitable to be used in tests.
 *
 * @since 3.0
 */
public class TestProcessorSupplierContext
        extends TestProcessorMetaSupplierContext
        implements ProcessorSupplier.Context {

    private int memberIndex;

    @Nonnull @Override
    public TestProcessorSupplierContext setLogger(@Nonnull ILogger logger) {
        return (TestProcessorContext) super.setLogger(logger);
    }

    @Nonnull @Override
    public TestProcessorSupplierContext setJetInstance(@Nonnull JetInstance jetInstance) {
        return (TestProcessorSupplierContext) super.setJetInstance(jetInstance);
    }

    @Nonnull @Override
    public TestProcessorSupplierContext setVertexName(@Nonnull String vertexName) {
        return (TestProcessorSupplierContext) super.setVertexName(vertexName);
    }

    @Nonnull @Override
    public TestProcessorSupplierContext setTotalParallelism(int totalParallelism) {
        return (TestProcessorSupplierContext) super.setTotalParallelism(totalParallelism);
    }

    @Nonnull @Override
    public TestProcessorSupplierContext setLocalParallelism(int localParallelism) {
        return (TestProcessorSupplierContext) super.setLocalParallelism(localParallelism);
    }

    @Nonnull @Override
    public TestProcessorSupplierContext setProcessingGuarantee(ProcessingGuarantee processingGuarantee) {
        return (TestProcessorSupplierContext) super.setProcessingGuarantee(processingGuarantee);
    }

    @Override
    public int memberIndex() {
        assert memberIndex >= 0 && memberIndex < memberCount()
                : "memberIndex should be in range 0.." + (memberCount() - 1);
        return memberIndex;
    }

    /**
     * Set the member index.
     */
    public TestProcessorSupplierContext setMemberIndex(int memberIndex) {
        this.memberIndex = memberIndex;
        return this;
    }

    @Override
    protected String loggerName() {
        return vertexName() + "#PS";
    }
}
