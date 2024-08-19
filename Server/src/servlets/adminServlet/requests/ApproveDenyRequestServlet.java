package servlets.adminServlet.requests;

import dto.RequestDetailsDTO;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import utils.user.RequestsManager;

import java.io.IOException;
import java.util.List;
import java.util.Map;

@WebServlet("/approved-or-deny")
public class ApproveDenyRequestServlet extends HttpServlet {
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String optionParam = req.getParameter("option");
        String username = req.getParameter("username");
        Integer requestID =Integer.parseInt(req.getParameter("requestID"));
        if(optionParam != null){
            Map<String, List<RequestDetailsDTO>> allRequests= ((RequestsManager) getServletContext().getAttribute("requestDetailsManager")).getRequestsPerUser();
            for(RequestDetailsDTO requestDetailsDTO : allRequests.get(username)){
                if(requestDetailsDTO.getRequestNumber() == requestID){
                    requestDetailsDTO.setRequestStatus(optionParam);
                    break;
                }
            }
            resp.getWriter().println("Update status to " + optionParam + " successfully");

        }
    }
}
