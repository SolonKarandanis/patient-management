package testcontainers.postgres;


import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;

@TestConfiguration
public class PostgresDBTestConfiguration {
    private static CustomPostgresContainer postgres;

    private static synchronized CustomPostgresContainer getInstance() {
        if (postgres == null) {
            postgres = new CustomPostgresContainer();
            postgres.start();
            Runtime.getRuntime().addShutdownHook(new Thread(postgres::close));
        }
        return postgres;
    }

    public static final String DEFAULT_PDB_CONTAINER_BEAN_NAME = "postgres";

    // We are overriding the destroy method, because this bean is a singleton across all contexts
    // and we are closing it via runtime shutdown hook after all the tests are done
    @Bean(destroyMethod = "",name = DEFAULT_PDB_CONTAINER_BEAN_NAME)
    public CustomPostgresContainer getCustomPostgresContainer() {
        return getInstance();
    }
}
