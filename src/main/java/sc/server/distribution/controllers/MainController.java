package sc.server.distribution.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class MainController {

    @Autowired
    private Environment environment;


    @GetMapping("/ping")
    public ResponseEntity<?> ping(){
        return new ResponseEntity("Сервер работает!", HttpStatus.OK);
    }

    @GetMapping("/id")
    public ResponseEntity<?> getId(){
        String serverId = environment.getProperty("values.server-id");

        if (serverId == null || serverId.isEmpty()){
            return new ResponseEntity<>("Не удалось получить id", HttpStatus.BAD_REQUEST);
        }
        return new ResponseEntity<>(serverId, HttpStatus.OK);
    }
}
