package servlets.user.results;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import dto.SimulationDTO;
import dto.SimulationManagerDTO;
import facade.Facade;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import utils.SessionUtils;
import utils.user.SimulationUserManager;

import java.io.IOException;
import java.util.List;

@WebServlet("/get-simulations-per-user")
public class SimulationsUserServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String username = SessionUtils.getUsername(req);
        if (username == null) {
            resp.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        }
        else{

            Gson gson = new GsonBuilder().setPrettyPrinting().create();
            SimulationUserManager simulationUserManager = (SimulationUserManager) getServletContext().getAttribute("simulationUserManager");
            List<SimulationDTO> simulationPerUser = simulationUserManager.getSimulationsPerUser(username);
            String jsonOutput = gson.toJson(simulationPerUser);
            resp.setContentType("application/json");
            resp.getWriter().write(jsonOutput);
        }

    }

}
