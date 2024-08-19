package logic.execution.instance.property.impl;
import logic.definition.property.api.PropertyDefinition;
import logic.execution.instance.property.api.PropertyInstance;

public class PropertyInstanceImpl implements PropertyInstance {
    private PropertyDefinition propertyDefinition;
    private Object value;
    private int ticksUpdateValue;
    private int sumOfUpdatesTicks = 0;
    private int consistency;

    public PropertyInstanceImpl(PropertyDefinition propertyDefinition, Object value) {
        this.propertyDefinition = propertyDefinition;
        this.value = value;

    }
    @Override
    public int getConsistency(){
        return consistency;
    }
    @Override
    public void setConsistency(int ticks){
        this.consistency = (ticks-sumOfUpdatesTicks)/ticks;
    }

    @Override
    public int getUpdateTicks(){
        return ticksUpdateValue;
    }
    @Override
    public int getSumOfUpdatesTicks(){
        return sumOfUpdatesTicks;
    }
    @Override
    public void setTicks(int ticksUpdateValue){

        this.ticksUpdateValue = ticksUpdateValue;


    }

    @Override
    public PropertyDefinition getPropertyDefinition() {
        return propertyDefinition;
    }

    @Override
    public Object getValue() {
        return value;
    }

    @Override
    public void setValue(Object val) {
        this.value = val;

    }

    @Override
    public void setSumOfUpdateTicks(int sumOfUpdateTicks) {
        this.sumOfUpdatesTicks = sumOfUpdateTicks;
    }
}
