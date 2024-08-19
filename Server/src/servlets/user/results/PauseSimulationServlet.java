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

@WebServlet("/pause-simulation")
public class PauseSimulationServlet extends HttpServlet {
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String simulationID = req.getParameter("simulationID");
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        Integer id = gson.fromJson(simulationID, Integer.class);
        Facade facade = (Facade) getServletContext().getAttribute("facade");
        facade.pauseSimulation(id);
        resp.getWriter().println("Pause simulation successful");
    }
}
