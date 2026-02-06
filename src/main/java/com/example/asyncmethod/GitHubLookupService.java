package com.example.asyncmethod;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

// this class is marked as a service, making it a candidate for Spring's component scanning
// to detect and add to the application context
@Service
public class GitHubLookupService {

    private static final Logger logger = LoggerFactory.getLogger(GitHubLookupService.class);

    // used to invoke remote REST point (api.github.com/users/
    // the find user method will be used to hit this endpoint and convert the response into
    // an User object
    private final RestTemplate restTemplate;

    // Spring automatically provides a RestTemplateBuilder that customizes the defaults with any
    // auto-configuration bits like MessageConverter
    public GitHubLookupService(RestTemplateBuilder restTemplateBuilder) {
        this.restTemplate = restTemplateBuilder.build();
    }

    // Async is marked here to indicate that it should run on a separate thread.
    // the method's return type is a CompleteableFuture<User> instead of user. A requirement
    // for any asynchronous service. This code uses the completedFuture method to return a
    // completable feature instance that is already complted with the result of the Github Query
    @Async
    public CompletableFuture<User> findUser(String user) throws InterruptedException {
        logger.info("[findUser] user={}", user);

        String url = String.format("https://api.github.com/users/%s", user);

        User results = restTemplate.getForObject(url, User.class);

        // Artificially delay of 1s for demonstration purposes
        Thread.sleep(1000L);
        return CompletableFuture.completedFuture(results);
    }
    // Creating a local instance of the GitHubLookupService does not allow the FindUser
    // method to run asynchronously. It must be created inside a @Configuration class or
    // be picked up by @Component Scan

    // is that why we put the @Service annotation on the GitHubLookupService?
    // Yes, the @Service annotation is used to indicate that the GitHubLookupService is a service component in the Spring application. This allows Spring to automatically detect and manage it as a bean in the application context. By doing so, when you inject this service into other components (like controllers or other services), Spring can manage its lifecycle and ensure that the asynchronous behavior works correctly when you call the findUser method.


}
