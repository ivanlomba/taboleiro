package taboleiro;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@EnableAutoConfiguration
@SpringBootApplication
public class TaboleiroApplication {

    public static void main(String[] args) {
        SpringApplication.run(TaboleiroApplication.class, args);
    }
}
