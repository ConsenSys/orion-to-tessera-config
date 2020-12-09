package net.consensys.tessera.migration;

import com.quorum.tessera.config.JdbcConfig;

public class JdbcConfigBuilder {

    static JdbcConfigBuilder create() {
        return new JdbcConfigBuilder();
    }

    public JdbcConfig buildDefault() {
        JdbcConfig jdbcConfig = new JdbcConfig();
        jdbcConfig.setUrl("[JDBC URL]");
        jdbcConfig.setUsername("[JDBC user]");
        jdbcConfig.setPassword("[JDBC password]");
        return jdbcConfig;
    }

}
