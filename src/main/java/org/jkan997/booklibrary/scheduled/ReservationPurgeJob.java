package org.jkan997.booklibrary.scheduled;

import java.time.LocalDateTime;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.jcr.Node;
import javax.jcr.NodeIterator;
import javax.jcr.RepositoryException;
import javax.jcr.Session;
import javax.jcr.Value;
import javax.jcr.query.Query;
import javax.jcr.query.QueryManager;
import javax.jcr.query.QueryResult;
import org.apache.sling.jcr.api.SlingRepository;
import org.slf4j.LoggerFactory;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.jkan997.booklibrary.servlet.ImportBooks;

@Component(name = "Reservation Purge Service", service = Runnable.class, immediate = true, property = {"scheduler.period:Long=10"}) // Executed every 10 seconds
public class ReservationPurgeJob implements Runnable {
    
    @Reference
    SlingRepository repository;

    private static final org.slf4j.Logger LOGGER = LoggerFactory.getLogger(ImportBooks.class);

    public void run() {
        LOGGER.info("Executing a perodic job " + getClass().getSimpleName());
        // YOUR IMPLEMENTATION HERE
        try {
            Session jcrSession = repository.loginAdministrative(null);
            
            QueryManager qM = jcrSession.getWorkspace().getQueryManager();
            String QUERY = "SELECT * FROM [nt:unstructured] AS nodes WHERE ISDESCENDANTNODE ([/books]) AND nodes.reserved IS NOT NULL";
            Query query = qM.createQuery(QUERY, Query.JCR_SQL2);
            QueryResult result = query.execute();
            
            NodeIterator resultIter = result.getNodes();
            
            while (resultIter.hasNext()) {
                Node resNode =  resultIter.nextNode();
                LocalDateTime reservedTime = LocalDateTime.parse(resNode.getProperty("reserved").getString());
                if (reservedTime.plusMinutes(1).isBefore(LocalDateTime.now())) {
                    resNode.setProperty("reserved", (Value)null);
                }
            }
            
            jcrSession.save();
            
        } catch (RepositoryException ex) {
            Logger.getLogger(ReservationPurgeJob.class.getName()).log(Level.SEVERE, null, ex);
        }
        
    }

}
