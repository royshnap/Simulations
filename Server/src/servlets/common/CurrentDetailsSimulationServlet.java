package servlets.common;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import dto.SimulationCurrentDetailsDTO;
import facade.Facade;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import logic.simulation.Simulation;
import logic.simulation.SimulationsManager;

import java.io.IOException;

@WebServlet("/current-datails-simulation")
public class CurrentDetailsSimulationServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String simulationID = req.getParameter("simulationID");
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        Integer id = gson.fromJson(simulationID, Integer.class);
        Facade facade = (Facade) getServletContext().getAttribute("facade");
        Simulation simulation = facade.getSimulationsManager().getSimulationById(id);
        SimulationCurrentDetailsDTO simulationCurrentDetailsDTO = simulation.getCurrentDetailsDTO();
        String result = gson.toJson(simulationCurrentDetailsDTO);
        resp.setContentType("application/json");
        resp.getWriter().write(result);

    }
}
