package resource;

import java.io.Serializable;

public class ProcessInfo implements Serializable {

    private String name;
    private String pid;
    private String usedMem;

    public void setPid(String pid) {
        this.pid = pid;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setUsedMem(String usedMem) {
        this.usedMem = usedMem;
    }
}
