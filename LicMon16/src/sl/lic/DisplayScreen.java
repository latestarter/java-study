package sl.lic;

import java.io.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.table.*;

public class DisplayScreen extends JPanel implements ActionListener {

	String licensePath = null;
	String logFileName = null;
	BufferedReader statusOut = null;
	String statusResult = null;
	String currentUsers = null;
	String productList = null;
	String inOutList = null;
	
	JButton openButton, logButton, statusButton, fileBrowser, csvButton, exitButton;
    JLabel logFilenNameLabel, lmgrdPathLabel;
    JTextField logFileNameTextField, displayDirectoryTextField;
    JScrollPane taScrollPane;
    JFrame frame, popupFrame;
    
    JTable ta;
    JFileChooser fc;
  
    String[] colNames = {"���� ", "�ð�","��ǰ","���ż���", "������", "���1","���2","���3"};
    Object[][] data = {{" "," "," "," "," "," "," "," "},
                      {" "," "," "," "," "," "," "," "}};
    
    DefaultTableModel dt = new DefaultTableModel(data,colNames);
    
    public DisplayScreen() {                                                    //GUI

        super(new BorderLayout());
    	
        logFilenNameLabel = new JLabel("���̼��� �α� ȭ�ϸ� : ");
        logFileNameTextField = new JTextField("C:\\Program Files (x86)\\ibm\\RationalRLKS\\common\\lmgrd.log",50);
        logFileNameTextField.setEditable(false);
        fileBrowser = new JButton("Log File ã��");
        fileBrowser.addActionListener(this);
        
        JPanel setENVArea = new JPanel();
        setENVArea.add(logFilenNameLabel);
        setENVArea.add(logFileNameTextField);
        setENVArea.add(fileBrowser);

        JLabel displayDirectoryLabel = new JLabel("���̼���  ���丮  :");
        displayDirectoryTextField = new JTextField("C:\\Program Files (x86)\\ibm\\RationalRLKS\\common",50);
        displayDirectoryTextField.setEditable(false);
        
        JPanel displayDirectory = new JPanel();
        displayDirectory.add(displayDirectoryLabel);
        displayDirectory.add(displayDirectoryTextField);
        
        JPanel Header = new JPanel();
        Header.setLayout(new BorderLayout());
        Header.add(setENVArea, BorderLayout.NORTH);
        JLabel warning = new JLabel(" ���� ) ���̼��� Ȩ ���丮�� Logȭ�� ���丮�� �����մϴ�.!! " );
        Header.add(warning, BorderLayout.CENTER);
        Header.add(displayDirectory, BorderLayout.SOUTH);

        ta = new JTable(dt);
        ta.setPreferredScrollableViewportSize(new Dimension(500, 270));
        ta.setFillsViewportHeight(true);

        taScrollPane = new JScrollPane(ta);

        fc = new JFileChooser();                                                     //ȭ�� ���ñ� �����

        openButton = new JButton("���� ���̼��� ���� ����");
        openButton.addActionListener(this);

        logButton = new JButton("���� �̷� ����");
        logButton.addActionListener(this);

        statusButton = new JButton("���� ����� ����...");
        statusButton.addActionListener(this);

        csvButton = new JButton("Excel ȭ�� �����");
        csvButton.addActionListener(this);
        
        exitButton = new JButton("������(Exit)...");
        exitButton.addActionListener(this);

        JPanel buttonPanel = new JPanel();                                         //use FlowLayout
        buttonPanel.add(openButton);
        buttonPanel.add(logButton);
        buttonPanel.add(statusButton);
        buttonPanel.add(csvButton);
        buttonPanel.add(exitButton);

 //       add(setENVArea, BorderLayout.NORTH);
        add(Header, BorderLayout.NORTH);
        
        add(buttonPanel, BorderLayout.SOUTH);                                    //JPanel�� add�ȴ�.
        add(taScrollPane, BorderLayout.CENTER);
        
     }

    public void actionPerformed(ActionEvent e) {

    	if (e.getSource() != csvButton ) {
    		dt.fireTableStructureChanged();
    	}
        if (e.getSource() == openButton) {                                                
        	// ���� ���̼��� ���� ����
    		LicenseStatus lmStat = new LicenseStatus();
    		statusOut = lmStat.runLMStat(displayDirectoryTextField.getText());
    		statusResult = lmStat.runExtractLicense(statusOut);
//    		System.out.println(statusResult);
    		// Column ���� ����
    		if ( dt.getColumnCount() > 5 ) {
     			for ( int d = dt.getColumnCount() ; d > 5 ; d --){
    				TableColumn tcol = ta.getColumnModel().getColumn(d-1);
    				ta.getColumnModel().removeColumn(tcol);
     			}
    		}
    		if (dt.getColumnCount() < 5) {
    			for ( int d = 5 - dt.getColumnCount() ; d <  5  ; d ++){
    	    		dt.addColumn("?");
    			}
    		}
    		JTableHeader th = ta.getTableHeader();
    		TableColumnModel tcm = th.getColumnModel();
    		TableColumn tc0 = tcm.getColumn(0); tc0.setHeaderValue("��������");
    		TableColumn tc1 = tcm.getColumn(1); tc1.setHeaderValue("����ð�");
    		TableColumn tc2 = tcm.getColumn(2); tc2.setHeaderValue("��ǰ");
    		TableColumn tc3 = tcm.getColumn(3); tc3.setHeaderValue("���ż���");
    		TableColumn tc4 = tcm.getColumn(4); tc4.setHeaderValue("�ý��۸�");
    		th.repaint();
    		
//    		System.out.println("row count => " + Integer.toString(dt.getRowCount()));
    		for ( int d = dt.getRowCount(); d > 0 ; d -- ){
    			dt.removeRow(0);
    		}
    		String[] line = statusResult.split("\r\n");
    		int lineSize = line.length;
    		for ( int i = 0 ; i < lineSize ; i++) {
    			String[] column = line[i].split(",");
        		dt.addRow(column);    				
    		}
// **********************  ��� �̷� ó�� ************************    		
        } else if (e.getSource() == logButton) {                                        //save
        	// ���̼��� ��� �̷� ����
//          in/out ��Ȳ - log�о �м���
    		dt.fireTableStructureChanged();
        	InOutProcess IOP = new InOutProcess();
    		inOutList = IOP.inOutList(logFileNameTextField.getText(), displayDirectoryTextField.getText());

//          Product List ����
     		LicenseStatus lmStat = new LicenseStatus();
    		statusOut = lmStat.runLMStat(displayDirectoryTextField.getText());
    		statusResult = lmStat.runExtractLicense(statusOut);
    		productList = lmStat.getProductList(statusResult);
//    		System.out.println("productList=> " + productList);
    		
//          log����� table�� ����
    		String licenseStartDate = IOP.getStartDate();
//    		System.out.println("license start date => " + licenseStartDate);
    		LogProcess LP = new LogProcess();
//    		System.out.println("list=> "+ inOutList);
    		String outputString = LP.makeOutput(productList, inOutList, licenseStartDate);
//    		System.out.println("outputString=> " + outputString);
    		
    		String[] productName = productList.split(",");
    		int productNumber = productName.length;
    		
    		// Column ������ ��ǰ ��  + 3���� ����
    		if ( dt.getColumnCount() < (productNumber + 4 )){
    			// �� �����
    			for ( int d = dt.getColumnCount(); d < (productNumber + 4 ) ; d ++){
    	    		dt.addColumn("?");
    			}
    		}
    		int dtCount = dt.getColumnCount();
    		if ( dtCount > (productNumber + 4 )){
    		    // ���̱�
    			for ( int d = dtCount ; d > (productNumber + 4 ) ; d --){
    				TableColumn tcol = ta.getColumnModel().getColumn(d-1);
    				ta.getColumnModel().removeColumn(tcol);
    			}
    		}
    	
    		// Header �ٲٱ� 
    		JTableHeader th = ta.getTableHeader();
    		TableColumnModel tcm = th.getColumnModel();
    		TableColumn tc0 = tcm.getColumn(0); tc0.setHeaderValue("����");
    		TableColumn tc1 = tcm.getColumn(1); tc1.setHeaderValue("�ð�");
    		TableColumn tc2 = tcm.getColumn(2); tc2.setHeaderValue("������ǰ");
    		TableColumn tc3 = tcm.getColumn(3); tc3.setHeaderValue("�����");

    		TableColumn[] tcp = new TableColumn[productNumber];
    		
    		for ( int pn = 0; pn < productNumber; pn++){
    			tcp[pn] = tcm.getColumn(pn + 4);
    			tcp[pn].setHeaderValue(productName[pn]);
    		}
    		th.repaint();

    		// ������ �����
    		for ( int d = dt.getRowCount(); d > 0 ; d -- ){
    			dt.removeRow(0);
    		}
    		
    		String[] line = outputString.split("\r\n");
    		int lineSize = line.length;
    		
    		for ( int i = 0 ; i < lineSize ; i++) {
//    			System.out.println("line=> " + line[i]);
    			String[] column = line[i].split(",");
//    			System.out.println("0=> " + column[0]);
        		dt.addRow(column);    				
    		}
// *********************** ���º��� ******************************        	
        } else if (e.getSource() == statusButton) {    
    		dt.fireTableStructureChanged();
    		// Column ���� ����
        	System.out.println("column count =>" + dt.getColumnCount());
        	System.out.println("displayColumnCount => " + dt.getColumnCount());
    		if ( dt.getColumnCount() > 8 ) {
     			for ( int d = dt.getColumnCount() ; d > 8 ; d --){
    				TableColumn tcol = ta.getColumnModel().getColumn(d-1);
    				ta.getColumnModel().removeColumn(tcol);
     			}
    		}
    		if (dt.getColumnCount() < 8) {
    			for ( int d = 0; d <  8 - dt.getColumnCount() ; d ++){
    	    		dt.addColumn("?");
    	    		System.out.println("Hear ==> 3");
    			}
    		}
    		// ���� ����� ����
    		JTableHeader th = ta.getTableHeader();
    		TableColumnModel tcm = th.getColumnModel();
    		TableColumn tc0 = tcm.getColumn(0); tc0.setHeaderValue("��������");
    		TableColumn tc1 = tcm.getColumn(1); tc1.setHeaderValue("����ð�");
    		TableColumn tc2 = tcm.getColumn(2); tc2.setHeaderValue("��ǰ");
    		TableColumn tc3 = tcm.getColumn(3); tc3.setHeaderValue("�����");
    		TableColumn tc4 = tcm.getColumn(4); tc4.setHeaderValue("�ý���ID");
    		TableColumn tc5 = tcm.getColumn(5); tc5.setHeaderValue("������� ");
    		TableColumn tc6 = tcm.getColumn(6); tc6.setHeaderValue("���ð� ");
    		TableColumn tc7 = tcm.getColumn(7); tc7.setHeaderValue("���ӽð�(��)");
    		th.repaint();
    		
    		for ( int d = dt.getRowCount(); d > 0 ; d -- ){
    			dt.removeRow(0);
    		}
     		LicenseStatus lmStat = new LicenseStatus();
    		statusOut = lmStat.runLMStat(displayDirectoryTextField.getText());
    		statusResult = lmStat.runExtractLicense(statusOut);
//    		System.out.println(statusResult);
    		productList = lmStat.getProductList(statusResult);
//    		System.out.println("productList=> " + productList);
    		currentUsers = lmStat.getCurrentUsers();

    		String[] line = currentUsers.split("\r\n");
    		int lineSize = line.length;
    		
    		for ( int i = 0 ; i < lineSize ; i++) {
    			String[] column = line[i].split(",");
        		dt.addRow(column);    				
    		}
    		// ���� �����
// ******************** �α� ȭ�� ��������  ******************************    		
        } else if (e.getSource() == fileBrowser){
            int returnVal = fc.showOpenDialog(this);
            if (returnVal == JFileChooser.APPROVE_OPTION) {
                String fileName = fc.getCurrentDirectory().getAbsolutePath() + "\\" + fc.getSelectedFile().getName();
                logFileNameTextField.setText(fileName);
                displayDirectoryTextField.setText(fc.getCurrentDirectory().getAbsolutePath());
            }        	
         // ******************** �����ϱ�  ******************************    		
        } else if (e.getSource() == exitButton){
        	System.exit(0);
//*********************  CSV ȭ�� ����� ********************************
        } else if (e.getSource() == csvButton ){

        	int returnVal = fc.showSaveDialog(this);
        	if (returnVal == JFileChooser.APPROVE_OPTION){
                String fileName = fc.getCurrentDirectory().getAbsolutePath() + "\\" + fc.getSelectedFile().getName();
                System.out.println( "csv Filename =>" + fileName );
                this.writeFile(fileName);
        	}

        }
    }

    private void go() {
        JFrame.setDefaultLookAndFeelDecorated(true);

        frame = new JFrame("��� ���̼��� ����͸�");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        JPanel newContentPane = new DisplayScreen();                              //JPanel
        frame.setContentPane(newContentPane);
        Toolkit tk = Toolkit.getDefaultToolkit();
        Dimension sc = tk.getScreenSize();
        
        frame.pack();
        frame.setVisible(true);
        
        frame.setLocation(sc.width/2 - 500, sc.height/2 - 200);

    }
    private void writeFile(String csvFileName){
        int tableColumnCount = dt.getColumnCount();
        int tableRowCount = dt.getRowCount();
        
        System.out.println("columncount => " + tableColumnCount);
        System.out.println("rowcount => " + tableRowCount);
        
        String writeLine = "";
        
		JTableHeader th = ta.getTableHeader();
		TableColumnModel tcm = th.getColumnModel();
	
		for ( int h = 0; h < tableColumnCount ; h ++){
			TableColumn tcTemp = tcm.getColumn(h);
			if ( writeLine == "") {
				writeLine = tcTemp.getHeaderValue().toString();
			} else {
				writeLine = writeLine + "," + tcTemp.getHeaderValue().toString();
			}
		}
		
		try {
			FileWriter output = new FileWriter(csvFileName);
			BufferedWriter bufOutput = new BufferedWriter(output);
			
//			System.out.println("writeLine==>" + writeLine);
			bufOutput.write(writeLine);
			bufOutput.newLine();
			
			String readValue = "";
			for ( int i = 0; i < tableRowCount ; i ++){
				writeLine = "";    // �ʱ�ȭ
				for ( int j = 0; j < tableColumnCount; j++){
					if ( dt.getValueAt(i, j) != null ) {
						readValue = dt.getValueAt(i, j).toString();
						if (readValue == "") readValue = " ";
						if (writeLine == ""){
							writeLine = readValue;
						} else {
							writeLine = writeLine + "," + readValue;
						}
					} else {
						if (writeLine == ""){
							writeLine = " ";
						} else {
							writeLine = writeLine + ", ";
						}
					}
				}
//				System.out.println("writeLine ==>" + writeLine);
				bufOutput.write(writeLine);
				bufOutput.newLine();
			}
			bufOutput.close();
		} catch (IOException e){
			e.printStackTrace();
		}
		
    }

    public static void main(String[] args) {
       new DisplayScreen().go();
    }
}
