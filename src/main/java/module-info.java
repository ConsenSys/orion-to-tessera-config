module orion.to.tessera.config {
    requires toml4j;
    requires java.sql;
    requires config;
    requires java.validation;
    requires info.picocli;

    exports net.consensys.tessera.migration to info.picocli;
    opens net.consensys.tessera.migration to info.picocli;
}