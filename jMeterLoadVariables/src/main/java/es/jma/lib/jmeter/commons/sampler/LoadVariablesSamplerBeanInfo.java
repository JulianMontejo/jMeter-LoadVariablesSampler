package es.jma.lib.jmeter.commons.sampler;

import java.beans.PropertyDescriptor;

import org.apache.jmeter.testbeans.BeanInfoSupport;
import org.apache.jmeter.testbeans.gui.TypeEditor;

/**
 * Class that define the graphic interface
 * @author Julian Montejo
 *
 */
public class LoadVariablesSamplerBeanInfo extends BeanInfoSupport {
	
	/** If not null the file containing the json to load **/
	private static final String PATH_TO_FILE = "pathToFile";
	
	/** json to load in the environment **/
	private static final String JSONTOLOAD = "jsonToLoad";

	public LoadVariablesSamplerBeanInfo() {
		super(LoadVariablesSampler.class);
		createPropertyGroup("File to load",
	            new String[]{PATH_TO_FILE});
		
		createPropertyGroup("JSON to load",
	            new String[]{JSONTOLOAD});
		
		PropertyDescriptor p;
		p = property(PATH_TO_FILE, TypeEditor.FileEditor);
        p.setValue(NOT_UNDEFINED, Boolean.TRUE);
        p.setValue(DEFAULT, "");
        
		p = property(JSONTOLOAD, TypeEditor.TextAreaEditor);
        p.setValue(NOT_UNDEFINED, Boolean.TRUE);
        p.setValue(MULTILINE, Boolean.TRUE);
        p.setValue(DEFAULT, "");
        
	}

}
