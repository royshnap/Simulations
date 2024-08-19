package logic.execution.instance.environment.api;

import logic.execution.instance.property.api.PropertyInstance;

import java.util.Collection;

public interface ActiveEnvironment {
    PropertyInstance getProperty(String name);
    void addPropertyInstance(PropertyInstance propertyInstance);
    Collection<PropertyInstance> getEnvs();
}
