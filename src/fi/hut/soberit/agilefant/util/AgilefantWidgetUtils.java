package fi.hut.soberit.agilefant.util;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import fi.hut.soberit.agilefant.model.AgilefantWidget;
import fi.hut.soberit.agilefant.model.WidgetCollection;

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
    
    public static WidgetCollection getMockWidgetCollection() {
        WidgetCollection mock = new WidgetCollection();
        mock.setName("Foo widgets");
        
        AgilefantWidget textWidget = new AgilefantWidget();
        textWidget.setListNumber(0);
        textWidget.setPosition(0);
        textWidget.setType("text");
        textWidget.setId(123);
        mock.getWidgets().add(textWidget);
        
        AgilefantWidget textWidget2 = new AgilefantWidget();
        textWidget2.setListNumber(1);
        textWidget2.setPosition(1);
        textWidget2.setType("text");
        textWidget2.setId(124);
        mock.getWidgets().add(textWidget2);
        
        AgilefantWidget burndownWidget = new AgilefantWidget();
        burndownWidget.setListNumber(0);
        burndownWidget.setPosition(1);
        burndownWidget.setType("burndown");
        burndownWidget.setId(125);
        mock.getWidgets().add(burndownWidget);
        
        AgilefantWidget burndownWidget2 = new AgilefantWidget();
        burndownWidget2.setListNumber(1);
        burndownWidget2.setPosition(0);
        burndownWidget2.setType("burndown");
        burndownWidget2.setId(126);
        mock.getWidgets().add(burndownWidget2);
        
        return mock;
    }
}
