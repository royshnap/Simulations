package facade;

import com.*;
import logic.definition.entity.api.EntityDefinition;
import logic.definition.entity.impl.EntityDefinitionImpl;
import logic.definition.property.api.PropertyDefinition;
import dto.*;
import logic.definition.property.impl.BooleanPropertyDefinition;
import logic.definition.property.impl.FloatPropertyDefinition;
import logic.definition.property.impl.StringPropertyDefinition;
import logic.definition.value.random.AbstractRandomValueGenerator;
import logic.execution.instance.environment.api.ActiveEnvironment;
import logic.execution.instance.environment.impl.ActiveEnvironmentImpl;
import logic.execution.instance.property.api.PropertyInstance;
import logic.execution.instance.property.impl.PropertyInstanceImpl;
import logic.simulation.*;
import logic.terminateCondition.TerminateCondition;
import logic.world.WorldDefinition;
import logic.world.WorldInstance;


import javax.xml.bind.JAXBException;
import java.io.InputStream;
import java.util.*;
import java.util.concurrent.*;

public class Facade {
    private GenerateXML generator;
    private WorldDefinition worldDefinition;
    private List<WorldDefinition> worldDefinitions = new ArrayList<>();
    private WorldInstance worldInstance;
    private SimulationsManager simulationsManager;
    private SimulationManagerDTO simulationManagerDTO;
    private SimulationBuilder builder;
    private DTOCreator dtoCreator;
    private ExecutorService threadPool;
    private int numberOfThread;
    private SimulationHistoryDTO simulationHistory;
    private String file;
    private QueueManagmentDTO queueManagmentDTO;

    public WorldDefinition getWorldDefinition() {
        return worldDefinition;
    }

    public Facade(){
        dtoCreator = new DTOCreator();
        generator = new GenerateXML();
        simulationsManager = new SimulationsManager();
        builder = new SimulationBuilder();
    }

    public QueueManagmentDTO getQueueManagmentDTO() {
        if(simulationsManager.getThreadPool() == null){
            queueManagmentDTO = new QueueManagmentDTO(0,0,0);
        }
        else{
            queueManagmentDTO = new QueueManagmentDTO(simulationsManager.getThreadPool().getQueue().size(),
                    simulationsManager.getThreadPool().getActiveCount(),
                    simulationsManager.getThreadPool().getCompletedTaskCount());
        }
        return queueManagmentDTO;
    }

    public WorldDefinitionDTO generatorOperation(String file, InputStream fileContent) throws JAXBException, IllegalArgumentException {
       worldDefinition =GenerateXML.fromXmlFileToObject(file, fileContent);
        WorldDefinition worldDefinition1 = GenerateXML.buildFromExistingWorld(worldDefinition.getName());
        worldDefinitions.add(worldDefinition1);
       numberOfThread = worldDefinition.getNumberOfThreads();
       WorldDefinitionDTO worldDefinitionDTO = dtoCreator.createWorldDefinitionDTO(worldDefinition);
       this.file = file;
        return worldDefinitionDTO;

    }
    public WorldDefinitionDTO createExistingWorld(String simulationName){
        worldDefinition = GenerateXML.buildFromExistingWorld(simulationName);
        numberOfThread = worldDefinition.getNumberOfThreads();
        WorldDefinitionDTO worldDefinitionDTO = dtoCreator.createWorldDefinitionDTO(worldDefinition);
        return worldDefinitionDTO;
    }
    public WorldDefinitionDTO createWorldFromTheSameRequest(String simulationName){
        for(WorldDefinition worldDefinition1 : worldDefinitions){
            if(worldDefinition1.getName().equals(simulationName)){
                List<TerminateCondition> terminateConditions = worldDefinition1.getTerminateConditions();
                worldDefinition1 = GenerateXML.buildFromExistingWorld(simulationName);
                worldDefinition = worldDefinition1;
                for(TerminateCondition terminateCondition : terminateConditions){
                    worldDefinition.addTerminateCondition(terminateCondition);
                }
                break;
            }
        }
        numberOfThread = worldDefinition.getNumberOfThreads();
        WorldDefinitionDTO worldDefinitionDTO = dtoCreator.createWorldDefinitionDTO(worldDefinition);
        return worldDefinitionDTO;
    }
    public void addTerminateCondition(TerminateCondition terminateCondition, String simulationName,
                                      Integer requestNamber){
        if(worldDefinition.getTerminateConditions().size() > 0){
            createExistingWorld(simulationName);
        }
        worldDefinition.addTerminateCondition(terminateCondition);
        for(WorldDefinition worldDefinition1 : worldDefinitions){
            if(worldDefinition1.getName().equals(worldDefinition.getName()) &&
            worldDefinition1.getRequestNumber() == null){
                for(TerminateCondition terminateCondition1 : worldDefinition.getTerminateConditions()){
                    worldDefinition1.addTerminateCondition(terminateCondition1);
                    worldDefinition1.setRequestNumber(requestNamber);
                }
                break;
            }
        }
    }

    public void createThreadPool(int numberOfThread){

        //threadPool = Executors.newFixedThreadPool(numberOfThread);
        simulationsManager.createThreadPool(numberOfThread);
    }

    public void createHistory(ActiveEnvironmentDTO activeEnvironmentDTO,
                              List<PropertyInstanceDTO> propertyInstances, WorldDefinitionDTO worldDefinitionDTO){
        //TODO : instead here - do the createHistroy in the server (engine)
        List<EntityDefinitionDTO> population = new ArrayList<>();
        EntityDefinitionDTO newEntityDefinition;
        PropertyDefinitionDTO newPropertyDefinition = null;
        for(EntityDefinition entityDefinition : worldDefinition.getPopulation()){
            for(EntityDefinitionDTO entityDefinitionDTO : worldDefinitionDTO.getEntityDefinitionDTOS()){
                if(entityDefinitionDTO.getName().equals(entityDefinition.getName())){
                    entityDefinition.setStartPopulation(entityDefinitionDTO.getStartPopulation());
                    entityDefinition.setEndPopulation(entityDefinitionDTO.getEndPopulation());
                    break;
                }

            }
            newEntityDefinition = new EntityDefinitionDTO(entityDefinition.getName(),entityDefinition.getStartPopulation(), entityDefinition.getEndPopulation());
            for(PropertyDefinition propertyDefinition : entityDefinition.getProps()){
                if(propertyDefinition instanceof BooleanPropertyDefinition){
                        newPropertyDefinition = dtoCreator.createPropertyDefinitionDTO(propertyDefinition.getName(),
                                propertyDefinition.getType().name(),
                                null, null,"fixed", propertyDefinition.generateValue());

                }
                else if(propertyDefinition instanceof StringPropertyDefinition){
                        newPropertyDefinition =dtoCreator.createPropertyDefinitionDTO(propertyDefinition.getName(),
                                propertyDefinition.getType().name(),
                                null, null,"fixed", propertyDefinition.generateValue());

                }
                else if(propertyDefinition instanceof FloatPropertyDefinition){
                    newPropertyDefinition = dtoCreator.createPropertyDefinitionDTO(propertyDefinition.getName(),
                            propertyDefinition.getType().name(), String.valueOf(((FloatPropertyDefinition)propertyDefinition).getFrom()),
                            String.valueOf(((FloatPropertyDefinition)propertyDefinition).getTo()),"fixed", propertyDefinition.generateValue());
                }

                newEntityDefinition.addProperty(newPropertyDefinition);
            }
            population.add(newEntityDefinition);
        }
        //TODO : will get from the server
        simulationHistory = new SimulationHistoryDTO(activeEnvironmentDTO,propertyInstances, population, worldDefinition.getName());
    }

    public ActiveEnvironment createEnvVarible(ActiveEnvironmentDTO activeEnvironmentDTO){
        ActiveEnvironment activeEnvironment = new ActiveEnvironmentImpl();
        for(PropertyDefinition propertyDefinition : worldDefinition.getEnvironmentsVariables().getEnvVariables()){
            for(PropertyInstanceDTO propertyInstanceDTO : activeEnvironmentDTO.getPropertyInstanceDTOS()){
                if(propertyInstanceDTO.getPropertyDefinition().getName().equals(propertyDefinition.getName())){
                    PropertyInstance propertyInstance = new PropertyInstanceImpl(propertyDefinition,
                            propertyInstanceDTO.getValue());
                    activeEnvironment.addPropertyInstance(propertyInstance);
                }
            }
        }
        return activeEnvironment;
    }

    public ThreadPoolExecutor getThreadPool() {

        return simulationsManager.getThreadPool();
    }

    public SimulationDTO startSimulationInHistory(ActiveEnvironmentDTO activeEnvironmentDTO,
                                                  List<EntityDefinitionDTO> population, String userName, String simulationName) throws JAXBException {
        List<TerminateCondition> terminateConditions = new ArrayList<>();
        for(WorldDefinition worldDefinition1 : worldDefinitions) {
            if (worldDefinition1.getName().equals(simulationName)) {
               terminateConditions = worldDefinition1.getTerminateConditions();
                break;
            }
        }
        worldDefinition = GenerateXML.buildFromExistingWorld(simulationName);
        for(TerminateCondition terminateCondition : terminateConditions){
            worldDefinition.addTerminateCondition(terminateCondition);
        }
        ActiveEnvironment activeEnvironment = createEnvVarible(activeEnvironmentDTO);
        for(EntityDefinitionDTO entityDefinition : population){
            for(EntityDefinition entityDefinition1 : worldDefinition.getPopulation()){
                if(entityDefinition.getName().equals(entityDefinition1.getName())){
                    entityDefinition1.setStartPopulation(entityDefinition.getStartPopulation());
                    entityDefinition1.setEndPopulation(entityDefinition.getEndPopulation());
                }
            }
        }
        SimulationDTO simulationDTO = simulationsManager.startSimulation(activeEnvironment,worldDefinition,
                simulationHistory, userName);
        return simulationDTO;
    }

    public SimulationDTO startSimulation(ActiveEnvironmentDTO activeEnvironmentDTO, String userName){
        ActiveEnvironment activeEnvironment = createEnvVarible(activeEnvironmentDTO);
      /** worldInstance = new WorldInstance(worldDefinition, activeEnvironment);


       Simulation simulation = new Simulation(worldInstance,worldDefinition);
        simulationsManager.addSimulation(simulation);
        threadPool.execute(simulation);
        SimulationDTO simulationDTO1 = dtoCreator.createSimulationDTO(worldDefinition,simulation, simulation.getSimulationOutput());
        return simulationDTO1;*/
       SimulationDTO simulationDTO = simulationsManager.startSimulation(activeEnvironment,worldDefinition,
               simulationHistory, userName);
       return simulationDTO;
    }
    public void setSimulationHistory(SimulationHistoryDTO simulationHistory) {
        this.simulationHistory = simulationHistory;

    }

    public SimulationHistoryDTO getSimulationHistory(int id) {
        return simulationsManager.getSimulationById(id).getSimulationHistory();
    }

    public void pauseSimulation(int idSimulation) {
        simulationsManager.pauseSimulation(idSimulation);

    }
    public void resumeSimulation(int idSimulation) {
        simulationsManager.resumeSimulation(idSimulation);
    }
    public void stopSimulation(int idSimulation) {
        simulationsManager.stopSimulation(idSimulation);
    }

    public SimulationManagerDTO getSimulationManagerDTO() {
        /**this is will be not null - just after start simulation! it there isn't simulation
         * it will be null
         */
        if(simulationsManager.getSimulationList()!=null){
            simulationManagerDTO = dtoCreator.createManagerSimulationDTO(simulationsManager);
        }
        return simulationManagerDTO;
    }
    public SimulationDTO getCurrentRunningSimulationDTO(){
        return simulationsManager.getCurrentRunningSimulationDTO();
    }

    public PropertyInstanceDTO createPropertyInstance(PropertyDefinitionDTO propertyDefinition,
                                                      Object value){
        return dtoCreator.createPropertyInstanceDTO(propertyDefinition, value);

    }
    public void createEntityInstance(EntityDefinitionDTO entityDefinition, String population){
        List<PropertyInstanceDTO> propertyInstanceDTOS = new ArrayList<>();


    }
    private  boolean integerCheck(String input) {
        try {
            float integerValue = Integer.parseInt(input);
            return true;
        }
        catch (NumberFormatException e) {
            return false;
        }
    }
    private boolean integerInRange(String input, Object from, Object to) {
        try {
            int value = Integer.parseInt(input);
            return value >= (Integer) from && value <= (Integer) to;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    private void showHistogram(HistogramSimulationDTO histogramSimulationManger,
                                      String propertyName){
        Map<Object, Integer> histogram = histogramSimulationManger.getHistogram();
        if(!histogram.isEmpty()){
            System.out.println("Showing histogram for the property "+ propertyName);
            for (Map.Entry<Object, Integer> entry : histogram.entrySet()) {
                System.out.println("There are " + entry.getValue() + " entities that" +
                        " the " + propertyName + " value is "+ entry.getKey());
            }
        }

    }


    public List<SimulationDTO> getSimulationList(){

        return simulationManagerDTO.getSimulationList();
    }

    public HistogramSimulationDTO createHistogramForSimulation(HistogramSimulationManger histogramSimulationManger) {
        return dtoCreator.createHistogramForSimulation(histogramSimulationManger);
    }

    public SimulationsManager getSimulationsManager() {
        return simulationsManager;
    }
}
