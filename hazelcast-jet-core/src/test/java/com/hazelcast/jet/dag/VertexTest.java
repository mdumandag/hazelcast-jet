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

package com.hazelcast.jet.dag;

import com.hazelcast.jet.Edge;
import com.hazelcast.jet.TestProcessors;
import com.hazelcast.jet.Vertex;
import com.hazelcast.jet.impl.job.JobContext;
import com.hazelcast.jet.runtime.DataWriter;
import com.hazelcast.jet.runtime.Producer;
import com.hazelcast.jet.Sink;
import com.hazelcast.jet.Source;
import com.hazelcast.test.HazelcastParallelClassRunner;
import com.hazelcast.test.annotation.QuickTest;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;

import java.util.List;

import static com.hazelcast.jet.JetTestSupport.createVertex;
import static org.junit.Assert.assertEquals;

@Category(QuickTest.class)
@RunWith(HazelcastParallelClassRunner.class)
public class VertexTest {

    @Test
    public void testVertexNameAndProcessorFactory() throws Exception {
        String name = "v1";
        Class<TestProcessors.Noop> procesorClass = TestProcessors.Noop.class;
        Vertex v1 = createVertex(name, procesorClass);
        assertEquals(name, v1.getName());
        assertEquals(procesorClass.getName(), v1.getProcessorClass());
    }

    @Test
    public void testVertexInput() throws Exception {
        Vertex v1 = createVertex("v1", TestProcessors.Noop.class);
        Vertex input = createVertex("input", TestProcessors.Noop.class);

        Edge edge = new Edge("e1", input, v1);
        v1.addInputVertex(input, edge);

        List<Vertex> inputVertices = v1.getInputVertices();
        List<Edge> inputEdges = v1.getInputEdges();
        assertEquals(1, inputVertices.size());
        assertEquals(1, inputEdges.size());
        assertEquals(input, inputVertices.iterator().next());
        assertEquals(edge, inputEdges.iterator().next());
    }

    @Test
    public void testVertexOutput() throws Exception {
        Vertex v1 = createVertex("v1", TestProcessors.Noop.class);
        Vertex output = createVertex("output", TestProcessors.Noop.class);

        Edge edge = new Edge("e1", v1, output);
        v1.addOutputVertex(output, edge);

        List<Vertex> outputVertices = v1.getOutputVertices();
        List<Edge> outputEdges = v1.getOutputEdges();
        assertEquals(1, outputVertices.size());
        assertEquals(1, outputEdges.size());
        assertEquals(output, outputVertices.iterator().next());
        assertEquals(edge, outputEdges.iterator().next());

    }

    @Test
    public void testVertexSources() throws Exception {
        Vertex vertex = createVertex("vertex", TestProcessors.Noop.class);

        final String sourceTapName = "sourceTapName";
        Source source = new Source() {
            @Override
            public Producer[] getProducers(JobContext jobContext, Vertex vertex) {
                return new Producer[0];
            }

            @Override
            public String getName() {
                return sourceTapName;
            }
        };
        vertex.addSource(source);

        assertEquals(1, vertex.getSources().size());
        assertEquals(source, vertex.getSources().get(0));
    }

    @Test
    public void testVertexSinks() throws Exception {
        Vertex vertex = createVertex("vertex", TestProcessors.Noop.class);

        final String sinkTapName = "sinkTapWithWriterStrategyName";
        Sink sink = new Sink() {
            @Override
            public DataWriter[] getWriters(JobContext jobContext) {
                return new DataWriter[0];
            }

            @Override
            public boolean isPartitioned() {
                return false;
            }

            @Override
            public String getName() {
                return sinkTapName;
            }
        };
        vertex.addSink(sink);

        assertEquals(1, vertex.getSinks().size());
        assertEquals(sink, vertex.getSinks().get(0));
    }
}