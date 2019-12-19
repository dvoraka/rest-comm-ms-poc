package dvoraka.restcommmspoc;

import dvoraka.restcommmspoc.service.RestClientTransferService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class TransferApp {

    @Autowired
    private RestClientTransferService clientService;

    public static void main(String[] args) {
        SpringApplication.run(TransferApp.class, args);
    }

    @Bean
    public CommandLineRunner runner() {
        return args -> {
            clientService.send("Hello");
            clientService.sendAsync("BBBB");
            clientService.sendAsync("CCCCC");
        };
    }
}
