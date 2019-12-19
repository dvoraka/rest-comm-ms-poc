package dvoraka.restcommmspoc.configuration;

import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

@Configuration
class TransferConfig {

//    @Bean
//    public RestClientTransferService restClientTransferService(RestTemplate restTemplate) {
//        return new DefaultRestClientTransferService(restTemplate);
//    }
//
//    @Bean
//    public RestServerTransferService restServerTransferService() {
//        return new DefaultRestServerTransferService();
//    }

    @Bean
    public RestTemplate restTemplate(RestTemplateBuilder restTemplateBuilder) {
        return restTemplateBuilder.rootUri("http://localhost:8080/")
                .build();
    }
}
