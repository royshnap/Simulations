package dto;

public class WorldInstanceDTO {

    private WorldDefinitionDTO worldDefinitionDTO;

    public WorldDefinitionDTO getWorldDefinitionDTO() {
        return worldDefinitionDTO;
    }

    public WorldInstanceDTO(WorldDefinitionDTO worldDefinitionDTO) {
        this.worldDefinitionDTO = worldDefinitionDTO;
    }
}
