package servlets.user.execution;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import dto.ActiveEnvironmentDTO;
import facade.Facade;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import utils.SessionUtils;

import javax.xml.bind.JAXBException;
import java.io.IOException;

@WebServlet("/start-simulation-history")
public class StartSimulationHistoryServlet extends HttpServlet {
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String username = SessionUtils.getUsername(req);
        if (username == null) {
            resp.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        }
        else{
            String idJson = req.getParameter("simulationID");
            Gson gson = new GsonBuilder().setPrettyPrinting().create();
            Integer id = gson.fromJson(idJson, Integer.class);
            Facade facade = (Facade) getServletContext().getAttribute("facade");
            try {
                facade.startSimulationInHistory(facade.getSimulationHistory(id).getActiveEnvironmentDTO(),
                        facade.getSimulationHistory(id).getEntityDefinitionsDTOS(), username,facade.getSimulationHistory(id).getName());
            } catch (JAXBException e) {
                throw new RuntimeException(e);
            }
        }

    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String idJson = req.getParameter("simulationID");
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        Integer id = gson.fromJson(idJson, Integer.class);
        String jsonOutput = gson.toJson(((Facade) getServletContext().getAttribute("facade")).getSimulationHistory(id));
        resp.setContentType("application/json");
        resp.getWriter().write(jsonOutput);
    }
}
