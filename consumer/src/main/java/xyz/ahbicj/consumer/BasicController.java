package xyz.ahbicj.consumer;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.loadbalancer.LoadBalancerClient;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

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
    private LoadBalancerClient loadBalancerClient;
    @Autowired
    private RestTemplate restTemplate;

    @Value("${spring.application.name}")
    private String appName;

    @GetMapping("/echo/app-name")
    public String echoAppName() {
        //Access through the combination of LoadBalanceClient and RestTemplate
        ServiceInstance serviceInstance = loadBalancerClient.choose("nacos-producer");
        String path = String.format("http://%s:%s/api/echo/%s", serviceInstance.getHost(), serviceInstance.getPort(), appName);
        System.out.println("request path:" + path);
        return restTemplate.getForObject(path, String.class);
    }
}
