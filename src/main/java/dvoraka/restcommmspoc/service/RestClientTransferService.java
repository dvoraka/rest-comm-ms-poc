package dvoraka.restcommmspoc.service;

import dvoraka.restcommmspoc.exception.NetworkException;

public interface RestClientTransferService extends BaseService {

    void send(String data) throws NetworkException;

    void send(byte[] data) throws NetworkException;

    void sendAsync(String data);

    void sendAsync(byte[] data);
}
