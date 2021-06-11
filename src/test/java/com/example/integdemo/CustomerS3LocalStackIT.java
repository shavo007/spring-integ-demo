package com.example.integdemo;

import com.example.IntegDemoApplication;
import com.example.model.Customer;
import com.example.repo.CustomerRepository;
import com.example.service.GreetingsService;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.containers.localstack.LocalStackContainer;
import org.testcontainers.containers.localstack.LocalStackContainer.Service;
import org.testcontainers.images.ImagePullPolicy;
import org.testcontainers.images.PullPolicy;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;
import de.adesso.junitinsights.annotations.JUnitInsights;
import lombok.extern.java.Log;
import lombok.extern.log4j.Log4j;
import lombok.extern.slf4j.Slf4j;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.Matchers.greaterThanOrEqualTo;
import static org.hamcrest.Matchers.hasSize;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import java.io.IOException;
import java.net.URI;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.LogManager;



@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT, classes = IntegDemoApplication.class)
@Testcontainers
@DisplayNameGeneration(org.junit.jupiter.api.DisplayNameGenerator.ReplaceUnderscores.class)
@JUnitInsights
@ActiveProfiles("test-containers")
@Slf4j
public class CustomerS3LocalStackIT {

	// @TestConfiguration
	// static class S3ClientContextConfiguration {
	// 	@Bean
	// 	@Primary
	// 	public S3Client s3ClientMock() {
	// 		return S3Client.builder()
	// 				.endpointOverride(localstack.getEndpointOverride(LocalStackContainer.Service.S3))
	// 				.credentialsProvider(StaticCredentialsProvider.create(
	// 						AwsBasicCredentials.create(localstack.getAccessKey(), localstack.getSecretKey())))
	// 				.region(Region.of(localstack.getRegion())).build();
	// 	}
	// }

  @Autowired
  private TestRestTemplate testRestTemplate;
  @LocalServerPort
  int randomServerPort;
	@MockBean
	GreetingsService greetingsService;

  @Autowired
  private CustomerRepository repository;
  public static final DockerImageName GREETING_IMAGE =
      DockerImageName.parse("shanelee007/greetings-api:latest");
	public static final DockerImageName LOCAL_STACK_IMAGE = DockerImageName.parse("localstack/localstack:0.12.12");


  static {
    // Postgres JDBC driver uses JUL; disable it to avoid annoying, irrelevant, stderr logs during
    // connection testing
    LogManager.getLogManager().getLogger("").setLevel(Level.OFF);
  }

	// Using Testcontainers’s Localstack module in particular can allow you to remove AWS dependencies
	// from functional tests. Localstack provides a containerized way of mocking AWS components,
	// eliminating any need to connect to your real AWS infrastructure during testing.
	@Container
	private static final LocalStackContainer localstack =
			new LocalStackContainer(LOCAL_STACK_IMAGE).withServices(Service.S3);

  // will be shared between test methods
  @Container
  private static final PostgreSQLContainer<?> POSTGRES_CONTAINER =
      new PostgreSQLContainer<>(DockerImageName.parse("postgres:11")).withDatabaseName("postgres")
          .withUsername("postgres").withPassword("root");

  // dynamically configures the runtime port for postgres defined above

  @DynamicPropertySource
  static void registerPgProperties(DynamicPropertyRegistry registry) {
    log.info("mapped port for postgres is {}",
        String.valueOf(POSTGRES_CONTAINER.getFirstMappedPort()));
    log.info("url for postgres is {}", String.valueOf(POSTGRES_CONTAINER.getJdbcUrl()));

    // registry.add("spring.datasource.url",
    // () -> String.format("jdbc:postgresql://localhost:%d/postgres",
    // POSTGRES_CONTAINER.getFirstMappedPort()));
    registry.add("spring.datasource.url", () -> POSTGRES_CONTAINER.getJdbcUrl());
    registry.add("spring.datasource.username", () -> POSTGRES_CONTAINER.getUsername());
    registry.add("spring.datasource.password", () -> POSTGRES_CONTAINER.getPassword());
    registry.add("aws.url", () -> localstack
				.getEndpointOverride(LocalStackContainer.Service.S3));
  }



  @Test
  public void whenValidInput_thenGetCustomer() throws IOException, Exception {
    Customer bob = new Customer();
    bob.setFirstName("bob");
    bob.setLastName("tmart");
		bob = repository.save(bob);

    final String baseUrl = "http://localhost:" + randomServerPort + "/customers/{id}";
		final Map<String, String> variables = new HashMap<>();
		variables.put("id", String.valueOf(bob.getId()));
    ResponseEntity<Customer> result =
				testRestTemplate.getForEntity(baseUrl, Customer.class, variables);

    assertThat(result.getBody().getFirstName()).isEqualTo("bob");
    assertThat(result.getStatusCodeValue()).isEqualTo(200);
  }

}
