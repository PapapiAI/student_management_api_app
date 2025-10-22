package student.management.api_app.controller.demo2;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("${api.prefix}/demo2b")
public class DemoController2b {
    @GetMapping
    public ResponseEntity<?> demo2b() {
        return ResponseEntity.ok("Hello Demo2b");
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> demo2bById(@PathVariable int id) {
        return ResponseEntity.ok("Hello Demo2b By ID");
    }
}
