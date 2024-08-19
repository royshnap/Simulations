package dto;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public class SimulationCurrentDetailsDTO {
    private final int currentTick;
    private final int currentSecond;
    private final Map<String, Integer> amoutOfEntities;
    private final Map<Integer, List<Integer>> amoutOfEntitiesByTicks;

    public int getCurrentTick() {
        return currentTick;
    }

    public int getCurrentSecond() {
        return currentSecond;
    }

    public Set<String> getEntitiesName() {
        return amoutOfEntities.keySet();
    }
    public Collection<Integer> getEntitiesAmount() {
        return amoutOfEntities.values();
    }

    public Map<String, Integer> getAmoutOfEntities() {
        return amoutOfEntities;
    }

    public Map<Integer, List<Integer>> getAmoutOfEntitiesByTicks() {
        if(amoutOfEntitiesByTicks.size() <= 100){
            return amoutOfEntitiesByTicks;
        }
        else{
            int target = 100;
            int step = amoutOfEntitiesByTicks.size() / target;
            return amoutOfEntitiesByTicks.entrySet().stream()
                    .filter(entry -> entry.getKey() % step == 0)
                    .limit(target)
                    .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
        }

    }

    public SimulationCurrentDetailsDTO(int currentTick, int currentSecond, Map<String, Integer> amoutOfEntities,
                                       Map<Integer, List<Integer>> amoutOfEntitiesByTicks) {
        this.currentTick = currentTick;
        this.currentSecond = currentSecond;
        this.amoutOfEntities= amoutOfEntities;
        this.amoutOfEntitiesByTicks = amoutOfEntitiesByTicks;
    }
}
