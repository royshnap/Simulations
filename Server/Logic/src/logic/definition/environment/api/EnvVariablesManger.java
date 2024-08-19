package logic.definition.environment.api;

import logic.definition.property.api.PropertyDefinition;
import logic.execution.instance.environment.api.ActiveEnvironment;

import java.util.Collection;

public interface EnvVariablesManger {
        void addEnvironmentVariable(PropertyDefinition propertyDefinition);
        ActiveEnvironment createActiveEnvironment();
        Collection<PropertyDefinition> getEnvVariables();

        PropertyDefinition getEnvVariable(int i);
}
