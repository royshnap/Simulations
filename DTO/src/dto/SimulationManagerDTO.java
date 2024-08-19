package dto;


import java.util.List;

public class SimulationManagerDTO {

    private final List<SimulationDTO> simulationList;


    public SimulationManagerDTO(List<SimulationDTO> simulationDTOList){
        simulationList = simulationDTOList;
    }

    public List<SimulationDTO> getSimulationList() {
        return simulationList;
    }
}
