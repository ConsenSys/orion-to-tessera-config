package net.consensys.tessera.migration;

import com.quorum.tessera.config.Config;
import com.quorum.tessera.config.EncryptorType;
import com.quorum.tessera.config.util.JaxbUtil;
import org.junit.Test;
import picocli.CommandLine;

import static org.assertj.core.api.Assertions.assertThat;

public class MigrateCommandTest {

    @Test
    public void loadFullConfigSample() {

        String file = getClass().getResource("/fullConfigTest.toml").getFile();
        CommandLine cmd = new CommandLine(MigrateCommand.class);

        CommandLine.ParseResult result = cmd.parseArgs("orionfile=".concat(file));
        assertThat(result).isNotNull();

        cmd.execute("orionfile=".concat(file));
        Config config = cmd.getExecutionResult();
        assertThat(config).isNotNull();
        assertThat(config.getEncryptor().getType()).isEqualTo(EncryptorType.NACL);
        assertThat(config.isBootstrapNode()).isFalse();
        assertThat(config.isDisablePeerDiscovery()).isFalse();
        assertThat(config.isUseWhiteList()).isFalse();

        assertThat(config.getJdbcConfig()).isEqualTo(JdbcConfigBuilder.create().buildDefault());


        JaxbUtil.marshalWithNoValidation(config,System.out);

    }

    @Test
    public void loadMinimalConfigSample() {


        String file = getClass().getResource("/minimal-sample.toml").getFile();
        CommandLine cmd = new CommandLine(MigrateCommand.class);
        CommandLine.ParseResult result = cmd.parseArgs("orionfile=".concat(file));
        assertThat(result).isNotNull();

        cmd.execute("orionfile=".concat(file));
        Config config = cmd.getExecutionResult();
        assertThat(config).isNotNull();
        assertThat(config.getEncryptor().getType()).isEqualTo(EncryptorType.NACL);
        assertThat(config.isBootstrapNode()).isFalse();
        assertThat(config.isDisablePeerDiscovery()).isFalse();
        assertThat(config.isUseWhiteList()).isFalse();

        assertThat(config.getJdbcConfig()).isEqualTo(JdbcConfigBuilder.create().buildDefault());


        JaxbUtil.marshalWithNoValidation(config,System.out);
    }

}
