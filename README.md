# Spring integration tests

> Integration testing is a healthy part of the [test pyramid](https://martinfowler.com/articles/practical-test-pyramid.html)

## TODO

add in diagrams
showcase benefits of integ tests

## Definition of an integration test

 An integration test can be any of the following:

- a test that covers multiple “units”. It tests the interaction between two or more clusters of cohesive classes.
- a test that covers multiple layers. This is actually a specialization of the first case and might cover the interaction between a business service and the persistence layer, for instance.
- a test that covers the whole path through the application. In these tests, we send a request to the application and check that it responds correctly and has changed the database state according to our expectations.

## Test showcases

- showcase full blown integ test with db (bootstraps entire web container) `mvn test -Dtest=CustomerFullBlownIT`
- showcase full blown integ test with embedded db (bootstraps entire web container) `mvn test -Dtest=CustomerFullBlownEmbeddedIT`
- showcase full blown integ test with testcontainer db (bootstraps entire web container) `mvn test -Dtest=CustomerFullBlownTestConIT`
- showcase integ test with only web layer (not start http server) `mvn test -Dtest=CustomerControllerIT`
- showcase integ test with test containers
- showcase generic container to test out integration with 3rd party API
- showcase running as github action with docker containers
- mock dao layer with @mockbean
- showcase using restAssured
- showcase exception handling

## Mvn failsafe plugin

Run all integration tests

```bash
mvn verify
```

## Considerations

- `Do not run unit and integ tests together` (use diff src sets or use maven failsafe plugin)
- Caution when Customizing the Application Context : Each customization of the application context is one more thing that makes it different from the "real" application context that is started up in a production setting. So, in order to make our tests as close to production as we can, we should only customize what's really necessary to get the tests running!
- The benefit of Testcontainers is that the database always starts in a known state, without any contamination between test runs or on developers' local machines.
- If our PostgreSQL container is going to listen to a random port every time, then we should somehow set and change the `spring.datasource.url` configuration property dynamically. Solution is to use Spring `@DynamicPropertySource`

## Start the app

```bash
docker run --name postgres-demo -e POSTGRES_PASSWORD=root -p 5432:5432 -d postgres:11
mvn spring-boot:run
```

```bash
docker exec -it postgres-demo bash
psql -d postgres -U postgres
\l #list databases
\dt #list tables
\dn #list schemas
\s #command history

SELECT * from customer;
CREATE DATABASE mytestdatabase; #create test database
```

## Resources

- [test slices](https://docs.spring.io/spring-boot/docs/current/reference/html/test-auto-configuration.html)
- [test insights](https://github.com/adessoAG/junit-insights)
- [dynamically set config](https://www.baeldung.com/spring-dynamicpropertysource)
- [test containers database](https://www.testcontainers.org/modules/databases/)
- [maven failsafe plugin](https://www.baeldung.com/maven-integration-test#failsafe)
