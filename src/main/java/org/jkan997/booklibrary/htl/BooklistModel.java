package org.jkan997.booklibrary.htl;

import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.jcr.query.Query;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.models.annotations.DefaultInjectionStrategy;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.injectorspecific.SlingObject;
import org.slf4j.LoggerFactory;

@Model(adaptables = SlingHttpServletRequest.class,
        defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL)
public class BooklistModel {
    
    private static final org.slf4j.Logger LOGGER = LoggerFactory.getLogger(BooklistModel.class);
    
    @SlingObject
    private SlingHttpServletRequest request;
    
    private List<Book> book = new ArrayList<Book>();
    
    public String getDate() {
        return (new Date()).toString();
    }

    /*
        YOUR HTL MODEL HERE HERE
     */
    
    public List<Book> getBook() {
        LOGGER.info("getBook called");
        return book;
    }
    
    @PostConstruct
    protected void init() {
        LOGGER.info("Inside PC called");
        try {
            String QUERY = "SELECT * FROM [nt:unstructured] WHERE ISDESCENDANTNODE ([/books])";
            Iterator<Resource> result = request.getResourceResolver().findResources(QUERY, Query.JCR_SQL2);
            LOGGER.info("Inside PostConstruct");
            
            while(result.hasNext()) {
                Book bookItem = result.next().adaptTo(Book.class);
                if (book != null)
                    LOGGER.info(bookItem.getTitle());
                    book.add(bookItem);
            }
        } catch (Exception ex) {
            LOGGER.error("Exception caused: " + ex);
        }
        
    }
    
}
