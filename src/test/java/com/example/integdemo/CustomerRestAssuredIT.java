package com.example.integdemo;

import com.example.IntegDemoApplication;
import com.example.model.Customer;
import com.example.model.Greeting;
import com.example.repo.CustomerRepository;
import com.example.service.GreetingsService;
import com.example.service.ObjectService;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.context.junit4.SpringRunner;
import de.adesso.junitinsights.annotations.JUnitInsights;
import io.restassured.http.ContentType;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.Matchers.greaterThanOrEqualTo;
import static org.hamcrest.Matchers.hasSize;
import static io.restassured.RestAssured.*;
import static io.restassured.matcher.RestAssuredMatchers.*;
import static org.hamcrest.Matchers.*;

import java.io.IOException;
import java.net.URI;
import javax.annotation.PostConstruct;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doNothing;

@ExtendWith(SpringExtension.class)
@TestInstance(org.junit.jupiter.api.TestInstance.Lifecycle.PER_CLASS)
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT, classes = IntegDemoApplication.class)
@DisplayNameGeneration(org.junit.jupiter.api.DisplayNameGenerator.ReplaceUnderscores.class)
@JUnitInsights
@AutoConfigureTestDatabase(connection = org.springframework.boot.jdbc.EmbeddedDatabaseConnection.H2)
public class CustomerRestAssuredIT {

	@LocalServerPort
	int randomServerPort;

	private String uri;


	@Autowired
	private CustomerRepository repository;
	@MockBean
	private GreetingsService service;
	@MockBean
	private ObjectService objectService;

	@BeforeEach
	public void setup() {
		given(service.sayHi()).willReturn(new Greeting());
		doNothing().when(objectService).putObject(Mockito.any());
	}

	@AfterEach
	public void resetDb() {
		repository.deleteAll();
	}

	@PostConstruct
	public void init() {
		uri = "http://localhost:" + randomServerPort;
	}

	@Test
	public void givenCustomers_whenGetCustomers_thenStatus200() throws Exception {
		createTestCustomer("bob", "tmart");
		createTestCustomer("alex", "ferguson");

		io.restassured.RestAssured
				.given().log().all().when().get(uri + "/customers").then().log().all().assertThat()
				.statusCode(HttpStatus.OK.value()).contentType(ContentType.JSON).body("size()", is(2)).and()
				.body("[0].firstName", is("bob"));
	}

	//showcase mocking calls to s3 aws service
	@Test
	public void givenCustomers_whenGetCustomer_byID_thenStatus200() throws Exception {
		Customer bob = createTestCustomer("bob", "tmart");
		createTestCustomer("alex", "ferguson");

		io.restassured.RestAssured.given().log().all().when().get(uri + "/customers/{id}",bob.getId()).then().log().all()
				.assertThat().statusCode(HttpStatus.OK.value()).contentType(ContentType.JSON)
				.and().body("firstName", is("bob"));
	}

	//

	private Customer createTestCustomer(String name, String last) {
		Customer customer = new Customer(0L, name, last);
		return repository.save(customer);
	}

}
