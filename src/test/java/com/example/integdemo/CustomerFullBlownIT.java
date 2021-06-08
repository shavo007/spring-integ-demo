package com.example.integdemo;

import com.example.IntegDemoApplication;
import com.example.model.Customer;
import com.example.model.Greeting;
import com.example.repo.CustomerRepository;
import com.example.service.GreetingsService;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import de.adesso.junitinsights.annotations.JUnitInsights;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.Matchers.greaterThanOrEqualTo;
import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import java.io.IOException;
import java.net.URI;
import static org.mockito.BDDMockito.given;



// @RunWith(SpringRunner.class)
@ExtendWith(SpringExtension.class)
@TestInstance(org.junit.jupiter.api.TestInstance.Lifecycle.PER_CLASS)
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT, classes = IntegDemoApplication.class)
@DisplayNameGeneration(org.junit.jupiter.api.DisplayNameGenerator.ReplaceUnderscores.class)
@JUnitInsights
// @EnableAutoConfiguration
// @TestPropertySource(locations = "classpath:application-integrationtest.properties")
public class CustomerFullBlownIT {

	//TODO: the limitation here is that i am running against my local postgres database which will inevitably cause contamination

	@Autowired
	private TestRestTemplate testRestTemplate;
	@LocalServerPort
	int randomServerPort;
	@MockBean
	private GreetingsService service;

	@Autowired
	private CustomerRepository repository;

	@BeforeEach
	public void setup() {
		given(service.sayHi()).willReturn(new Greeting());
	}

	@AfterAll
	public void resetDb() {
		repository.deleteAll();
	}

	@Test
	public void whenValidInput_thenCreateCustomer() throws IOException, Exception {
		Customer bob = new Customer();
		bob.setFirstName("bob");
		bob.setLastName("tmart");
		final String baseUrl = "http://localhost:"+randomServerPort+"/customers/";
    URI uri = new URI(baseUrl);
		ResponseEntity<Customer> result = testRestTemplate.postForEntity(uri, bob, Customer.class);
		// mvc.perform(post("/customers").contentType(MediaType.APPLICATION_JSON)
		// 		.content(JsonUtil.toJson(bob)));

		Iterable<Customer> found = repository.findAll();
		assertThat(found).extracting(Customer::getFirstName).containsOnly("bob");
		assertThat(result.getStatusCodeValue()).isEqualTo(201);
	}

}
