package testcontainers.postgres;

import org.testcontainers.containers.BindMode;
import org.testcontainers.containers.PostgreSQLContainer;

public class CustomPostgresContainer extends PostgreSQLContainer<CustomPostgresContainer> {

    private static final String IMAGE = "postgres:15";

    public CustomPostgresContainer(){
        super(IMAGE);
        withFileSystemBind("/tmp/tbs_data", "/data/postgres/tablespaces/tbs_data",
                BindMode.READ_WRITE);
        withFileSystemBind("/tmp/tbs_index", "/data/postgres/tablespaces/tbs_index",
                BindMode.READ_WRITE);
        withFileSystemBind("/tmp/tbs_lob", "/data/postgres/tablespaces/tbs_lob",
                BindMode.READ_WRITE);
    }

    @Override
    public void start(){
        super.start();

        // Run your scripts as postgres superuser
        try {
            execInContainer("psql", "-U", "postgres", "-f", "/init-scripts/00.create.tablespace.sql");
            execInContainer("psql", "-U", "postgres", "-f", "/init-scripts/01.create.db.users.langs.sql");
            execInContainer("psql", "-U", "postgres", "-f", "/init-scripts/dict_xsyn.sql");
            execInContainer("psql", "-U", "postgres", "-f", "/init-scripts/pgcrypto.sql");
        } catch (Exception e) {
            throw new RuntimeException("Failed to init DB", e);
        }
    }
}
