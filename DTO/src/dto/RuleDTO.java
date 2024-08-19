package dto;



import java.util.List;

public class RuleDTO {
    private final String name;
    private final List<ActionDTO> actions;
    private final ActivationDTO activation;

    public RuleDTO(String name, List<ActionDTO> actions, ActivationDTO activation) {
        this.name = name;
        this.actions = actions;
        this.activation = activation;
    }

    public ActivationDTO getActivation() {
        return activation;
    }

    public List<ActionDTO> getActions() {
        return actions;
    }

    public String getName() {
        return name;
    }
}
