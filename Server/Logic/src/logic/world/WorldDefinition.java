package logic.world;

import logic.definition.entity.api.EntityDefinition;
import logic.definition.environment.api.EnvVariablesManger;
import logic.definition.environment.impl.EnvVariableManagerImpl;
import logic.definition.property.api.PropertyDefinition;
import logic.rule.Rule;
import logic.terminateCondition.TerminateCondition;

import java.util.*;

public class WorldDefinition {
    private List<EntityDefinition> population;
    private Integer requestNumber;
    private int ticks;
    private int numberOfThreads;
    private int rows;
    private int columns;

    private List<TerminateCondition> terminateConditions;

    private static WorldDefinition instance;

    private EnvVariablesManger environmentsVariables = new EnvVariableManagerImpl();

    private List<Rule> rules;
    private String name;
    private int sleep;

    public void setRequestNumber(int requestNumber) {
        this.requestNumber = requestNumber;
    }

    public Integer getRequestNumber() {
        return requestNumber;
    }

    public void initialize(){
        ticks = 0;
        this.terminateConditions = new ArrayList<>();
        for(EntityDefinition entityDefinition : population){
            entityDefinition.setStartPopulation(0);
            entityDefinition.setEndPopulation(0);
        }
    }

    public WorldDefinition(){
        ticks = 0;
        population = new ArrayList<>();
        this.terminateConditions = new ArrayList<>();
        rules = new ArrayList<>();

    }
    public WorldDefinition(int rows, int columns){
        this();
        this.rows = rows;
        this.columns = columns;
    }

    public int getSleep() {
        return sleep;
    }

    public void setSleep(int sleep) {
        this.sleep = sleep;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setColumns(int columns) {
        this.columns = columns;
    }

    public void setRows(int rows) {
        this.rows = rows;
    }

    public int getRows() {
        return rows;
    }

    public int getColumns() {
        return columns;
    }

    public void setEnvironmentsVariables(EnvVariablesManger envVariablesManger){
        this.environmentsVariables = envVariablesManger;
    }

    public int getNumberOfThreads() {
        return numberOfThreads;
    }

    public void setNumberOfThreads(int numberOfThreads) {
        this.numberOfThreads = numberOfThreads;
    }

    public int getTicks() {
        return ticks;
    }

    public List<Rule> getRules() {
        return rules;
    }

    public Collection<EntityDefinition> getPopulation() {
        return population;
    }

    public  void addEntity(EntityDefinition entity){
        population.add(entity);
    }

    public void addTerminateCondition(TerminateCondition terminateCondition){
        if(terminateConditions.size() > 0){

        }
        terminateConditions.add(terminateCondition);
    }

    public void addRule(Rule rule){
        rules.add(rule);
    }

    public static WorldDefinition getInstance() {
        if (instance == null) {
            instance = new WorldDefinition();
        }
        return instance;
    }

    public PropertyDefinition findEnvironmentsVariables(String propertyName) throws NoSuchElementException {
        for(PropertyDefinition p : environmentsVariables.getEnvVariables()){
            if(p.getName().equals(propertyName)){
                return p;
            }
        }
        throw new NoSuchElementException("The environment variable name " +
                propertyName + " doesn't exists");
    }

    public List<TerminateCondition> getTerminateConditions() {
        return terminateConditions;
    }

    public EnvVariablesManger getEnvironmentsVariables(){
        return environmentsVariables;
    }

    public void searchForZeroEntity(){
        population.removeIf(entity -> entity.getStartPopulation() == 0);
    }

    public EntityDefinition getEntityByName(String entityName) {
        for(EntityDefinition entityDefinition : population){
            if(entityDefinition.getName().equals(entityName)){
                return entityDefinition;
            }
        }
        throw new NoSuchElementException("The entity name " + entityName + " doesn't exist");
    }
}
