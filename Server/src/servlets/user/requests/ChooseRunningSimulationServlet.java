package servlets.user.requests;

import dto.RequestDetailsDTO;
import dto.WorldDefinitionDTO;
import facade.Facade;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import logic.terminateCondition.TerminateCondition;
import utils.SessionUtils;
import utils.user.RequestsManager;

import java.io.IOException;
import java.util.List;
import java.util.Map;

@WebServlet("/choose-running-simulation")
public class ChooseRunningSimulationServlet extends HttpServlet {
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String optionParam = req.getParameter("option");
        Integer requestID = Integer.parseInt(req.getParameter("requestID"));
        String username = SessionUtils.getUsername(req);
        String simulationName = req.getParameter("simulationName");
        if (username == null) {
            resp.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        }
        if (optionParam != null) {
            Map<String, List<RequestDetailsDTO>> allRequests = ((RequestsManager) getServletContext().getAttribute("requestDetailsManager")).getRequestsPerUser();
            for (RequestDetailsDTO requestDetailsDTO : allRequests.get(username)) {
                if (requestDetailsDTO.getRequestNumber() == requestID) {
                    if(requestDetailsDTO.getAmountOfRunning() > 0){
                        requestDetailsDTO.setAmountOfRunning(requestDetailsDTO.getAmountOfRunning() - 1);
                        Facade facade = (Facade) getServletContext().getAttribute("facade");
                       WorldDefinitionDTO worldDefinitionDTO = facade.createWorldFromTheSameRequest(simulationName);
                        resp.getWriter().println("Update running simulation successfully");
                    }
                    else{
                        resp.getWriter().println("There isn't any running simulation");
                    }

                    break;
                }
            }

        }
    }
}
