package contextInitialize;

import facade.Facade;
import jakarta.servlet.ServletContextEvent;
import jakarta.servlet.ServletContextListener;
import jakarta.servlet.annotation.WebListener;
import utils.admin.SimulationsDefinitionsManager;
import utils.user.RequestsManager;
import utils.user.SimulationUserManager;

@WebListener
public class ContextInitialize implements ServletContextListener {
    @Override
    public void contextInitialized(ServletContextEvent servletContextEvent) {
        servletContextEvent.getServletContext()
                .setAttribute("simulationsDefinitionsManager",  new SimulationsDefinitionsManager());
        servletContextEvent.getServletContext().setAttribute("facade",new Facade());
        servletContextEvent.getServletContext().setAttribute("requestDetailsManager", new RequestsManager());
        servletContextEvent.getServletContext().setAttribute("requestsCounter", 1);
        servletContextEvent.getServletContext().setAttribute("simulationUserManager", new SimulationUserManager());


    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {
        ServletContextListener.super.contextDestroyed(sce);
    }
}
