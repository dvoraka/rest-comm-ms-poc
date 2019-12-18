package dvoraka.restcommmspoc.data.message;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.nio.charset.StandardCharsets;

@Data
@AllArgsConstructor
public class TransferMessage {

    private MessageType type;
    private byte[] data;


    public TransferMessage(String message) {
        data = message.getBytes(StandardCharsets.UTF_8);
    }

    public TransferMessage(byte[] data) {
        this.data = data;
    }
}
