package dto;

import java.util.List;

public class EnvVariableManagerDTO {

    private List<PropertyDefinitionDTO> definitionDTOS;

    public EnvVariableManagerDTO(List<PropertyDefinitionDTO> propNameToPropDefinition) {
        this.definitionDTOS = propNameToPropDefinition;
    }

    public List<PropertyDefinitionDTO> getDefinitionDTOS() {
        return definitionDTOS;
    }
}
