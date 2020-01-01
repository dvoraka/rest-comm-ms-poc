package dvoraka.restcommmspoc;

import com.squareup.tape2.QueueFile;
import dvoraka.restcommmspoc.data.message.TransferMessage;
import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.io.IOException;

import static java.util.Objects.requireNonNull;

@Slf4j
public class FsQueueStrategy implements QueueStrategy<TransferMessage> {

    private final String queueName;
    private final QueueFile queueFile;


    public FsQueueStrategy(String queueName) throws IOException {
        this.queueName = requireNonNull(queueName);

        File file = new File(queueName);
        queueFile = new QueueFile.Builder(file)
                .build();
    }


    @Override
    public void saveItem(TransferMessage item) {
        log.debug("Storing message...");
        try {
            queueFile.add(item.getData());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public TransferMessage loadItem() {
        return null;
    }

    @Override
    public void storeQueue(Iterable<TransferMessage> items) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Iterable<TransferMessage> loadQueue() {
        return null;
    }
}
