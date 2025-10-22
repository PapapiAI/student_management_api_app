package student.management.api_app.controller.demo2;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("${api.prefix}/demo2a")
public class DemoController2a {
    @GetMapping
    public ResponseEntity<String> demo2a() {
        return ResponseEntity.ok("Hello Demo2a");
    }
}
