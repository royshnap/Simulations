package dto;


import java.util.Date;

public class SimulationDTO {
    private final WorldDefinitionDTO worldDefinitionDTO;
    private boolean isWaitingSimulation;
    private String userName;

    private final int id;
    private final  Date date;
    private final SimulationOutputDTO simulationOutput;
    private boolean endSimualtion;
    private boolean isRUnning = false;
    private SimulationCurrentDetailsDTO currentDetailsDTO;
    private String simulationName;

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public void setCurrentDetailsDTO(SimulationCurrentDetailsDTO currentDetailsDTO) {
        this.currentDetailsDTO = currentDetailsDTO;
    }

    public SimulationCurrentDetailsDTO getCurrentDetailsDTO() {
        return currentDetailsDTO;
    }

    public SimulationDTO(int id, Date date, SimulationOutputDTO simulationOutput,
                         WorldDefinitionDTO worldInstanceDTO, String userName, String nameSimulation) {
        this.id = id;
        this.date = date;
        this.simulationOutput = simulationOutput;
        this.worldDefinitionDTO = worldInstanceDTO;
        this.userName = userName;
        this.simulationName = nameSimulation;

    }

    public String getSimulationName() {
        return simulationName;
    }

    public void setWaitingSimulation(boolean waitingSimulation) {
        isWaitingSimulation = waitingSimulation;
    }

    public boolean isWaitingSimulation() {
        return isWaitingSimulation;
    }

    public WorldDefinitionDTO getWorldDefinitionDTO() {
        return worldDefinitionDTO;
    }

    public SimulationOutputDTO getSimulationOutput() {
        return simulationOutput;
    }

    public int getId() {
        return id;
    }

    public Date getDate() {
        return date;
    }

    public boolean getEndSimualtion(){
        return endSimualtion;
    }
    public void setEndSimualtion(boolean endSimualtion){
        this.endSimualtion = endSimualtion;
    }

    public boolean isRunning() {
        return isRUnning;
    }
    public void setRunning(boolean isRUnning){
        this.isRUnning = isRUnning;
    }
}
