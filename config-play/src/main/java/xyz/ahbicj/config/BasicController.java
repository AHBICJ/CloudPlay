package xyz.ahbicj.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Class info
 *
 * @author yefeng
 * @since 2020/12/9
 */

@RestController
@RefreshScope
@RequestMapping("/api")
public class BasicController {

    @Value("${user.name}")
    String userName;

    @Value("${user.age}")
    int userAge;

    @RequestMapping("/ping")
    public String ping() {
        return "user name: " + userName + "; age: " + userAge;
    }
}
