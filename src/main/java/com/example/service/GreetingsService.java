package com.example.service;

import com.example.model.Greeting;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class GreetingsService {
	@Value("${greetings.url}")
	private String greetingsUrl;

	private final RestTemplate restTemplate;

	public Greeting sayHi() {
		log.info("url for greetings is {}", greetingsUrl);
		return restTemplate.getForEntity(greetingsUrl + "/greetings/1", Greeting.class).getBody();
	}

}
