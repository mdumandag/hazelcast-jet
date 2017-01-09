/*
 * Copyright (c) 2008-2017, Hazelcast, Inc. All Rights Reserved.
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

package com.hazelcast.jet;

import com.hazelcast.config.Config;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Properties;

import static com.hazelcast.util.ExceptionUtil.rethrow;

/**
 * Javadoc pending
 */
public class JetConfig {

    /**
     * Javadoc pending
     */
    public static final int DEFAULT_FLOW_CONTROL_PERIOD_MS = 100;
    /**
     * Javadoc pending
     */
    public static final int DEFAULT_JET_MULTICAST_PORT = 54326;

    private Config hazelcastConfig = defaultHazelcastConfig();
    private int threadCount = Runtime.getRuntime().availableProcessors();
    private int flowControlPeriodMs = DEFAULT_FLOW_CONTROL_PERIOD_MS;
    private String workingDirectory;
    private EdgeConfig defaultEdgeConfig = new EdgeConfig();
    private Properties properties = new Properties();

    /**
     * @return Javadoc pending
     */
    public Config getHazelcastConfig() {
        return hazelcastConfig;
    }

    /**
     * @return Javadoc pending
     */
    public JetConfig setHazelcastConfig(Config config) {
        hazelcastConfig = config;
        return this;
    }

    /**
     * @return Jet specific properties
     */
    public Properties getProperties() {
        return properties;
    }

    /**
     * Sets Jet specific properties
     */
    public JetConfig setProperties(Properties properties) {
        this.properties = properties;
        return this;
    }

    /**
     * @return the number of execution threads per node
     */
    public int getExecutionThreadCount() {
        return threadCount;
    }

    /**
     * Sets the number of execution threads per node
     */
    public JetConfig setExecutionThreadCount(int size) {
        this.threadCount = size;
        return this;
    }

    /**
     * @return the working directory used for storing deployed resources
     */
    public String getWorkingDirectory() {
        if (workingDirectory == null) {
            try {
                Path tempDirectory = Files.createTempDirectory("hazelcast-jet");
                tempDirectory.toFile().deleteOnExit();
                workingDirectory = tempDirectory.toString();
            } catch (IOException e) {
                throw rethrow(e);
            }
        }
        return workingDirectory;
    }

    /**
     * Sets the working directory used for storing deployed resources
     */
    public JetConfig setWorkingDirectory(String workingDirectory) {
        this.workingDirectory = workingDirectory;
        return this;
    }

    /**
     * @return Javadoc pending
     */
    public int getFlowControlPeriodMs() {
        return flowControlPeriodMs;
    }

    /**
     * Javadoc pending
     *
     * @param flowControlPeriodMs
     */
    public JetConfig setFlowControlPeriodMs(int flowControlPeriodMs) {
        this.flowControlPeriodMs = flowControlPeriodMs;
        return this;
    }

    /**
     * Javadoc pending*
     *
     * @return
     */
    public EdgeConfig getDefaultEdgeConfig() {
        return defaultEdgeConfig;
    }

    /**
     * Javadoc pending
     *
     * @param defaultEdgeConfig
     */
    public JetConfig setDefaultEdgeConfig(EdgeConfig defaultEdgeConfig) {
        this.defaultEdgeConfig = defaultEdgeConfig;
        return this;
    }

    private static Config defaultHazelcastConfig() {
        Config config = new Config();
        config.getNetworkConfig().getJoin().getMulticastConfig().setMulticastPort(DEFAULT_JET_MULTICAST_PORT);
        config.getGroupConfig().setName("jet");
        config.getGroupConfig().setPassword("jet-pass");
        return config;
    }

}