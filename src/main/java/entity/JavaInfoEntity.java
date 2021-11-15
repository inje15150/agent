package entity;

import resource.ResourceExtraction;

import java.io.Serializable;

public class JavaInfoEntity {

    private float totalMem;
    private float usedMem;
    private float freeMem;
    private String event_time;

    public JavaInfoEntity(ResourceExtraction resourceExtraction) {
        this.totalMem = resourceExtraction.jvmTotalMemory();
        this.usedMem = resourceExtraction.jvmUsedMemory();
        this.freeMem = resourceExtraction.jvmFreeMemory();
        this.event_time = resourceExtraction.getEventDate();
    }
}
