package sc.server.distribution.controllers;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class MainController {

    @GetMapping("/ping")
    public ResponseEntity<?> ping(){
        return new ResponseEntity("Сервер работает!", HttpStatus.OK);
    }
}
