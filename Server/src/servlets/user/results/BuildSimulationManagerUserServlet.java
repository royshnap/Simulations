package servlets.user.results;

import dto.SimulationDTO;
import dto.SimulationManagerDTO;
import facade.Facade;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import utils.user.SimulationUserManager;

import java.io.IOException;

@WebServlet("/build-simulation-per-user")
public class BuildSimulationManagerUserServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        SimulationManagerDTO simulationManagerDTO = ((Facade) getServletContext().getAttribute("facade")).getSimulationManagerDTO();
        SimulationUserManager simulationUserManager = (SimulationUserManager) getServletContext().getAttribute("simulationUserManager");
        for(SimulationDTO simulationDTO : simulationManagerDTO.getSimulationList()){

            simulationUserManager.addSimualtionByUser(simulationDTO.getUserName(),simulationDTO);

        }
        resp.getWriter().write("OK");
    }
}
