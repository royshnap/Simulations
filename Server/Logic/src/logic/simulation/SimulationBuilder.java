package logic.simulation;
import com.bean.*;
import logic.Expression.impl.*;
import logic.definition.context.ContextDefinition;
import logic.definition.context.ContextDefinitionImpl;
import logic.definition.entity.api.EntityDefinition;
import logic.definition.entity.impl.EntityDefinitionImpl;
import logic.definition.environment.api.EnvVariablesManger;
import logic.definition.environment.impl.EnvVariableManagerImpl;
import logic.definition.property.api.PropertyDefinition;
import logic.definition.property.api.PropertyType;
import logic.definition.property.impl.BooleanPropertyDefinition;
import logic.definition.property.impl.FloatPropertyDefinition;
import logic.definition.property.impl.IntegerPropertyDefinition;
import logic.definition.property.impl.StringPropertyDefinition;
import logic.definition.value.api.ValueGeneratorFactory;
import logic.definition.value.fixed.FixedValueGenerator;
import logic.rule.Rule;
import logic.rule.RuleImpl;
import logic.Expression.api.Expression;
import logic.rule.action.api.AbstractAction;
import logic.rule.action.api.Action;
import logic.rule.action.api.ActionType;
import logic.rule.action.impl.*;
import logic.rule.action.impl.calculation.Divide;
import logic.rule.action.impl.calculation.Multiply;
import logic.rule.action.impl.condition.*;
import logic.rule.helperFunction.impl.EnvironmentFunction;
import logic.rule.helperFunction.impl.RandomFunction;
import logic.terminateCondition.TerminateBySeconds;
import logic.terminateCondition.TerminateByTicks;
import logic.terminateCondition.TerminateByUser;
import logic.terminateCondition.TerminateCondition;
import logic.world.WorldDefinition;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class SimulationBuilder {
    /**this class build the simulation definition - from it, we will produce the instance*/

    public WorldDefinition buildWorld(PRDWorld world){
        WorldDefinition worldDefinition = new WorldDefinition();
        //worldDefinition.setNumberOfThreads(world.getPRDThreadCount());
        //List<TerminateCondition> terminateConditions = buildTerminateCondition
                //(world.getPRDTermination().getPRDBySecondOrPRDByTicks());
        //for(TerminateCondition terminateCondition : terminateConditions){
            //worldDefinition.addTerminateCondition(terminateCondition);
        //}
        worldDefinition.setName(world.getName());
        worldDefinition.setSleep(world.getSleep());
        List<EntityDefinition> entities = buildEntityDefinition
                (world.getPRDEntities().getPRDEntity());
        for(EntityDefinition entityDefinition : entities){
            worldDefinition.addEntity(entityDefinition);
        }
        List<Rule> rules = buildRule(world.getPRDRules().getPRDRule(), entities);
        for(Rule rule : rules){
            worldDefinition.addRule(rule);
        }
        EnvVariablesManger environmentVariables = buildEnvVariableManager(world.getPRDEnvironment().getPRDEnvProperty());
        worldDefinition.setEnvironmentsVariables(environmentVariables);
        worldDefinition.setRows(world.getPRDGrid().getRows());
        worldDefinition.setColumns(world.getPRDGrid().getColumns());
        return worldDefinition;
    }

    public EnvVariablesManger buildEnvVariableManager(List<PRDEnvProperty> envProperties){
        EnvVariablesManger envVariablesManger = new EnvVariableManagerImpl();
        PropertyDefinition result;
        for(PRDEnvProperty envProperty: envProperties){
            if(envProperty.getType().equals("decimal")){
                result = new IntegerPropertyDefinition(envProperty.getPRDName(),
                        PropertyType.DECIMAL,new FixedValueGenerator<Integer>(0),
                        ((Double)envProperty.getPRDRange().getFrom()).intValue(), ((Double)envProperty.getPRDRange().getTo()).intValue());

            }
            else if(envProperty.getType().equals("float")){
                result = new FloatPropertyDefinition(envProperty.getPRDName(),
                        PropertyType.FLOAT,new FixedValueGenerator<Float>(new Float(1.0)),
                        ((Double) envProperty.getPRDRange().getFrom()).floatValue(),
                        ((Double) envProperty.getPRDRange().getTo()).floatValue());
            }
            else if(envProperty.getType().equals("boolean")){
                result = new BooleanPropertyDefinition(envProperty.getPRDName(),
                        PropertyType.BOOLEAN, new FixedValueGenerator<>(false));
            }
            else{
                result = new StringPropertyDefinition(envProperty.getPRDName(),
                        PropertyType.STRING, new FixedValueGenerator<>(""));
            }
            envVariablesManger.addEnvironmentVariable(result);
        }
        return envVariablesManger;
    }

    private List<Rule> buildRule(List<PRDRule> rules, List<EntityDefinition> entities){
        List<Rule> rules1 = new ArrayList<>();
        Rule rule1 = null;
        for(PRDRule rule : rules){
            if(rule.getPRDActivation() == null){
                rule1 = new RuleImpl(rule.getName(), 1, 1);
            }
            else if(rule.getPRDActivation().getProbability() != null && rule.getPRDActivation().getTicks()!=null){
                rule1 = new RuleImpl(rule.getName(), rule.getPRDActivation().getTicks(),
                        rule.getPRDActivation().getProbability());
            }
            else if(rule.getPRDActivation().getTicks() != null){
                rule1 = new RuleImpl(rule.getName(), rule.getPRDActivation().getTicks(),
                        1);
            }
            else if(rule.getPRDActivation().getProbability() != null){
                rule1 = new RuleImpl(rule.getName(), 1,
                        rule.getPRDActivation().getProbability());
            }


            for(PRDAction action : rule.getPRDActions().getPRDAction()){
                ContextDefinition contextDefinition = buildContextDefinition(action, entities);

                Action action1 = buildAction(contextDefinition,action, entities);
                rule1.addAction(action1);
            }
            rules1.add(rule1);
        }
        return rules1;
    }

    private ContextDefinition buildContextDefinition(PRDAction action, List<EntityDefinition> entities){
        EntityDefinition primaryEntity = null;
        EntityDefinition secondaryEntity = null;
        if(action.getEntity() == null){
            if(action.getPRDBetween() != null){
                primaryEntity = getEntityDefinition(action.getPRDBetween().getSourceEntity(), entities);
                secondaryEntity = getEntityDefinition(action.getPRDBetween().getTargetEntity(), entities);
            }
            else if(action.getKill() != null){
                primaryEntity = getEntityDefinition(action.getKill(), entities);
                secondaryEntity = getEntityDefinition(action.getCreate(), entities);
            }
            else {
                throw new RuntimeException("The file doesn't good");
            }
        }
        else{
            primaryEntity = getEntityDefinition(action.getEntity(), entities);
        }


        String amountOfSeconderyEntites = null;
        Condition conditionForSecondery = null;
        if(action.getPRDSecondaryEntity() != null){
           secondaryEntity = getEntityDefinition(
                    action.getPRDSecondaryEntity().getEntity(), entities);
            amountOfSeconderyEntites = action.getPRDSecondaryEntity().getPRDSelection().getCount();
            conditionForSecondery = buildCondition(action.getPRDSecondaryEntity().getPRDSelection().getPRDCondition(), entities);
        }
        return new ContextDefinitionImpl(primaryEntity, secondaryEntity, amountOfSeconderyEntites, conditionForSecondery);
    }

    private Action buildAction(ContextDefinition contextDefinition,PRDAction action, List<EntityDefinition> entities) {
        ActionType actionType = ActionType.getActionType(action.getType());


        switch (actionType) {

            case INCREASE:
                return buildIncreaseAction(action, contextDefinition);
            case DECREASE:
                return buildDecreaseAction(action, contextDefinition);
            case CALCULATION:
                return buildCalculationAction(action, contextDefinition);
            case CONDITION:
                return buildContidionAction(action, contextDefinition, entities);
            case SET:
                return buildSetAction(action, contextDefinition);
            case KILL:
                return buildKillAction(action, contextDefinition);
            case REPLACE:
                return buildReplaceAction(action, contextDefinition);
            case PROXIMITY:
                return buildProximityAction(action, contextDefinition, entities);
            default:
                throw new RuntimeException("Unknown action type: " + actionType);
        }
    }


    private Action buildKillAction(PRDAction action, ContextDefinition contextDefinition) {

            return new Kill(contextDefinition, action.getEntity());

    }


    private Action buildSetAction(PRDAction action, ContextDefinition contextDefinition) {
        String property = action.getProperty();
        String value = action.getValue();

        return new Set(contextDefinition, action.getEntity(),property, value);
    }

    private Action buildContidionAction(PRDAction action, ContextDefinition contextDefinition, List<EntityDefinition> entities
                                        ) {
        Condition condition = buildCondition(action.getPRDCondition(), entities);
        List<Action> thenActions = buildActionsList(contextDefinition,action.getPRDThen().getPRDAction(), entities);
        List<Action> elseActions = null;
        if (action.getPRDElse() != null) {
            elseActions = buildActionsList(contextDefinition,action.getPRDElse().getPRDAction(), entities);
        }
        return new ActionCondition(contextDefinition, condition, thenActions, elseActions);
    }

    private List<Action> buildActionsList(ContextDefinition contextDefinition,List<PRDAction> prdAction, List<EntityDefinition> entities) {
        List<Action> actions = new ArrayList<>();
        for (PRDAction action: prdAction) {
            if(action.getType().equals("replace") || action.getType().equals("proximity")){
                contextDefinition = buildContextDefinition(action, entities);
            }

            actions.add(buildAction(contextDefinition,action, entities));
        }
        return actions;
    }

    private Condition buildCondition(PRDCondition prdCondition, List<EntityDefinition> entities) {
        if (prdCondition != null) { //when the amount is all
            String singularity = prdCondition.getSingularity();
            if (singularity.equals("single")) {
                EntityDefinition entityDefinition = getEntityDefinition(prdCondition.getEntity(), entities);
                Operation operator = Operation.evaluate(prdCondition.getOperator());
                String value = prdCondition.getValue();
                return new SingleCondition(entityDefinition, operator, prdCondition.getProperty(), value);
            } else if (singularity.equals("multiple")) {
                Logical logical = Logical.evaluate(prdCondition.getLogical());
                List<Condition> conditions = new ArrayList<>();
                for (PRDCondition condition : prdCondition.getPRDCondition()) {
                    conditions.add(buildCondition(condition, entities));
                }
                return new MultipleCondition(logical, conditions);
            } else {
                throw new IllegalArgumentException("Unknown singularity: " + singularity);
            }
        }
        return null;
    }


    private Action buildCalculationAction(PRDAction action, ContextDefinition contextDefinition) {
        String resultProperty = action.getResultProp();
        if (action.getPRDMultiply() != null) {
            String argument1 = action.getPRDMultiply().getArg1();
            String argument2 = action.getPRDMultiply().getArg2();
            return new Multiply(contextDefinition, argument1, argument2,action.getEntity(), resultProperty);
        } else{
            String argument1 = action.getPRDDivide().getArg1();
            String argument2 = action.getPRDDivide().getArg2();
            return new Divide(contextDefinition, argument1, argument2,action.getEntity(), resultProperty);
        }
    }

    private Action buildDecreaseAction(PRDAction action, ContextDefinition contextDefinition) {
        String property = action.getProperty();
        String byExpression = action.getBy();

        return new Decrease(contextDefinition, property, action.getEntity(), byExpression);
    }

    private EntityDefinition getEntityDefinition(String entity, List<EntityDefinition> entities) {
        List<EntityDefinition> filteredEntities = entities
                .stream()
                .filter(entityDefinition -> entityDefinition.getName().equals(entity))
                .collect(Collectors.toList());
        if (filteredEntities.size() != 1) {
            throw new IllegalArgumentException("There is no entity named " + entity);
        }
        return filteredEntities.get(0);
    }

    private Action buildIncreaseAction(PRDAction action, ContextDefinition contextDefinition) {
        String property = action.getProperty();
        String byExpression = action.getBy();

        return new Increase(contextDefinition, property,action.getEntity(), byExpression);
    }

    private Action buildProximityAction(PRDAction action, ContextDefinition contextDefinition, List<EntityDefinition> entities){
        String of = action.getPRDEnvDepth().getOf();
        List<Action> actionsList = buildActionsList(contextDefinition,action.getPRDActions().getPRDAction(), entities);
        return new Proximity(contextDefinition, of, actionsList);
    }

    private Action buildReplaceAction(PRDAction action, ContextDefinition contextDefinition){
        ReplaceType replaceType = ReplaceType.getReplaceType(action.getMode());
        return new Replace(contextDefinition, replaceType);
    }


    private List<TerminateCondition> buildTerminateCondition(List<Object> terminations){
        List<TerminateCondition> result = new ArrayList<>();
        for(Object termination : terminations){
            if(termination instanceof PRDBySecond){
                PRDBySecond prdBySecond = (PRDBySecond) termination;
                TerminateCondition terminateCondition = new TerminateBySeconds(prdBySecond.getCount());
                result.add(terminateCondition);
            }
            else if(termination instanceof PRDByTicks){
                PRDByTicks prdByTicks = (PRDByTicks) termination;
                TerminateCondition terminateCondition = new TerminateByTicks(prdByTicks.getCount());
                result.add(terminateCondition);
            }
            else{
                TerminateCondition terminateCondition = new TerminateByUser();
                result.add(terminateCondition);
            }
        }
        return result;
    }

    private List<EntityDefinition> buildEntityDefinition(List<PRDEntity> entities){
        List<EntityDefinition> entityDefinitions = new ArrayList<>();
        for(PRDEntity entity : entities){
            EntityDefinition entityDefinition = new EntityDefinitionImpl(entity.getName());
            for(PRDProperty property : entity.getPRDProperties().getPRDProperty()){
                PropertyDefinition propertyDefinition = buildPropertyDefinition(property);
                entityDefinition.addProperty(propertyDefinition);

            }
            entityDefinitions.add(entityDefinition);
        }
        return entityDefinitions;
    }

    private PropertyDefinition buildPropertyDefinition(PRDProperty property){
        PropertyDefinition result;
        if(property.getType().equals("decimal")){
           result = buildIntegerPropertyDefinition(property);

        }
        else if(property.getType().equals("float")){
            result = buildFloatPropertyDefinition(property);

        }
        else if(property.getType().equals("boolean")){
             result = buildBooleanPropertyDefinition(property);
        }
        else {
            result = buildStringPropertyDefinition(property);
        }
        return result;
    }

    private StringPropertyDefinition buildStringPropertyDefinition(PRDProperty property){
        StringPropertyDefinition result;
        if(property.getPRDValue().isRandomInitialize()){
            result = new StringPropertyDefinition(property.getPRDName(), PropertyType.STRING,
                    ValueGeneratorFactory.createRandomString());
        }
        else{
            result = new StringPropertyDefinition(property.getPRDName(), PropertyType.STRING,
                    ValueGeneratorFactory.createFixedValue(property.getPRDValue().getInit()));
        }

        return result;
    }

    private BooleanPropertyDefinition buildBooleanPropertyDefinition(PRDProperty property){
        BooleanPropertyDefinition result;
        if(property.getPRDValue().isRandomInitialize()){
            result = new BooleanPropertyDefinition(property.getPRDName(), PropertyType.BOOLEAN,
                    ValueGeneratorFactory.createRandomBoolean());
        }
        else{
            result = new BooleanPropertyDefinition(property.getPRDName(), PropertyType.BOOLEAN,
                    ValueGeneratorFactory.createFixedValue(Boolean.parseBoolean(property.getPRDValue().getInit())));
        }

        return result;
    }

    private IntegerPropertyDefinition buildIntegerPropertyDefinition(PRDProperty property){
        IntegerPropertyDefinition result;
        if(property.getPRDValue().isRandomInitialize()){
            result = new IntegerPropertyDefinition(property.getPRDName(), PropertyType.DECIMAL,
                    ValueGeneratorFactory.createRandomInteger
                            (((Double)property.getPRDRange().getFrom()).intValue(),
                                    ((Double)property.getPRDRange().getTo()).intValue()),
                    ((Double)property.getPRDRange().getFrom()).intValue(), ((Double)property.getPRDRange().getTo()).intValue());
        }
        else{
            result = new IntegerPropertyDefinition(property.getPRDName(), PropertyType.DECIMAL,
                    ValueGeneratorFactory.createFixedValue(Integer.parseInt(property.getPRDValue().getInit())),
                    ((Double)property.getPRDRange().getFrom()).intValue(), ((Double)property.getPRDRange().getTo()).intValue());
        }
        return result;
    }

    private FloatPropertyDefinition buildFloatPropertyDefinition(PRDProperty property){
        FloatPropertyDefinition result;
        if(property.getPRDValue().isRandomInitialize()){
            result = new FloatPropertyDefinition(
                    property.getPRDName(),
                    PropertyType.FLOAT,
                    ValueGeneratorFactory.createRandomFloat(
                            ((Double) property.getPRDRange().getFrom()).floatValue(),
                            ((Double) property.getPRDRange().getTo()).floatValue()
                    ),
                    ((Double) property.getPRDRange().getFrom()).floatValue(),
                    ((Double) property.getPRDRange().getTo()).floatValue()
            );
        }
        else{
            result = new FloatPropertyDefinition(property.getPRDName(), PropertyType.FLOAT,
                    ValueGeneratorFactory.createFixedValue(Float.parseFloat(property.getPRDValue().getInit())),
                    ((Double)property.getPRDRange().getFrom()).floatValue(), ((Double)property.getPRDRange().getTo()).floatValue());
        }
        return result;
    }
}
