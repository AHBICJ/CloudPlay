package xyz.ahbicj.autoconfigure.idgen;

import com.alibaba.cloud.nacos.NacosDiscoveryProperties;
import com.alibaba.cloud.nacos.registry.NacosRegistration;
import com.alibaba.nacos.api.common.Constants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.util.StringUtils;

import javax.annotation.PostConstruct;

@ConfigurationProperties(prefix = "ahbicj.idgen")
public class IDGenProperties {
    private int workerId = -1;
    private String ip;
    @Value("${ahbicj.idgen.port:${server.port}}")
    private int port;

    private String serverAddr;
    private String namespace;
    private String servicePrefix;
    private String serviceName;
    private String serviceGroup;
    private String serviceCluster;

    @Autowired
    private NacosRegistration registration;
    @PostConstruct
    public void init(){
        NacosDiscoveryProperties nacosDiscoveryProperties = registration.getNacosDiscoveryProperties();
        if (StringUtils.isEmpty(ip)) ip = nacosDiscoveryProperties.getIp();
        if (StringUtils.isEmpty(serverAddr)) serverAddr = nacosDiscoveryProperties.getServerAddr();
        // 注意空字符串为public的Id
        if (namespace==null) namespace = nacosDiscoveryProperties.getNamespace();
        if (servicePrefix == null) servicePrefix = "snowflake_id_";
        if (StringUtils.isEmpty(serviceName)) serviceName = "default";
        if (StringUtils.isEmpty(serviceGroup)) serviceGroup = Constants.DEFAULT_GROUP;
        if (StringUtils.isEmpty(serviceCluster)) serviceCluster = Constants.DEFAULT_CLUSTER_NAME;
    }

    public int getWorkerId() {
        return workerId;
    }

    public void setWorkerId(int workerId) {
        this.workerId = workerId;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public String getServerAddr() {
        return serverAddr;
    }

    public void setServerAddr(String serverAddr) {
        this.serverAddr = serverAddr;
    }

    public String getNamespace() {
        return namespace;
    }

    public void setNamespace(String namespace) {
        this.namespace = namespace;
    }

    public String getServiceName() {
        return serviceName;
    }

    public void setServiceName(String serviceName) {
        this.serviceName = serviceName;
    }

    public String getServicePrefix() {
        return servicePrefix;
    }

    public void setServicePrefix(String servicePrefix) {
        this.servicePrefix = servicePrefix;
    }

    public String getServiceGroup() {
        return serviceGroup;
    }

    public void setServiceGroup(String serviceGroup) {
        this.serviceGroup = serviceGroup;
    }

    public String getServiceCluster() {
        return serviceCluster;
    }

    public void setServiceCluster(String serviceCluster) {
        this.serviceCluster = serviceCluster;
    }
}
