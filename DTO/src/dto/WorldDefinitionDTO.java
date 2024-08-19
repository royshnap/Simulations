package dto;



import java.util.List;

public class WorldDefinitionDTO {
   private final List<EntityDefinitionDTO> entityDefinitionDTOS;
   private final int ticks;
    private final List<TerminateConditionDTO> terminateConditions;

    private final EnvVariableManagerDTO environmentsVariablesDTO;

    private final List<RuleDTO> rules;
    private final int numberOfThreads;
    private final int rows;
    private final int cols;
    private final String name;
    private final int sleep;

    public int getNumberOfThreads() {
        return numberOfThreads;
    }

    public WorldDefinitionDTO(List<EntityDefinitionDTO> entityDefinitionDTOS, int ticks, List<TerminateConditionDTO> terminateConditions, EnvVariableManagerDTO environmentsVariablesDTO, List<RuleDTO> rules,
                              int numberOfThreads, int rows, int cols, String name, int sleep) {
        this.entityDefinitionDTOS = entityDefinitionDTOS;
        this.ticks = ticks;
        this.terminateConditions = terminateConditions;
        this.environmentsVariablesDTO = environmentsVariablesDTO;
        this.rules = rules;
        this.numberOfThreads = numberOfThreads;
        this.rows = rows;
        this.cols = cols;
        this.name = name;
        this.sleep = sleep;
    }

    public String getName() {
        return name;
    }

    public int getSleep() {
        return sleep;
    }

    public int getRows() {
        return rows;
    }

    public int getCols() {
        return cols;
    }

    public List<EntityDefinitionDTO> getEntityDefinitionDTOS() {
        return entityDefinitionDTOS;
    }

    public int getTicks() {
        return ticks;
    }

    public List<TerminateConditionDTO> getTerminateConditions() {
        return terminateConditions;
    }

    public EnvVariableManagerDTO getEnvironmentsVariablesDTO() {
        return environmentsVariablesDTO;
    }

    public List<RuleDTO> getRules() {
        return rules;
    }
}
