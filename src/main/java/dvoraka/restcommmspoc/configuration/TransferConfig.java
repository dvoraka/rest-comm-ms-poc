package dvoraka.restcommmspoc.configuration;

import dvoraka.restcommmspoc.service.DefaultRestClientTransferService;
import dvoraka.restcommmspoc.service.DefaultRestServerTransferService;
import dvoraka.restcommmspoc.service.RestClientTransferService;
import dvoraka.restcommmspoc.service.RestServerTransferService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
class TransferConfig {
    @Bean
    public RestClientTransferService restClientTransferService() {
        return new DefaultRestClientTransferService();
    }

    @Bean
    public RestServerTransferService restServerTransferService() {
        return new DefaultRestServerTransferService();
    }
}
