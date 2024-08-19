package logic.definition.environment.impl;

import logic.definition.environment.api.EnvVariablesManger;
import logic.definition.property.api.PropertyDefinition;
import logic.execution.instance.environment.api.ActiveEnvironment;
import logic.execution.instance.environment.impl.ActiveEnvironmentImpl;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class EnvVariableManagerImpl implements EnvVariablesManger {
        private Map<String, PropertyDefinition> propNameToPropDefinition;

        public EnvVariableManagerImpl() {
                propNameToPropDefinition = new HashMap<>();
        }
        @Override
        public void addEnvironmentVariable(PropertyDefinition propertyDefinition) {
                propNameToPropDefinition.put(propertyDefinition.getName(), propertyDefinition);
        }
        @Override
        public ActiveEnvironment createActiveEnvironment(){
                return new ActiveEnvironmentImpl();
        }
        @Override
        public Collection<PropertyDefinition> getEnvVariables(){
                return propNameToPropDefinition.values();
        }

        @Override
        public PropertyDefinition getEnvVariable(int i) {
                int j = 0;
                for(PropertyDefinition propertyDefinition : propNameToPropDefinition.values()){
                        if (j==i){
                                return propertyDefinition;
                        }
                        j++;
                }
                return null;
        }

}
