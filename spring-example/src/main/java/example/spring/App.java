package example.spring;

import org.springframework.beans.BeansException;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.reactive.function.client.WebClient;

@SpringBootApplication
public class App {
    public static void main(String[] args) {
        ConfigurableApplicationContext run = SpringApplication.run(App.class, args);
        try {
            WebClient.Builder builder = run.getBean(WebClient.Builder.class);

            Integer port = run.getEnvironment().getProperty("local.server.port", Integer.class);

            WebClient webClient = builder
                    .baseUrl("http://localhost:" + port)
                    .build();

            String s = webClient.get().uri("/hello").retrieve().bodyToMono(String.class).blockOptional().orElseThrow();
            System.out.println(s);
        } catch (BeansException e) {
            e.printStackTrace();
        }

        run.close();
        System.out.println("closed");
    }

    @RestController
    public static class Ctrl {
        @GetMapping("/hello")
        String hello() {
            return "world";
        }
    }
}
