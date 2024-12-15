package com.error;

import io.micronaut.http.HttpRequest;
import io.micronaut.http.HttpStatus;
import io.micronaut.http.MediaType;
import io.micronaut.http.client.BlockingHttpClient;
import io.micronaut.http.client.HttpClient;
import io.micronaut.http.client.annotation.Client;
import io.micronaut.http.client.exceptions.HttpClientResponseException;
import io.micronaut.runtime.EmbeddedApplication;
import io.micronaut.test.extensions.junit5.annotation.MicronautTest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Assertions;

import jakarta.inject.Inject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Optional;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.jupiter.api.Assertions.assertThrows;

@MicronautTest
class ExampleTest {

    private static final Logger log = LoggerFactory.getLogger(ExampleTest.class);
    @Inject
    EmbeddedApplication<?> application;

    @Client("/")
    @Inject
    HttpClient httpClient;

    @Test
    void testItWorks() {
        Assertions.assertTrue(application.isRunning());
    }

    @Test
    void testHttpErrorsAreNotCached() {
        BlockingHttpClient client = httpClient.toBlocking();
        HttpClientResponseException ex = assertThrows(HttpClientResponseException.class, () ->
            client.exchange(HttpRequest.GET("/example")
                .accept("text/html,application/xhtml+xml,application/xml;q=0.9,image/avif,image/webp,image/apng,*/*;q=0.8,application/signed-exchange;v=b3;q=0.7")));
        assertEquals(HttpStatus.BAD_REQUEST, ex.getStatus());
        assertTrue(ex.getResponse().getContentType().isPresent());
        assertEquals(MediaType.TEXT_HTML, ex.getResponse().getContentType().get().toString());
        Optional<String> htmlOptional = ex.getResponse().getBody(String.class);
        assertTrue(htmlOptional.isPresent());
        String html = htmlOptional.get();

        log.info("Response 1: {}", html);

        assertTrue(html.contains("<!doctype html>"));
        assertTrue(html.contains("Required argument [String a] not specified"));

        HttpClientResponseException ex2 = assertThrows(HttpClientResponseException.class, () ->
            client.exchange(HttpRequest.GET("/example?a=a")
                .accept("text/html,application/xhtml+xml,application/xml;q=0.9,image/avif,image/webp,image/apng,*/*;q=0.8,application/signed-exchange;v=b3;q=0.7")));

        Optional<String> htmlOptional2 = ex.getResponse().getBody(String.class);
        assertTrue(htmlOptional2.isPresent());
        String html2 = htmlOptional2.get();
        log.info("Response 2: {}", html2);
        assertTrue(html2.contains("Required argument [String b] not specified"));
    }

}
