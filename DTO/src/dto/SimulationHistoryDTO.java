package dto;

import logic.definition.entity.api.EntityDefinition;

import java.util.List;

public class SimulationHistoryDTO {
    private List<PropertyInstanceDTO> propertyInstanceDTOS;
    private List<EntityDefinitionDTO> entityDefinitionsDTOS;
    private ActiveEnvironmentDTO activeEnvironmentDTO;
    private String name;

    public SimulationHistoryDTO(ActiveEnvironmentDTO activeEnvironmentDTO,
                                List<PropertyInstanceDTO> propertyInstanceDTOS, List<EntityDefinitionDTO> entityDefinitionsDTOS,
                                String name) {
        this.propertyInstanceDTOS = propertyInstanceDTOS;
        this.entityDefinitionsDTOS = entityDefinitionsDTOS;
        this.activeEnvironmentDTO = activeEnvironmentDTO;
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public ActiveEnvironmentDTO getActiveEnvironmentDTO() {
        return activeEnvironmentDTO;
    }

    public List<PropertyInstanceDTO> getPropertyInstanceDTOS() {

        return activeEnvironmentDTO.getPropertyInstanceDTOS();
    }

    public List<EntityDefinitionDTO> getEntityDefinitionsDTOS() {
        return entityDefinitionsDTOS;
    }
}
