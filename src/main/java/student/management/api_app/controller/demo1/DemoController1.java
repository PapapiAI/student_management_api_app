package student.management.api_app.controller.demo1;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("${api.prefix}/demo1")
public class DemoController1 {
    @GetMapping
    public ResponseEntity<String> demo() {
        return ResponseEntity.ok("Hello Demo1");
    }
}
