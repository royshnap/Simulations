package servlets.adminServlet.management;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import utils.admin.SimulationsDefinitionsManager;
import java.io.IOException;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

@WebServlet(name = "simulations definition", urlPatterns = "/simulations-definition")
public class SimulationsDefinitionsServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        SimulationsDefinitionsManager simulationsDefinitionsManager =
                (SimulationsDefinitionsManager) getServletContext().getAttribute("simulationsDefinitionsManager");
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        String jsonOutput = gson.toJson(simulationsDefinitionsManager.getAllSimulations());
        resp.setContentType("application/json");
        resp.getWriter().write(jsonOutput);
    }

}
