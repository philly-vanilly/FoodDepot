package Management.Controller;

import java.util.concurrent.atomic.AtomicLong;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import Management.Model.Account;

@RestController
public class AccountController {

    private static final String template = "Hello, %s!";
    private final AtomicLong counter = new AtomicLong();

    @RequestMapping("/account")
    public Account greeting(@RequestParam(value="name", defaultValue="World") String name) {
        return new Account("Hans", "Franz");
    }
}
