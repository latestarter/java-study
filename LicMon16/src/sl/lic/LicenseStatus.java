package sl.lic;

import java.io.*;
import java.util.*;

public class LicenseStatus {

	String currentUsers = "";

	BufferedReader runLMStat(String licensePath){
		// lmutil lmstat -a 를 읽어서 buffer에 저장?
		String runCommand = "\"" + licensePath + "\\lmutil\" lmstat -a";
		System.out.println("run command" + runCommand);
		BufferedReader br = null;
		try{
			Process p = Runtime.getRuntime().exec(runCommand);
			br = new BufferedReader(new InputStreamReader(p.getInputStream()));
/*			String line = null;
			while((line = br.readLine()) != null){
				System.out.println(line);
			}
*/
		} catch (Exception e){
			System.err.println(e);
		}
		return br;
	}
	String runExtractLicense(BufferedReader statusOut){
		String dateKeyWord = "Flexible License Manager status on";

		String resultString = null;
		String line = null;
		String resultLine = null;
		String dateString = null;
		String timeString = null;
        String productString = null;

		try {
			while((line = statusOut.readLine()) != null){
				if ( line.contains(dateKeyWord)){
					dateString = line.split(" ")[6];
					timeString = line.split(" ")[7];
				}
				if ( line.contains("Users of")){
					productString = line.split(" ")[2];
					productString = productString.replace(":", "");
					String totalCountString = line.split(" ")[6];
					String useCountString = line.split(" ")[12];
					
					resultLine = dateString + "," +
								 timeString + "," +
								 productString + "," +
								 totalCountString + "," +
								 useCountString +"\r\n";
					if ( resultString != null ){
					resultString = resultString + resultLine;
					} else {
						resultString = resultLine;
					}
				}
				if ( line.contains("start ")){
					String userID = line.split(" ")[4];
					String systemID = line.split(" ")[5];
					String outDate = line.split(" ")[13];
					String outTime = line.split(" ")[14];
					String lastedTime = "계산필요";

					resultLine = dateString + "," +
								 timeString + "," +
								 productString + "," +
								 userID + "," +
								 systemID + "," +
								 outDate + "," +
								 outTime + "," +
								 lastedTime +"\r\n";
					if ( currentUsers != null ){
						currentUsers = currentUsers + resultLine;
					} else {
						currentUsers = resultLine;
					}
				}
			}
		} catch (Exception e){
			System.err.println(e);
		}
		return resultString;
	}
	String getProductList(String statusResult){
		String productList = null;
		
		String[] line = statusResult.split("\r\n");
		int lineSize = line.length;
		
		for ( int i = 0 ; i < lineSize ; i++){
			String productName = line[i].split(",")[2];
			if ( productList == null){ 
				productList = productName;
			} else {
				productList = productList + "," + productName;
			}
		}
		return productList;
	}
	String getCurrentUsers(){
		Calendar fromDateTime = Calendar.getInstance();
		Calendar toDateTime = Calendar.getInstance();
			
		String returnString = null;
		String[] line = currentUsers.split("\r\n");
		int lineSize = line.length;
		int fromYear = 0;
		int fromMonth = 0;
		int fromDate = 0;
		int fromHour = 0;
		int fromMinute = 0;
		int toYear = 0;
		int toMonth = 0;
		int toDate = 0;
		int toHour = 0;
		int toMinute = 0;

		if ( lineSize > 0){
			for ( int i= 0; i < lineSize; i++){
				String[] items = line[i].split(",");
				int itemsSize = items.length;
				if ( itemsSize > 5 ) {
					for ( int j=0;j < itemsSize; j++){
						if (j == 0) {
							String tempMonth = items[j].split("/")[0];
							String tempDate = items[j].split("/")[1];
							String tempYear = items[j].split("/")[2];
							toYear = Integer.parseInt(tempYear);
							toMonth = Integer.parseInt(tempMonth);
							toDate = Integer.parseInt(tempDate);								
						}
						if ( j == 1){
							String tempHour = items[j].split(":")[0];
							String tempMinute = items[j].split(":")[1];
							toHour = Integer.parseInt(tempHour);
							toMinute = Integer.parseInt(tempMinute);
						}
						if ( j == 5 ){
							String tempMonth = items[j].split("/")[0];
							String tempDate = items[j].split("/")[1];
							fromYear = toYear;  // 같은 년도로 계산함
							fromMonth = Integer.parseInt(tempMonth);
							fromDate = Integer.parseInt(tempDate);								
						}
						if ( j == 6 ){
							String tempHour = items[j].split(":")[0];
							String tempMinute = items[j].split(":")[1];
							fromHour = Integer.parseInt(tempHour);
							fromMinute = Integer.parseInt(tempMinute);
						}
					}    // for end
					fromDateTime.set(fromYear, fromMonth, fromDate, fromHour, fromMinute);
					toDateTime.set(toYear, toMonth, toDate, toHour, toMinute);
				}
				int lastedMinute = (int)((toDateTime.getTimeInMillis() - fromDateTime.getTimeInMillis())/(1000*60));
				line[i] = line[i].replace("계산필요", Integer.toString(lastedMinute));
				if ( returnString != null ){
					returnString = returnString + line[i];
				} else {
					returnString = line[i];
				}
			}      // for end
		}
		return returnString;
	}
}
