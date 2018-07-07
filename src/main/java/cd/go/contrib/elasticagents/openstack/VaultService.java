package cd.go.contrib.elasticagents.openstack;

import com.bettercloud.vault.SslConfig;
import com.bettercloud.vault.Vault;
import com.bettercloud.vault.VaultConfig;
import com.bettercloud.vault.VaultException;
import com.bettercloud.vault.api.Auth;
import com.bettercloud.vault.response.AuthResponse;
import com.thoughtworks.go.plugin.api.logging.Logger;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class VaultService {

    public static final Logger LOG = Logger.getLoggerFor(VaultService.class);

    private Vault vault;
    private VaultServiceConfig connectedConfig;

    public void connect(VaultServiceConfig userConfig) throws VaultException {
        final SslConfig ssl = new SslConfig()
                .pemFile(new File(userConfig.getSslCertFile()));
        final VaultConfig config =
                new VaultConfig()
                        .address(userConfig.getVaultAddress())
                        .token(userConfig.getServerToken())
                        .sslConfig(ssl.build())
                        .build();
        vault = new Vault(config);
        connectedConfig = userConfig;
    }

    public void renewToken() {
        try {
            vault.auth().renewSelf();
        } catch (VaultException e) {
            LOG.error("Failed to renew vault token", e);
        }
    }

    public String createAgentToken(VaultServiceConfig userConfig, String vaultPolicy, String vaultTtl) {
        try {
            if(this.connectedConfig == null || !this.connectedConfig.equals(userConfig)) {
                LOG.info("Vault configuration has changed, re-connecting");
                connect(userConfig);
            }

            List policies = Arrays.asList(vaultPolicy.split(","));
            AuthResponse token =
                    vault.auth().createToken(new Auth.TokenRequest()
                    .polices(policies)
                    .ttl(vaultTtl));
            return token.getAuthClientToken();
        } catch (VaultException e) {
            LOG.error("Failed to create agent vault token", e);
            throw new RuntimeException("Failed to create agent token", e);
        }
    }
}
