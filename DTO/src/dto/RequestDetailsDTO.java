package dto;

import java.util.List;

public class RequestDetailsDTO {
    private String userName;
    private int requestNumber;
    private String simulationName;
    private int amountOfRunning;
    private String requestStatus;
    private int amountOfSimulationsRuunning;
    private int amountOfSimulationEnding;
    private List<TerminateConditionDTO> terminateConditions;

    public void setRequestStatus(String requestStatus) {
        this.requestStatus = requestStatus;
    }

    public void setAmountOfRunning(int amountOfRunning) {
        this.amountOfRunning = amountOfRunning;
    }

    public RequestDetailsDTO(String userName, int requestNumber, String simulationName, int amountOfRunning,
                             String requestStatus, int amountOfSimulationsRuunning, int amountOfSimulationEnding, List<TerminateConditionDTO> terminateConditions) {
        this.requestNumber = requestNumber;
        this.simulationName = simulationName;
        this.amountOfRunning = amountOfRunning;
        this.requestStatus = requestStatus;
        this.amountOfSimulationsRuunning = amountOfSimulationsRuunning;
        this.amountOfSimulationEnding = amountOfSimulationEnding;
        this.terminateConditions = terminateConditions;
        this.userName = userName;
    }
    public RequestDetailsDTO(int requestNumber,String simulationName, int amountOfRunning,
                             String requestStatus, int amountOfSimulationsRuunning, int amountOfSimulationEnding, List<TerminateConditionDTO> terminateConditions) {
        this.requestNumber = requestNumber;
        this.simulationName = simulationName;
        this.amountOfRunning = amountOfRunning;
        this.requestStatus = requestStatus;
        this.amountOfSimulationsRuunning = amountOfSimulationsRuunning;
        this.amountOfSimulationEnding = amountOfSimulationEnding;
        this.terminateConditions = terminateConditions;

    }

    public String getUserName() {
        return userName;
    }

    public int getRequestNumber() {
        return requestNumber;
    }

    public String getRequestStatus() {
        return requestStatus;
    }

    public int getAmountOfSimulationsRuunning() {
        return amountOfSimulationsRuunning;
    }

    public int getAmountOfSimulationEnding() {
        return amountOfSimulationEnding;
    }

    public String getSimulationName() {
        return simulationName;
    }

    public int getAmountOfRunning() {
        return amountOfRunning;
    }

    public List<TerminateConditionDTO> getTerminateConditions() {
        return terminateConditions;
    }
}
