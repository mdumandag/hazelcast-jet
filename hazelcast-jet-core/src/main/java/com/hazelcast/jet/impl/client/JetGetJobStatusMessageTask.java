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

package com.hazelcast.jet.impl.client;

import com.hazelcast.client.impl.protocol.ClientMessage;
import com.hazelcast.client.impl.protocol.codec.JetGetJobStatusCodec;
import com.hazelcast.instance.Node;
import com.hazelcast.jet.core.JobStatus;
import com.hazelcast.jet.impl.operation.GetJobStatusOperation;
import com.hazelcast.nio.Connection;
import com.hazelcast.spi.Operation;

public class JetGetJobStatusMessageTask extends AbstractJetMessageTask<JetGetJobStatusCodec.RequestParameters, JobStatus> {

    JetGetJobStatusMessageTask(ClientMessage clientMessage, Node node, Connection connection) {
        super(clientMessage, node, connection,
                JetGetJobStatusCodec::decodeRequest,
                response -> JetGetJobStatusCodec.encodeResponse(response.ordinal()));
    }

    @Override
    protected Operation prepareOperation() {
        return new GetJobStatusOperation(parameters.jobId);
    }

    @Override
    public String getMethodName() {
        return "getJobStatus";
    }

    @Override
    public Object[] getParameters() {
        return new Object[0];
    }
}
