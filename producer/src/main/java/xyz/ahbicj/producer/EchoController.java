package xyz.ahbicj.producer;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Random;

/**
 * Class info
 *
 * @author yefeng
 * @since 2020/12/9
 */

@RestController
@RequestMapping("/api")
public class EchoController {
    @GetMapping(value = "echo/{string}")
    public String echo(@PathVariable String string) {
        return "Hello Nacos Discovery " + string;
    }

    @RequestMapping("ping")
    public String ping(){
        return "pong!";
    }

    @RequestMapping("nextId")
    public String gen(){
        return String.valueOf(new Random().nextLong());
    }
}
