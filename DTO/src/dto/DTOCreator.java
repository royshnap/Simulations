package dto;

import logic.definition.entity.api.EntityDefinition;
import logic.definition.environment.api.EnvVariablesManger;
import logic.definition.property.api.PropertyDefinition;
import logic.definition.property.impl.AbstractNumericPropertyDefinition;
import logic.definition.value.random.AbstractRandomValueGenerator;
import logic.rule.Rule;
import logic.rule.action.api.Action;
import logic.rule.action.api.ActionType;
import logic.rule.action.impl.Set;
import logic.rule.action.impl.*;
import logic.rule.action.impl.calculation.Calculation;
import logic.rule.action.impl.calculation.Divide;
import logic.rule.action.impl.condition.ActionCondition;
import logic.rule.action.impl.condition.MultipleCondition;
import logic.rule.action.impl.condition.SingleCondition;
import logic.simulation.HistogramSimulationManger;
import logic.simulation.Simulation;
import logic.simulation.SimulationOutput;
import logic.simulation.SimulationsManager;
import logic.terminateCondition.TerminateBySeconds;
import logic.terminateCondition.TerminateByTicks;
import logic.terminateCondition.TerminateCondition;
import logic.world.WorldDefinition;
import logic.rule.action.api.ActionType;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class DTOCreator {


    public SimulationManagerDTO createManagerSimulationDTO(SimulationsManager simulationsManager){
        List<Simulation> pastSimulation = simulationsManager.getSimulationList();
        List<SimulationDTO> simulationDTOList = new ArrayList<>();
        SimulationDTO simulationDTO;
        for(Simulation simulation : pastSimulation){
            simulationDTO = createSimulationDTO(simulation.getWorldDefinition(),simulation, simulation.getSimulationOutput(), simulation.getUserName(),
                    simulation.getWorldDefinition().getName());
            simulationDTOList.add(simulationDTO);
        }
        return new SimulationManagerDTO(simulationDTOList);
    }

    public WorldDefinitionDTO createWorldDefinitionDTO(WorldDefinition worldDefinition){
        List<EntityDefinitionDTO> entityDefinitionDTOS = createPopulationDTO(worldDefinition.getPopulation());
        List<RuleDTO> ruleDTOS = createRuleDTO(worldDefinition.getRules());
        List<TerminateConditionDTO> terminateConditionDTOS = createTerminateDTO(worldDefinition.getTerminateConditions());
        EnvVariableManagerDTO envVariableManagerDTO = createEnvVariableManagerDTO(worldDefinition.getEnvironmentsVariables());
        WorldDefinitionDTO worldDefinitionDTO = new WorldDefinitionDTO(entityDefinitionDTOS,worldDefinition.getTicks(),terminateConditionDTOS,
                envVariableManagerDTO,ruleDTOS, worldDefinition.getNumberOfThreads(),
                worldDefinition.getRows(), worldDefinition.getColumns(), worldDefinition.getName(), worldDefinition.getSleep());
        return worldDefinitionDTO;


    }

    public EnvVariableManagerDTO createEnvVariableManagerDTO(EnvVariablesManger envVariablesManger){
        Collection<PropertyDefinition> propertyDefinitions = envVariablesManger.getEnvVariables();
        List<PropertyDefinitionDTO> propertyDefinitionDTOS = new ArrayList<>();
        PropertyDefinitionDTO propertyDefinitionDTO;
        for(PropertyDefinition propertyDefinition : propertyDefinitions){
            if(propertyDefinition instanceof AbstractNumericPropertyDefinition){
                if(propertyDefinition.getValueGenerator() instanceof AbstractRandomValueGenerator){
                    propertyDefinitionDTO = createPropertyDefinitionDTO(propertyDefinition.getName(),
                            propertyDefinition.getType().name(),
                            ((AbstractNumericPropertyDefinition) propertyDefinition).getFrom().toString(),
                            ((AbstractNumericPropertyDefinition)propertyDefinition).getTo().toString(),"random",
                            null);
                }
                else{
                    propertyDefinitionDTO = createPropertyDefinitionDTO(propertyDefinition.getName(),
                            propertyDefinition.getType().name(),
                            ((AbstractNumericPropertyDefinition) propertyDefinition).getFrom().toString(),
                            ((AbstractNumericPropertyDefinition)propertyDefinition).getTo().toString(),"fixed",
                            propertyDefinition.generateValue());
                }

            }
            else if(propertyDefinition.getValueGenerator() instanceof AbstractRandomValueGenerator){
                propertyDefinitionDTO = createPropertyDefinitionDTO(propertyDefinition.getName(),
                        propertyDefinition.getType().name(),
                        null, null,"random", null);
            }
            else{
                propertyDefinitionDTO = createPropertyDefinitionDTO(propertyDefinition.getName(),
                        propertyDefinition.getType().name(),
                        null, null,"fixed", propertyDefinition.generateValue());
            }
            propertyDefinitionDTOS.add(propertyDefinitionDTO);
        }
        return new EnvVariableManagerDTO(propertyDefinitionDTOS);
    }

    public PropertyInstanceDTO createPropertyInstanceDTO(PropertyDefinitionDTO propertyDefinition,
                                                         Object value){
        return new PropertyInstanceDTO(propertyDefinition, value);
    }

    public List<TerminateConditionDTO> createTerminateDTO(List<TerminateCondition> terminateConditions){
        List<TerminateConditionDTO> terminateConditionDTOS = new ArrayList<>();
        TerminateConditionDTO terminateConditionDTO;
        for(TerminateCondition terminateCondition : terminateConditions){
            if(terminateCondition instanceof TerminateBySeconds){
                terminateConditionDTO = new TerminateConditionDTO(terminateCondition.getCount(), null, null);
            }
            else if(terminateCondition instanceof TerminateByTicks){
                terminateConditionDTO = new TerminateConditionDTO(null, terminateCondition.getCount(), null);
            }
            else{
                terminateConditionDTO = new TerminateConditionDTO(null, null, "by user");
            }

            terminateConditionDTOS.add(terminateConditionDTO);

        }
        return terminateConditionDTOS;

    }

    public List<RuleDTO> createRuleDTO(List<Rule> rules){
        ActivationDTO activationDTO;
        List<RuleDTO> ruleDTOS = new ArrayList<>();
        List<ActionDTO> actions = new ArrayList<>();
        RuleDTO ruleDTO;
        for(Rule rule : rules){
            actions = new ArrayList<>();
            activationDTO = createActivationDTO(rule.getActivation().getTicks(), rule.getActivation().getProbability());
            for(Action action : rule.getActionsToPerform()){
                ActionDTO actionDTO = showAction(action);
                actions.add(actionDTO);

            }
            ruleDTO = new RuleDTO(rule.getName(),actions,activationDTO);
            ruleDTOS.add(ruleDTO);
        }
        return ruleDTOS;
    }
    private ActionDTO showAction(Action action){
        ActionDTO actionDTO;
        switch (action.getActionType()){
            case DECREASE:
                Decrease decrease = (Decrease) action;
                if(action.getContextDefinition().getSecondaryEntity() != null){
                    actionDTO = new ActionDTO(action.getActionType().name(), action.getContextDefinition().getPrimaryEntity().getName(),
                            action.getContextDefinition().getSecondaryEntity().getName(),((Decrease) action).getPropertyName(),
                            decrease.getByExpression());

                }
                else{
                    actionDTO = new ActionDTO(action.getActionType().name(), action.getContextDefinition().getPrimaryEntity().getName(),
                           null,((Decrease) action).getPropertyName(),
                            decrease.getByExpression());
                }
                return actionDTO;

            case INCREASE:
                Increase increase = (Increase) action;
                if(action.getContextDefinition().getSecondaryEntity() != null){
                    actionDTO = new ActionDTO(action.getActionType().name(), action.getContextDefinition().getPrimaryEntity().getName(),
                            action.getContextDefinition().getSecondaryEntity().getName(),increase.getPropertyName(),
                            increase.getByExpression());

                }
                else{
                    actionDTO = new ActionDTO(action.getActionType().name(), action.getContextDefinition().getPrimaryEntity().getName(),
                            null,increase.getPropertyName(),
                            increase.getByExpression());
                }
                return actionDTO;

            case KILL:
                Kill kill = (Kill) action;
                if(kill.getContextDefinition().getSecondaryEntity() != null){
                    actionDTO = new ActionDTO(kill.getActionType().name(), kill.getContextDefinition().getPrimaryEntity().getName(),
                            action.getContextDefinition().getSecondaryEntity().getName(),null,
                            null);

                }
                else{
                    actionDTO = new ActionDTO(action.getActionType().name(), action.getContextDefinition().getPrimaryEntity().getName(),
                            null,null,
                            null);
                }
                return actionDTO;

            case PROXIMITY:
                Proximity proximity = (Proximity) action;
                actionDTO = new ActionDTO(proximity.getActionType().name(),
                        proximity.getContextDefinition().getPrimaryEntity().getName(),proximity.getContextDefinition().getSecondaryEntity().getName()
                , proximity.getOf(), String.valueOf(proximity.getSizeOfAction()));
                return actionDTO;

             case REPLACE:
                 Replace replace = (Replace) action;
                 actionDTO = new ActionDTO(replace.getActionType().name(),
                         replace.getContextDefinition().getPrimaryEntity().getName(),replace.getContextDefinition().getSecondaryEntity().getName()
                         , null,null);
                 return actionDTO;

            case SET:
                Set set = (Set) action;
                if(set.getContextDefinition().getSecondaryEntity() != null){
                    actionDTO = new ActionDTO(set.getActionType().name(), set.getContextDefinition().getPrimaryEntity().getName(),
                            set.getContextDefinition().getSecondaryEntity().getName(),set.getPropertyName(),
                            set.getNewValue());

                }
                else{
                    actionDTO = new ActionDTO(action.getActionType().name(), action.getContextDefinition().getPrimaryEntity().getName(),
                            null,set.getPropertyName(),
                            set.getNewValue());
                }
                return actionDTO;

            case MULTIPLY:
                Calculation calculation = (Calculation) action;
                if(calculation.getContextDefinition().getSecondaryEntity() != null){
                    actionDTO = new ActionDTO(calculation.getActionType().name(), calculation.getContextDefinition().getPrimaryEntity().getName(),
                            calculation.getContextDefinition().getSecondaryEntity().getName(),calculation.getArgument1(),
                            calculation.getArgument2());

                }
                else{
                    actionDTO = new ActionDTO(calculation.getActionType().name(), calculation.getContextDefinition().getPrimaryEntity().getName(),
                            null,calculation.getArgument1(),
                            calculation.getArgument2());
                }
                return actionDTO;
            case DIVIDE:
                Divide divide = (Divide) action;
                if(divide.getContextDefinition().getSecondaryEntity() != null){
                    actionDTO = new ActionDTO(divide.getActionType().name(), divide.getContextDefinition().getPrimaryEntity().getName(),
                            divide.getContextDefinition().getSecondaryEntity().getName(),divide.getArgument1(),
                            divide.getArgument2());

                }
                else{
                    actionDTO = new ActionDTO(divide.getActionType().name(), divide.getContextDefinition().getPrimaryEntity().getName(),
                            null,divide.getArgument1(),
                            divide.getArgument2());
                }
                return actionDTO;

            case CONDITION:
                ActionCondition actionCondition = (ActionCondition) action;
                int sizeOfElse = 0;
                if(actionCondition.getListElse() != null){
                    sizeOfElse = actionCondition.getListElse().size();
                }
                if(actionCondition.getContextDefinition().getSecondaryEntity() != null){
                    if(actionCondition.getCondition() instanceof SingleCondition){

                        actionDTO = new ActionDTO(actionCondition.getActionType().name(), actionCondition.getContextDefinition().getPrimaryEntity().getName(),
                                actionCondition.getContextDefinition().getSecondaryEntity().getName(),
                                ((SingleCondition) actionCondition.getCondition()).getExpression(),
                                ((SingleCondition) actionCondition.getCondition()).getValue(),
                                ((SingleCondition) actionCondition.getCondition()).getOperator().name(),
                                String.valueOf(actionCondition.getListThen().size()),
                                        String.valueOf(sizeOfElse));
                    }
                    else{
                        actionDTO = new ActionDTO(actionCondition.getActionType().name(), actionCondition.getContextDefinition().getPrimaryEntity().getName(),
                                actionCondition.getContextDefinition().getSecondaryEntity().getName(),
                                String.valueOf(((MultipleCondition) actionCondition.getCondition()).getConditions().size()),
                                null,
                                ((MultipleCondition) actionCondition.getCondition()).getLogical().name(),
                                String.valueOf(actionCondition.getListThen().size()),
                                String.valueOf(sizeOfElse));
                    }

                }
                else{
                    if(actionCondition.getCondition() instanceof SingleCondition){

                        actionDTO = new ActionDTO(actionCondition.getActionType().name(), actionCondition.getContextDefinition().getPrimaryEntity().getName(),
                               null,
                                ((SingleCondition) actionCondition.getCondition()).getExpression(),
                                ((SingleCondition) actionCondition.getCondition()).getValue(),
                                ((SingleCondition) actionCondition.getCondition()).getOperator().name(),
                                String.valueOf(actionCondition.getListThen().size()),
                                String.valueOf(sizeOfElse));
                    }
                    else{
                        actionDTO = new ActionDTO(actionCondition.getActionType().name(), actionCondition.getContextDefinition().getPrimaryEntity().getName(),
                                null,
                                String.valueOf(((MultipleCondition) actionCondition.getCondition()).getConditions().size()),
                                null,
                                ((MultipleCondition) actionCondition.getCondition()).getLogical().name(),
                                String.valueOf(actionCondition.getListThen().size()),
                                String.valueOf(sizeOfElse));
                    }
                }
                return actionDTO;

            default:
                return null;



        }
    }
    public ActivationDTO createActivationDTO(int ticks, double probability){
        return new ActivationDTO(ticks,probability);

    }

    public List<EntityDefinitionDTO> createPopulationDTO(Collection<EntityDefinition> entityDefinitions){
        List<EntityDefinitionDTO> entityDefinitionDTOS = new ArrayList<>();
        EntityDefinitionDTO entityDefinitionDTO;
        List<PropertyDefinitionDTO> propertyDefinitionDTOS = new ArrayList<>();
        PropertyDefinitionDTO propertyDefinitionDTO;
        for(EntityDefinition entityDefinition : entityDefinitions){
            propertyDefinitionDTOS = new ArrayList<>();
            for(PropertyDefinition propertyDefinition : entityDefinition.getProps()){
                if(propertyDefinition instanceof AbstractNumericPropertyDefinition){
                    if(propertyDefinition.getValueGenerator() instanceof AbstractRandomValueGenerator){
                        propertyDefinitionDTO = createPropertyDefinitionDTO(propertyDefinition.getName(),
                                propertyDefinition.getType().name(),
                                ((AbstractNumericPropertyDefinition) propertyDefinition).getFrom().toString(),
                                ((AbstractNumericPropertyDefinition)propertyDefinition).getTo().toString(),"random",
                                null);
                    }
                    else{
                        propertyDefinitionDTO = createPropertyDefinitionDTO(propertyDefinition.getName(),
                                propertyDefinition.getType().name(),
                                ((AbstractNumericPropertyDefinition) propertyDefinition).getFrom().toString(),
                                ((AbstractNumericPropertyDefinition)propertyDefinition).getTo().toString(),"fixed",
                                propertyDefinition.generateValue());
                    }

                }
                else if(propertyDefinition.getValueGenerator() instanceof AbstractRandomValueGenerator){
                    propertyDefinitionDTO = createPropertyDefinitionDTO(propertyDefinition.getName(),
                            propertyDefinition.getType().name(),
                            null, null,"random", null);
                }
                else{
                    propertyDefinitionDTO = createPropertyDefinitionDTO(propertyDefinition.getName(),
                            propertyDefinition.getType().name(),
                            null, null,"fixed", propertyDefinition.generateValue());
                }
                propertyDefinitionDTOS.add(propertyDefinitionDTO);

            }
            entityDefinitionDTO = new EntityDefinitionDTO(entityDefinition.getName(), propertyDefinitionDTOS,
                    entityDefinition.getStartPopulation(),entityDefinition.getEndPopulation());
            entityDefinitionDTOS.add(entityDefinitionDTO);

        }
        return entityDefinitionDTOS;

    }

    public EntityInstanceDTO createEntityInstanceDTO(EntityDefinitionDTO entityDefinition){
        List<PropertyInstanceDTO> propertyInstanceDTOS = new ArrayList<>();
        for(PropertyDefinitionDTO propertyDefinition : entityDefinition.getProperties()){
            propertyInstanceDTOS.add(createPropertyInstanceDTO(propertyDefinition,propertyDefinition.getValue()));

        }
        return new EntityInstanceDTO(entityDefinition,propertyInstanceDTOS);
    }

    public PropertyDefinitionDTO createPropertyDefinitionDTO(String name, String type, String from, String to, String valueGenerator,
                                                             Object value){
        if(from != null){
            return new PropertyDefinitionDTO(name,type,from,to,valueGenerator, value);
        }
        else{
            return new PropertyDefinitionDTO(name,type,valueGenerator, value);
        }

    }



    public SimulationDTO createSimulationDTO(WorldDefinition worldDefinition,Simulation simulation, SimulationOutput simulationOutput,
                                             String userName, String nameSimulation){
        WorldDefinitionDTO worldDefinitionDTO= createWorldDefinitionDTO(worldDefinition);
        SimulationOutputDTO simulationOutputDTO = null;
        if(simulationOutput != null){
             simulationOutputDTO = new SimulationOutputDTO(simulation.getId(),simulationOutput.getReasonsOfEnding());
        }
        SimulationDTO simulationDTO = new SimulationDTO(simulation.getId(),simulation.getDate(), simulationOutputDTO,
                worldDefinitionDTO, userName, nameSimulation);
        //simulationDTO.setCurrentDetailsDTO(simulation.getCurrentDetailsDTO());

        return simulationDTO;
    }

    public HistogramSimulationDTO createHistogramForSimulation(HistogramSimulationManger histogramSimulationManger){
        Map<Object, Integer> histogram= new ConcurrentHashMap<>();
        histogram.putAll(histogramSimulationManger.getHistogram());
        return new HistogramSimulationDTO(histogram,histogramSimulationManger.getConsistency());
    }
}
