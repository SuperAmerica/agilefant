package fi.hut.soberit.agilefant.util;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class AgilefantWidgetUtils {

    private static final Map<String, String> typeToAction = mapActions();
    
    private static Map<String, String> mapActions() {
        Map<String,String> actions = new HashMap<String, String>();
        
        actions.put("text", "static/html/widgets/textWidget.html");
        actions.put("burndown", "static/html/widgets/burndownWidget.html");
        
        return Collections.unmodifiableMap(actions);
    }
    
    public static final String getActionForType(String type) {
        return typeToAction.get(type);
    }
    
}
