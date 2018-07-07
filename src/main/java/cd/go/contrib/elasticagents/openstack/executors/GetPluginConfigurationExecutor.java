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

package cd.go.contrib.elasticagents.openstack.executors;

import cd.go.contrib.elasticagents.openstack.RequestExecutor;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.thoughtworks.go.plugin.api.response.DefaultGoPluginApiResponse;
import com.thoughtworks.go.plugin.api.response.GoPluginApiResponse;

import java.util.HashMap;
import java.util.Map;

public class GetPluginConfigurationExecutor implements RequestExecutor {
    public static final Gson GSON = new GsonBuilder().excludeFieldsWithoutExposeAnnotation().create();

    public static final Field GO_SERVER_URL = new NonBlankField("go_server_url", "Go Server URL", null, true, false, "0");
    public static final Field AUTOREGISTER_TIMEOUT = new PositiveNumberField("auto_register_timeout", "Agent auto-register Timeout (in minutes)", "10", true,
            false, "1");
    public static final Field AGENT_TTL_MAX = new PositiveNumberField("agent_ttl_max", "Agent TTL maximum (in minutes)", "0", true,
            false, "2");
    public static final Field OPENSTACK_ENDPOINT = new NonBlankField("openstack_endpoint", "OpenStack Endpoint", null, true, false, "3");
    public static final Field OPENSTACK_KEYSTONE_VERSION = new NonBlankField("openstack_keystone_version", "OpenStack Keystone Version", null, true, false, "4");
    public static final Field OPENSTACK_DOMAIN = new NonBlankField("openstack_domain", "OpenStack Domain", null, true, false, "5");
    public static final Field OPENSTACK_TENANT = new NonBlankField("openstack_tenant", "OpenStack Tenant", null, true, false, "6");
    public static final Field OPENSTACK_USER = new NonBlankField("openstack_user", "OpenStack User", null, true, false, "7");
    public static final Field OPENSTACK_PASSWORD = new NonBlankField("openstack_password", "OpenStack Password", null, true, true, "8");
    public static final Field OPENSTACK_VM_PREFIX = new NonBlankField("openstack_vm_prefix", "OpenStack VM Prefix", null, true, false, "9");
    public static final Field OPENSTACK_IMAGE = new NonBlankField("openstack_image", "OpenStack Image", null, true, false, "10");
    public static final Field OPENSTACK_IMAGE_CACHE_TTL = new PositiveNumberField("openstack_image_cache_ttl", "OpenStack Image Cache TTL (in minutes)", "30", true, false, "11");
    public static final Field USE_PREVIOUS_OPENSTACK_IMAGE = new NonBlankField("use_previous_openstack_image", "Allow Use of Previous Openstack Image", "false", true, false, "12");
    public static final Field OPENSTACK_FLAVOR = new NonBlankField("openstack_flavor", "OpenStack Flavor", null, true, false, "13");
    public static final Field OPENSTACK_NETWORK = new NonBlankField("openstack_network", "OpenStack Network", null, true, false, "14");
    public static final Field DEFAULT_MIN_INSTANCE_LIMIT = new PositiveNumberField("default_min_instance_limit", "Default Minimum Instance Limit", "1", true,
            false, "15");
    public static final Field DEFAULT_MAX_INSTANCE_LIMIT = new PositiveNumberField("default_max_instance_limit", "Default Max Instance Limit", "10", true,
            false, "16");
    public static final Field OPENSTACK_USERDATA = new Field("openstack_userdata", "OpenStack Userdata", null, false, false, "17");
    public static final Field SSL_VERIFICATION_DISABLED = new NonBlankField("ssl_verification_disabled", "Disable SSL verification", "false", true, false, "18");

    public static final Field VAULT_ADDR = new Field("vault_addr", "Vault address", null, false, false, "19");
    public static final Field VAULT_SSL_CERT = new Field("vault_ssl_cert", "Vault SSL certificate path", null, false, false, "20");
    public static final Field VAULT_SERVER_TOKEN = new Field("vault_server_token", "Vault Orphan token", null, false, false, "21");
    public static final Field VAULT_POLICY = new Field("vault_policy", "Vault policy", null, false, false, "22");
    public static final Field VAULT_TTL = new Field("vault_ttl", "Vault TTL", null, false, false, "23");

    //public static final Field AGENT_RESOURCES = new Field("resources", "Agent Resources", null, false, false, "11");
    //public static final Field AGENT_ENVIRONMENTS = new Field("environments", "Environments", null, false, false, "12");

    public static final Map<String, Field> FIELDS = new HashMap<>();

    static {
        FIELDS.put(GO_SERVER_URL.key(), GO_SERVER_URL);
        FIELDS.put(AUTOREGISTER_TIMEOUT.key(), AUTOREGISTER_TIMEOUT);
        FIELDS.put(AGENT_TTL_MAX.key(), AGENT_TTL_MAX);
        FIELDS.put(DEFAULT_MIN_INSTANCE_LIMIT.key(), DEFAULT_MIN_INSTANCE_LIMIT);
        FIELDS.put(DEFAULT_MAX_INSTANCE_LIMIT.key(), DEFAULT_MAX_INSTANCE_LIMIT);
        FIELDS.put(OPENSTACK_ENDPOINT.key(), OPENSTACK_ENDPOINT);
        FIELDS.put(OPENSTACK_KEYSTONE_VERSION.key(), OPENSTACK_KEYSTONE_VERSION);
        FIELDS.put(OPENSTACK_DOMAIN.key(), OPENSTACK_DOMAIN);
        FIELDS.put(OPENSTACK_TENANT.key(), OPENSTACK_TENANT);
        FIELDS.put(OPENSTACK_USER.key(), OPENSTACK_USER);
        FIELDS.put(OPENSTACK_PASSWORD.key(), OPENSTACK_PASSWORD);
        FIELDS.put(OPENSTACK_VM_PREFIX.key(), OPENSTACK_VM_PREFIX);
        FIELDS.put(OPENSTACK_IMAGE.key(), OPENSTACK_IMAGE);
        FIELDS.put(OPENSTACK_IMAGE_CACHE_TTL.key(), OPENSTACK_IMAGE_CACHE_TTL);
        FIELDS.put(USE_PREVIOUS_OPENSTACK_IMAGE.key(), USE_PREVIOUS_OPENSTACK_IMAGE);
        FIELDS.put(OPENSTACK_FLAVOR.key(), OPENSTACK_FLAVOR);
        FIELDS.put(OPENSTACK_NETWORK.key(), OPENSTACK_NETWORK);
        FIELDS.put(OPENSTACK_USERDATA.key(), OPENSTACK_USERDATA);
        FIELDS.put(SSL_VERIFICATION_DISABLED.key(), SSL_VERIFICATION_DISABLED);
        FIELDS.put(VAULT_ADDR.key(), VAULT_ADDR);
        FIELDS.put(VAULT_SSL_CERT.key(), VAULT_SSL_CERT);
        FIELDS.put(VAULT_SERVER_TOKEN.key(), VAULT_SERVER_TOKEN);
        FIELDS.put(VAULT_POLICY.key(), VAULT_POLICY);
        FIELDS.put(VAULT_TTL.key(), VAULT_TTL);
        // agent spec
        //FIELDS.put(AGENT_RESOURCES.key(), AGENT_RESOURCES);
        //FIELDS.put(AGENT_ENVIRONMENTS.key(), AGENT_ENVIRONMENTS);
    }

    public GoPluginApiResponse execute() {
        return new DefaultGoPluginApiResponse(200, GSON.toJson(FIELDS));
    }
}
