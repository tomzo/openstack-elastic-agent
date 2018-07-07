/*
 * Copyright 2016 ThoughtWorks, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package cd.go.contrib.elasticagents.openstack;

import cd.go.contrib.elasticagents.openstack.executors.AgentNotFoundException;
import cd.go.contrib.elasticagents.openstack.requests.CreateAgentRequest;
import cd.go.contrib.elasticagents.openstack.utils.OpenstackClientWrapper;

import java.util.Map;

/**
 * Plugin implementors should implement these methods to interface to your cloud.
 * This interface is merely a suggestion for a very simple plugin. You may change it to your needs.
 */
public interface AgentInstances<T> {
    /**
     * This message is sent to request creation of an agent instance.
     * Implementations may, at their discretion choose to not spin up an agent instance.
     * <p>
     * So that instances created are auto-registered with the server, the agent instance should have an
     * <code>autoregister.properties</code>.
     * @param request       the request object
     * @param settings        the plugin settings object
     * @param vault_token
     * @param transactionId
     */
    OpenStackInstance create(CreateAgentRequest request, PluginSettings settings, String vault_token, String transactionId) throws Exception;

    /**
     * This message is sent to assist the plugin to refresh any metadata about the agent.
     * The implementation is expected to connect to the cloud provider
     * and make a call to check if the instance is alive and operating well.
     *
     * @param agentId  the elastic agent id
     * @param settings the plugin settings object
     * @throws AgentNotFoundException if there was an error refreshing, or finding the agent
     */
    void refresh(String agentId, PluginSettings settings) throws Exception;

    /**
     * This message is sent when the plugin needs to terminate the agent instance.
     *
     * @param agentId  the elastic agent id
     * @param settings the plugin settings object
     * @throws AgentNotFoundException if the agent instance could not be found
     */
    void terminate(String agentId, PluginSettings settings) throws Exception;

    /**
     * This message is sent via from the {@link cd.go.contrib.elasticagents.openstack.executors.ServerPingRequestExecutor}
     * to terminate instances that did not register with the server after a timeout. The timeout may be configurable and
     * set via the {@link PluginSettings} instance that is passed in.
     *
     * @param settings the plugin settings object
     * @param agents   the list of all the agents
     */
    void terminateUnregisteredInstances(PluginSettings settings, Agents agents) throws Exception;


    boolean isInstanceAlive(PluginSettings settings, String id) throws Exception;

    boolean matchInstance(String id, Map<String, String> properties, String environment, PluginSettings pluginSettings, OpenstackClientWrapper client,
                          String transactionId, boolean usePreviousImageId);

    /**
     * This message is sent from the {@link cd.go.contrib.elasticagents.openstack.executors.ServerPingRequestExecutor}
     * to filter out any new agents, that have registered before the timeout period. The timeout may be configurable and
     * set via the {@link PluginSettings} instance that is passed in.
     *
     * @param settings the plugin settings object
     * @param agents   the list of all the agents
     * @return a list of agent instances which were created after {@link PluginSettings#getAutoRegisterPeriod()} ago.
     */
    Agents instancesCreatedAfterTimeout(PluginSettings settings, Agents agents);

    /**
     * This message is sent after plugin initialization time so that the plugin may connect to the cloud provider
     * and fetch a list of all instances that have been spun up by this plugin (before the server was shut down).
     * This call should be should ideally remember if the agent instances are refreshed, and do nothing if instances
     * were previously refreshed.
     *
     * @param pluginRequest the plugin request object
     */
    void refreshAll(PluginRequest pluginRequest) throws Exception;

    /**
     * This
     * Returns an agent instance with the specified <code>id</code> or <code>null</code>, if the agent is not found.
     *
     * @param agentId the elastic agent id
     */
    T find(String agentId);
}
