package utils.admin;

import dto.WorldDefinitionDTO;

import java.util.ArrayList;
import java.util.List;

public class SimulationsDefinitionsManager {
    private List<WorldDefinitionDTO> allSimulations;

    public SimulationsDefinitionsManager() {
        this.allSimulations = new ArrayList<>();
    }

    public void addSimulation(WorldDefinitionDTO worldDefinitionDTO) {
        allSimulations.add(worldDefinitionDTO);
    }

    public List<WorldDefinitionDTO> getAllSimulations() {
        return allSimulations;
    }
    public WorldDefinitionDTO getSimulationByName(String name){
        return allSimulations.stream()
                .filter(worldDefinitionDTO -> worldDefinitionDTO.getName().equals(name))
                .findFirst().orElse(null);
    }
    public void removeSimulationByName(String name){
        allSimulations.removeIf(worldDefinitionDTO -> worldDefinitionDTO.getName().equals(name));
    }
}
