package fi.hut.soberit.agilefant.business;

import org.easymock.IArgumentMatcher;

import fi.hut.soberit.agilefant.model.Setting;

/**
 * An argument matcher for EasyMock. Compares two Setting objects. These kinds of
 * classes can be used in unit tests when working with EasyMock. See EasyMock 
 * documentation for instructions. The section is called
 * Defining own Argument Matchers. 
 * @see http://www.easymock.org/EasyMock2_0_Documentation.html
 * 
 * @author Vesa Pirila / Spider
 *
 */
public class SettingEquals implements IArgumentMatcher {
    private Setting expected;
    
    public SettingEquals(Setting expected){
        this.expected = expected;
    }
    
    public void appendTo(StringBuffer buffer) {
        buffer.append(expected.getName() + ", " + expected.getValue());
    }

    public boolean matches(Object actual) {
        if(!(actual instanceof Setting)){
            return false;    
        }
        String name = ((Setting)actual).getName();
        String value = ((Setting)actual).getValue();
        
        return expected.getName().equals(name) && expected.getValue().equals(value);
    }

}
