package utils.user;

import dto.RequestDetailsDTO;
import dto.WorldDefinitionDTO;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RequestsManager {
   private Map<String, List<RequestDetailsDTO>> requestsPerUser;
    //List<RequestDetailsDTO> allRequests;

    public RequestsManager(){
        //allRequests = new ArrayList<>();
        requestsPerUser = new HashMap<>();
    }

    public Map<String, List<RequestDetailsDTO>> getRequestsPerUser() {
        return requestsPerUser;
    }

    public void addRequestByUser(String user, RequestDetailsDTO requestDetailsDTO){
        if(!requestsPerUser.containsKey(user)){
            requestsPerUser.put(user, new ArrayList<>());
        }
        requestsPerUser.get(user).add(requestDetailsDTO);
    }
    public List<RequestDetailsDTO> getAllRequestsByUser(String user){
        return requestsPerUser.get(user);
    }
    public RequestDetailsDTO getRequestByName(String user, String name){
        return requestsPerUser.get(user).stream()
                .filter(requestDetailsDTO -> requestDetailsDTO.getSimulationName().equals(name))
                .findFirst().orElse(null);
    }
    public void removeSimulationByName(String user, String name){
        requestsPerUser.get(user).removeIf(requestDetailsDTO -> requestDetailsDTO.getSimulationName().equals(name));
    }

}
