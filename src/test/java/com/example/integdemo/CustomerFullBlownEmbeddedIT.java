package com.example.integdemo;

import com.example.IntegDemoApplication;
import com.example.model.Customer;
import com.example.model.Greeting;
import com.example.repo.CustomerRepository;
import com.example.service.GreetingsService;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import de.adesso.junitinsights.annotations.JUnitInsights;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
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
import static org.mockito.BDDMockito.given;


// @ExtendWith(SpringExtension.class)
@TestInstance(org.junit.jupiter.api.TestInstance.Lifecycle.PER_CLASS)
@SpringBootTest(webEnvironment = WebEnvironment.MOCK, classes = IntegDemoApplication.class)
@AutoConfigureMockMvc // In this test, the full Spring application context is started but without
											// the server
@ActiveProfiles(profiles = "test")
@AutoConfigureTestDatabase(connection = org.springframework.boot.jdbc.EmbeddedDatabaseConnection.H2)
@JUnitInsights
public class CustomerFullBlownEmbeddedIT {

	@Autowired
	private MockMvc mvc;

	@Autowired
	private CustomerRepository repository;
	@MockBean
	private GreetingsService service;

	@BeforeEach
	public void setup() {
		given(service.sayHi()).willReturn(new Greeting());
	}

	@AfterEach
	public void resetDb() {
		repository.deleteAll();
	}

	@Test
	public void whenValidInput_thenCreateCustomer() throws IOException, Exception {
		Customer bob = new Customer();
		bob.setFirstName("bob");
		bob.setLastName("tmart");
		mvc.perform(post("/customers").contentType(MediaType.APPLICATION_JSON)
				.content(JsonUtil.toJson(bob)));

		Iterable<Customer> found = repository.findAll();
		assertThat(found).extracting(Customer::getFirstName).containsOnly("bob");
	}

	@Test
	public void givenCustomers_whenGetCustomers_thenStatus200() throws Exception {
		createTestCustomer("bob", "tmart");
		createTestCustomer("alex", "ferguson");

        mvc.perform(get("/customers").contentType(MediaType.APPLICATION_JSON))
          .andDo(print())
          .andExpect(status().isOk())
          .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
          .andExpect(jsonPath("$", hasSize(greaterThanOrEqualTo(2))))
          .andExpect(jsonPath("$[0].firstName", is("bob")))
          .andExpect(jsonPath("$[1].firstName", is("alex")));
	}

	//

	private void createTestCustomer(String name, String last) {
		Customer customer = new Customer(0L,name,last);
		repository.save(customer);
	}

}
