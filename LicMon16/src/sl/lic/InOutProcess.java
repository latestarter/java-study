package sl.lic;

import java.io.*;

public class InOutProcess {

	String licenseStartDate = null;
	
	String inOutList(String logFileName, String logFilePath){
		String inOutList = null;
		String readLine = null;
		
		try {
//			System.out.println("log file Path => " + logFileName);
			BufferedReader input = new BufferedReader(new FileReader(logFileName));

			while((readLine = input.readLine()) != null){
				if ( ( readLine.contains("TIMESTAMP")) || ( readLine.contains("IN:"))
						|| ( readLine.contains("DENIED:")) || ( readLine.contains("OUT:"))){
					if ( inOutList == null ){
						inOutList = readLine + "\r\n";
					} else {
						inOutList = inOutList + readLine + "\r\n";
					}
				}
				if (readLine.contains("(lmgrd) FLEXnet Licensing") ){
					String[] item = readLine.split(" ");
					int itemEnd = item.length;
					licenseStartDate = item[itemEnd - 1];
					licenseStartDate = licenseStartDate.replace("(", "");
					licenseStartDate = licenseStartDate.replace(")", "");
				}
			}
			input.close();
		} catch(IOException ex){
			ex.printStackTrace();
		}
		return inOutList;
	}
	String getStartDate(){
		return licenseStartDate;
	}
}
