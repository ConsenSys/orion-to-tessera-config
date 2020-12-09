package net.consensys.tessera.migration;

import com.moandjiezana.toml.Toml;
import com.quorum.tessera.config.*;
import picocli.CommandLine;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.stream.Collectors;

@CommandLine.Command
public class MigrateCommand implements Callable<Config> {

    @CommandLine.Option(names = {"-f","orionfile"},required = true)
    private Path orionConfigFile;

    @CommandLine.Option(names = {"-o","outputfile"})
    private Path outputFile;

    @CommandLine.Option(names = {"-sv","skipValidation"})
    private boolean skipValidation;

    @CommandLine.Option(names = {"-v","--verbose"})
    private boolean verbose;

    @Override
    public Config call() throws Exception {

        Toml toml = new Toml().read(orionConfigFile.toAbsolutePath().toFile());

        String knownnodesstorage = toml.getString("knownnodesstorage");

        Long nodeport = toml.getLong("nodeport");//p2p
        String nodeurl = toml.getString("nodeurl");//p2p
        List<String> tlsserverchain = toml.getList("tlsserverchain",List.of());

        String tlsservercert = toml.getString("tlsservercert");

        String clienturl = toml.getString("clienturl");
        Integer clientport = toml.contains("clientport") ? Math.toIntExact(toml.getLong("clientport")) : null;

        String tlsclientkey = toml.getString("tlsclientkey");
        String tlsclienttrust = toml.getString("tlsclienttrust");
        String clientconnectiontls = toml.getString("clientconnectiontls");
        String clientconnectiontlsservercert = toml.getString("clientconnectiontlsservercert");
        List<String> clientconnectiontlsserverchain = toml.getList("clientconnectiontlsserverchain");
        String clientconnectiontlsserverkey = toml.getString("clientconnectiontlsserverkey");
        String clientconnectionTlsServerTrust = toml.getString("clientconnectionTlsServerTrust");
        String tlsknownclients = toml.getString("tlsknownclients");
        String tlsknownservers = toml.getString("tlsknownservers");

        String tlsclientcert = toml.getString("tlsclientcert");


        String serverAuthTls = toml.getString("tls");
        String socketfile = toml.getString("socket","[No IPC socket file specfied]");

        String tlsserverkey = toml.getString("tlsserverkey");
        String tlsservertrust = toml.getString("tlsservertrust");

        List<String> privateKeys = toml.getList("privatekeys",List.of());
        String passwordsFile = toml.getString("passwords");
        List<String> publicKeys = toml.getList("publickeys",List.of());

        List<String> otherNodes = toml.getList("othernodes",List.of());

        List<String> alwaysSendTo = toml.getList("alwayssendto",List.of());

        Config config = new Config();
        config.setBootstrapNode(false);
        config.setUseWhiteList(false);
        config.setRecoveryMode(false);
        config.setEncryptor(new EncryptorConfig() {
            {
                setType(EncryptorType.NACL);
            }
        });
        config.setPeers(otherNodes.stream()
                .map(Peer::new)
                .collect(Collectors.toList()));

        config.setJdbcConfig(JdbcConfigBuilder.create().buildDefault());

        ServerConfig q2tServer = ServerConfigBuilder.create()
                .withAppType(AppType.Q2T)
                .withSocketFile(socketfile)
           //     .withServerPort(clientport)
           //     .withServerAddress(clienturl)
//                .withSslConfig(SslConfigBuilder.create()
//                        .withClientTrustMode(clientconnectiontls)
//                        .withClientKeyStore(tlsclientkey)
//                        .build())
                .build();

        ServerConfig p2pServer = ServerConfigBuilder.create()
                .withAppType(AppType.P2P)
                .withServerAddress(nodeurl)
                .withServerPort(Math.toIntExact(nodeport))
                .withSslConfig(SslConfigBuilder.create()
                        .withSslAuthenticationMode(serverAuthTls)
                        .withServerKeyStore(tlsserverkey)
                        .withTlsServerTrust(tlsservertrust)
                        .withKnownClientFilePath(tlsknownclients)
                        .withKnownServersFilePath(tlsknownservers)
                        .withClientTrustMode(tlsclienttrust)
                        .withClientKeyStore(tlsclientkey)
                        .withServerTlsCertificatePath(tlsservercert)
                        .withClientTlsCertificatePath(tlsclientcert)
                        .withClientTrustMode(clientconnectionTlsServerTrust)
                        .build())
                .build();

        config.setServerConfigs(List.of(q2tServer, p2pServer));

        List<String> encodeKeyValues = alwaysSendTo.stream()
                .map(Paths::get)
                .map(p -> {
                    try {
                        return Files.lines(p).findFirst()
                                .orElse(String.format("[Error: No lines found in file %s",p.toAbsolutePath()));
                    } catch (IOException e) {
                        return String.format("[Error: Unable to read key file %s]", p.toAbsolutePath());
                    }
                }).collect(Collectors.toList());

        config.getAlwaysSendTo().addAll(encodeKeyValues);

        config.setKeys(
                KeyConfigBuilder.create()
                    .withPrivateKeys(privateKeys)
                    .withPublicKeys(publicKeys)
                    .withPasswordsFile(passwordsFile).build()
        );

        return config;
    }


}
