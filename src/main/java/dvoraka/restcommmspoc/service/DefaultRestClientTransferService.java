package dvoraka.restcommmspoc.service;

import com.squareup.tape2.QueueFile;
import dvoraka.restcommmspoc.controller.ControllerConstants;
import dvoraka.restcommmspoc.data.QueueType;
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
import java.util.Iterator;
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

    public static final QueueType DEFAULT_QUEUE_TYPE = QueueType.IN_MEMORY;
    public static final String FS_QUEUE_NAME = "queue";

    private final QueueType queueType;

    private final BlockingQueue<TransferMessage> waitingMessages;
    private final ExecutorService asyncService;

    private final QueueFile queueFile;

    private volatile boolean running;


    @Autowired
    public DefaultRestClientTransferService(RestTemplate restTemplate, QueueType queueType) throws IOException {
        this.restTemplate = requireNonNull(restTemplate);
        this.queueType = queueType == null ? DEFAULT_QUEUE_TYPE : queueType;

        waitingMessages = new ArrayBlockingQueue<>(100);
        asyncService = Executors.newSingleThreadExecutor();

        File file = new File(FS_QUEUE_NAME);
        queueFile = new QueueFile.Builder(file)
                .build();
    }

    @PostConstruct
    public void start() {
        running = true;
//        try {
//            loadQueue();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
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
//        saveMessage(request);
        storeMessage(request);
    }

    @Override
    public void sendAsync(byte[] data) {
        TransferMessage request = new TransferMessage(data);
//        saveMessage(request);
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
//            saveMessage(request);
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
//                TransferMessage message = waitingMessages.take();
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

    private void saveMessage(TransferMessage message) {
        log.debug("Saving message...");
        waitingMessages.add(message);
    }

    private void storeMessage(TransferMessage message) {
        log.debug("Storing message...");
        try {
            queueFile.add(message.getData());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void storeQueue() throws IOException {

//        File file = new File(FS_QUEUE_NAME);
//        QueueFile queueFile = new QueueFile.Builder(file)
//                .build();

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

    private void loadQueue() throws IOException {

        File file = new File(FS_QUEUE_NAME);
        QueueFile queueFile = new QueueFile.Builder(file)
                .build();

        int messageCount = 0;
        Iterator<byte[]> iterator = queueFile.iterator();
        while (iterator.hasNext()) {
            log.debug("Loading message...");
            byte[] element = iterator.next();
            messageCount++;
            saveMessage(new TransferMessage(element));
            iterator.remove();
        }
        log.info("Loaded {} messages", messageCount);
    }
}
