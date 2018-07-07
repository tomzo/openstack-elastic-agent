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

package cd.go.contrib.elasticagents.openstack.utils;

import cd.go.contrib.elasticagents.openstack.Constants;
import cd.go.contrib.elasticagents.openstack.PluginSettings;
import cd.go.contrib.elasticagents.openstack.VaultServiceConfig;
import cd.go.contrib.elasticagents.openstack.executors.GetViewRequestExecutor;
import com.google.common.base.Charsets;
import com.google.common.io.ByteStreams;
import com.google.common.io.CharStreams;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.apache.commons.lang.StringUtils;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.util.Map;
import java.util.Properties;
import java.util.Random;

public class Util {

    public static final Gson GSON = new GsonBuilder().excludeFieldsWithoutExposeAnnotation().create();

    public static String readResource(String resourceFile) {
        try (InputStreamReader reader = new InputStreamReader(GetViewRequestExecutor.class.getResourceAsStream(resourceFile), Charsets.UTF_8)) {
            return CharStreams.toString(reader);
        } catch (IOException e) {
            throw new RuntimeException("Could not find resource " + resourceFile, e);
        }
    }

    public static byte[] readResourceBytes(String resourceFile) {
        try (InputStream in = GetViewRequestExecutor.class.getResourceAsStream(resourceFile)) {
            return ByteStreams.toByteArray(in);
        } catch (IOException e) {
            throw new RuntimeException("Could not find resource " + resourceFile, e);
        }
    }

    public static String pluginId() {
        String s = readResource("/plugin.properties");
        try {
            Properties properties = new Properties();
            properties.load(new StringReader(s));
            return (String) properties.get("pluginId");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * E.g. if agentMinTTL 10 and agentMaxTTL 20 is given, a random value between 10 and 20 should be returned.
     *
     * @param agentMinTTL positive int
     * @param agentMaxTTL positive int
     * @return Random value between agentMinTTL and agentMaxTTL or at least agentMinTTL minutes.
     */
    public static int calculateTTL(int agentMinTTL, int agentMaxTTL) {
        if (agentMaxTTL < agentMinTTL)
            return agentMinTTL;
        Random rand = new Random();
        int result;
        int random = agentMaxTTL - agentMinTTL;
        result = agentMinTTL + rand.nextInt(random + 1);
        return result;
    }

    public static String getVaultPolicy(Map<String, String> profileProperties, PluginSettings settings) {
        return StringUtils.isNotBlank(profileProperties.get(Constants.VAULT_POLICY)) ? profileProperties.get(Constants.VAULT_POLICY) : settings.getVaultPolicy();
    }

    public static String getVaultTtl(Map<String, String> profileProperties, PluginSettings settings) {
        String vaultTtl = StringUtils.isNotBlank(profileProperties.get(Constants.VAULT_TTL)) ? profileProperties.get(Constants.VAULT_TTL) : settings.getVaultTtl();
        return vaultTtl;
    }

    public static VaultServiceConfig getVaultConfig(PluginSettings pluginSettings) {
        return new VaultServiceConfig(pluginSettings.getVaultAddr(), pluginSettings.getVaultServerToken(), pluginSettings.getVaultSslCert());
    }
}
