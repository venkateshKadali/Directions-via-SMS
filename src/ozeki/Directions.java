package ozeki;



import java.sql.DriverManager;
import java.sql.ResultSet;

import com.mysql.jdbc.Connection;
import com.mysql.jdbc.Statement;


public class Directions {

	/**
	 * @param args
	 * @throws Exception 
	 */
	
	   static String origin;
       static String destination;
       static String mode;
		
	public static void main(String[] args) throws Exception {
		// TODO Auto-generated method stub

		Statement smnt = null ;
		Connection conn = null;
		
         try {
                Class.forName("com.mysql.jdbc.Driver").newInstance();
                
                ResultSet rs = null;
                smnt     = null;;
                
                String dbHost      = "localhost";
                String database    = "ozeki";
                String dbUsername  = "nikhil";
                String dbPassword  = "123456";
                
                String receiver = null;;
                String message = null;
                int id;
    
                String dbUrl = "jdbc:mysql://"+ dbHost +"/" + database +"";

                conn = (Connection) DriverManager.getConnection(dbUrl, dbUsername, dbPassword);
                smnt = (Statement) conn.createStatement();
               
                while(true){
                
                String query = "SELECT * FROM ozekimessagein where received = 'false'  LIMIT 1";
                
                rs = smnt.executeQuery(query);               
                
                
                	if(rs.next()) {
                	if(rs.getString("received").equals("false")) {
                		 //new message
                		
                        //query = "SELECT * FROM ozekimessagein ORDER BY id DESC LIMIT 1";                                
                       
                       //rs = smnt.executeQuery(query);

                    // iterate through the java resultset
                         
                         receiver = rs.getString("sender");
                         message = rs.getString("msg");
                         id = rs.getInt("id");
                        
                         // print the results
                         System.out.format("%s\n %s\n", receiver, message);
                       
         			
       		//String message =  "JNTU to hitechcity bike";
       		
       		System.out.println("Original message : "+ message);
       		
       		   String message_reply_dir = null;
                  String message_reply_ads = null;
               		
               
       if(parseMessage(message)) { //Obtain origin and destination from message

       if(mode.equalsIgnoreCase("car") || mode.equalsIgnoreCase("bike") || mode.equalsIgnoreCase("bicycle") 	
           || mode.equalsIgnoreCase("cycle") || mode.equalsIgnoreCase("auto") || mode.equalsIgnoreCase("lorry")
           || mode.equalsIgnoreCase("truck")) {
       	//get directions for the above mentioned vehicles
       	GetDirectionsOther obj = new GetDirectionsOther(origin,destination);
       	
       	obj.generateURL(); //generates URL from given data

       	obj.readUrl(); //reads data from the URL


       	if(obj.getDataFromJSONString()) { //get required data from obtained JSON string    
       		message_reply_dir = obj.createMessage();
       	 
            Ads object = new Ads(origin, destination, smnt);
            object.getAdsDataFromDB();
            message_reply_ads = object.createAds();

           System.out.println(message_reply_dir);
           System.out.println();
           System.out.println(message_reply_ads);
                    
           sendMessage(message,receiver,message_reply_dir,smnt,conn);
           sendMessage(message,receiver,message_reply_ads,smnt,conn);


           //Now for this row, update the 'received' column of ozekimessagein table

           String sqlUpdate =
                   "UPDATE "+
           "ozekimessagein "+
           "SET "+
           "received = "+"true"+" WHERE "+"id = "+id;

           if(smnt.executeUpdate(sqlUpdate) != 0)
           {
                   System.out.println("Updated 'received' column");

           }
           else
           {
                   System.out.println("ERROR in Updating 'received' column");
           }
                           	
       	}
       	else{
       		message_reply_dir ="Sorry, we couldn't get your required directions. Please send the message in following format :\n" +
       				"<Origin> to <Destination> [vehicle type - optional]. Eg:- Miyapur to Secunderabad car \n";
            sendMessage(message,receiver,message_reply_dir,smnt,conn);
       	}
       	
       } else {
       	//get directions for bus
          // also gets called when mode is not specified
       	
       	GetDirectionsBus obj = new GetDirectionsBus(origin,destination);
       	
       	obj.generateURL(); //generates URL from given data

       	obj.readUrl(); //reads data from the URL

       	if(obj.getDataFromJSONString()) { //get required data from obtained JSON string    
       		message_reply_dir = obj.createMessage();
       	 
            Ads object = new Ads(origin, destination, smnt);
            object.getAdsDataFromDB();
            message_reply_ads = object.createAds();

           System.out.println(message_reply_dir);
           System.out.println();
           System.out.println(message_reply_ads);
                    
           sendMessage(message,receiver,message_reply_dir,smnt,conn);
           sendMessage(message,receiver,message_reply_ads,smnt,conn);


           //Now for this row, update the 'received' column of ozekimessagein table

           String sqlUpdate =
                   "UPDATE "+
           "ozekimessagein "+
           "SET "+
           "received = "+"true"+" WHERE "+"id = "+id;

           if(smnt.executeUpdate(sqlUpdate) != 0)
           {
                   System.out.println("Updated 'received' column");

           }
           else
           {
                   System.out.println("ERROR in Updating 'received' column");
           }
                           	
       	}
       	else{
       		message_reply_dir ="Sorry, we couldnot get your required directions. Please send the message in following format :\n" +
       				"<Origin> to <Destination> [vehicle type - optional]. Eg:- Miyapur to Secunderabad car \n";
            sendMessage(message,receiver,message_reply_dir,smnt,conn);
            

            //Now for this row, update the 'received' column of ozekimessagein table

            String sqlUpdate =
                    "UPDATE "+
            "ozekimessagein "+
            "SET "+
            "received = "+"true"+" WHERE "+"id = "+id;

            if(smnt.executeUpdate(sqlUpdate) != 0)
            {
                    System.out.println("Updated 'received' column");

            }
            else
            {
                    System.out.println("ERROR in Updating 'received' column");
            }

       	}
       	
       }
                       	
                } else {
                	message_reply_dir ="Your request doesnot contain <to> keyword. Please send the message in following format :\n" +
               				"<Origin> to <Destination> [vehicle type - optional]. Eg:- Miyapur to Secunderabad car \n";
                    sendMessage(message,receiver,message_reply_dir,smnt,conn);
                    

                    //Now for this row, update the 'received' column of ozekimessagein table

                    String sqlUpdate =
                            "UPDATE "+
                    "ozekimessagein "+
                    "SET "+
                    "received = "+"true"+" WHERE "+"id = "+id;

                    if(smnt.executeUpdate(sqlUpdate) != 0)
                    {
                            System.out.println("Updated 'received' column");

                    }
                    else
                    {
                            System.out.println("ERROR in Updating 'received' column");
                    }

                	}           	
                }
                	} else {
                		System.out.println("No New Messages\n");
                	
                	}
                	Thread.sleep(10*1000);
                }
          
         }finally {
             smnt.close();
          	 conn.close();        
         }
 
         }
        

    static void sendMessage(String message, String receiver, String message_reply_dir, Statement smnt, Connection conn) {
   	 try{        
//Inserting into ozekimessageout table   		 

   		 message_reply_dir = message_reply_dir.replaceAll("\'", "");   		
   		
   		 
        String sqlInsert =
                "INSERT INTO "+
"ozekimessageout (receiver,msg,status) "+
"VALUES "+
"('" + receiver + "','"+ message_reply_dir +"','send')";

        if(smnt.executeUpdate(sqlInsert) != 0)
        {
                System.out.println("OK");

        }
        else
        {
                System.out.println("ERROR");
        }

         
        
     }
catch(Exception ex)
{
        System.out.println("Exception: " + ex.getMessage());
}
finally {
	 
}
}
         
	
	static boolean parseMessage(String message) {
		
		message = message.trim();
		
		String delimeter;						
		
	// check if message contains " to " or " TO " or " To " or " tO "
		if (message.contains(" to ")) {
        
			delimeter = " to ";
		} 
		else if (message.contains(" TO ")) {			
			delimeter = " TO ";	 
			}
		else if (message.contains(" To ")) {			
			delimeter = " To ";
		}
		else if (message.contains(" tO ")) {			
			delimeter = " tO ";
		} 
		else {
			
		    //throw new IllegalArgumentException("String " + message + " does not contain \"to\" ");
			return false;
		}
				
		
	 //Split the message accordingly	
		
		String[] parts = message.split(delimeter);
		
		origin = parts[0]; 
		destination = parts[1];
		
	//Destination should be split too, since we have 'mode' now
    			
		int index = destination.lastIndexOf(" ");
		
		mode = destination.substring(index+1);

		if(mode.equalsIgnoreCase("bus") || mode.equalsIgnoreCase("car") || mode.equalsIgnoreCase("bike") || mode.equalsIgnoreCase("bicycle") 	
			    || mode.equalsIgnoreCase("cycle") || mode.equalsIgnoreCase("auto") || mode.equalsIgnoreCase("lorry")
			    || mode.equalsIgnoreCase("truck")) {
			//proper mode
		   // can delete mode from destination
		destination = destination.replace(mode,""); //remove mode from destination
			
		} else {
			//improper mode or no mode
		   // set mode to bus
		   mode = "bus";
		}			
	
   //Should also consider the fact that names of places may have spaces in between
		
		origin = origin.replaceAll(" ", "");
		destination = destination.replaceAll(" ", "");		
		
		System.out.println("origin : "+origin);
		System.out.println("destination : "+destination);
		System.out.println("mode : "+mode);
		System.out.println();
		
		return true;
	}

}
