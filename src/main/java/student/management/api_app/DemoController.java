package student.management.api_app;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("${api.prefix}/demo")
public class DemoController {
    @GetMapping
    public ResponseEntity<String> demo() {
        return ResponseEntity.ok("Demo123");
    }
}
