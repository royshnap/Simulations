package logic.execution.instance.environment.impl;

import logic.execution.instance.environment.api.ActiveEnvironment;
import logic.execution.instance.property.api.PropertyInstance;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class ActiveEnvironmentImpl implements ActiveEnvironment {

    private Map<String, PropertyInstance> envVariables;

    public ActiveEnvironmentImpl() {
        envVariables = new HashMap<>();
    }
    @Override
    public PropertyInstance getProperty(String name) {
        if (!envVariables.containsKey(name)) {
            throw new IllegalArgumentException("Can't find env variable with name " + name);
        }
        return envVariables.get(name);
    }
    @Override
    public void addPropertyInstance(PropertyInstance propertyInstance) {
        envVariables.put(propertyInstance.getPropertyDefinition().getName(), propertyInstance);
    }

    @Override
    public Collection<PropertyInstance> getEnvs() {
        return envVariables.values();
    }
}
