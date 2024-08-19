package logic.execution.instance.property.api;
import logic.definition.property.api.PropertyDefinition;

public interface PropertyInstance {

    PropertyDefinition getPropertyDefinition();
    Object getValue();
    void setValue(Object val);
    void setSumOfUpdateTicks(int sumOfUpdateTicks);


    int getUpdateTicks();
    void setConsistency(int consistency);

    int getSumOfUpdatesTicks();
    int getConsistency();

    void setTicks(int ticksUpdateValue);
}
