package servlets.adminServlet.requests;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import utils.SessionUtils;
import utils.user.RequestsManager;

import java.io.IOException;

@WebServlet("/requests-users")
public class RequestsUserServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

            Gson gson = new GsonBuilder().setPrettyPrinting().create();
            String jsonOutput = gson.toJson(((RequestsManager) getServletContext().getAttribute("requestDetailsManager")).getRequestsPerUser());
            resp.setContentType("application/json");
            resp.getWriter().write(jsonOutput);

    }
}
