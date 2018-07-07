package cd.go.contrib.elasticagents.openstack.executors;

import cd.go.contrib.elasticagents.openstack.*;
import cd.go.contrib.elasticagents.openstack.requests.CreateAgentRequest;
import cd.go.contrib.elasticagents.openstack.utils.OpenstackClientWrapper;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentMatchers;

import java.util.HashMap;
import java.util.Map;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

public class CreateAgentRequestExecutorTest {
    private static final String IMAGE_ID = "7637f039-027d-471f-8d6c-4177635f84f8";
    private static final String FLAVOR_ID = "5";
    private CreateAgentRequest createAgentRequest;
    private AgentInstances agentInstances;
    private PluginRequest pluginRequest;
    private Agents agents;
    private PluginSettings pluginSettings;
    private OpenstackClientWrapper openstackClientWrapper;
    private PendingAgentsService pendingAgents;
    private OpenStackInstance osInstance;
    private Map<String, Object> job1;
    private Map<String, Object> job2;
    private VaultService vaultService;
    private String agentToken = "e0b32ba1-ffa1-46c4-8b49-b67200f5e525";
    private String serverToken = "79913f80-b303-47e3-be0d-81374263a54c";

    @Before
    public void SetUp() {
        createAgentRequest = mock(CreateAgentRequest.class);
        agentInstances = mock(AgentInstances.class);
        pluginRequest = mock(PluginRequest.class);
        pendingAgents = mock(PendingAgentsService.class);
        vaultService = mock(VaultService.class);
        when(vaultService.createAgentToken(any(VaultServiceConfig.class),anyString(), anyString())).thenReturn(agentToken);
        when(pendingAgents.getAgents()).thenReturn(new PendingAgent[0]);
        agents = new Agents();
        openstackClientWrapper = mock(OpenstackClientWrapper.class);
        pluginSettings = new PluginSettings();
        pluginSettings.setOpenstackEndpoint("http://some/url");
        pluginSettings.setOpenstackFlavor("default-flavor");
        pluginSettings.setOpenstackImage(IMAGE_ID);
        pluginSettings.setOpenstackNetwork("780f2cfc-389b-4cc5-9b85-ed03a73975ee");
        pluginSettings.setOpenstackPassword("secret");
        pluginSettings.setOpenstackUser("user");
        pluginSettings.setOpenstackTenant("tenant");
        pluginSettings.setOpenstackVmPrefix("prefix-");
        pluginSettings.setVaultServerToken(serverToken);
        pluginSettings.setVaultAddr("http://vault.com");
        pluginSettings.setVaultPolicy("gocd");
        pluginSettings.setVaultTtl("1h");
        pluginSettings.setVaultSslCert("/etc/ssl/vault.pem");
        osInstance = mock(OpenStackInstance.class);
        when(osInstance.getImageIdOrName()).thenReturn(IMAGE_ID);
        when(osInstance.getFlavorIdOrName()).thenReturn(FLAVOR_ID);
        when(openstackClientWrapper.getImageId(anyString(), anyString())).thenReturn(IMAGE_ID);
        when(openstackClientWrapper.getFlavorId(anyString(), anyString())).thenReturn(FLAVOR_ID);
        job1 = new HashMap<>();
        populateJobFields(job1);
        job2 = new HashMap<>();
        populateJobFields(job2);
        job2.put("job_id", 101);
    }

    private void populateJobFields(Map<String, Object> job) {
        job.put("job_id", 100);
        job.put("job_name", "test-job1");
        job.put("pipeline_counter", 1);
        job.put("pipeline_label", "build");
        job.put("pipeline_name", "build");
        job.put("stage_counter", "1");
        job.put("stage_name", "test-stage");
    }

    @Test
    public void executeShouldCallVaultServiceWithConfigurationFromPluginSettings() throws Exception {
        // Arrange
        when(pendingAgents.getAgents()).thenReturn(new PendingAgent[0]);
        when(pluginRequest.listAgents()).thenReturn(agents);
        when(pluginRequest.getPluginSettings()).thenReturn(pluginSettings);
        when(agentInstances.matchInstance(anyString(), ArgumentMatchers.<String, String>anyMap(), anyString(), any(PluginSettings.class),
                any(OpenstackClientWrapper.class), anyString(), anyBoolean())).thenReturn(true);
        CreateAgentRequestExecutor executor = new CreateAgentRequestExecutor(createAgentRequest, agentInstances, pluginRequest, openstackClientWrapper, pendingAgents, vaultService);

        // Act
        executor.execute();

        // Assert
        VaultServiceConfig vaultConfig = new VaultServiceConfig("http://vault.com", serverToken, "/etc/ssl/vault.pem");
        verify(vaultService, times(1)).createAgentToken(eq(vaultConfig), eq("gocd"), eq("1h"));
    }


    @Test
    public void executeShouldCallVaultServiceWithConfigurationFromAgentProfileWhenDefined() throws Exception {
        // Arrange
        Map<String, String> props = new HashMap<>();
        props.put(Constants.VAULT_POLICY, "policy2");
        props.put(Constants.VAULT_TTL, "3h");
        when(createAgentRequest.properties()).thenReturn(props);
        when(pendingAgents.getAgents()).thenReturn(new PendingAgent[0]);
        when(pluginRequest.listAgents()).thenReturn(agents);
        when(pluginRequest.getPluginSettings()).thenReturn(pluginSettings);
        when(agentInstances.matchInstance(anyString(), ArgumentMatchers.<String, String>anyMap(), anyString(), any(PluginSettings.class),
                any(OpenstackClientWrapper.class), anyString(), anyBoolean())).thenReturn(true);
        CreateAgentRequestExecutor executor = new CreateAgentRequestExecutor(createAgentRequest, agentInstances, pluginRequest, openstackClientWrapper, pendingAgents, vaultService);

        // Act
        executor.execute();

        // Assert
        VaultServiceConfig vaultConfig = new VaultServiceConfig("http://vault.com", serverToken, "/etc/ssl/vault.pem");
        verify(vaultService, times(1)).createAgentToken(eq(vaultConfig), eq("policy2"), eq("3h"));
    }

    @Test
    public void executeShouldCreateAgentWithVaultTokenWhenConfigured() throws Exception {
        // Arrange
        when(pendingAgents.getAgents()).thenReturn(new PendingAgent[0]);
        when(pluginRequest.listAgents()).thenReturn(agents);
        when(pluginRequest.getPluginSettings()).thenReturn(pluginSettings);
        when(agentInstances.matchInstance(anyString(), ArgumentMatchers.<String, String>anyMap(), anyString(), any(PluginSettings.class),
                any(OpenstackClientWrapper.class), anyString(), anyBoolean())).thenReturn(true);
        CreateAgentRequestExecutor executor = new CreateAgentRequestExecutor(createAgentRequest, agentInstances, pluginRequest, openstackClientWrapper, pendingAgents, vaultService);

        // Act
        executor.execute();

        // Assert
        verify(agentInstances, times(1)).create(any(CreateAgentRequest.class), any(PluginSettings.class), eq(agentToken), anyString());
    }

    @Test
    public void executeShouldCreateAgentWhenNoAgentsExist() throws Exception {
        // Arrange
        when(pendingAgents.getAgents()).thenReturn(new PendingAgent[0]);
        when(pluginRequest.listAgents()).thenReturn(agents);
        when(pluginRequest.getPluginSettings()).thenReturn(pluginSettings);
        when(agentInstances.matchInstance(anyString(), ArgumentMatchers.<String, String>anyMap(), anyString(), any(PluginSettings.class),
                any(OpenstackClientWrapper.class), anyString(), anyBoolean())).thenReturn(true);
        CreateAgentRequestExecutor executor = new CreateAgentRequestExecutor(createAgentRequest, agentInstances, pluginRequest, openstackClientWrapper, pendingAgents, vaultService);

        // Act
        executor.execute();

        // Assert
        verify(agentInstances, times(1)).create(any(CreateAgentRequest.class), any(PluginSettings.class), anyString(), anyString());
    }

    @Test
    public void executeShouldNotCreateAgentWhenPendingAgentsExistsForSameJob() throws Exception {
        // Arrange
        PendingAgent[] pending = new PendingAgent[1];
        Map<String, String> props = new HashMap<>();
        CreateAgentRequest originalRequest = new CreateAgentRequest("123", props, job1, null);
        pending[0] = new PendingAgent(osInstance, originalRequest);
        when(pendingAgents.getAgents()).thenReturn(pending);
        when(pluginRequest.listAgents()).thenReturn(agents);
        when(pluginRequest.getPluginSettings()).thenReturn(pluginSettings);
        when(agentInstances.matchInstance(anyString(), ArgumentMatchers.<String, String>anyMap(), anyString(), any(PluginSettings.class),
                any(OpenstackClientWrapper.class), anyString(), anyBoolean())).thenReturn(false);
        when(createAgentRequest.job()).thenReturn(job1);
        CreateAgentRequestExecutor executor = new CreateAgentRequestExecutor(createAgentRequest, agentInstances, pluginRequest, openstackClientWrapper, pendingAgents, vaultService);

        // Act
        executor.execute();

        // Assert
        verify(agentInstances, never()).create(any(CreateAgentRequest.class), any(PluginSettings.class),anyString() , anyString());
    }

    @Test
    public void executeShouldCreateAgentWhenPendingAgentsExistsForAnotherJob() throws Exception {
        // Arrange
        PendingAgent[] pending = new PendingAgent[1];
        Map<String, String> props = new HashMap<>();
        CreateAgentRequest originalRequest = new CreateAgentRequest("123", props, job1, null);
        pending[0] = new PendingAgent(osInstance, originalRequest);
        when(pendingAgents.getAgents()).thenReturn(pending);
        when(pluginRequest.listAgents()).thenReturn(agents);
        when(pluginRequest.getPluginSettings()).thenReturn(pluginSettings);
        when(agentInstances.matchInstance(anyString(), ArgumentMatchers.<String, String>anyMap(), anyString(), any(PluginSettings.class),
                any(OpenstackClientWrapper.class), anyString(), anyBoolean())).thenReturn(false);
        when(createAgentRequest.job()).thenReturn(job2);
        CreateAgentRequestExecutor executor = new CreateAgentRequestExecutor(createAgentRequest, agentInstances, pluginRequest, openstackClientWrapper, pendingAgents, vaultService);

        // Act
        executor.execute();

        // Assert
        verify(agentInstances, times(1)).create(any(CreateAgentRequest.class), any(PluginSettings.class),anyString() , anyString());
    }

    @Test
    public void executeShouldCreateAgentWhenOnlyBuildingAgentsExist() throws Exception {
        // Arrange
        agents.add(new Agent("id1", Agent.AgentState.Building, Agent.BuildState.Building, Agent.ConfigState.Enabled));
        when(pluginRequest.listAgents()).thenReturn(agents);
        when(pluginRequest.getPluginSettings()).thenReturn(pluginSettings);
        when(agentInstances.matchInstance(anyString(), ArgumentMatchers.<String, String>anyMap(), anyString(), any(PluginSettings.class),
                any(OpenstackClientWrapper.class), anyString(), anyBoolean())).thenReturn(true);
        CreateAgentRequestExecutor executor = new CreateAgentRequestExecutor(createAgentRequest, agentInstances, pluginRequest, openstackClientWrapper, pendingAgents, vaultService);

        // Act
        executor.execute();

        // Assert
        verify(agentInstances, times(1)).create(any(CreateAgentRequest.class), any(PluginSettings.class), anyString(), anyString());
    }

    @Test
    public void executeShouldCreateAgentWhenOnlyAgentsWithoutEnvironmentExist() throws Exception {
        // Arrange
        agents.add(new Agent("id1", Agent.AgentState.Building, Agent.BuildState.Building, Agent.ConfigState.Enabled));
        when(pluginRequest.listAgents()).thenReturn(agents);
        when(pluginRequest.getPluginSettings()).thenReturn(pluginSettings);
        when(agentInstances.matchInstance(anyString(), ArgumentMatchers.<String, String>anyMap(), anyString(), any(PluginSettings.class),
                any(OpenstackClientWrapper.class), anyString(), anyBoolean())).thenReturn(true);
        Map<String, String> properties = new HashMap<>();
        properties.put(Constants.OPENSTACK_MAX_INSTANCE_LIMIT, "3");
        createAgentRequest = new CreateAgentRequest("abc-key", properties, job1, "testing");
        CreateAgentRequestExecutor executor = new CreateAgentRequestExecutor(createAgentRequest, agentInstances, pluginRequest, openstackClientWrapper, pendingAgents, vaultService);

        // Act
        executor.execute();

        // Assert
        verify(agentInstances, times(1)).create(any(CreateAgentRequest.class), any(PluginSettings.class), anyString(), anyString());
    }

    @Test
    public void executeShouldCreateAgentWhenOnlyIdleAgentExist() throws Exception {
        // Arrange
        agents.add(new Agent("id1", Agent.AgentState.Idle, Agent.BuildState.Idle, Agent.ConfigState.Enabled));
        agents.add(new Agent("id2", Agent.AgentState.Idle, Agent.BuildState.Idle, Agent.ConfigState.Enabled));
        when(pluginRequest.listAgents()).thenReturn(agents);
        when(pluginRequest.getPluginSettings()).thenReturn(pluginSettings);
        when(agentInstances.matchInstance(anyString(), ArgumentMatchers.<String, String>anyMap(), anyString(), any(PluginSettings.class),
                any(OpenstackClientWrapper.class), anyString(), anyBoolean())).thenReturn(true);
        Map<String, String> properties = new HashMap<>();
        properties.put(Constants.OPENSTACK_MIN_INSTANCE_LIMIT, "3");
        properties.put(Constants.OPENSTACK_MAX_INSTANCE_LIMIT, "");
        createAgentRequest = new CreateAgentRequest("abc-key", properties, job1, "");
        CreateAgentRequestExecutor executor = new CreateAgentRequestExecutor(createAgentRequest, agentInstances, pluginRequest, openstackClientWrapper, pendingAgents, vaultService);

        // Act
        executor.execute();

        // Assert
        verify(agentInstances, atLeastOnce()).create(any(CreateAgentRequest.class), any(PluginSettings.class), anyString(), anyString());
    }

    @Test
    public void executeShouldNotCreateAgentWhenThreeIdleAgentExist() throws Exception {
        // Arrange
        agents.add(new Agent("id1", Agent.AgentState.Idle, Agent.BuildState.Idle, Agent.ConfigState.Enabled));
        agents.add(new Agent("id2", Agent.AgentState.Idle, Agent.BuildState.Idle, Agent.ConfigState.Enabled));
        agents.add(new Agent("id3", Agent.AgentState.Idle, Agent.BuildState.Idle, Agent.ConfigState.Enabled));
        when(pluginRequest.listAgents()).thenReturn(agents);
        when(pluginRequest.getPluginSettings()).thenReturn(pluginSettings);
        when(agentInstances.matchInstance(anyString(), ArgumentMatchers.<String, String>anyMap(), anyString(), any(PluginSettings.class),
                any(OpenstackClientWrapper.class), anyString(), anyBoolean())).thenReturn(true);
        Map<String, String> properties = new HashMap<>();
        properties.put(Constants.OPENSTACK_MAX_INSTANCE_LIMIT, "");
        createAgentRequest = new CreateAgentRequest("abc-key", properties, job1, "");
        CreateAgentRequestExecutor executor = new CreateAgentRequestExecutor(createAgentRequest, agentInstances, pluginRequest, openstackClientWrapper, pendingAgents, vaultService);

        // Act
        executor.execute();

        // Assert
        verify(agentInstances, never()).create(any(CreateAgentRequest.class), any(PluginSettings.class), anyString(), anyString());
    }

    @Test
    public void executeShouldNotCreateAgentWhenIdleAndBuildingAgentExist() throws Exception {
        // Arrange
        agents.add(new Agent("id1", Agent.AgentState.Idle, Agent.BuildState.Idle, Agent.ConfigState.Enabled));
        agents.add(new Agent("id2", Agent.AgentState.Idle, Agent.BuildState.Idle, Agent.ConfigState.Enabled));
        agents.add(new Agent("id3", Agent.AgentState.Idle, Agent.BuildState.Idle, Agent.ConfigState.Enabled));
        agents.add(new Agent("id4", Agent.AgentState.Building, Agent.BuildState.Building, Agent.ConfigState.Enabled));
        when(pluginRequest.listAgents()).thenReturn(agents);
        when(pluginRequest.getPluginSettings()).thenReturn(pluginSettings);
        Map<String, String> properties = new HashMap<>();
        properties.put(Constants.OPENSTACK_MIN_INSTANCE_LIMIT, "3");
        createAgentRequest = new CreateAgentRequest("abc-key", properties, job1, "");
        when(agentInstances.matchInstance(anyString(), ArgumentMatchers.<String, String>anyMap(), anyString(), any(PluginSettings.class),
                any(OpenstackClientWrapper.class), anyString(), anyBoolean())).thenReturn(true);
        CreateAgentRequestExecutor executor = new CreateAgentRequestExecutor(createAgentRequest, agentInstances, pluginRequest, openstackClientWrapper, pendingAgents, vaultService);

        // Act
        executor.execute();

        // Assert
        verify(agentInstances, never()).create(any(CreateAgentRequest.class), any(PluginSettings.class), anyString(), anyString());
    }

    @Test
    public void executeShouldNotCreateAgentWhenMaximumAgentsExist() throws Exception {
        // Arrange
        agents.add(new Agent("id1", Agent.AgentState.Building, Agent.BuildState.Building, Agent.ConfigState.Enabled));
        agents.add(new Agent("id2", Agent.AgentState.Building, Agent.BuildState.Building, Agent.ConfigState.Enabled));
        agents.add(new Agent("id3", Agent.AgentState.Building, Agent.BuildState.Building, Agent.ConfigState.Enabled));
        when(pluginRequest.listAgents()).thenReturn(agents);
        Map<String, String> properties = new HashMap<>();
        properties.put(Constants.OPENSTACK_MAX_INSTANCE_LIMIT, "3");
        createAgentRequest = new CreateAgentRequest("abc-key", properties, job1, "");
        when(pluginRequest.getPluginSettings()).thenReturn(pluginSettings);
        when(agentInstances.matchInstance(anyString(), ArgumentMatchers.<String, String>anyMap(), anyString(), any(PluginSettings.class),
                any(OpenstackClientWrapper.class), anyString(), anyBoolean())).thenReturn(true);
        CreateAgentRequestExecutor executor = new CreateAgentRequestExecutor(createAgentRequest, agentInstances, pluginRequest, openstackClientWrapper, pendingAgents, vaultService);

        // Act
        executor.execute();

        // Assert
        verify(agentInstances, never()).create(any(CreateAgentRequest.class), any(PluginSettings.class), anyString(), anyString());
    }

    @Test
    public void executeShouldNotThrowNumberFormatExceptionWhenLimitIsEmptyString() throws Exception {
        // Arrange
        when(pluginRequest.listAgents()).thenReturn(agents);
        Map<String, String> properties = new HashMap<>();
        properties.put(Constants.OPENSTACK_MAX_INSTANCE_LIMIT, "");
        createAgentRequest = new CreateAgentRequest("abc-key", properties, job1, "testing");
        when(pluginRequest.getPluginSettings()).thenReturn(pluginSettings);
        when(agentInstances.matchInstance(anyString(), ArgumentMatchers.<String, String>anyMap(), anyString(), any(PluginSettings.class),
                any(OpenstackClientWrapper.class), anyString(), anyBoolean())).thenReturn(true);
        CreateAgentRequestExecutor executor = new CreateAgentRequestExecutor(createAgentRequest, agentInstances, pluginRequest, openstackClientWrapper, pendingAgents, vaultService);

        // Act
        executor.execute();

        // Assert
        verify(agentInstances, times(1)).create(any(CreateAgentRequest.class), any(PluginSettings.class), anyString(), anyString());
    }

    @Test
    public void executeShouldNotCreateAgentWhenMoreThanMaximumAgentsExist() throws Exception {
        // Arrange
        agents.add(new Agent("id1", Agent.AgentState.Building, Agent.BuildState.Building, Agent.ConfigState.Enabled));
        agents.add(new Agent("id2", Agent.AgentState.Building, Agent.BuildState.Building, Agent.ConfigState.Enabled));
        agents.add(new Agent("id3", Agent.AgentState.Building, Agent.BuildState.Building, Agent.ConfigState.Enabled));
        agents.add(new Agent("id4", Agent.AgentState.Building, Agent.BuildState.Building, Agent.ConfigState.Enabled));
        agents.add(new Agent("id5", Agent.AgentState.Building, Agent.BuildState.Building, Agent.ConfigState.Enabled));
        agents.add(new Agent("id6", Agent.AgentState.Building, Agent.BuildState.Building, Agent.ConfigState.Enabled));
        when(pluginRequest.listAgents()).thenReturn(agents);
        Map<String, String> properties = new HashMap<>();
        properties.put(Constants.OPENSTACK_MAX_INSTANCE_LIMIT, "3");
        createAgentRequest = new CreateAgentRequest("abc-key", properties, job1, "testing");
        when(pluginRequest.getPluginSettings()).thenReturn(pluginSettings);
        when(agentInstances.matchInstance(anyString(), ArgumentMatchers.<String, String>anyMap(), anyString(), any(PluginSettings.class),
                any(OpenstackClientWrapper.class), anyString(), anyBoolean())).thenReturn(true);
        CreateAgentRequestExecutor executor = new CreateAgentRequestExecutor(createAgentRequest, agentInstances, pluginRequest, openstackClientWrapper, pendingAgents, vaultService);

        // Act
        executor.execute();

        // Assert
        verify(agentInstances, never()).create(any(CreateAgentRequest.class), any(PluginSettings.class), anyString(), anyString());
    }

    @Test
    public void executeShouldNotCreateAgentWhenMoreThanMaximumAgentsExistUsingPluginDefaultValue() throws Exception {
        // Arrange
        agents.add(new Agent("id1", Agent.AgentState.Building, Agent.BuildState.Building, Agent.ConfigState.Enabled));
        agents.add(new Agent("id2", Agent.AgentState.Building, Agent.BuildState.Building, Agent.ConfigState.Enabled));
        agents.add(new Agent("id3", Agent.AgentState.Building, Agent.BuildState.Building, Agent.ConfigState.Enabled));
        agents.add(new Agent("id4", Agent.AgentState.Building, Agent.BuildState.Building, Agent.ConfigState.Enabled));
        agents.add(new Agent("id5", Agent.AgentState.Building, Agent.BuildState.Building, Agent.ConfigState.Enabled));
        agents.add(new Agent("id6", Agent.AgentState.Building, Agent.BuildState.Building, Agent.ConfigState.Enabled));
        agents.add(new Agent("id7", Agent.AgentState.Building, Agent.BuildState.Building, Agent.ConfigState.Enabled));
        agents.add(new Agent("id8", Agent.AgentState.Building, Agent.BuildState.Building, Agent.ConfigState.Enabled));
        agents.add(new Agent("id9", Agent.AgentState.Building, Agent.BuildState.Building, Agent.ConfigState.Enabled));
        agents.add(new Agent("id10", Agent.AgentState.Building, Agent.BuildState.Building, Agent.ConfigState.Enabled));
        agents.add(new Agent("id11", Agent.AgentState.Building, Agent.BuildState.Building, Agent.ConfigState.Enabled));
        when(pluginRequest.listAgents()).thenReturn(agents);
        Map<String, String> properties = new HashMap<>();
        createAgentRequest = new CreateAgentRequest("abc-key", properties, job1, "testing");
        when(pluginRequest.getPluginSettings()).thenReturn(pluginSettings);
        when(agentInstances.matchInstance(anyString(), ArgumentMatchers.<String, String>anyMap(), anyString(), any(PluginSettings.class),
                any(OpenstackClientWrapper.class), anyString(), anyBoolean())).thenReturn(true);
        CreateAgentRequestExecutor executor = new CreateAgentRequestExecutor(createAgentRequest, agentInstances, pluginRequest, openstackClientWrapper, pendingAgents, vaultService);

        // Act
        executor.execute();

        // Assert
        verify(agentInstances, never()).create(any(CreateAgentRequest.class), any(PluginSettings.class), anyString(), anyString());
    }
}