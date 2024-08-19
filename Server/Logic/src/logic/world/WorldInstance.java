package logic.world;

import logic.definition.entity.api.EntityDefinition;
import logic.definition.property.api.PropertyDefinition;
import logic.definition.property.api.PropertyType;
import logic.definition.property.impl.FloatPropertyDefinition;
import logic.definition.property.impl.IntegerPropertyDefinition;
import logic.definition.value.api.ValueGeneratorFactory;
import dto.*;
import logic.execution.instance.entity.api.EntityInstance;
import logic.execution.instance.entity.manager.api.EntityInstanceManager;
import logic.execution.instance.entity.manager.impl.EntityInstanceManagerImpl;
import logic.execution.instance.environment.api.ActiveEnvironment;
import logic.execution.instance.property.api.PropertyInstance;
import logic.execution.instance.property.impl.PropertyInstanceImpl;
import logic.rule.Rule;
import logic.terminateCondition.TerminateCondition;
import logic.world.entitiesMatrix.Matrix;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.NoSuchElementException;

public class WorldInstance{

    private WorldDefinition worldDefinition;

    private EntityInstanceManager entityInstanceManagerCreating;
    private int ticks;
    private int seconds;
    private SimulationHistoryDTO simulationHistory;

    private List<TerminateCondition> terminateConditions;
    private Matrix grid;


    private ActiveEnvironment environmentVariablesManager;

    private List<Rule> rules;


    public WorldInstance(WorldDefinition worldDefinition, ActiveEnvironment environmentVariablesManager,
                         SimulationHistoryDTO simulationHistory){
        this.worldDefinition = worldDefinition;
        terminateConditions = new ArrayList<>();
        this.environmentVariablesManager = environmentVariablesManager;
        entityInstanceManagerCreating = new EntityInstanceManagerImpl();
        rules = new ArrayList<>();
        seconds = 0;
        ticks = 0;
        this.simulationHistory = simulationHistory;


    }

    public void setSeconds(int seconds) {
        this.seconds = seconds;
    }


    public WorldDefinition getWorldDefinition() {
        return worldDefinition;
    }

    public Matrix getGrid() {
        return grid;
    }

    public void initialize(){
        grid = new Matrix(worldDefinition.getRows(), worldDefinition.getColumns());
        Collection<EntityDefinition> entityDefinitions = worldDefinition.getPopulation();
        for(EntityDefinition entityDefinition : entityDefinitions){
            for(int i=0; i< entityDefinition.getStartPopulation(); i++){
               EntityInstance entityInstance = entityInstanceManagerCreating.create(entityDefinition);
                grid.placeEntityRandomly(entityInstance);
            }

        }
        PropertyInstanceDTO propertyInstanceDTO = null;
       for(PropertyInstance propertyInstance : environmentVariablesManager.getEnvs()){
           for(PropertyInstanceDTO propertyInstanceDTO1 : simulationHistory.getPropertyInstanceDTOS()){
               if(propertyInstanceDTO1.getPropertyDefinition().getName().equals(propertyInstance.getPropertyDefinition().getName())){
                   propertyInstanceDTO = propertyInstanceDTO1;
                   break;
               }
           }
           if(propertyInstance.getValue() == null){
               if (propertyInstance.getPropertyDefinition().getType() == PropertyType.BOOLEAN){
                   propertyInstance.setValue(ValueGeneratorFactory.createRandomBoolean().generateValue());
                   propertyInstanceDTO.setValue(propertyInstance.getValue());
               }

               else if (propertyInstance.getPropertyDefinition().getType() == PropertyType.FLOAT) {
                   propertyInstance.setValue(ValueGeneratorFactory.createRandomFloat(((FloatPropertyDefinition)propertyInstance.getPropertyDefinition()).getFrom(),
                           ((FloatPropertyDefinition)propertyInstance.getPropertyDefinition()).getTo()).generateValue());
                   propertyInstanceDTO.setValue(propertyInstance.getValue());

               }
               else if (propertyInstance.getPropertyDefinition().getType() == PropertyType.DECIMAL) {
                   propertyInstance.setValue(ValueGeneratorFactory.createRandomInteger(((IntegerPropertyDefinition)propertyInstance.getPropertyDefinition()).getFrom(),
                           ((IntegerPropertyDefinition)propertyInstance.getPropertyDefinition()).getTo()).generateValue());
                   propertyInstanceDTO.setValue(propertyInstance.getValue());
               }
               else {
                  propertyInstance.setValue(ValueGeneratorFactory.createRandomString().generateValue());
                  propertyInstanceDTO.setValue(propertyInstance.getValue());
               }
           }

       }
        terminateConditions = worldDefinition.getTerminateConditions();
        rules.addAll(worldDefinition.getRules());
    }
    public void createAndAddEnvVariableInstance(PropertyDefinition envVariable, Object userInput){
        PropertyInstance newPropertyInstance = new PropertyInstanceImpl(envVariable, userInput);
        environmentVariablesManager.addPropertyInstance(newPropertyInstance);
    }

    public List<Rule> getRules() {
        return rules;
    }
    public int getSeconds(){
        return seconds;
    }

    public void setTicks(int ticks) {
        this.ticks = ticks;
    }

    public int getTicks() {
        return ticks;
    }

    public void setWorldDefinition(WorldDefinition definition) {
        worldDefinition = definition;
    }

    public List<EntityInstance> getPopulation(){
        return entityInstanceManagerCreating.getInstances();
    }
    public void addTerminateCondition(TerminateCondition terminateCondition){
        terminateConditions.add(terminateCondition);
    }
    public EntityInstanceManager getEntityInstanceManager(){
        return entityInstanceManagerCreating;
    }

    public void addRule(Rule rule){
        rules.add(rule);
    }



    public PropertyInstance findEnvironmentsVariables(String propertyName) throws NoSuchElementException {
        for(PropertyInstance p : environmentVariablesManager.getEnvs()){
            if(p.getPropertyDefinition().getName().equals(propertyName)){
                return p;
            }
        }
        throw new NoSuchElementException("The environment variable name " +
                propertyName + " doesn't exists");
    }

    public List<TerminateCondition> getTerminateConditions() {
        return terminateConditions;
    }

    public ActiveEnvironment getEnvironmentsVariables(){
        return environmentVariablesManager;
    }

    public void searchForZeroEntity(){
        entityInstanceManagerCreating.getInstances().removeIf(entity -> entity.getEntityDefinition().getStartPopulation() == 0);
    }

}
