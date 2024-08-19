package com;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import java.io.File;
import java.io.InputStream;
import java.util.*;

import com.bean.*;
import logic.Expression.api.AbstractExpression;
import logic.Expression.impl.HelperFunctionExpression;
import logic.Expression.impl.PropertyNameExpression;
import logic.rule.Rule;
import logic.Expression.api.Expression;
import logic.Expression.impl.NumberExpression;
import logic.rule.action.api.Action;
import logic.rule.action.api.ActionType;
import logic.rule.action.impl.Decrease;
import logic.rule.action.impl.Increase;
import logic.rule.action.impl.calculation.Calculation;
import logic.rule.action.impl.calculation.Divide;
import logic.rule.action.impl.calculation.Multiply;
import logic.rule.action.impl.condition.ActionCondition;
import logic.rule.action.impl.condition.Condition;
import logic.simulation.SimulationBuilder;
import logic.world.WorldDefinition;

public class GenerateXML {
    private static SimulationBuilder builder = new SimulationBuilder();
    private static List<PRDWorld> worlds;

    public static WorldDefinition buildFromExistingWorld(String worldName){
        //tODO:dibug this
        for(PRDWorld prdWorld : worlds){
           if(prdWorld.getName().equals(worldName)){
               return builder.buildWorld(prdWorld);
           }
        }

        return null;
    }

    public static WorldDefinition fromXmlFileToObject(String fileName, InputStream fileContent) throws JAXBException, IllegalArgumentException {
        File file = new File(fileName);

        String extension = getFileExtension(fileName);
        if (!extension.equalsIgnoreCase("xml")) {
            throw new IllegalArgumentException("File is not an XML file: " + fileName);
        }
        if(worlds == null){
            worlds = new ArrayList<>();
        }
        JAXBContext jaxbContext = JAXBContext.newInstance(PRDWorld.class);

        Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();
        worlds.add((PRDWorld) jaxbUnmarshaller.unmarshal(fileContent));
        checkXML(worlds.get(worlds.size()-1));
        WorldDefinition worldDefinition =  builder.buildWorld(worlds.get(worlds.size()-1));
        checkIfArgumentIsNumber(worldDefinition.getRules());
        return worldDefinition;
    }

    private static String getFileExtension(String fileName) {
        int dotIndex = fileName.lastIndexOf(".");
        if (dotIndex == -1) {
            return "";
        }
        return fileName.substring(dotIndex + 1);
    }

    private static void checkXML(PRDWorld world) throws IllegalArgumentException{
        checkDuplicateEnvironmentNames(world.getPRDEnvironment().getPRDEnvProperty());
        checkDuplicateProperties(world.getPRDEntities().getPRDEntity());
        checkIfEntityFromRuleExistsInWorld(world.getPRDRules().getPRDRule(),world.getPRDEntities().getPRDEntity());
        checkIfPropertyFromRuleExistsInWorld(world.getPRDRules().getPRDRule(),world.getPRDEntities().getPRDEntity());
        checkIfRowsAndColsInRange(world.getPRDGrid().getRows(), world.getPRDGrid().getColumns());
    }

    private static void checkIfRowsAndColsInRange(int rows, int cols) throws IllegalArgumentException{
        if(rows < 10 || rows > 100) {
            throw new IllegalArgumentException("The number of rows: " + rows + " should be between 10 and 100");
        }
        if(cols < 10 || cols > 100){
            throw new IllegalArgumentException("The number of columns: " + cols + " should be between 10 and 100");
        }
    }
    public static void checkIfArgumentIsNumber(List<Rule> rules) throws IllegalArgumentException{
        for(Rule rule : rules){
            for(Action action : rule.getActionsToPerform()){
                if (action.getActionType().name().equals("INCREASE")){
                    Increase increase = (Increase) action;
                    Expression byExpression = AbstractExpression.valueExpressionByString(increase.getByExpression(), increase.getContextDefinition().getPrimaryEntity());
                    if (!(byExpression instanceof NumberExpression || byExpression instanceof HelperFunctionExpression || byExpression instanceof PropertyNameExpression)) {
                        throw new IllegalArgumentException("Increase action has non-numeric argument");
                    }
                }
                else if (action.getActionType().name().equals("DECREASE")){
                    Decrease decrease = (Decrease) action;
                    Expression byExpression = AbstractExpression.valueExpressionByString(decrease.getByExpression(), decrease.getContextDefinition().getPrimaryEntity());
                    if (!(byExpression instanceof NumberExpression || byExpression instanceof HelperFunctionExpression || byExpression instanceof PropertyNameExpression)) {
                        throw new IllegalArgumentException("Decrease action has non-numeric argument");
                    }
                }
                else if(action.getActionType().name().equals("MULTIPLY")){
                    Multiply multiply = (Multiply) action;
                    Expression arg1Exp = AbstractExpression.valueExpressionByString(multiply.getArgument1(), multiply.getContextDefinition().getPrimaryEntity());
                    Expression arg2Exp = AbstractExpression.valueExpressionByString(multiply.getArgument2(), multiply.getContextDefinition().getPrimaryEntity());
                    if (!(arg1Exp instanceof NumberExpression || arg1Exp instanceof HelperFunctionExpression || arg1Exp instanceof PropertyNameExpression)) {
                        throw new IllegalArgumentException("Multiply action has non-numeric argument");
                    }
                    if (!(arg2Exp instanceof NumberExpression || arg2Exp instanceof HelperFunctionExpression || arg2Exp instanceof PropertyNameExpression)) {
                        throw new IllegalArgumentException("Multiply action has non-numeric argument");
                    }
                }
                else if(action.getActionType().name().equals("DIVIDE")){
                    Divide divide = (Divide) action;
                    Expression arg1Exp = AbstractExpression.valueExpressionByString(divide.getArgument1(), divide.getContextDefinition().getPrimaryEntity());
                    Expression arg2Exp = AbstractExpression.valueExpressionByString(divide.getArgument2(), divide.getContextDefinition().getPrimaryEntity());
                    if (!(arg1Exp instanceof NumberExpression || arg1Exp instanceof HelperFunctionExpression || arg1Exp instanceof PropertyNameExpression)) {
                        throw new IllegalArgumentException("Divide action has non-numeric argument");
                    }
                    if (!(arg2Exp instanceof NumberExpression || arg2Exp instanceof HelperFunctionExpression || arg2Exp instanceof PropertyNameExpression)) {
                        throw new IllegalArgumentException("Divide action has non-numeric argument");
                    }
                }
                else if(action.getActionType().name().equals("CALCULATION")){
                    Calculation calculation = (Calculation) action;
                    Expression arg1Exp = AbstractExpression.valueExpressionByString(calculation.getArgument1(), calculation.getContextDefinition().getPrimaryEntity());
                    Expression arg2Exp = AbstractExpression.valueExpressionByString(calculation.getArgument2(), calculation.getContextDefinition().getPrimaryEntity());
                    if (!(arg1Exp instanceof NumberExpression || arg1Exp instanceof HelperFunctionExpression || arg1Exp instanceof PropertyNameExpression)) {
                        throw new IllegalArgumentException("Calculation action has non-numeric argument");
                    }
                    if (!(arg2Exp instanceof NumberExpression || arg2Exp instanceof HelperFunctionExpression || arg2Exp instanceof PropertyNameExpression)) {
                        throw new IllegalArgumentException("Calculation action has non-numeric argument");
                    }
                }
//                else if(action.getActionType().name().equals("CONDITION")){
//                    break;
//                }
//                else if(action.getActionType().name().equals("PROXIMITY")){
//                    break;
//                }
            }
        }

    }

    private static void checkIfEntityFromRuleExistsInWorld(List<PRDRule> rules, Collection<PRDEntity> entities) throws IllegalArgumentException{
        boolean isFound = false;
        List<String> entitiesNames = new ArrayList<>();
        for(PRDEntity entity : entities)
        {
            entitiesNames.add(entity.getName());
        }
        for(PRDRule rule : rules){
            for(PRDAction action : rule.getPRDActions().getPRDAction()){
                isFound = false;
                if (action.getType().equals("condition")) {
                    if(checkIfEntityInCondition(action, entitiesNames)) {
                        isFound = true;
                    }
                }
                else if (action.getType().equals("proximity")){
                    if(checkIfEntityInProximity(action, entitiesNames)){
                        isFound = true;
                    }
                }
                else if (action.getType().equals("replace")){
                    if(entitiesNames.contains(action.getKill()) && entitiesNames.contains(action.getCreate())) {
                        isFound = true;
                    }
                }
                else if(entitiesNames.contains(action.getEntity())){
                    isFound = true;
                }
                else{
                    isFound = false;
                }
                if(!isFound){
                    throw new IllegalArgumentException("There is entity " + " that doesn't exists");
//                    throw new IllegalArgumentException("The entity " + action.getEntity() + " doesn't " +
//                            "exists");
                }
            }
        }
    }

    private static boolean checkIfEntityInCondition(PRDAction action, List<String> entitiesNames)
    {
        boolean entityInCond = false;
        for (PRDCondition condition : action.getPRDCondition().getPRDCondition()) {
            if (entitiesNames.contains(condition.getEntity())) {
                entityInCond = true;
            }
        }
        for (PRDAction action1 : action.getPRDThen().getPRDAction()) {
            entityInCond = false;
            if (action1.getType().equals("replace")) {
                if (entitiesNames.contains(action1.getKill()) && entitiesNames.contains(action1.getCreate())) {
                    entityInCond = true;
                }
            } else {
                if (entitiesNames.contains(action1.getEntity())) {
                    entityInCond = true;
                }
            }
        }
        if (action.getPRDElse() != null){
            for (PRDAction action1 : action.getPRDElse().getPRDAction()) {
                entityInCond = false;
                if (action1.getType().equals("replace")) {
                    if (entitiesNames.contains(action1.getKill()) && entitiesNames.contains(action1.getCreate())) {
                        entityInCond = true;
                    }
                } else {
                    if (entitiesNames.contains(action1.getEntity())) {
                        entityInCond = true;
                    }
                }
            }
        }
        return entityInCond;
    }

    private static boolean checkIfEntityInProximity(PRDAction action, List<String> entitiesNames){
        boolean entityInProximity = false;
        if(entitiesNames.contains(action.getPRDBetween().getSourceEntity()) && entitiesNames.contains(action.getPRDBetween().getTargetEntity())) {
            for(PRDAction action1 : action.getPRDActions().getPRDAction()){
                entityInProximity = false;
                if (action1.getType().equals("replace")) {
                    if (entitiesNames.contains(action1.getKill()) && entitiesNames.contains(action1.getCreate())){
                        entityInProximity = true;
                    }
                }
                else if (entitiesNames.contains(action1.getEntity())) {
                    entityInProximity = true;
                }
            }
        }
        return entityInProximity;
    }


    private static boolean isEntityPropertyExist(Collection<PRDEntity> entities, String entityName, String propertyName) {
        for(PRDEntity entity: entities) {
            if(entity.getName().equals(entityName)) {
                for(PRDProperty property: entity.getPRDProperties().getPRDProperty()) {
                    if(property.getPRDName().equals(propertyName)) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    private static void checkIfPropertyFromRuleExistsInWorld(List<PRDRule> rules, Collection<PRDEntity> entities) throws IllegalArgumentException {
        for(PRDRule rule: rules) {
            for(PRDAction action: rule.getPRDActions().getPRDAction()) {
                if(action.getProperty() !=null && !isEntityPropertyExist(entities, action.getEntity(), action.getProperty())) {
                    throw new IllegalArgumentException("The action refers to a property who doesn't exist");
                } else if (action.getResultProp() != null && !isEntityPropertyExist(entities, action.getEntity(), action.getResultProp())) {
                    throw new IllegalArgumentException("The action refers to a property who doesn't exist");
                }
            }
        }
    }

    private static void checkDuplicateEnvironmentNames(List<PRDEnvProperty> environments) throws IllegalArgumentException {
        Set<String> environmentNames = new HashSet<>();
        for (PRDEnvProperty environment : environments) {
            if (!environmentNames.add(environment.getPRDName())) {
                throw new IllegalArgumentException("Duplicate environment name found: " + environment.getPRDName());
            }
        }
    }

    private static void checkDuplicateProperties(Collection<PRDEntity> entities) throws IllegalArgumentException {
        Set<String> propertiesNames = new HashSet<>();
        for (PRDEntity entity : entities) {
            propertiesNames = new HashSet<>();
            for (PRDProperty property : entity.getPRDProperties().getPRDProperty()) {
                if (!propertiesNames.add(property.getPRDName())) {
                    throw new IllegalArgumentException("Duplicate property name found: " + property.getPRDName());
                }
            }
        }
    }
}
