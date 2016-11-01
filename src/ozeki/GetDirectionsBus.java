package ozeki;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class GetDirectionsBus{

	//variables	
	
	String origin;
	String destination;
	
	String url;
    String key = "AIzaSyCxOy9h4yCk8XkQjakE_G0qftonnurlMy0";
    
    String data;
    
    String distance;
    String duration;
    
    public ArrayList<String[]> steps = new ArrayList<String[]>();        
        
	//constructors	
	GetDirectionsBus() {
	      		
		}
		
	GetDirectionsBus(String origin, String destination) {
      this.origin = origin;
      this.destination = destination;
      
      url = "https://maps.googleapis.com/maps/api/directions/json?";
	}


	//methods
	
	//generate URL
	void generateURL() {
		url = url + "origin="+ origin + "&" + "destination=" + destination + "&";
		
	    url = url + "mode=transit&transit_mode=bus&transit_routing_preference=fewer_transfers&";
	
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
	        
	        steps.get(i)[0] = cleanSteps(steps.get(i)[0]);
	        
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
        
        	String step[] = new String[8];
        	
        	try {
				
        		object = jarray_steps.getJSONObject(i);
				
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				System.out.println("No objects in Steps main array");				
			}
        	
        	try {
			
        		if(object.has("html_instructions")) {        		        	
        			
        		step[0] = (String) object.get("html_instructions");        		        		        		
								
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

        	try {
        		if(object.has("travel_mode")) {
        			
        			step[1] = (String) object.get("travel_mode");

        			if(step[1].equals("TRANSIT")) {
        				
        				JSONObject temp,temp2;
        				
        				temp = object.getJSONObject("transit_details");
        				temp2 = temp.getJSONObject("departure_stop");
        						
        				step[2] = (String) temp2.get("name"); //gets  transit_details -> departure_stop -> name
        				
        				temp2 = temp.getJSONObject("arrival_stop");
        				step[3]= (String) temp2.get("name"); //gets  transit_details -> arrival_stop -> name
        				
        				step[4] = (String) temp.get("headsign");//gets transit_details -> headsign
        				
        				temp2 = temp.getJSONObject("line");
        				
        				if(temp2.has("name"))
        				step[5] = (String) temp2.getString("name");//gets  transit_details -> line -> name
        				
        				if(temp2.has("short_name"))
        				step[6] = (String) temp2.getString("short_name");//gets  transit_details -> line -> short_name
        				
        				step[7] =  temp.get("num_stops").toString();//gets  transit_details -> num_stops
        				
        			}
        			else if (step[1].equals("WALKING")) {
        						
        				for(int j=2;j<8;j++)
        				step[j] = null;        				        				
        			}
        		}
        	}
        	catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				System.out.println("Error with travel_mode");
			}
        	
        	steps.add(step); //Each step gets added to the steps ArrayList
        	        	
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
        					
        }
					 
		 	
	String createMessage() {
	
		String msg = "";

        //printing steps
        for(int i=0;i<steps.size();i++){

        	   String[] myString= new String[8];
        	   
        	   myString=steps.get(i);
        	  
        	   if(myString[1].equals("TRANSIT")) {
        	   
        		   myString[4] = myString[4].replaceAll("Station","");
        		   myString[4] = myString[4].replaceAll("station","");
        		   
        		   myString[4] = myString[4].replaceAll("Depot","");
        		   myString[4] = myString[4].replaceAll("depot","");
        		   
        		   if(!myString[4].contains("Bus") && myString[6]!=null) {
        			   myString[4] = myString[4].concat(" Bus");
        		   } 
        			   
        		   
        			         		   
        	   //msg += myString[0]+"\n";
        		   
        	   msg += "1) Go to "+ "\""+ myString[2]+ "\""+"\n";
        	   if(myString[6]!=null) {        		
        	   msg += "2) Get into "+ "\""+ myString[4]+"\""+"-"+ myString[6]+"\n";
        	   } else {
        		   msg += "2) Get into "+ "\""+ myString[4]+"\""+"\n";   
        	   }        	  
        	   msg += "3) Get down at "+"\""+ myString[3]+"\""+"\n";
        	   msg += "No.of stops = "+myString[7]+"\n";
        	   
        	  }
        	  }        	           					
		       
        msg += distance+"\n";
        msg += duration+"\n";        
		
		return msg;				
	}
	
	
	}
	
