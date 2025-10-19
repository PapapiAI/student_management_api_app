package student.management.api_app.controller.demo2;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("${api.prefix}/demo2")
public class DemoController2 {
    @GetMapping
    public ResponseEntity<String> demo2() {
        return ResponseEntity.ok("Hello Demo2");
    }
}
