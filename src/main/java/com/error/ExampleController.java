package com.error;

import io.micronaut.http.HttpResponse;
import io.micronaut.http.annotation.*;

import java.util.Map;

@Controller("/example")
public class ExampleController {

    @Get
    public HttpResponse<Map<String, String>> index(String a, String b) {
        return HttpResponse.ok(Map.of("a", a, "b", b));
    }
}