package servlets.user.requests;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import dto.RequestDetailsDTO;
import dto.TerminateConditionDTO;
import facade.Facade;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import logic.terminateCondition.*;
import logic.world.WorldDefinition;
import utils.SessionUtils;
import utils.user.RequestsManager;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@WebServlet(name = "RequestProcessServlet", urlPatterns = "/request-process-user-servlet")
public class RequestProcessServlet extends HttpServlet {
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String username = SessionUtils.getUsername(req);
        if (username == null) {
            resp.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        } else {
            Gson gson = new Gson();
            String simulationName = req.getParameter("simulationName");
            String amountOfRunning = req.getParameter("amountOfRunning");
            String byUser = req.getParameter("byUser");
            String byTicks = req.getParameter("byTicks");
            String bySeconds = req.getParameter("bySeconds");
            Integer user = gson.fromJson(byUser, Integer.class);
            Integer ticks = gson.fromJson(byTicks, Integer.class);
            Integer seconds = gson.fromJson(bySeconds, Integer.class);
            TerminateCondition terminateCondition = null;
            if (user != null) {
                terminateCondition = new TerminateByUser();
            } else if (ticks != null && seconds != null) {
                terminateCondition= new TerminateByBoth(Integer.parseInt(byTicks), Integer.parseInt(bySeconds));
            } else if (ticks != null) {
                terminateCondition= new TerminateByTicks(Integer.parseInt(byTicks));
            } else if (seconds != null) {
                terminateCondition = new TerminateBySeconds(Integer.parseInt(bySeconds));
            }
            Facade facade = (Facade) getServletContext().getAttribute("facade");
            WorldDefinition worldDefinition = facade.getWorldDefinition();
            worldDefinition.addTerminateCondition(terminateCondition);
            Integer requestsCounter = (Integer) getServletContext().getAttribute("requestsCounter");

            RequestDetailsDTO requestDetailsDTO;
            List<TerminateConditionDTO> terminateConditions = new ArrayList<>();
            if (terminateCondition != null) {
                facade.addTerminateCondition(terminateCondition, simulationName,requestsCounter);
                requestDetailsDTO = new
                        RequestDetailsDTO(username,(Integer)getServletContext().getAttribute("requestsCounter"),
                        simulationName, Integer.parseInt(amountOfRunning),"pending",0,0, terminateConditions);
                ((RequestsManager) getServletContext().getAttribute("requestDetailsManager")).addRequestByUser(username, requestDetailsDTO);
                String jsonOutput = gson.toJson(((RequestsManager) getServletContext().getAttribute("requestDetailsManager")).getAllRequestsByUser(username));
                resp.setContentType("application/json");
                resp.getWriter().write(jsonOutput);
                getServletContext().setAttribute("requestsCounter", (Integer)getServletContext().getAttribute("requestsCounter") + 1);
            } else {
                resp.getWriter().println(HttpServletResponse.SC_BAD_REQUEST);
            }
        }
    }
}
