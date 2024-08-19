package servlets.user.execution;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import dto.ActiveEnvironmentDTO;
import dto.PropertyInstanceDTO;
import dto.SimulationHistoryDTO;
import dto.WorldDefinitionDTO;
import facade.Facade;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.List;

@WebServlet("/create-history")

public class CreateHistoryServlet extends HttpServlet {
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String environmentManager = req.getParameter("environmentManager");
        String propertyInstanceDTOS = req.getParameter("propertyInstances");
        String worldDefinition = req.getParameter("worldDefinition");
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        ActiveEnvironmentDTO activeEnvironmentDTO = gson.fromJson(environmentManager, ActiveEnvironmentDTO.class);
        List<PropertyInstanceDTO> propertyInstances = gson.fromJson(propertyInstanceDTOS, new TypeToken<List<PropertyInstanceDTO>>(){}.getType());
        WorldDefinitionDTO worldDefinitionDTO = gson.fromJson(worldDefinition, WorldDefinitionDTO.class);
        Facade facade = (Facade) getServletContext().getAttribute("facade");
        facade.createHistory(activeEnvironmentDTO, propertyInstances, worldDefinitionDTO);
        resp.getWriter().println("Create history successful");
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String idJson = req.getParameter("simulationID");
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        Integer id = gson.fromJson(idJson, Integer.class);
        Facade facade = (Facade) getServletContext().getAttribute("facade");
        SimulationHistoryDTO simulationHistoryDTO = facade.getSimulationHistory(id);
        String jsonOutput = gson.toJson(simulationHistoryDTO);
        resp.setContentType("application/json");
        resp.getWriter().write(jsonOutput);
    }
}
