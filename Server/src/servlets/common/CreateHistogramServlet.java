package servlets.common;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import dto.EntityDefinitionDTO;
import dto.HistogramSimulationDTO;
import dto.HistogramSimulationDTOSerializer;
import dto.PropertyDefinitionDTO;
import facade.Facade;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import logic.simulation.HistogramSimulationManger;
import logic.simulation.Simulation;

import java.io.IOException;
import java.util.Map;

@WebServlet("/create-histogram")
public class CreateHistogramServlet extends HttpServlet {
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String simulationID = req.getParameter("simulationID");
        String entity = req.getParameter("entity");
        String chosenProperty = req.getParameter("property");
        Gson gson = new GsonBuilder()
                .setPrettyPrinting()
                .create();
        Integer id = gson.fromJson(simulationID, Integer.class);
        EntityDefinitionDTO chosenEntity = gson.fromJson(entity, EntityDefinitionDTO.class);
        PropertyDefinitionDTO property = gson.fromJson(chosenProperty, PropertyDefinitionDTO.class);
        Facade facade = (Facade) getServletContext().getAttribute("facade");
        Simulation simulation = facade.getSimulationsManager().getSimulationById(id);
        HistogramSimulationManger manager = simulation.buildHistogramForSimulation(chosenEntity.getName(),
                property.getName());
        HistogramSimulationDTO histogramSimulationDTO =facade.createHistogramForSimulation(manager);
       getServletContext().setAttribute("simulationHistogram", histogramSimulationDTO);

    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        HistogramSimulationDTO histogramSimulationDTO = (HistogramSimulationDTO) getServletContext().getAttribute("simulationHistogram");
        Gson gson = new Gson();
        String map = gson.toJson(histogramSimulationDTO.getHistogram());
        String avg = gson.toJson(histogramSimulationDTO.getAverage());
        String con = gson.toJson(histogramSimulationDTO.getConsistency());
        String result = gson.toJson(histogramSimulationDTO);
        resp.setContentType("application/json");
        resp.getWriter().write(result);
    }
}
