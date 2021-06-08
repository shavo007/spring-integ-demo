package com.example.integdemo;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.mockito.internal.verification.VerificationModeFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.client.RestTemplate;
import de.adesso.junitinsights.annotations.JUnitInsights;
import com.example.controller.CustomerController;
import com.example.model.Customer;
import com.example.service.CustomerService;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.Matchers.hasSize;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import java.util.Arrays;
import java.util.List;
import org.springframework.boot.test.context.TestConfiguration;

@ExtendWith(SpringExtension.class)
@WebMvcTest(CustomerController.class) // A mock MVC testing slice without the rest of the app
@JUnitInsights
public class CustomerControllerIT {

	// By setting the controllers parameter to CustomerController.class in the example above, we're
	// telling Spring Boot
	// to restrict the application context created for this test to the given controller bean and some
	// framework
	// beans needed for Spring Web MVC. All other beans we might need have to be included separately
	// or mocked away with @MockBean.

	// If we leave away the controllers parameter, Spring Boot will include all controllers in the
	// application context. Thus, we need to include or mock away all beans any controller depends on.
	// This makes for a much more complex test setup with more dependencies, but saves runtime since
	// all controller tests will re-use the same application context.

	@Autowired
	private MockMvc mvc;

	@MockBean
	private CustomerService service;
	@MockBean
	private RestTemplate restTemplate;

	// @TestConfiguration
	// static class RestTemplateContextConfiguration {
	// 	@Bean
	// 	public RestTemplate restTemplate() {
	// 	return new RestTemplate();
	// 	}
	// }

	@Test
	public void whenPostCustomer_thenCreateCustomer() throws Exception {
	Customer alex = new Customer(0L, "alex", "james");
	given(service.save(Mockito.any())).willReturn(alex);

	mvc.perform(post("/customers").contentType(MediaType.APPLICATION_JSON)
	.content(JsonUtil.toJson(alex))).andExpect(status().isCreated())
	.andExpect(jsonPath("$.firstName", is("alex")));
	verify(service, VerificationModeFactory.times(1)).save(Mockito.any());
	reset(service);
	}

	@Test
	public void givenEmployees_whenGetEmployees_thenReturnJsonArray() throws Exception {
	Customer alex = new Customer(0L, "alex", "james");
	Customer john = new Customer(0L, "john", "smith");
	Customer bob = new Customer(0L, "bob", "tmart");

	List<Customer> allCustomers = Arrays.asList(alex, john, bob);

	given(service.getAllCustomers()).willReturn(allCustomers);

	mvc.perform(get("/customers").contentType(MediaType.APPLICATION_JSON))
	.andExpect(status().isOk()).andExpect(jsonPath("$", hasSize(3)))
	.andExpect(jsonPath("$[0].firstName", is(alex.getFirstName())))
	.andExpect(jsonPath("$[1].firstName", is(john.getFirstName())))
	.andExpect(jsonPath("$[2].firstName", is(bob.getFirstName())));
	verify(service, VerificationModeFactory.times(1)).getAllCustomers();
	reset(service);
	}

}
