package com.cosmocats.cosmomarket;

import org.junit.jupiter.api.extension.RegisterExtension;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.GenericContainer;
import static java.lang.String.format;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@DirtiesContext
public class AbstractIT {

    private static final int POSTGRES_PORT = 5432;

    static final GenericContainer POSTGRES_CONTAINER = new GenericContainer("postgres:latest")
            .withEnv("POSTGRES_PASSWORD", "secret")
            .withEnv("POSTGRES_USER", "myuser")
            .withEnv("POSTGRES_DB", "galactic_cats")
            .withExposedPorts(POSTGRES_PORT);

    static {
        POSTGRES_CONTAINER.start();
    }

    @DynamicPropertySource
    static void setupTestContainerProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", () -> format("jdbc:postgresql://%s:%d/galactic_cats",
                POSTGRES_CONTAINER.getHost(), POSTGRES_CONTAINER.getMappedPort(POSTGRES_PORT)));

        registry.add("spring.datasource.username", () -> "myuser");
        registry.add("spring.datasource.password", () -> "secret");
    }
}
