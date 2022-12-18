package com.example.example.micronaut;

import io.micronaut.context.ApplicationContext;
import io.micronaut.http.annotation.Get;
import io.micronaut.http.client.HttpClient;
import io.micronaut.runtime.EmbeddedApplication;
import io.micronaut.runtime.Micronaut;
import io.micronaut.runtime.server.EmbeddedServer;
import reactor.core.publisher.Mono;

public class Application {
    public static void main(String[] args) {
        ApplicationContext run = Micronaut.run(Application.class, args);

        try {
            EmbeddedServer server = run.getBean(EmbeddedServer.class);

            HttpClient httpClient = HttpClient.create(server.getURL());

            Mono<String> from = Mono.from(httpClient.retrieve("/hello"));
            String s = from.blockOptional().orElseThrow();
            System.out.println(s);
        } catch (Exception e) {
            e.printStackTrace();
        }

        run.stop();
        run.close();
        System.out.println("closed");
    }

    @io.micronaut.http.annotation.Controller
    public static class Controller {
        @Get("/hello")
        String get() {
            return "world";
        }
    }
}
