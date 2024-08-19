package servlets.adminServlet.management;

import facade.Facade;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

@WebServlet(name = "thread count", urlPatterns = "/thread-count")
public class setThreadCountServlet extends HttpServlet {
    @Override
    public void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String choiceParam = req.getParameter("choice");
        if(choiceParam != null){
            Integer choice = Integer.parseInt(choiceParam);
            Facade facade =(Facade) getServletContext().getAttribute("facade");
            facade.createThreadPool(choice);

        }
    }
}
