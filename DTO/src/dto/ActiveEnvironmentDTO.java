package dto;

import java.util.List;

public class ActiveEnvironmentDTO {
    private final List<PropertyInstanceDTO> propertyInstanceDTOS;

    public List<PropertyInstanceDTO> getPropertyInstanceDTOS() {
        return propertyInstanceDTOS;
    }

    public ActiveEnvironmentDTO(List<PropertyInstanceDTO> propertyInstanceDTOS) {
        this.propertyInstanceDTOS = propertyInstanceDTOS;
    }
}
