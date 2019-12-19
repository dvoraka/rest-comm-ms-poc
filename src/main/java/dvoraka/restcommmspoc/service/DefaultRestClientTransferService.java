package dvoraka.restcommmspoc.service;

import com.squareup.tape2.QueueFile;
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
import javax.annotation.PreDestroy;
import java.io.File;
import java.io.IOException;
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

    @PreDestroy
    public void stop() {
        running = false;
        try {
            storeQueue();
        } catch (IOException e) {
            e.printStackTrace();
        }
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
        saveMessage(request);
    }

    public void sendAsync(byte[] data) {
        TransferMessage request = new TransferMessage(data);
        saveMessage(request);
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
            saveMessage(request);
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

    private void saveMessage(TransferMessage message) {
        log.debug("Saving message...");
        waitingMessages.add(message);
    }

    private void storeQueue() throws IOException {

        File file = new File("queue");
        QueueFile queueFile = new QueueFile.Builder(file)
                .build();

        int messageCount = 0;
        while (!waitingMessages.isEmpty()) {
            try {
                TransferMessage message = waitingMessages.take();
                log.debug("Storing message...");
                queueFile.add(message.getData());
                messageCount++;
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        log.info("Stored {} messages", messageCount);
    }
}
