package logic.simulation;

import dto.*;
import logic.execution.instance.environment.api.ActiveEnvironment;
import logic.world.WorldDefinition;
import logic.world.WorldInstance;

import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;

public class SimulationsManager {

    private int id = 1;
    private List<Simulation> simulationList;
    private ExecutorService threadPool;
    private DTOCreator dtoCreator;
    private Simulation currentRunningSimulation;
    public void createThreadPool(int numberOfThread){
        threadPool = Executors.newFixedThreadPool(numberOfThread);
    }
    public SimulationsManager(){
        dtoCreator = new DTOCreator();
    }
    public void addSimulation(Simulation simulation) {
        if (simulationList == null) {
            simulationList = new ArrayList<>();
        }
        simulation.setId(id);
        simulation.setDate(new Date());
        simulationList.add(simulation);
        id++;
    }

    public List<Simulation> getSimulationList(){
        return simulationList;
    }

    public Simulation getSimulationById(int id){
        for(Simulation simulation : simulationList){
            if(simulation.getId() == id){
                return simulation;
            }
        }
        return null;
    }

    public SimulationDTO startSimulation(ActiveEnvironment activeEnvironment, WorldDefinition worldDefinition,
                                         SimulationHistoryDTO simulationHistory, String userName){
       WorldInstance worldInstance = new WorldInstance(worldDefinition, activeEnvironment,
               simulationHistory);

        Simulation simulation = new Simulation(worldInstance,worldDefinition);
        simulation.setSimulationHistory(simulationHistory);
        simulation.setUserName(userName);
        currentRunningSimulation = simulation;
        this.addSimulation(simulation);
        threadPool.execute(simulation);
        return simulation.getSimulationDTO();
    }

    public SimulationDTO getCurrentRunningSimulationDTO(){
        return currentRunningSimulation.getSimulationDTO();
    }
    public void pauseSimulation(int id){
        for(Simulation simulation : simulationList){
            if(simulation.getId() == id){
               simulation.pauseSimulation();
                    simulation.setPause(true);

            }
        }

    }
    public void resumeSimulation(int id){
        for(Simulation simulation : simulationList){
            if(simulation.getId() == id){
                simulation.resumeSimulation();
                    simulation.setPause(false);

            }
        }
    }
    public void stopSimulation(int id){
        for(Simulation simulation : simulationList){
            if(simulation.getId() == id){
                simulation.stopSimulation();
                    simulation.setEndSimulation(true);

            }
        }
    }
    public ThreadPoolExecutor getThreadPool() {

        return (ThreadPoolExecutor) threadPool;
    }


}


