package es.jma.lib.jmeter.commons.sampler;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;

import org.apache.commons.io.IOUtils;
import org.apache.jmeter.engine.util.CompoundVariable;
import org.apache.jmeter.functions.InvalidVariableException;
import org.apache.jmeter.samplers.AbstractSampler;
import org.apache.jmeter.samplers.Entry;
import org.apache.jmeter.samplers.SampleResult;
import org.apache.jmeter.testbeans.TestBean;
import org.apache.jmeter.threads.JMeterContextService;
import org.apache.jmeter.threads.JMeterVariables;
import org.slf4j.LoggerFactory;
import org.slf4j.Logger;

import net.minidev.json.JSONArray;
import net.minidev.json.JSONObject;
import net.minidev.json.JSONValue;

public class LoadVariablesSampler extends AbstractSampler implements TestBean
{
	/**
	 * This class allows to load variables in jMeter from a json defined in the sampler or in a file
	 * @author Julian Montejo
	 */
	private static final long serialVersionUID = 1L;

	private static final Logger log = LoggerFactory.getLogger(LoadVariablesSampler.class);
	
	/** If not null the file containing the json to load **/
	private String pathToFile;
	
	/** json to load in the environment **/
	private String jsonToLoad;
	
	
	public void setPathToFile(String pathToFile){
		this.pathToFile = pathToFile;
	}
	
	public String getPathToFile(){
		return this.pathToFile;
	}
	
	public void setJsonToLoad(String jsonToLoad){
		this.jsonToLoad = jsonToLoad;
	}
	
	public String getJsonToLoad(){
		return this.jsonToLoad;
	}
	
	public String getTitle(){
        return getName();
    }
	
	/**
	 * Creates the variable in the environment
	 * @param name variable name
	 * @param value variable value
	 * @return informative string
	 */
	private String putVar(String name, Object value){
		JMeterVariables vars = JMeterContextService.getContext().getVariables();
		String inserted=name+"\t";
		
		vars.putObject(name, value);
		if (value!= null )
			inserted+=""+value;
		else
			inserted+="null";
		return inserted+"\n";
	}
	
	/**
	 * Return the parameters of the sampler in a structured way
	 * @return informative string
	 */
	private String passedParameters(){
        StringBuilder devolver = new StringBuilder();
        devolver.append("File to load: "+pathToFile+"\n");
        devolver.append("\nJson to load:\n"+jsonToLoad+"\n");
        return devolver.toString();
    }
	
	/**
	 * Return the parameters of the sampler in a structured way
	 * @param loadedJson json already loaded
	 * @return
	 */
	private String passedParameters(String loadedJson){
        StringBuilder devolver = new StringBuilder();
        devolver.append("File to load: "+pathToFile+"\n");
        devolver.append("\nJson loaded from file:\n"+loadedJson+"\n");
        return devolver.toString();
    }
	
	/**
	 * Sampler creation
	 */
    public SampleResult sample(Entry entry) {
        StringBuilder devolver = new StringBuilder();
        boolean IsSuccess = true;
        SampleResult retunedSR=new SampleResult();
        retunedSR.sampleStart();
        try {
        	retunedSR.setSamplerData(passedParameters());
            Object  jsonSource =null;
            /*
             * If a file has to be loaded, load it in a string and convert inner variables and functions
             */
            if (pathToFile !=null && pathToFile.trim().length()>0){
            	InputStream fichero = new FileInputStream(pathToFile.trim());
            	retunedSR.setSamplerData(passedParameters(
            			IOUtils.toString(new FileInputStream(pathToFile.trim()), "UTF8"))); 
            	StringWriter writer = new StringWriter();
            	IOUtils.copy(fichero, writer, "UTF8");
            	String fileString = writer.toString();
            	CompoundVariable var = new CompoundVariable("__FILE_STRING__");
                try {
    				var.setParameters(fileString);
    				 setJsonToLoad(var.execute());
    			} catch (InvalidVariableException e) {
    				setJsonToLoad(fileString);
    			}
            } 
        	
            jsonSource = JSONValue.parseWithException(getJsonToLoad());
            
            
            Object jsonLoaded = jsonSource;
            
            JSONArray jsonToProcess = new JSONArray();
            
            if (jsonLoaded instanceof JSONObject){
            	jsonToProcess.add((JSONObject)jsonLoaded);
            } else {
            	jsonToProcess = (JSONArray) jsonLoaded;
            }
            
            for (int i=0; i<jsonToProcess.size();i++){
            	JSONObject jsonElement= (JSONObject) jsonToProcess.get(i);
	            for (java.util.Map.Entry<String, Object> key : jsonElement.entrySet()) {
	            	devolver.append(putVar(key.getKey(), key.getValue()));
	            }
            }
            
            
        }catch (RuntimeException e) {
            devolver.append("\nError:"+e.getMessage()+"\n\n");
            devolver.append(e);
            IsSuccess = false;
             retunedSR.setResponseMessage(e.getMessage());
            log.error("Error in LoadVariablesSampler:", e);
         } catch (net.minidev.json.parser.ParseException e) {
        	 devolver.append("\nError parsing:"+e.getMessage()+"\n\n");
             devolver.append(e);
             IsSuccess = false;
             retunedSR.setResponseMessage(e.getMessage());
            log.error("Parse error:", e);
		} catch (FileNotFoundException e) {
			devolver.append("\nError file not found:"+pathToFile
					+"\n"+e.getMessage()+"\n\n");
            devolver.append(e);
            IsSuccess = false;
            retunedSR.setResponseMessage(e.getMessage());
           log.error("File not found:"+pathToFile
					+"\n", e);
		} catch (IOException e) {
			devolver.append("\nError loading file:"+e.getMessage()+"\n\n");
            devolver.append(e);
            IsSuccess = false;
            retunedSR.setResponseMessage(e.getMessage());
           log.error("File load error:", e);
		}finally {
             retunedSR.sampleEnd();
             retunedSR.setSuccessful(IsSuccess);
         }
         retunedSR.setDataType("text");
         retunedSR.setResponseData(devolver.toString().getBytes());
         retunedSR.setSampleLabel(getName());
         return retunedSR;
    }
}
