package com.example;

import java.net.URI;
import java.net.URISyntaxException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Profile;
import org.springframework.web.client.RestTemplate;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.DefaultCredentialsProvider;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.regions.providers.DefaultAwsRegionProviderChain;
import software.amazon.awssdk.services.s3.S3Client;

@SpringBootApplication
public class IntegDemoApplication {

	@Value("${aws.url}")
	private String awsUrl;

	public static void main(String[] args) {
		SpringApplication.run(IntegDemoApplication.class, args);
	}

	@Bean
	public RestTemplate restTemplate(RestTemplateBuilder builder) {
		return builder.build();
	}

	@Bean
	@Profile("!prod")
	public S3Client s3Client() throws URISyntaxException {
		AwsBasicCredentials awsCreds =
				AwsBasicCredentials.create("your_access_key_id", "your_secret_access_key");
		URI endpointOverride = new URI(awsUrl);

	return S3Client.builder().endpointOverride(endpointOverride)
			.region(Region.of("us-east-1")).credentialsProvider(
					StaticCredentialsProvider.create(awsCreds)).build();

	}
}
