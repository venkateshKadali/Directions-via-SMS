package ozeki;

import java.sql.ResultSet;
import java.sql.SQLException;

import com.mysql.jdbc.Statement;

public class Ads {

	String origin;
	String destination;
	String place;
	
	String message ="";
	String title;
	String address;
	
	Statement smnt;
	ResultSet rs = null;
	
Ads() {
	
}
	
Ads(String origin, String destination, Statement smnt) {
	this.origin = origin;
	this.destination = destination;
	this.smnt = smnt;
}

boolean queryDB(String place) {
	
	boolean bool = false;
	
	String query = "SELECT * FROM ads WHERE area = '"+place+"';";
	
			try {

				rs = smnt.executeQuery(query);		
				
				if(!rs.isBeforeFirst()) {
					bool = false;
					
				}
				else					
				  { 
					while(rs.next()) {
		              title   = rs.getString("title");
		              address = rs.getString("address");                  
					}
					
					this.place = place;

					bool = true;

		          }
				
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			
			return bool;			
			
}

void getAdsDataFromDB() {
	
	if(!queryDB(origin)) {
		

		
		if(!queryDB(destination)) {
			
			place = null;
			title = null;
			address = null;
		}
	}

}

String createAds() {
	
	message += "Going to "+destination+"?\n";
	message += "Book a Uber Cab NOW and GET Rs.400/- off with coupon:GRAB400\n";
	message += "Call +1-866-576-1039.\n\n";

	if(title!=null && place!=null && address!=null) {
	message += "Please visit "+title+" at "+place+".\n";
	message += address+".\n";
	}
		return message;
}
}
