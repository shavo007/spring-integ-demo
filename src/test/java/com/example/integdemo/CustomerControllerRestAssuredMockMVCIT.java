package com.example.integdemo;

import org.junit.jupiter.api.BeforeEach;
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
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.web.client.RestTemplate;
import de.adesso.junitinsights.annotations.JUnitInsights;
import io.restassured.http.ContentType;
import io.restassured.module.mockmvc.RestAssuredMockMvc;
import static io.restassured.module.mockmvc.RestAssuredMockMvc.given;
import com.example.controller.CustomerController;
import com.example.model.Customer;
import com.example.service.CustomerService;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.Matchers.hasSize;
import static org.assertj.core.api.Assertions.assertThat;
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
import static com.example.model.exception.CustomerExceptionHandler.ErrorResult;

@ExtendWith(SpringExtension.class)
@WebMvcTest(CustomerController.class) // A mock MVC testing slice without the rest of the app
@JUnitInsights
public class CustomerControllerRestAssuredMockMVCIT {

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

	@BeforeEach
	void setUp() {
		RestAssuredMockMvc.mockMvc(mvc);
	}

	@Test
	public void whenNullValue_thenReturns400AndErrorResult_RA() throws Exception {
		Customer bob = Customer.builder().firstName(null).lastName("tmart").build();
		String actualResponseBody = RestAssuredMockMvc.given().log().all()
				.body(JsonUtil.toJsonString(bob)).contentType(ContentType.JSON).when().post("/customers")
				.then().log().all().assertThat().statusCode(HttpStatus.BAD_REQUEST.value())
				.contentType(ContentType.JSON).and().extract().asString();
		ErrorResult expectedErrorResponse = new ErrorResult("firstName", "must not be null");
		String expectedResponseBody = JsonUtil.toJsonString(expectedErrorResponse).toString();
		assertThat(actualResponseBody).isEqualToIgnoringWhitespace(expectedResponseBody);
	}


	@Test
	public void whenNullValue_thenReturns400AndErrorResult_Mock() throws Exception {
		Customer alex = new Customer(0L, null, "james");

		MvcResult mvcResult = mvc.perform(
				post("/customers").contentType(MediaType.APPLICATION_JSON).content(JsonUtil.toJson(alex)))
				.andExpect(status().isBadRequest()).andReturn();

		ErrorResult expectedErrorResponse = new ErrorResult("firstName", "must not be null");
		String actualResponseBody = mvcResult.getResponse().getContentAsString();
		String expectedResponseBody = JsonUtil.toJsonString(expectedErrorResponse).toString();
		assertThat(actualResponseBody).isEqualToIgnoringWhitespace(expectedResponseBody);
	}

}
