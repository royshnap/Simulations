package logic.simulation;

import logic.definition.entity.api.EntityDefinition;
import logic.definition.property.api.PropertyDefinition;
import dto.*;
import logic.execution.context.ContextImpl;
import logic.execution.instance.entity.api.EntityInstance;
import logic.execution.instance.property.api.PropertyInstance;
import logic.rule.Rule;
import logic.rule.action.api.Action;
import logic.rule.action.api.ActionType;
import logic.terminateCondition.TerminateByBoth;
import logic.terminateCondition.TerminateBySeconds;
import logic.terminateCondition.TerminateByTicks;
import logic.terminateCondition.TerminateCondition;
import logic.world.WorldDefinition;
import logic.world.WorldInstance;

import java.util.*;
import java.util.concurrent.CountDownLatch;
import java.util.stream.Collectors;

public class Simulation implements Runnable {

    private int id;
    private  Date date;
    private WorldInstance worldInstance;
    private SimulationDTO simulationDTO;

    private WorldDefinition worldDefinition;
    private SimulationOutput simulationOutput;
    private HistogramSimulationManger histogramSimulationManger;
    private volatile boolean endSimulation = false;
    private boolean isPause = false;
    private final CountDownLatch simulationEndedLatch;
    private SimulationCurrentDetailsDTO currentDetailsDTO;
    private Thread simulationThread;
    private  Map<Integer, List<Integer>> amountOfEntitiesByTicks = new HashMap<>();
    private int ticks = 0;
    private SimulationHistoryDTO simulationHistory;
    private boolean isRunning = false;
    private Object pauseLock = new Object();
    private DTOCreator dtoCreator = new DTOCreator();
    private String userName;

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public void setSimulationHistory(SimulationHistoryDTO simulationHistory) {
        this.simulationHistory = simulationHistory;
    }

    public SimulationHistoryDTO getSimulationHistory() {
        return simulationHistory;
    }

    public void setPause(boolean pause) {
        isPause = pause;
    }

    public boolean isPause() {
        return isPause;
    }

    public Thread getSimulationThread() {
        return simulationThread;
    }
    public void pauseSimulation(){
        isPause = true;
    }
    public void resumeSimulation(){
        isPause = false;
        synchronized (pauseLock) {
            pauseLock.notify();
        }

    }
    public void stopSimulation(){
        resumeSimulation();
        simulationThread.interrupt();
    }

    public SimulationOutput getSimulationOutput() {
        return simulationOutput;
    }

    //public Simulation(){};
    public Simulation(WorldInstance worldInstance, WorldDefinition worldDefinition){
        this.worldInstance = worldInstance;
        this.worldDefinition = worldDefinition;
        simulationEndedLatch = new CountDownLatch(1);



    }


    public HistogramSimulationManger getHistogramSimulationManger() {
        return histogramSimulationManger;
    }

    public WorldInstance getWorldInstance() {
        return worldInstance;
    }

    public WorldDefinition getWorldDefinition() {
        return worldDefinition;
    }

    private void createDetails(int ticks, int seconds, Collection<EntityDefinition> entityDefinitions){
        Map<String, Integer> amountOfEntities = new HashMap<>();
        for(EntityDefinition entityDefinition : entityDefinitions){
            amountOfEntities.put(entityDefinition.getName(), entityDefinition.getEndPopulation());
        }
        List<Integer> amountOfEntityList = new ArrayList<>();
        for(EntityDefinition entityDefinition : entityDefinitions){
            amountOfEntityList.add(entityDefinition.getEndPopulation());
        }
        amountOfEntitiesByTicks.put(ticks, amountOfEntityList);
        currentDetailsDTO = new SimulationCurrentDetailsDTO(ticks, seconds, amountOfEntities, amountOfEntitiesByTicks);

    }

    public SimulationCurrentDetailsDTO getCurrentDetailsDTO() {
        return currentDetailsDTO;
    }

    public void setEndSimulation(boolean endSimulation) {
        this.endSimulation = endSimulation;
    }

    public boolean isRunning() {
        return isRunning;
    }

    public void run() {
        simulationDTO = dtoCreator.createSimulationDTO(worldDefinition, this, simulationOutput, userName, worldDefinition.getName());
        createDetails(0, 0, worldInstance.getWorldDefinition().getPopulation());
        simulationDTO.setCurrentDetailsDTO(currentDetailsDTO);
        isRunning = true;
        simulationDTO.setRunning(isRunning);
        endSimulation = false;
        simulationOutput = new SimulationOutput(id, worldInstance);
        worldInstance.initialize();
        List<Rule> rules = worldInstance.getRules();
        ticks = 0;
        long startTime = System.currentTimeMillis();
        long currentTime = System.currentTimeMillis();
        long elapsedTimeMillis = currentTime - startTime;
        int elapsedSeconds = (int) (elapsedTimeMillis / 1000);
        while (!endSimulation) {
            endSimulation();
            if (isPause) {
                synchronized (pauseLock) {
                    try {
                        pauseLock.wait(); // Wait until resumed
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                    }
                }
            }
            simulationThread = Thread.currentThread();
//            isSimulationPaused();
            worldInstance.setTicks(++ticks);
            worldInstance.getGrid().moveAllCellsRandomly();//should move all the cells every tick
            currentTime = System.currentTimeMillis();
            elapsedTimeMillis = currentTime - startTime;
            elapsedSeconds = (int) (elapsedTimeMillis / 1000);
            worldInstance.setSeconds(elapsedSeconds);
            for (Rule rule : rules) {
                if (rule.getActivation().isActive(ticks)) {
                    List<Action> firstActions = new ArrayList<>();
                    List<Action> endActions = new ArrayList<>();
                    for (Action action : rule.getActionsToPerform()) {
                        if (action.getActionType().equals(ActionType.KILL) || action.getActionType()
                                .equals(ActionType.REPLACE) || action.getActionType().equals(ActionType.CONDITION)
                                || action.getActionType().equals(ActionType.PROXIMITY)) {
                            endActions.add(action);
                        } else {
                            firstActions.add(action);
                        }
                    }
                    for (EntityInstance primaryEntityInstance : new ArrayList<EntityInstance>(worldInstance.getPopulation())) {
                        for (Action action : firstActions) {
                            actionOnticks(action, primaryEntityInstance, ticks);
                        }
                        for (Action action : endActions) {
                            actionOnticks(action, primaryEntityInstance, ticks);

                        }
                    }
                }

                createDetails(ticks, elapsedSeconds, worldInstance.getWorldDefinition().getPopulation());
                simulationDTO.setCurrentDetailsDTO(currentDetailsDTO);
            }
            //endSimulation = true;
            simulationEndedLatch.countDown();

        }
        simulationDTO = dtoCreator.createSimulationDTO(worldDefinition, this, simulationOutput, userName, worldDefinition.getName());
    }

    public SimulationDTO getSimulationDTO() {
        return simulationDTO;
    }

    private void actionOnticks(Action action, EntityInstance primaryEntityInstance, int ticks){
        boolean isVisit = false;
        if(action.getContextDefinition().getPrimaryEntity().getName().equals(primaryEntityInstance.getEntityDefinition().getName())){
            if(!action.getActionType().equals(ActionType.REPLACE) &&
            !action.getActionType().equals(ActionType.PROXIMITY)){
                int i = 0;
                if(action.getContextDefinition().getSecondaryEntity() != null){
                    String count = action.getContextDefinition().getAmountOfSecondaryEntities();
                    if(count.equalsIgnoreCase("ALL")){
                        for(EntityInstance secondEntityInstance : new ArrayList<EntityInstance>(worldInstance.getPopulation())){
                            if(secondEntityInstance.getEntityDefinition().getName().equals(action.getContextDefinition().getSecondaryEntity().getName())){
                                i++;
                                action.invoke(new ContextImpl(primaryEntityInstance,secondEntityInstance, worldInstance.getEntityInstanceManager(),
                                        worldInstance.getEnvironmentsVariables(), worldInstance.getGrid(), ticks, i));
                            }
                        }

                    }
                    else {
                        i = 0;
                        int countInt = Integer.parseInt(count);
                        List<EntityInstance> filteredList = new ArrayList<EntityInstance>(worldInstance.getPopulation()).stream()
                                .filter(entity -> entity.getEntityDefinition().getName().equals(action.getContextDefinition().getSecondaryEntity().getName()))
                                .filter(entity ->action.getContextDefinition().getConditionOfSecondary()==null ||action.getContextDefinition().getConditionOfSecondary().
                                        evaluate(new ContextImpl(primaryEntityInstance,entity, worldInstance.getEntityInstanceManager(),
                                                worldInstance.getEnvironmentsVariables(), worldInstance.getGrid(), ticks)))
                                .collect(Collectors.toList());
                        Collections.shuffle(filteredList, new Random());

                        List<EntityInstance> chosenSecondaryEntities = filteredList.stream()
                                .limit(Math.min(countInt, filteredList.size()))
                                .collect(Collectors.toList());
                        for(EntityInstance secondEntityInstance : new ArrayList<EntityInstance>(chosenSecondaryEntities)){
                            if(secondEntityInstance.getEntityDefinition().getName().equals(action.getContextDefinition().getSecondaryEntity().getName())){
                                i++;
                                action.invoke(new ContextImpl(primaryEntityInstance,secondEntityInstance, worldInstance.getEntityInstanceManager(),
                                        worldInstance.getEnvironmentsVariables(), worldInstance.getGrid(), ticks, i));
                            }
                        }


                    }
                }
                else{ i=0;
                    i++;
                    action.invoke(new ContextImpl(primaryEntityInstance,null,worldInstance.getEntityInstanceManager(),
                            worldInstance.getEnvironmentsVariables(), worldInstance.getGrid(), ticks, i));
                }
            }
            else{
                if(action.getContextDefinition().getSecondaryEntity() != null){
                    int i = 0;
                    for(EntityInstance secondEntityInstance : new ArrayList<EntityInstance>(worldInstance.getPopulation())){
                        if(secondEntityInstance.getEntityDefinition().getName().equals(action.getContextDefinition().getSecondaryEntity().getName())){
                            i++;
                            if(i==1 && action.getActionType().equals(ActionType.REPLACE)){
                                action.invoke(new ContextImpl(primaryEntityInstance,secondEntityInstance, worldInstance.getEntityInstanceManager(),
                                        worldInstance.getEnvironmentsVariables(), worldInstance.getGrid(), ticks));

                            }
                            else if(action.getActionType().equals(ActionType.PROXIMITY)){
                                action.invoke(new ContextImpl(primaryEntityInstance,secondEntityInstance, worldInstance.getEntityInstanceManager(),
                                        worldInstance.getEnvironmentsVariables(), worldInstance.getGrid(), ticks, i));
                            }

                        }
                    }


                }
                else{ int i=0;
                    i++;
                    action.invoke(new ContextImpl(primaryEntityInstance,null,worldInstance.getEntityInstanceManager(),
                            worldInstance.getEnvironmentsVariables(), worldInstance.getGrid(), ticks,i));
                }
            }

        }
    }
    public boolean getEndSimulation(){
        return endSimulation;
    }


    public HistogramSimulationManger buildHistogramForSimulation(String entityName, String propertyName){
        histogramSimulationManger = new HistogramSimulationManger();
        EntityDefinition entityDefinition = worldInstance.getWorldDefinition().getEntityByName(entityName);
        PropertyDefinition propertyDefinition = entityDefinition.getPropertyDefinitionByName(propertyName);
        for (EntityInstance entityInstance :new ArrayList<>(worldInstance.getEntityInstanceManager().getInstances())) {
            if (entityInstance.getEntityDefinition().getName().equals(entityName)) {
                PropertyInstance propertyInstance = entityInstance.getPropertyByName(propertyName);
                Object value = propertyInstance.getValue();
                propertyInstance.setConsistency(ticks);
                histogramSimulationManger.setConsistency(propertyInstance.getConsistency());
                if (histogramSimulationManger.getHistogram().containsKey(value)) {
                    histogramSimulationManger.getHistogram().put(value, histogramSimulationManger.getHistogram().get(value) + 1);
                } else {
                    histogramSimulationManger.getHistogram().put(value, 1);
                }
            }
        }

        return histogramSimulationManger;

}

    public void endSimulation(){
        endSimulation = false;
        simulationDTO.setEndSimualtion(endSimulation);
        simulationOutput.setReasonsOfEnding("The simulation was ended because user stop it");
       if(Thread.currentThread().isInterrupted()){
           endSimulation = true;
           simulationDTO.setEndSimualtion(endSimulation);
           return;
       }
       List<TerminateCondition> terminateConditions = worldInstance.getTerminateConditions();
       for(TerminateCondition terminateCondition : terminateConditions){
           if(terminateCondition instanceof TerminateByBoth){
               if(terminateCondition.getCount() <= worldInstance.getTicks() &&
                       terminateCondition.getSeconds() <= worldInstance.getSeconds()){
                   simulationOutput.setReasonsOfEnding("the terminate condition by both" +
                           " was invoked");
                   endSimulation = true;
                   simulationDTO.setEndSimualtion(endSimulation);
                   return;
               }
           }
           else if(terminateCondition instanceof TerminateByTicks){
               if(terminateCondition.getCount() == worldInstance.getTicks()){
                   simulationOutput.setReasonsOfEnding("the terminate condition by ticks" +
                           " was invoked");
                   endSimulation = true;
                   simulationDTO.setEndSimualtion(endSimulation);
                   return;
               }
           }
           else if(terminateCondition instanceof TerminateBySeconds){
               if(worldInstance.getSeconds() >= terminateCondition.getCount()){
                   simulationOutput.setReasonsOfEnding("the terminate condition by seconds" +
                           " was invoked");
                   endSimulation = true;
                   simulationDTO.setEndSimualtion(endSimulation);
                   return;
               }
           }

       }
    }

    public void setId(int id){
        this.id = id;
    }

    public int getId(){
        return id;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public Date getDate() {
        return date;
    }

}
