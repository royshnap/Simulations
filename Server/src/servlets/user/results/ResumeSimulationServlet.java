package servlets.user.results;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import facade.Facade;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

@WebServlet("/resume-simulation")
public class ResumeSimulationServlet extends HttpServlet {
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String simulationID = req.getParameter("simulationID");
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        Integer id = gson.fromJson(simulationID, Integer.class);
        Facade facade = (Facade) getServletContext().getAttribute("facade");
        facade.resumeSimulation(id);
        resp.getWriter().println("Resume simulation successful");
    }
}
