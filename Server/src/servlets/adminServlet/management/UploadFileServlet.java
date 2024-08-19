package servlets.adminServlet.management;

import facade.Facade;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.MultipartConfig;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.Part;
import dto.*;
import utils.admin.SimulationsDefinitionsManager;

import javax.xml.bind.JAXBException;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

@MultipartConfig(
        fileSizeThreshold = 1024 * 1024,  // 1 MB
        maxFileSize = 1024 * 1024 * 5,   // 5 MB
        maxRequestSize = 1024 * 1024 * 10 // 10 MB
)

@WebServlet(name="upload file", urlPatterns="/upload-file")
public class UploadFileServlet extends HttpServlet {

    public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
        Part filePart = request.getPart("file1"); // "file1" should match the name attribute of the file input field in your HTML form
        InputStream fileContent = null;
        WorldDefinitionDTO worldDefinitionDTO;
        if (filePart != null) {
            String fileName = filePart.getSubmittedFileName(); //master-ex3.xml
            fileContent = filePart.getInputStream();
            try {
             worldDefinitionDTO = ((Facade)getServletContext().getAttribute("facade")).generatorOperation(fileName, fileContent);
                ((SimulationsDefinitionsManager) getServletContext().getAttribute("simulationsDefinitionsManager"))
                        .addSimulation(worldDefinitionDTO);
            } catch (JAXBException e) {
                response.getWriter().println("An Exception occurred while uploading file. name:\n" + fileName);
                return;
            }



            StringBuilder fileContentString = new StringBuilder();
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(fileContent))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    fileContentString.append(line).append("\n");
                }
            }


            // Print the file content
            response.getWriter().println("File uploaded successfully. Content:\n" + fileContentString.toString());

        } else {
            response.getWriter().println("No file uploaded.");
        }
    }
    public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.getWriter().println("Upload file successfully");
    }
}
