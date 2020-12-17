package xyz.ahbicj.consumer;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import xyz.ahbicj.snowflake.ID;
import xyz.ahbicj.snowflake.IDGen;
import xyz.ahbicj.snowflake.Status;

/**
 * Class info
 *
 * @author yefeng
 * @since 2020/12/9
 */
@RestController
@RequestMapping("/api")
public class BasicController {

    @Autowired
    private IDGen idgen;

    @GetMapping("ping")
    public String echoAppName() {
        while (true) {
            ID id = idgen.get();
            if (id.getStatus() == Status.SUCCESS) return id.toString();
        }
    }
}
