package dvoraka.restcommmspoc.data.message;

import java.nio.charset.StandardCharsets;

public class TransferMessage {

    private MessageType type;
    private byte[] data;


    public TransferMessage(String message) {
        data = message.getBytes(StandardCharsets.UTF_8);
    }

    public TransferMessage(byte[] data) {
        this.data = data;
    }

    public TransferMessage() {
    }

    public TransferMessage(MessageType type, byte[] data) {
        this.type = type;
        this.data = data;
    }

    public MessageType getType() {
        return this.type;
    }

    public byte[] getData() {
        return this.data;
    }

    public void setType(MessageType type) {
        this.type = type;
    }

    public void setData(byte[] data) {
        this.data = data;
    }

    public boolean equals(final Object o) {
        if (o == this) return true;
        if (!(o instanceof TransferMessage)) return false;
        final TransferMessage other = (TransferMessage) o;
        if (!other.canEqual(this)) return false;
        final Object this$type = this.getType();
        final Object other$type = other.getType();
        if (this$type == null ? other$type != null : !this$type.equals(other$type)) return false;
        return java.util.Arrays.equals(this.getData(), other.getData());
    }

    protected boolean canEqual(final Object other) {
        return other instanceof TransferMessage;
    }

    public int hashCode() {
        final int PRIME = 59;
        int result = 1;
        final Object $type = this.getType();
        result = result * PRIME + ($type == null ? 43 : $type.hashCode());
        result = result * PRIME + java.util.Arrays.hashCode(this.getData());
        return result;
    }

    public String toString() {
        return "TransferMessage(type=" + this.getType() + ", data=" + java.util.Arrays.toString(this.getData()) + ")";
    }
}
