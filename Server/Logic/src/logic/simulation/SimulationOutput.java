package logic.simulation;

import logic.world.WorldInstance;

public class SimulationOutput {
    private int id;
    private WorldInstance worldInstance;
    private String reasonsOfEnding;

    public SimulationOutput(int id, WorldInstance worldInstance){
        this.id = id;
        this.worldInstance = worldInstance;
    }

    public String getReasonsOfEnding() {
        return reasonsOfEnding;
    }

    public int getId() {
        return id;
    }

    public WorldInstance getWorldInstance() {
        return worldInstance;
    }

    public void setReasonsOfEnding(String reasonsOfEnding) {
        this.reasonsOfEnding = reasonsOfEnding;
    }
}
