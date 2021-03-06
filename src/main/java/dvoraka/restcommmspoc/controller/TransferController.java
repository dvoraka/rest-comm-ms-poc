package dvoraka.restcommmspoc.controller;

import dvoraka.restcommmspoc.data.message.TransferMessage;
import dvoraka.restcommmspoc.service.RestServerTransferService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.nio.charset.StandardCharsets;

import static java.util.Objects.requireNonNull;

@Slf4j
@RestController
public class TransferController {

    private final RestServerTransferService service;


    @Autowired
    public TransferController(RestServerTransferService service) {
        this.service = requireNonNull(service);
    }

    @PostMapping(ControllerConstants.SAVE_PATH)
    public void save(@RequestBody TransferMessage message) {
        log.warn("Save: {}", new String(message.getData(), StandardCharsets.UTF_8));
    }
}
