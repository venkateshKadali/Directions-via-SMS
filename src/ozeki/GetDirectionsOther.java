package ozeki;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class GetDirectionsOther{

	//variables	
	
	String origin;
	String destination;
	
	String url;
    String key = "AIzaSyCxOy9h4yCk8XkQjakE_G0qftonnurlMy0";
    
    String data;
    
    String distance;
    String duration;
    
    public ArrayList<String> steps = new ArrayList<String>();        
        
	//constructors	
    GetDirectionsOther() {
	      		
		}
		
    GetDirectionsOther(String origin, String destination) {
      this.origin = origin;
      this.destination = destination;
      
      url = "https://maps.googleapis.com/maps/api/directions/json?";
	}


	//methods
	
	//generate URL
	void generateURL() {
		url = url + "origin="+ origin + "&" + "destination=" + destination + "&";
		
	    url = url + "mode=driving&transit_routing_preference=fewer_transfers&";
	
        url = url + "key=" + key;				          
        
	}
	
	//read the contents of the URL into a string 
	void readUrl() throws Exception {
	    BufferedReader reader = null;
	    try {
	    	
	    	String urlString = url;
	    	
	        URL url = new URL(urlString);
	        reader = new BufferedReader(new InputStreamReader(url.openStream()));
	        
	        StringBuffer buffer = new StringBuffer();
	        int read;
	        char[] chars = new char[1024];
	        while ((read = reader.read(chars)) != -1)
	            buffer.append(chars, 0, read); 

	        data = buffer.toString();	        
	        
	    } catch (Exception e) {
	    	// TODO Auto-generated catch block
	    	e.printStackTrace();
	    } finally {
	        if (reader != null)
	            reader.close();
	    }
	}
	
	boolean getDataFromJSONString() {
				
		
 JSONObject json_main = null;
 JSONArray jarray_routes = null;
 JSONObject jobj = null;
 
try {
	
	json_main = new JSONObject(data);
	
	if(json_main.has("routes")) {
    jarray_routes = json_main.getJSONArray("routes");
	} else {
		return false;
	}
  	
	if(jarray_routes.length()!=0) {
		jobj = jarray_routes.getJSONObject(0);	
	}else {
       return false;
	}	        
	
    JSONArray jarray_legs =  jobj.getJSONArray("legs");
    		
    JSONObject jobj2 = jarray_legs.getJSONObject(0);	        
    	        
    JSONArray jarray_steps =  jobj2.getJSONArray("steps");
    
    //We are now at Steps Array.
   // This array may have html_instructions or further steps[] which goes on recursively 
    	      
    stepsRecursive(jarray_steps);
   
    getDistance(jobj2);
    
    getDuration(jobj2);
   
        //clean all HTML from steps
	        for(int i=0;i<steps.size();i++){	        
	        
	         steps.set(i,cleanSteps(steps.get(i)));
	        
	        }
	        
	       /* //printing steps
	        for(int i=0;i<steps.size();i++){
	        	   String[] myString= new String[8]; 
	        	   myString=steps.get(i);
	        	   for(int j=0;j<myString.length;j++){
	        		   if(myString[j]!=null) 
	        	      System.out.print(myString[j]+" "); 
	        	   }
	        	   System.out.print("\n");
	        	}	        
	        
	        System.out.println("Distance : " +distance);
	        System.out.println("Duration : " +duration);
	        */
			
		} catch (JSONException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
			System.out.println("Exception - No routes");
		}
		return true; 				
	}
	
	String cleanSteps(String text) {

		if(text!=null) {
		text = text.replaceAll("\\<.*?\\>", "");
		text = text.replaceAll("&nbsp;", "");		
		}
		
		return text;
		
	}
	
	void getDistance(JSONObject obj) {
		
		try {
			
			obj = obj.getJSONObject("distance");
			
			distance = (String) obj.get("text");					
			
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	void getDuration(JSONObject obj) {
		
		try {
			
			obj = obj.getJSONObject("duration");
			
			duration = (String) obj.get("text");					
			
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	void stepsRecursive(JSONArray jarray_steps) {
		
		JSONObject object = null;
		JSONArray subArray;        
		
        for(int i=0;i<jarray_steps.length();i++) {                	
        	
        	try {
				
        		object = jarray_steps.getJSONObject(i);
				
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				System.out.println("No objects in Steps main array");				
			}
        	
        	try {
			
        		if(object.has("html_instructions")) {        		        	
        			
        		steps.add((String) object.get("html_instructions"));        		        		        		
								
        		}
				
        		//can use below snippet if "html_instructions" are too verbose or noisy
				/*if(object.has("maneuver")) {
        		 step = (String) object.get("maneuver");
        		}
        		else {
        		step = (String) object.get("html_instructions");
        		}
        		*/				        		
        		
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				System.out.println("No html_instructions here");
			}

        		}
        	
	
        	try {
				
        		if(object.has("steps")) {
        		subArray = object.getJSONArray("steps");
        		stepsRecursive(subArray);
        		}
        		
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				System.out.println("No Steps sub array here");
			}
        	        
        	
        	}
        					
        
					 
		 	
	String createMessage() {
	
		String msg = "";

        //printing steps
		
		   msg +="distance = "+distance+"\n";
	       msg +="duration = "+duration+"\n";     
	       msg +="no.of.steps = "+steps.size()+"\n\n";
		
        for(int i=0;i<steps.size();i++){        	           	   
        	   msg+=(i+1)+") "+steps.get(i)+" \n";
        }
        	   
     	
		return msg;				
	}
	
	
	}
	
