package dvoraka.restcommmspoc.controller;

import dvoraka.restcommmspoc.data.message.TransferMessage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.nio.charset.StandardCharsets;

@Slf4j
@RestController
public class TransferController {

    @PostMapping(ControllerConstants.SAVE_PATH)
    public void save(@RequestBody TransferMessage message) {
        log.warn("Save: {}", new String(message.getData(), StandardCharsets.UTF_8));
    }
}
