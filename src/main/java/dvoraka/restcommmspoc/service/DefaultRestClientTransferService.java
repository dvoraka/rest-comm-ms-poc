package dvoraka.restcommmspoc.service;

import dvoraka.restcommmspoc.data.message.TransferMessage;
import dvoraka.restcommmspoc.data.message.TransferResponseMessage;
import dvoraka.restcommmspoc.exception.NetworkException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import static java.util.Objects.requireNonNull;

@Service
public class DefaultRestClientTransferService extends AbstractBaseService implements RestClientTransferService {

    private final RestTemplate restTemplate;


    @Autowired
    public DefaultRestClientTransferService(RestTemplate restTemplate) {
        this.restTemplate = requireNonNull(restTemplate);
    }

    public void send(String data) throws NetworkException {
        TransferMessage request = new TransferMessage(data);
        try {
            ResponseEntity<TransferResponseMessage> response = restTemplate.postForEntity(
                    "/save",
                    request,
                    TransferResponseMessage.class
            );
        } catch (RestClientException e) {
            e.printStackTrace();
            throw new NetworkException();
        }
    }
}
