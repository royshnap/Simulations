package utils.user;

import dto.RequestDetailsDTO;
import dto.SimulationDTO;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SimulationUserManager {
    private Map<String, List<SimulationDTO>> simulationsPerUser;

    public SimulationUserManager() {
        simulationsPerUser = new HashMap<>();
    }

    public List<SimulationDTO> getSimulationsPerUser(String user) {

        return simulationsPerUser.get(user);
    }
    public void addSimualtionByUser(String user, SimulationDTO simulationDTO){
        if(!simulationsPerUser.containsKey(user)){
            simulationsPerUser.put(user, new ArrayList<>());
        }
        List<SimulationDTO> simulationDTOList = simulationsPerUser.get(user);
        for(SimulationDTO simulationDTO1 : simulationDTOList){
            if(simulationDTO1.getId() == simulationDTO.getId()){
                return;
            }
        }
        simulationsPerUser.get(user).add(simulationDTO);
    }
    public List<SimulationDTO> getAllRequestsByUser(String user){
        return simulationsPerUser.get(user);
    }

}
