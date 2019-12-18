package dvoraka.restcommmspoc.controller;

import dvoraka.restcommmspoc.data.message.TransferMessage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
public class TransferController {

    @PostMapping
    public void save(@RequestBody TransferMessage message) {
        log.warn("Save: {}", message.getData());
    }
}
