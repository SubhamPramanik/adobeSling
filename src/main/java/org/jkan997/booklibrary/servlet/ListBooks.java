package org.jkan997.booklibrary.servlet;

import com.google.gson.stream.JsonWriter;
import java.io.PrintWriter;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.jcr.Node;
import javax.jcr.NodeIterator;
import javax.jcr.Session;
import javax.servlet.Servlet;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Reference;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.servlets.HttpConstants;
import org.apache.sling.api.servlets.SlingSafeMethodsServlet;
import org.apache.sling.jcr.api.SlingRepository;
import org.slf4j.LoggerFactory;

@Component(service = Servlet.class, property = {
    "sling.servlet.methods=" + HttpConstants.METHOD_GET,
    "sling.servlet.paths=" + "/bin/ListBooks"
})
public class ListBooks extends SlingSafeMethodsServlet {

    private static final org.slf4j.Logger LOGGER = LoggerFactory.getLogger(ListBooks.class);

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
            Session jcrSession = repository.loginAdministrative(null);
            Node rootNode = jcrSession.getRootNode();
            Node booksNode = rootNode.getNode("books");
            Node fictionNode = booksNode.getNode("fiction");
            Node nonFictionNode = booksNode.getNode("nonfiction");
            
            NodeIterator fictionBooks = fictionNode.getNodes();
            NodeIterator nonFictionBooks = nonFictionNode.getNodes();
            
            JsonWriter writer = new JsonWriter(wrt);
            writer.beginArray();
            while(fictionBooks.hasNext()) {
                Node childBook = fictionBooks.nextNode();
                writer.beginObject();
                writer.name("title").value(childBook.getProperty("title").getString());
                writer.name("author").value(childBook.getProperty("author").getString());
                writer.name("genre").value(childBook.getParent().getName());
                writer.name("path").value(childBook.getPath());
                writer.endObject();
            }
            jcrSession.save();
            writer.endArray();
            writer.close();
            wrt.close();
            
            
            
            
            
        } catch (Exception ex) {
            throw new RuntimeException ("Following Exception tok place: " + ex); 
        }
        
        throw new RuntimeException("" + getClass().getCanonicalName() + " not implemented yet.");
    }

}
