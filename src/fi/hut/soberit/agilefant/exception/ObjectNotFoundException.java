package fi.hut.soberit.agilefant.exception;

/**
 * Suggestion how exceptions could be handled.
 * TODO: 
 * Design Exception architecture: 
 * Decide for example, how we want to handle situations where
 * id's are passed to queries but no object is found for that id.
 * -> Now results a null pointer exception
 * 
 * @author hhaataja, rstrom
 */
public class ObjectNotFoundException extends RuntimeException {

    private static final long serialVersionUID = 4587925916379254562L;
    
    private String i18nkey;

    public ObjectNotFoundException(){
        super();
    }

    public ObjectNotFoundException(String msg){
        super(msg);
    }
    
    public ObjectNotFoundException(String msg, String i18nkey){
        super(msg);
        this.i18nkey = i18nkey;
    }
    
    public String getI18nKey() {
        return this.i18nkey;
    }
}
