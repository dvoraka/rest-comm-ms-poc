package dvoraka.restcommmspoc;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.SpringBootConfiguration;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;

@SpringBootConfiguration
@EnableAutoConfiguration
public class TransferApp {
    public static void main(String[] args) {
        SpringApplication.run(TransferApp.class, args);
    }
}
