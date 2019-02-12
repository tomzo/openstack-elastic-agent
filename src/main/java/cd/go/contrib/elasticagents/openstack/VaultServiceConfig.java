package cd.go.contrib.elasticagents.openstack;

import static java.text.MessageFormat.format;
import static org.apache.commons.lang.StringUtils.isBlank;

public class VaultServiceConfig {
    private String vaultAddress;
    private String serverToken;
    private String sslCertFile;

    public VaultServiceConfig(String vaultAddress, String serverToken, String sslCertFile) {
        this.vaultAddress = vaultAddress;
        this.serverToken = serverToken;
        this.sslCertFile = sslCertFile;
    }

    @Override
    public String toString() {
        String token = isBlank(this.serverToken) ? "not-specified" : "****";
        return format("VaultConfig: Address: {0}, SslCertFile: {1}, Token: {2}", this.vaultAddress, this.sslCertFile, token);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        VaultServiceConfig that = (VaultServiceConfig) o;

        if (vaultAddress != null ? !vaultAddress.equals(that.vaultAddress) : that.vaultAddress != null)
            return false;
        if (serverToken != null ? !serverToken.equals(that.serverToken) : that.serverToken != null)
            return false;
        if (sslCertFile != null ? !sslCertFile.equals(that.sslCertFile) : that.sslCertFile != null)
            return false;
        return true;
    }

    @Override
    public int hashCode() {
        return super.hashCode();
    }

    public String getVaultAddress() {
        return vaultAddress;
    }

    public void setVaultAddress(String vaultAddress) {
        this.vaultAddress = vaultAddress;
    }

    public String getServerToken() {
        return serverToken;
    }

    public void setServerToken(String serverToken) {
        this.serverToken = serverToken;
    }

    public String getSslCertFile() {
        return sslCertFile;
    }

    public void setSslCertFile(String sslCertFile) {
        this.sslCertFile = sslCertFile;
    }
}
