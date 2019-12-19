package dvoraka.restcommmspoc.service;

import dvoraka.restcommmspoc.data.message.TransferMessage;
import dvoraka.restcommmspoc.data.message.TransferResponseMessage;
import dvoraka.restcommmspoc.exception.NetworkException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import javax.annotation.PostConstruct;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import static java.util.Objects.requireNonNull;

@Slf4j
@Service
public class DefaultRestClientTransferService extends AbstractBaseService implements RestClientTransferService {

    private final RestTemplate restTemplate;

    private final BlockingQueue<TransferMessage> waitingMessages;
    private final ExecutorService asyncService;

    private volatile boolean running;


    @Autowired
    public DefaultRestClientTransferService(RestTemplate restTemplate) {
        this.restTemplate = requireNonNull(restTemplate);
        waitingMessages = new ArrayBlockingQueue<>(10);
        asyncService = Executors.newSingleThreadExecutor();
    }

    @PostConstruct
    public void start() {
        running = true;
        asyncService.execute(this::asyncLoop);
    }

    public void send(String data) throws NetworkException {
        TransferMessage request = new TransferMessage(data);
        sendRequest(request);
    }

    public void send(byte[] data) throws NetworkException {
        TransferMessage request = new TransferMessage(data);
        sendRequest(request);
    }

    public void sendAsync(String data) {
        TransferMessage request = new TransferMessage(data);
        waitingMessages.add(request);
    }

    public void sendAsync(byte[] data) {
        TransferMessage request = new TransferMessage(data);
        waitingMessages.add(request);
    }

    private void sendRequest(TransferMessage request) throws NetworkException {
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

    private void sendRequestAsync(TransferMessage request) {
        try {
            restTemplate.postForEntity(
                    "/save",
                    request,
                    TransferResponseMessage.class
            );
        } catch (RestClientException e) {
            e.printStackTrace();
            waitingMessages.add(request);
        }
    }

    private void asyncLoop() {
        log.info("Async loop started.");
        while (running) {
            try {
                TransferMessage message = waitingMessages.take();
                sendRequestAsync(message);
                TimeUnit.SECONDS.sleep(1);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        log.info("Async loop done.");
    }
}
