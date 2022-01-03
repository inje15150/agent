package entity;

import resource.ProcessInfo;
import resource.ResourceExtraction;

import java.io.IOException;
import java.util.List;

public class AgentInfoEntity {

    private String hostname;
    private String osInfo;
    private String interface_name;
    private String ip;
    private String gateway;
    private String mac_address;
    private float cpu;
    private float memory;
    private float disk;
    private String event_time;
    private List<ProcessInfo> processes;

    public AgentInfoEntity(ResourceExtraction resourceExtraction) throws IOException {
        this.osInfo = resourceExtraction.osInfo();
        this.hostname = resourceExtraction.getHostName();
        this.ip = resourceExtraction.getIpConfig();
        this.cpu = resourceExtraction.getCpu();
        this.memory = resourceExtraction.getMem();
        this.disk = resourceExtraction.getDisk();
        this.event_time = resourceExtraction.getEventDate();
        this.processes = resourceExtraction.getProcess();
        this.mac_address = resourceExtraction.getMacAddress();
        this.gateway = resourceExtraction.getGateway();
        this.interface_name = resourceExtraction.getInterface();
     }

    public String getIp() {
        return ip;
    }

    public String getGateway() {
        return gateway;
    }

    public String getMac_address() {
        return mac_address;
    }
}
