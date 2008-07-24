package fi.hut.soberit.agilefant.business.impl;

import java.text.SimpleDateFormat;

import fi.hut.soberit.agilefant.business.TimelineBusiness;
import fi.hut.soberit.agilefant.db.ProductDAO;
import fi.hut.soberit.agilefant.exception.ObjectNotFoundException;
import fi.hut.soberit.agilefant.model.Iteration;
import fi.hut.soberit.agilefant.model.Product;
import fi.hut.soberit.agilefant.model.Project;

public class TimelineBusinessImpl implements TimelineBusiness {
    
    private ProductDAO productDAO;

    /** {@inheritDoc} */
    public String productContentsToJSON(int productId) throws ObjectNotFoundException {
        return productContentsToJSON(productDAO.get(productId));
    }

    /** {@inheritDoc} */
    public String productContentsToJSON(Product product) throws ObjectNotFoundException {
        if (product == null) {
            throw new ObjectNotFoundException();
        }
        String json = "{";
        
        /* Add the product details */
        json += "name:'" + stringToJSON(product.getName())
                        + "',id:" + product.getId()
                        + ",type:'product',\n"
        	        + "contents:[ \n";
        
        /* Time formatter */
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        
        /* Get the product's children */
        // TODO: remove the last comma
        for (Project project : product.getProjects()) {
            json += "{name:'" + stringToJSON(project.getName())  + "'," +
            		"id:" + project.getId() + "," + 
            		"type:'project'," +
            		"state:" + project.getStatus().getOrdinal() + "," +
            		"startDate:'" + sdf.format(project.getStartDate()) + "'," +
            		"endDate:'" + sdf.format(project.getEndDate()) + "'" +
                        ",contents:[\n";
            
            /* Get the project's iterations */
            for (Iteration iter : project.getIterations()) {
                json += "{name:'" + stringToJSON(iter.getName())  + "'," +
                    "id:" + iter.getId() + "," + 
                    "type:'iteration'," +
                    "startDate:'" + sdf.format(iter.getStartDate()) + "'," +
                    "endDate:'" + sdf.format(iter.getEndDate()) + "'" +
                    "},\n";    
            }
            if(project.getIterations().size() > 0) {
                json = json.substring(0, json.length() - 2);
            }
            json += "]\n},\n";
        }
        
        /* Remove the trailing comma */
        json = json.substring(0, json.length() - 2);
        
        /* Add the end */
        json += "\n]\n}";
        
        return json;
    }
    
    /**
     * Replace all carriage returns, newlines and quote-marks.
     * @param str
     * @return
     */
    private static String stringToJSON(String str) {
       str = str.replaceAll("'", "\\\\'");
       str = str.replaceAll("\n", "\\\\n");
       str = str.replaceAll("\r", "\\\\r");
       return str;
    }

    public ProductDAO getProductDAO() {
        return productDAO;
    }

    public void setProductDAO(ProductDAO productDAO) {
        this.productDAO = productDAO;
    }
    
    
}
