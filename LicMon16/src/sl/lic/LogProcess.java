package sl.lic;

public class LogProcess {

	int productCount = 0;
	
	String makeOutput(String productList, String inOutList, String licenseStartDate){
		String outputString = null;
		
		String[] productName = productList.split(",");
		productCount = productName.length;
		int [] userCount = new int[productCount];
		
		for ( int i=0; i < productCount; i++){
			userCount[i] = 0;
		}
		String[] line = inOutList.split("\r\n");
		int lineSize = line.length;
		
		String outDate = licenseStartDate;
		for ( int i = 0 ; i < lineSize ; i++){
			// �տ� �����̽� ���� 
			line[i] = line[i].trim();
			if ( line[i].contains("TIMESTAMP")){
				outDate = line[i].split(" ")[4];	
				/*
				outDate = line[i].split("TIMESTAMP ")[1];
				if ( outputString == null){
					outputString = outDate + ", , ,";
				} else {
					outputString = outputString + outDate + ", , ,"; // �ð�, System ID �߰�
				}
				for ( int j = 0; j < productCount; j++){
					outputString = outputString + Integer.toString(userCount[j]) + ",";
				}
				// ������ ","�� �����ϰ� \r\n�� �־���
					int outputLength = outputString.length();
					outputString = outputString.substring(0, outputLength - 1);
					outputString = outputString + "\r\n";
				*/
			}
			if ( line[i].contains("OUT:")){
				for ( int j = 0; j < productCount; j++){
					if ( line[i].contains( productName[j])) {
						userCount[j]++;
					}
				}
				// ���
				if ( outputString == null) {
					outputString = outDate + ",";
				} else {
					outputString = outputString + outDate + ",";
				}
				outputString = outputString + line[i].split(" ")[0] + ",,";
				outputString = outputString + line[i].split(" ")[4] + ",";
				for ( int j = 0; j < productCount; j++){
					outputString = outputString + Integer.toString(userCount[j])+ ",";
				}
				// ������ ","�� �����ϰ� \r\n�� �־���
				int outputLength = outputString.length();
				outputString = outputString.substring(0, outputLength - 1);
				outputString = outputString + "\r\n";
			}
			if ( line[i].contains("IN:")){
				for ( int j = 0; j < productCount; j++){
					if ( line[i].contains( productName[j])) {
						userCount[j]--;
					}
				}
				// ���
				if ( outputString == null) {
					outputString = outDate + ",";
				} else {
					outputString = outputString + outDate + ",";
				}
				outputString = outputString + line[i].split(" ")[0] + ",,";
				outputString = outputString + line[i].split(" ")[4] + ",";
				for ( int j = 0; j < productCount; j++){
					outputString = outputString + Integer.toString(userCount[j])+ ",";
				}
				// ������ ","�� �����ϰ� \r\n�� �־���
				int outputLength = outputString.length();
				outputString = outputString.substring(0, outputLength - 1);
				outputString = outputString + "\r\n";
			}
			if ( line[i].contains("DENIED:")){
				// ���
				if ( outputString == null) {
					outputString = outDate + ",";
				} else {
					outputString = outputString + outDate + ",";
				}
				outputString = outputString + line[i].split(" ")[0] + ",";
				outputString = outputString + line[i].split(" ")[3] + "," +
				               line[i].split(" ")[4] + ",";
				for ( int j = 0; j < productCount; j++){
					outputString = outputString + Integer.toString(userCount[j])+ ",";
				}
				// ������ ","�� �����ϰ� \r\n�� �־���
				int outputLength = outputString.length();
				outputString = outputString.substring(0, outputLength - 1);
				outputString = outputString + "\r\n";
			}
		}
		
		return outputString;
	}
	
	int getProductCount(){
		return productCount;
	}
}
