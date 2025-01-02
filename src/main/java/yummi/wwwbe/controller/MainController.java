package yummi.wwwbe.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.view.RedirectView;

@RestController
public class MainController {

    @GetMapping("/mainpage")
    public RedirectView mainP() {
        return new RedirectView("http://localhost:3000");
    }

    @GetMapping("/main/test")
    public ResponseEntity<String> testMessage() {
        return ResponseEntity.ok("Hello World");
    }
}
