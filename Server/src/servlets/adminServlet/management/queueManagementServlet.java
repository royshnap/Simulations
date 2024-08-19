package servlets.adminServlet.management;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import dto.QueueManagmentDTO;
import facade.Facade;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@WebServlet(name = "queue management", urlPatterns = "/queue-management")
public class queueManagementServlet extends HttpServlet {

    @Override
    public void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        QueueManagmentDTO queueManagmentDTO =
                ((Facade) getServletContext().getAttribute("facade")).getQueueManagmentDTO();
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        Map<String, Object> data = new HashMap<>();
        data.put("waitingSimulations", queueManagmentDTO.getWaitingSimulations());
        data.put("runningSimulations", queueManagmentDTO.getRunningSimulations());
        data.put("finishedSimulations", queueManagmentDTO.getFinishedSimulations());

        // Serialize the map to JSON
        String jsonOutput = gson.toJson(data);
        resp.setContentType("application/json");
        resp.getWriter().write(jsonOutput);

    }
}
