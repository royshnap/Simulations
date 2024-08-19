package dto;

import java.util.ArrayList;
import java.util.List;

public class EntityDefinitionDTO {
    private final String name;
    private  List<PropertyDefinitionDTO> properties;
    private  int startPopulation;
    private int endPopulation;

    public EntityDefinitionDTO(String name, List<PropertyDefinitionDTO> propertyDefinitionDTOS, int startPopulation
    , int endPopulation){
        this.name = name;
        properties = propertyDefinitionDTOS;
        this.startPopulation = startPopulation;
        this.endPopulation = endPopulation;
    }
    public EntityDefinitionDTO(String name,int startPopulation
            , int endPopulation){
        this.name = name;
        this.startPopulation = startPopulation;
        this.endPopulation = endPopulation;
    }
    public void addProperty(PropertyDefinitionDTO property){
        if(properties == null){
            properties = new ArrayList<>();
        }
        properties.add(property);
    }

    public void setStartPopulation(int startPopulation) {
        this.startPopulation = startPopulation;
    }

    public int getEndPopulation() {
        return endPopulation;
    }

    public int getStartPopulation() {
        return startPopulation;
    }

    public List<PropertyDefinitionDTO> getProperties() {
        return properties;
    }

    public String getName() {
        return name;
    }

    public void setEndPopulation(int endPopulation) {
        this.endPopulation = endPopulation;
    }
}
