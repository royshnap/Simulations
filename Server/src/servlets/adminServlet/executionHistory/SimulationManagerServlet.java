package servlets.adminServlet.executionHistory;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import dto.SimulationManagerDTO;
import facade.Facade;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

@WebServlet("/simulation-manager")
public class SimulationManagerServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        SimulationManagerDTO simulationManagerDTO = ((Facade) getServletContext().getAttribute("facade")).getSimulationManagerDTO();
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        String jsonOutput = gson.toJson(simulationManagerDTO);
        resp.setContentType("application/json");
        resp.getWriter().write(jsonOutput);
    }
}
