package dto;


import java.util.List;

public class EntityInstanceDTO {
    private final EntityDefinitionDTO entityDefinition;
    private final List<PropertyInstanceDTO> properties;
    public EntityInstanceDTO(EntityDefinitionDTO entityDefinition, List<PropertyInstanceDTO> properties) {
        this.entityDefinition = entityDefinition;
        this.properties = properties;
    }

    public EntityDefinitionDTO getEntityDefinition() {
        return entityDefinition;
    }

    public List<PropertyInstanceDTO> getProperties() {
        return properties;
    }

}
