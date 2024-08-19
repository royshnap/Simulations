package logic.simulation;

import logic.execution.instance.property.api.PropertyInstance;

import java.util.HashMap;
import java.util.Map;

public class HistogramSimulationManger {
//the Object is the value of the property
    private Map<Object, Integer> histogram = new HashMap<>();
    private int consistency;

    public Map<Object, Integer> getHistogram() {
        return histogram;
    }

    public int getConsistency() {
        return consistency;
    }

    public void setConsistency(int consistency) {
        this.consistency = consistency;
    }
}
