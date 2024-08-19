package dto;

public class PropertyDefinitionDTO {
    private final String name;
    private final String type;
    private String from;
    private String to;
    private final String valueGenerator;
    private Object value;
    private int consistency;

    public void setConsistency(int consistency) {
        this.consistency = consistency;
    }

    public int getConsistency() {
        return consistency;
    }

    public PropertyDefinitionDTO(String name, String type, String valueGenerator, Object value) {
        this.name = name;
        this.type = type;
        this.valueGenerator = valueGenerator;
        this.value = value;
    }

    public PropertyDefinitionDTO(String name, String type, String from, String to, String valueGenerator, Object value) {
        this.name = name;
        this.type = type;
        this.from = from;
        this.to = to;
        this.valueGenerator = valueGenerator;
        this.value = value;
    }

    public Object getValue() {
        return value;
    }
    public String getValueGenerator() {
        return valueGenerator;
    }

    public String getTo() {
        return to;
    }

    public String getFrom() {
        return from;
    }

    public String getType() {
        return type;
    }

    public String getName() {
        return name;
    }
}
