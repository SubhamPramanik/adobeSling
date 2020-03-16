package org.jkan997.booklibrary.servlet;

import com.google.gson.stream.JsonWriter;
import java.io.PrintWriter;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import javax.jcr.Node;
import javax.jcr.NodeIterator;
import javax.jcr.Session;
import javax.jcr.Value;
import javax.servlet.Servlet;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Reference;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.request.RequestPathInfo;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ResourceResolverFactory;
import org.apache.sling.api.servlets.HttpConstants;
import org.apache.sling.api.servlets.SlingSafeMethodsServlet;
import org.apache.sling.jcr.api.SlingRepository;
import org.slf4j.LoggerFactory;

@Component(service = Servlet.class, property = {
    "sling.servlet.resourceTypes=" + "book",
    "sling.servlet.extensions=" + "reserve",
    "sling.servlet.extensions=" + "unreserve",
    "sling.servlet.extensions=" + "rent",
    "sling.servlet.extensions=" + "return"
})
public class ModifyBookState extends SlingSafeMethodsServlet {

    private static final org.slf4j.Logger LOGGER = LoggerFactory.getLogger(ModifyBookState.class);

    @Reference
    SlingRepository repository;
    
    @Activate
    protected void activate(Map<String, Object> props) {
        LOGGER.info("Activating " + this.getClass().getSimpleName());
    }
    
    @Override
    protected void doGet(SlingHttpServletRequest request, SlingHttpServletResponse response) {
        /*
        YOUR IMPLEMENTATION HERE
         */
        try {
            
            response.setContentType("application/json");
            PrintWriter wrt = response.getWriter();
            JsonWriter writer = new JsonWriter(wrt);
            
            Session session = request.getResourceResolver().adaptTo(Session.class);
            String resourcePath = request.getResource().getPath();
            if (session.itemExists(resourcePath)) {
                Node source = session.getNode(resourcePath);
                
                if (request.getPathInfo().contains(".reserve")) {
                    LocalDateTime current = LocalDateTime.now();
                    source.setProperty("reserved", current.toString());
                    session.save();
                    writer.beginObject();
                    writer.name("action").value("reserve seccessful");
                    writer.endObject();
                } else if (request.getPathInfo().contains(".unreserve")) {
                    source.setProperty("reserved", (Value)null);
                    session.save();
                    writer.beginObject();
                    writer.name("action").value("unreserve seccessful");
                    writer.endObject();
                }
            } else {
                writer.beginObject();
                writer.name("error").value("item not found");
                writer.endObject();
            }

        } catch (Exception e) {
            throw new RuntimeException("URIPath: " + request.getRequestURI() + "" + getClass().getCanonicalName() + " not implemented yet.");
        }
        
    }

}
