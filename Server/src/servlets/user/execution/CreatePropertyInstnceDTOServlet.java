package servlets.user.execution;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import dto.PropertyDefinitionDTO;
import dto.PropertyInstanceDTO;
import facade.Facade;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

@WebServlet("/create-property-instance")
public class CreatePropertyInstnceDTOServlet extends HttpServlet {
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String propertyDefinition = req.getParameter("propertyDefinition");
        String value = req.getParameter("value");
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        PropertyDefinitionDTO propertyDefinitionDTO = gson.fromJson(propertyDefinition, PropertyDefinitionDTO.class);
        Object valueDTO = gson.fromJson(value, Object.class);
        PropertyInstanceDTO propertyInstanceDTO = ((Facade) getServletContext().getAttribute("facade")).createPropertyInstance(propertyDefinitionDTO, valueDTO);
        String jsonOutput = gson.toJson(propertyInstanceDTO);
        resp.setContentType("application/json");
        resp.getWriter().write(jsonOutput);
    }
}
