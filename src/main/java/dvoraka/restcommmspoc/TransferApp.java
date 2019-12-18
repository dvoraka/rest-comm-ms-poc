package dvoraka.restcommmspoc;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.SpringBootConfiguration;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.Bean;

@SpringBootConfiguration
@EnableAutoConfiguration
public class TransferApp {
    public static void main(String[] args) {
        SpringApplication.run(TransferApp.class, args);
    }

    @Bean
    public CommandLineRunner runner() {
        return args -> {
            System.out.println("App");
        };
    }
}
