package dvoraka.restcommmspoc.service;

import com.squareup.tape2.QueueFile;
import dvoraka.restcommmspoc.controller.ControllerConstants;
import dvoraka.restcommmspoc.data.message.TransferMessage;
import dvoraka.restcommmspoc.data.message.TransferResponseMessage;
import dvoraka.restcommmspoc.exception.NetworkException;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.io.File;
import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import static java.util.Objects.requireNonNull;

@Service
public class DefaultRestClientTransferService extends AbstractBaseService implements RestClientTransferService {

    private static final Logger log = org.slf4j.LoggerFactory.getLogger(DefaultRestClientTransferService.class);
    private final RestTemplate restTemplate;

    public static final String FS_QUEUE_NAME = "queue";

    private final ExecutorService asyncService;

    private final QueueFile queueFile;

    private volatile boolean running;


    @Autowired
    public DefaultRestClientTransferService(RestTemplate restTemplate) throws IOException {
        this.restTemplate = requireNonNull(restTemplate);
        asyncService = Executors.newSingleThreadExecutor();

        File file = new File(FS_QUEUE_NAME);
        queueFile = new QueueFile.Builder(file)
                .build();
    }

    @PostConstruct
    public void start() {
        running = true;
        asyncService.execute(this::asyncLoop);
    }

    @PreDestroy
    public void stop() {
        running = false;
    }

    @Override
    public void send(String data) throws NetworkException {
        TransferMessage request = new TransferMessage(data);
        sendRequest(request);
    }

    @Override
    public void send(byte[] data) throws NetworkException {
        TransferMessage request = new TransferMessage(data);
        sendRequest(request);
    }

    @Override
    public void sendAsync(String data) {
        TransferMessage request = new TransferMessage(data);
        storeMessage(request);
    }

    @Override
    public void sendAsync(byte[] data) {
        TransferMessage request = new TransferMessage(data);
        storeMessage(request);
    }

    private void sendRequest(TransferMessage request) throws NetworkException {
        try {
            ResponseEntity<TransferResponseMessage> response = restTemplate.postForEntity(
                    ControllerConstants.SAVE_PATH,
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
            storeMessage(request);
        }
    }

    private void asyncLoop() {
        log.info("Async loop started.");
        try {
            TimeUnit.SECONDS.sleep(2);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        while (running) {
            try {
                byte[] data = queueFile.peek();
                if (data == null) {
                    continue;
                } else {
                    queueFile.remove();
                }

                sendRequestAsync(new TransferMessage(data));
                TimeUnit.SECONDS.sleep(1);
            } catch (InterruptedException | IOException e) {
                e.printStackTrace();
            }
        }
        log.info("Async loop done.");
    }

    private void storeMessage(TransferMessage message) {
        log.debug("Storing message...");
        try {
            queueFile.add(message.getData());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
