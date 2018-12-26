/*
 * Author: J. Bajic, 2018.
 */

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.TooManyListenersException;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.usb.event.UsbServicesEvent;
import javax.usb.event.UsbServicesListener;
//import gnu.io.CommPortIdentifier;
//import gnu.io.CommPortOwnershipListener;
//import gnu.io.NoSuchPortException;
import gnu.io.SerialPortEvent;
import gnu.io.SerialPortEventListener;
import javafx.animation.AnimationTimer;
import javafx.application.Platform;
import javafx.beans.property.LongProperty;
import javafx.beans.property.SimpleLongProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.CheckMenuItem;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Control;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.MenuItem;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Separator;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.stage.FileChooser;
import javax.usb.*; 

public class SerialGUIControler {


    public  ObservableList<String> txtTerminal =FXCollections.observableArrayList();
    @FXML
    private ListView<String> terminal=new ListView<String>(txtTerminal);    
    @FXML
    private Button btnSave;
    @FXML
    private Button btnOpen; 
    @FXML
    private Button btnNew;  
    @FXML
    private Button btnClearR;  
    @FXML
    private Button btnClearT;	
	@FXML
	private Text  lblByteCount;	
	@FXML
    private ComboBox<String> cmbTimer;    
    @FXML
    private CheckMenuItem itemCR;    
    @FXML
    private CheckMenuItem itemLF;    
    @FXML
    private CheckMenuItem itemLight;    
    @FXML
    private CheckMenuItem itemDark;   
    @FXML
    private CheckMenuItem itemReadLine;   
    @FXML
    private Label txtInfo;
    @FXML
    private Label txtInfo2;    
    @FXML
    private Label lblReceiver;   
    @FXML
    private Label lblTransmiter; 
    @FXML
    private Label lblMessage;
    @FXML
    private Text  lblTransmisions;
    @FXML
    private Button btnScan;
    @FXML
    private Button btnConnect;
    @FXML
    private Button btnASCII;
    @FXML
    private Button btnDisconnect;
    @FXML
    private ComboBox<String> cmbPorts;
    @FXML
    private ComboBox<String> cmbParity;
    @FXML
    private ComboBox<Integer> cmbBaud;
    @FXML
    private ComboBox<String> cmbStopBits;    
    @FXML
    private ComboBox<Integer> cmbDataBits;    
    @FXML
    private Button btnSend;    
    @FXML
    private TextField txtWrite;    
    @FXML
    private MenuItem menuFormatT;
    @FXML
    private MenuItem menuFormatR;
    @FXML
    private MenuItem menuFormatLine;   
    @FXML
    private MenuItem menuAbout;    
    @FXML
    private MenuItem menuClearR; 
    @FXML
    private MenuItem menuClearT; 
    @FXML
    private MenuItem menuClearAll; 
    @FXML
    private MenuItem menuClose;    
    @FXML
    private MenuItem menuOpen;    
    @FXML
    private MenuItem menuSave;    
    @FXML
    private MenuItem menuSaveAs;  
    @FXML
    private MenuItem menuNew;    
    @FXML
    private CheckMenuItem itemHEXr;  
    @FXML
    private CheckMenuItem itemDECr;  
    @FXML
    private CheckMenuItem itemASCIIr;
    @FXML
    private CheckMenuItem itemBINr;
    @FXML
    private CheckMenuItem itemHEXt;
    @FXML
    private CheckMenuItem itemDECt;
    @FXML
    private CheckMenuItem itemASCIIt;
    @FXML
    private CheckMenuItem itemBINt;
    @FXML
    private Button btnAdd;    
    @FXML
    private Button btnRemove;       
    @FXML
    private Button btnCR;     
    @FXML
    private Button btnLF;     
    @FXML
    private Button btnCRLF;     
    @FXML
    private ScrollPane scroll;  
    @FXML
    private VBox vbox; 
    @FXML
    private HBox hboxTerminal;  
    @FXML
    private AnchorPane root; 
    @FXML
    private ImageView imgRefresh;    
    @FXML
    private ImageView imgAdd;    
    @FXML
    private ImageView imgRemove;    
    @FXML
    private ImageView imgOpen;    
    @FXML
    private ImageView imgClose;
    
    private SerialPortModel Serial=new SerialPortModel();
    private Converter Convert=new Converter();    
    final BlockingQueue<String> messageQueue = new ArrayBlockingQueue<>(1);
    private SerialWriter swrThread=new SerialWriter(Serial, messageQueue);
    private Thread serialWriter=new Thread(swrThread);     
    final LongProperty lastUpdate = new SimpleLongProperty();
    final long minUpdateInterval = 0;   
    private CheckForPort pThread=new CheckForPort(Serial);
    private Thread serialChecker=new Thread(pThread);

    private int formatR=0;
    private int formatT=0;
    private static boolean startReading=false;
    private String port=null;
    private int lastAdded=0;
    private String TXdata="";
    private String TXdataDisplay="";
    private int multiplier=1;
    private int numOfRX=0;
    private int byteCount=0;
    private ArrayList<String> timer=new ArrayList<String>(Arrays.asList("Single transmit", 
    																	"Periodically at 0.1 s",
    																	"Periodically at 0.2 s", 
    																	"Periodically at 0.5 s",
    																	"Periodically at 1 s",
    																	"Periodically at 2 s",
    																	"Periodically at 5 s",
    																	"Periodically at 10 s"));
    private static byte[] buffer = new byte[1024];
    private static int bufferSIZE=0;   
    int[] txtHashCodes=new int[16];
    private int theme=0;
    private boolean startUP=true;
    boolean ascii_added=false;  
    private Runtime runtime = Runtime.getRuntime();
    private File path;
    private textChange listenForChanges=new textChange();
    private StringBuilder RXstring=new StringBuilder("RX-> ");
    private Color rxColor=Color.RED;
    private Color txColor=Color.BLUE;
    private Color tableColor=Color.BLACK;
    private Color GREEN=new Color(0,1,0,1);
    private static boolean wait=false;
    private static boolean ok=false;
    private static boolean check=false;
 //   private CommPortIdentifier portID; 
 //   private SerialOwnershipHandler serialOwn=new SerialOwnershipHandler(Integer.toString(0));
    private InputStream inStream;
    private BufferedReader inLine;
 
    @FXML
    void onToolbarItem(ActionEvent e) {
    	if(e.getSource()==btnClearT)
    	{
    		menuClearT.fire();
    	}
    	if(e.getSource()==btnClearR)
    	{
    		menuClearR.fire();
    	}
    	if(e.getSource()==btnSave)
    	{
    		if(menuSave.isDisable())menuSaveAs.fire();
    		else menuSave.fire();
    	}
    	if(e.getSource()==btnNew)
    	{
    		menuNew.fire();
    	}
    	if(e.getSource()==btnOpen)
    	{
    		menuOpen.fire();
    	}	
    }
    
    @FXML
    void onThemeClick(ActionEvent e) {
    	
    	if(e.getSource()==itemDark&&theme==0)
    	{
    		itemDark.setSelected(true);
    		itemLight.setSelected(false);
        	Scene scene=root.getScene();

        	rxColor=Color.YELLOW;
            txColor=GREEN;
            tableColor=Color.WHITE;
            txtInfo.setTextFill(GREEN);   
            LoadIcons("/resources/dark/");
            scene.getStylesheets().remove("/resources/css/MyAppStyle1.css");
        	scene.getStylesheets().add("/resources/css/MyAppStyle2.css");
        	
    		ArrayList<String> x=new ArrayList<String>();
        	for(String n:txtTerminal)
    		{
        		x.add(n);
    		}
        	txtTerminal.clear();
        	for(String n:x)
        	{
        		txtTerminal.add(n);
        	}
        	
        	theme=1;
        	
    	}
    	if(e.getSource()==itemLight&&theme==1)
    	{
    		itemDark.setSelected(false);
    		itemLight.setSelected(true);
        	Scene scene=root.getScene();
   
        	rxColor=Color.RED;
            txColor=Color.BLUE;
            tableColor=Color.BLACK;
            txtInfo.setTextFill(Color.BLUE);
            LoadIcons("/resources/light/");
        	scene.getStylesheets().remove("/resources/css/MyAppStyle2.css");
        	scene.getStylesheets().add("/resources/css/MyAppStyle1.css");
        	
  
        	
    		ArrayList<String> x=new ArrayList<String>();
        	for(String n:txtTerminal)
    		{
        		x.add(n);
    		}
        	txtTerminal.clear();
        	for(String n:x)
        	{
        		txtTerminal.add(n);
        	}
        	
        	theme=0;
    	}
    	if(theme==0) 
    	{
    		itemLight.setSelected(true);
    		itemDark.setSelected(false);
    	}
    	else
    	{
    		itemLight.setSelected(false);
    		itemDark.setSelected(true);
    	}
    	
    }
    
    public void LoadIcons(String path)
    {
    	btnConnect.setGraphic(new ImageView(new Image(path+"open.png",33,15,true,true)));
    	btnDisconnect.setGraphic(new ImageView(new Image(path+"close.png",33,11,true,true)));
    	btnScan.setGraphic(new ImageView(new Image(path+"find.png",33,15,true,true)));
    	btnSave.setGraphic(new ImageView(new Image(path+"save.png",33,15,true,true)));
    	btnOpen.setGraphic(new ImageView(new Image(path+"load.png",33,15,true,true)));
    	btnClearT.setGraphic(new ImageView(new Image(path+"clearT.png",33,15,true,true)));
    	btnClearR.setGraphic(new ImageView(new Image(path+"clearR.png",33,15,true,true)));
    	btnNew.setGraphic(new ImageView(new Image(path+"new.png",33,15,true,true)));
    	btnAdd.setGraphic(new ImageView(new Image(path+"add.png",25,11,true,true)));
    	btnRemove.setGraphic(new ImageView(new Image(path+"remove.png",25,11,true,true)));
    	menuSave.setGraphic(new ImageView(new Image(path+"save.png",20,15,true,true)));
    	menuSaveAs.setGraphic(new ImageView(new Image(path+"save.png",20,15,true,true)));
    	menuOpen.setGraphic(new ImageView(new Image(path+"load.png",20,15,true,true)));
    	menuNew.setGraphic(new ImageView(new Image(path+"new.png",20,15,true,true)));
    	menuClose.setGraphic(new ImageView(new Image(path+"remove.png",20,15,true,true)));
    	menuClearT.setGraphic(new ImageView(new Image(path+"clearT.png",20,15,true,true)));
    	menuClearR.setGraphic(new ImageView(new Image(path+"clearR.png",20,15,true,true)));
    	menuClearAll.setGraphic(new ImageView(new Image(path+"clearAll.png",20,15,true,true)));
    	menuClearT.setGraphic(new ImageView(new Image(path+"clearT.png",20,15,true,true)));
    	menuClearR.setGraphic(new ImageView(new Image(path+"clearR.png",20,15,true,true)));
    	menuClearAll.setGraphic(new ImageView(new Image(path+"clearAll.png",20,15,true,true)));
    	menuFormatT.setGraphic(new ImageView(new Image(path+"tx.png",20,15,true,true)));
    	menuFormatR.setGraphic(new ImageView(new Image(path+"rx.png",20,15,true,true)));
    	menuFormatLine.setGraphic(new ImageView(new Image(path+"line.png",20,15,true,true)));
    	btnASCII.setGraphic(new ImageView(new Image(path+"ascii.png",33,15,true,true)));	
    	
    	itemDark.setGraphic(new ImageView(new Image(path+"dark.png",20,15,true,true)));
    	itemLight.setGraphic(new ImageView(new Image(path+"light.png",20,15,true,true)));
    }
    
    @FXML
    void onReceiverFormat(ActionEvent e) {
    	
    	if(e.getSource()==itemASCIIr)
    	{
    		itemASCIIr.setSelected(true);
    		itemHEXr.setSelected(false);
    		itemBINr.setSelected(false);
    		itemDECr.setSelected(false);
    		formatR=Converter.ASCII;
    		lblReceiver.setText("Receiver-ASCII");
    	}
    	if(e.getSource()==itemHEXr)
    	{
    		itemASCIIr.setSelected(false);
    		itemHEXr.setSelected(true);
    		itemBINr.setSelected(false);
    		itemDECr.setSelected(false);
    		formatR=Converter.HEX;
    		lblReceiver.setText("Receiver-HEX");
    	}
    	if(e.getSource()==itemDECr)
    	{
    		itemASCIIr.setSelected(false);
    		itemHEXr.setSelected(false);
    		itemBINr.setSelected(false);
    		itemDECr.setSelected(true);
    		formatR=Converter.DEC;
    		lblReceiver.setText("Receiver-DEC");
    	}
    	if(e.getSource()==itemBINr)
    	{
    		itemASCIIr.setSelected(false);
    		itemHEXr.setSelected(false);
    		itemBINr.setSelected(true);
    		itemDECr.setSelected(false);
    		formatR=Converter.BIN;
    		lblReceiver.setText("Receiver-BIN");
    	}
  
    }

    @FXML
    
    void onTransmiterFormat(ActionEvent e) {
    	
		
    	int formatTold=formatT;
    	byte[] b;
		String s;
			
    	if(e.getSource()==itemASCIIt)
    	{
    		itemASCIIt.setSelected(true);
    		itemHEXt.setSelected(false);
    		itemBINt.setSelected(false);
    		itemDECt.setSelected(false);
    		formatT=Converter.ASCII;
    		lblTransmiter.setText("Transmitter-ASCII");
    		

    	}
    	if(e.getSource()==itemHEXt)
    	{
    		itemASCIIt.setSelected(false);
    		itemHEXt.setSelected(true);
    		itemBINt.setSelected(false);
    		itemDECt.setSelected(false);
    		formatT=Converter.HEX;
    		lblTransmiter.setText("Transmitter-HEX");
    	}
    	if(e.getSource()==itemDECt)
    	{
    		itemASCIIt.setSelected(false);
    		itemHEXt.setSelected(false);
    		itemBINt.setSelected(false);
    		itemDECt.setSelected(true);
    		formatT=Converter.DEC;
    		lblTransmiter.setText("Transmitter-DEC");
    	}
    	if(e.getSource()==itemBINt)
    	{
    		itemASCIIt.setSelected(false);
    		itemHEXt.setSelected(false);
    		itemBINt.setSelected(true);
    		itemDECt.setSelected(false);		
    		formatT=Converter.BIN;
    		lblTransmiter.setText("Transmitter-BIN");
    	}
		for(int i=0;i<=lastAdded;i++)
		{
			HBox hbox=(HBox) vbox.getChildren().get(2*i);
	    	TextField txt=(TextField) hbox.getChildren().get(1);
	    	s=txt.getText();
			b=Convert.StringToByteArray(s, formatTold);
			s=Convert.ByteArrayToString(b, Converter.getLength(), Converter.ASCII);
			if(formatT!=Converter.ASCII)
			{
				b=Convert.StringToByteArray(s, Converter.ASCII);
				s=Convert.ByteArrayToString(b, Converter.getLength(), formatT);
			}
	    	txt.setText(s);
	    	txt.end();		
		}
    	
    }

    @FXML
    void onASCIIClick(ActionEvent event) {
  	
    	if(!ascii_added)
    	{
    		txtTerminal.add(0, Converter.generateASCIITable()+'\r');
    		ascii_added=true;
    		
    	}		
    	terminal.scrollTo(0);
    }
    
     
    @FXML
    void onFileClick(ActionEvent e) {
    	
    	if(e.getSource()==menuClose)
    	{
            btnDisconnect.fire();
            if(Serial.isConnected())
            {
            	Serial.disconnect();
            }
    		Platform.exit();
            System.exit(0);

    	}
    	if(e.getSource()==menuNew)
    	{
    		menuClearAll.fire();
    		int i=0;
    		while(i<lastAdded)
    		{
    			btnRemove.fire();
    		}
    	}
    	
    	if(e.getSource()==menuSaveAs)
    	{
    		
    		
    		FileChooser fileChooser = new FileChooser();
    		fileChooser.setTitle("Open Resource File");
    		FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter("TRM files (*.trm)", "*.trm");
    		fileChooser.getExtensionFilters().add(extFilter);
    	

    		try {
    			File UARTPath= fileChooser.showSaveDialog(root.getScene().getWindow());
    			if(UARTPath!=null)
    			{
    				PrintWriter UARTFile = new PrintWriter(new BufferedWriter(new FileWriter(UARTPath)));
    				UARTFile.println(FileModel.generateCODE(UARTFile.hashCode()));
				
					UARTFile.println("<start COM>");
					UARTFile.println(cmbPorts.getValue());
					UARTFile.println(cmbBaud.getValue());
					UARTFile.println(cmbDataBits.getValue());
					UARTFile.println(cmbStopBits.getValue());
					UARTFile.println(cmbParity.getValue());
					UARTFile.println(cmbTimer.getValue());
					UARTFile.println("<end COM>");
					UARTFile.println("<start TXdata>");
					UARTFile.println(Integer.toString(formatT));
					for(int i=0; i<=lastAdded; i++)
		    		{
		    			HBox hbox=(HBox) vbox.getChildren().get(2*i);
		    	    	TextField txt=(TextField) hbox.getChildren().get(1);
		    			UARTFile.println(txt.getText());
		    		}
					UARTFile.println("<end TXdata>");
		    		UARTFile.println("<start RXdata>");
		    		UARTFile.println(formatR);
		    		for(String n:txtTerminal)
		    		{
		    			
		    			UARTFile.print(n+'\n');
		    		}
		    		UARTFile.print("<end RXdata>");
		    		UARTFile.close();
		    		
		    		path=UARTPath;
		    		menuSave.setDisable(false);
    			}		

    		} 
    		catch (IOException e1) 
    		{
	
				txtInfo.setText("Error saving file");
			}
    		

    	}
    	if(e.getSource()==menuSave)
    	{
    		
    		

    	

    		try {
    			
    			if(path.exists())
    			{
    				PrintWriter UARTFile = new PrintWriter(new BufferedWriter(new FileWriter(path)));
    				UARTFile.println(FileModel.generateCODE(UARTFile.hashCode()));
				
					UARTFile.println("<start COM>");
					UARTFile.println(cmbPorts.getValue());
					UARTFile.println(cmbBaud.getValue());
					UARTFile.println(cmbDataBits.getValue());
					UARTFile.println(cmbStopBits.getValue());
					UARTFile.println(cmbParity.getValue());
					UARTFile.println(cmbTimer.getValue());
					UARTFile.println("<end COM>");
					UARTFile.println("<start TXdata>");
					UARTFile.println(Integer.toString(formatT));
					for(int i=0; i<=lastAdded; i++)
		    		{
		    			HBox hbox=(HBox) vbox.getChildren().get(2*i);
		    	    	TextField txt=(TextField) hbox.getChildren().get(1);
		    			UARTFile.println(txt.getText());
		    		}
					UARTFile.println("<end TXdata>");
		    		UARTFile.println("<start RXdata>");
		    		UARTFile.println(formatR);
		    		for(String n:txtTerminal)
		    		{
		    			UARTFile.print(n+'\n');
		    		}
		    		UARTFile.print("<end RXdata>");
		    		UARTFile.close();
		    		
		    		
    			}
    			else
    			{
    				txtInfo.setText("File not found");
    				menuSave.setDisable(true);
    			}

    		} 
    		catch (IOException e1) 
    		{
	
				txtInfo.setText("Error saving file");				
			}
    		

    	}
    	if(e.getSource()==menuOpen)
    	{
    		FileChooser fileChooser = new FileChooser();
    		fileChooser.setTitle("Open Resource File");
    		FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter("TRM files (*.trm)", "*.trm");
    		fileChooser.getExtensionFilters().add(extFilter);
    	

    		try {
    			
    			File UARTPath= fileChooser.showOpenDialog(root.getScene().getWindow());
    		    if(UARTPath!=null)
    		    {
	    			BufferedReader UARTFile = new BufferedReader(new FileReader(UARTPath));
	
	    		    if(FileModel.checkCODE(UARTFile.readLine()))
	    		    {
	    		    	
	    		 
	    		    	String line="";
	    		    	int i=0;
	    		    	line=UARTFile.readLine();

	        			txtTerminal.clear();

	    	    		cmbPorts.valueProperty().set(UARTFile.readLine()); 
	    				cmbBaud.valueProperty().set(Integer.parseInt(UARTFile.readLine()));
		    			cmbDataBits.valueProperty().set(Integer.parseInt(UARTFile.readLine()));
		    			cmbStopBits.valueProperty().set(UARTFile.readLine());
		    			cmbParity.valueProperty().set(UARTFile.readLine());
		    			cmbTimer.valueProperty().set(UARTFile.readLine());
		    			line=UARTFile.readLine();
		    			line=UARTFile.readLine();
		    			int f=Integer.parseInt(UARTFile.readLine());
		    			switch(f)
		    			{
			    			case Converter.ASCII:
			    				itemASCIIt.fire();
			    				break;
			    			case Converter.HEX:
			    				itemHEXt.fire();
			    				break;
			    			case Converter.BIN:
			    				itemBINt.fire();
			    				break;
			    			case Converter.DEC:
			    				itemDECt.fire();
			    				break;
		    				
		    			}
		    			
		    			menuClearAll.fire();
		    			line=UARTFile.readLine();
		    			while(!(line.contains("<end TXdata>")))
		    			{
			    			if(i>lastAdded)
		    				{
		    					btnAdd.fire();
		    	    	    	
		    				}
	    					HBox hbox=(HBox) vbox.getChildren().get(2*i);
	    	    	    	TextField txt=(TextField) hbox.getChildren().get(1);
	    	    	    	txt.setText(line);
	    	    	    	i++;
	    	    	    	line=UARTFile.readLine();
		    				
		    			}
		    			while(i<=lastAdded)
		    			{
		    				btnRemove.fire();
		    			}
		    			line=UARTFile.readLine();
		    			f=Integer.parseInt(UARTFile.readLine());
		    			switch(f)
		    			{
			    			case Converter.ASCII:
			    				itemASCIIr.fire();
			    				break;
			    			case Converter.HEX:
			    				itemHEXr.fire();
			    				break;
			    			case Converter.BIN:
			    				itemBINr.fire();
			    				break;
			    			case Converter.DEC:
			    				itemDECr.fire();
			    				break;
		    				
		    			}
		    			
		    			String r=UARTFile.readLine();
		    			
		    			while(!(r.contains("<end RXdata>")))
		    			{
		    				txtTerminal.add(r);
			    			r=UARTFile.readLine();
		    			}
		    			txtTerminal.add("RX-> ");
		    			lblByteCount.setText("byte count = 0");
			    		UARTFile.close();
			    		path=UARTPath;
			    		menuSave.setDisable(false);
	    		    }
    		    }
    		
    		} 
    		catch (IOException e1) 
    		{
    			txtInfo.setText("Error reading file");
    		//	txtInfo.setTextFill(Color.RED);
			}
    		

    	}

    }

    @FXML
    void onClearClick(ActionEvent e) {
    	
    	if(e.getSource()==menuClearR)
    	{
    		if(!txtTerminal.isEmpty())
    		{	    			
    			txtTerminal.clear();
    			ascii_added=false;
    			RXstring=new StringBuilder("RX-> ");
         	    txtTerminal.add(RXstring.toString());
    		}
    		lblByteCount.setText("byte count = 0");
    		lblTransmisions.setText("transmisions = 0");
    		numOfRX=0;
    		byteCount=0;
    	}
    	if(e.getSource()==menuClearT)
    	{
        	for(int i=0;i<=lastAdded;i++)
        	{
    			HBox hbox=(HBox) vbox.getChildren().get(2*i);
    	    	TextField txt=(TextField) hbox.getChildren().get(1);
    	    	txt.clear();
        	}
    	}
    	if(e.getSource()==menuClearAll)
    	{
    		if(!txtTerminal.isEmpty())
    		{
    			ascii_added=false;
    			txtTerminal.clear();
    			RXstring=new StringBuilder("RX-> ");
         	    txtTerminal.add(RXstring.toString());
    		}
    		lblByteCount.setText("byte count = 0");
			lblTransmisions.setText("transmisions = 0");
			numOfRX=0;
			byteCount=0;
        	for(int i=0;i<=lastAdded;i++)
        	{
    			HBox hbox=(HBox) vbox.getChildren().get(2*i);
    	    	TextField txt=(TextField) hbox.getChildren().get(1);
    	    	txt.clear();
        	}
    		
    	}
    	
    	runtime.gc();
		System.gc();
    	
    	

    }
    
    @FXML
    void onAboutClick(ActionEvent event) throws URISyntaxException {

    	InputStream in=getClass().getResourceAsStream("/resources/help.txt");

		try (BufferedReader UARTFile = new BufferedReader(new InputStreamReader(in)))
		{
			StringBuilder help=new StringBuilder("");
	    	String r;
	    	while(((r=UARTFile.readLine())!=null))
	    	{
	    		help.append(r+'\r'+'\n');
	    	}
	    	txtTerminal.add(0,help.toString());  	  
		} 
		catch (IOException e1) 
		{
			txtInfo.setText("Error reading file");
		//	txtInfo.setTextFill(Color.RED);
			e1.printStackTrace();
		}
		

	}

    			
    
    
    @FXML
    void onAddRemoveClick(ActionEvent e) {
    	if(e.getSource()==btnAdd)
    	{
    		if(lastAdded<15)
    		{
    			lastAdded++;
    			HBox hbox=new HBox();
    			Separator sep=new Separator();
    			TextField txt=new TextField();
    			Label lbl=new Label();
    			lbl.setText("TX->");
    			Button btn=new Button(">>");
    			lbl.setTextFill(txColor);
    			lbl.setStyle("-fx-font-family: Arial;");
    			lbl.setMinSize(30,10);
    			txt.setPrefSize(118, 25);
    			btn.setMinSize(35, 24);
    			lbl.setPadding(new Insets(5,0,0,0));
    			HBox.setMargin(lbl, new Insets(10,0,10,10));
    			HBox.setMargin(txt, new Insets(10,10,10,0));
    			HBox.setMargin(btn, new Insets(10,10,10,0)); 			
    			HBox.setHgrow(txt, Priority.SOMETIMES);
	
    			txt.setPromptText("type here!");
    			hbox.getChildren().add(lbl);  
    			hbox.getChildren().add(txt);  
    			hbox.getChildren().add(btn);
    			btn.setOnAction(this::onSendClick);
    			btn.setId("btnSend"+Integer.toString(lastAdded));
    	
    			vbox.getChildren().add(hbox);   		
    			vbox.getChildren().add(sep);
    			
    			if(Serial.isConnected())
    			{
    				txt.textProperty().addListener(listenForChanges);
    			}
    			
    			txtHashCodes[lastAdded]=txt.textProperty().hashCode();
    			
    		}
    	}
    	
    	if(e.getSource()==btnRemove)
    	{
    		if(lastAdded>0)
    		{
    			HBox hbox=(HBox) vbox.getChildren().get(2*lastAdded);
    	    	TextField txt=(TextField) hbox.getChildren().get(1);
    	    	txt.textProperty().removeListener(listenForChanges);

    			vbox.getChildren().remove(2*lastAdded+1);
    			vbox.getChildren().remove(2*lastAdded);
    			lastAdded--;
    		}
    	}
    	

    }
    
    @FXML
    void onScanClick(ActionEvent event) {

    	cmbPorts.getItems().clear();
    	cmbBaud.getItems().clear();
    	cmbDataBits.getItems().clear();
    	cmbStopBits.getItems().clear();
    	cmbParity.getItems().clear();
    	cmbTimer.getItems().clear();

    	if(Serial.scanForPorts())
    	{
    		toogleDisableStates(false);
    		cmbPorts.getItems().addAll(Serial.getSerialPorts());
    		cmbPorts.setValue(cmbPorts.getItems().get(0));
    
    	
    		txtInfo.setText("Ports found:"+" "+Integer.toString(Serial.getNumOfPortFound()));
    		

    		cmbBaud.getItems().addAll(Serial.getBaudRates());
    		cmbBaud.setValue(Serial.getBaudRates().get(Serial.getBaudRates().indexOf(Serial.getBaudRate())));
    		
    		cmbDataBits.getItems().addAll(Serial.getDataBitss());
    		cmbDataBits.setValue(Serial.getDataBitss().get(Serial.getDataBitss().indexOf(Serial.getDataBits())));
    		
    		cmbStopBits.getItems().addAll(Serial.getStopBitss());
    		cmbStopBits.setValue(Serial.getStopBitss().get(Serial.getStopBits()-1));
    		
    		cmbParity.getItems().addAll(Serial.getParities());
    		cmbParity.setValue(Serial.getParities().get(Serial.getParity()));
    		
    		cmbTimer.getItems().addAll(timer);
    		cmbTimer.setValue(cmbTimer.getItems().get(0));
    		
    //		cmbFlowControl.getItems().addAll(Serial.getFlowControls());
   // 		cmbFlowControl.setValue(Serial.getFlowControls().get(0));
    	}
    	else
    	{
    		toogleDisableStates(true);
    		txtInfo.setText("No ports found!");
			btnDisconnect.setDisable(true);
			btnScan.setDisable(false);
    	}
    	
    	check=false;
    	
    	if(startUP)
    	{
    		itemDark.setSelected(true);
    		itemLight.setSelected(false);
    		rxColor=Color.YELLOW;
            txColor=GREEN;
            tableColor=Color.WHITE;
            txtInfo.setTextFill(GREEN);   
            LoadIcons("/resources/dark/");
    		theme=1;
    		startUP=false;
    		
    		try 
    		{
    			UsbServices services = UsbHostManager.getUsbServices( );
    			services.addUsbServicesListener(new HotplugListener( ));
    			
    		} 
    		catch (SecurityException | UsbException e) {
    			e.printStackTrace();
    		}
    		

    		serialChecker.start();
    	}
    	if(txtTerminal.isEmpty())
    	{
			terminal.setItems(txtTerminal);		
			txtTerminal.add(RXstring.toString());
	
			terminal.setCellFactory(list -> new ListCell<String>() 
			{ 
				{
					this.prefWidthProperty().bind(terminal.widthProperty().subtract(100.0));
			        setMaxWidth(Control.USE_PREF_SIZE);
				}
				    @Override
				    protected void updateItem(String item, boolean empty) 
				    {
				        super.updateItem(item, empty); 
				        setText(empty ? null : item);	        
	
				        if(item!=null)
				        {
				        	this.setWrapText(true);
				        	if(item.contains("TX-> "))
				        		setTextFill(txColor);
				        	else if(item.contains("RX-> "))
				        		setTextFill(rxColor);
				        	else  setTextFill(tableColor);
				        }
				    }
			});
    	}

    }

    @FXML
    void onConnectClick(ActionEvent event) {
			
    	if(cmbPorts.getValue()==null)
    	{	
    		txtInfo.setText("Select port first!");
    		
    	}
    	else if(Serial.isConnected())
    	{
    		txtInfo.setText("Already connected!");
    	}
    	else
    	{
    		Serial.setBaudRate(cmbBaud.getValue());
    		Serial.setDataBits(cmbDataBits.getValue());
    		Serial.setStopBits(cmbStopBits.getValue());
    		Serial.setParity(cmbParity.getValue());
    		setTimer(cmbTimer.getValue());
    //		Serial.setFlowControl(cmbFlowControl.getValue());
    		
		/*	try 
			{
				portID=CommPortIdentifier.getPortIdentifier(cmbPorts.getValue());
			} 
			catch (NoSuchPortException e)
			{
				txtInfo.setText("An error occurred on get port id");
			}
			portID.addPortOwnershipListener(serialOwn);*/
    	
    		txtInfo.setText(Serial.connect(cmbPorts.getValue()));
    		port=cmbPorts.getValue();

    		txtHashCodes[0]=txtWrite.textProperty().hashCode();
    		
			for(int i=0;i<=lastAdded;i++)
			{
				HBox hbox=(HBox) vbox.getChildren().get(2*i);
				TextField txt=(TextField) hbox.getChildren().get(1);
				txt.textProperty().addListener(listenForChanges);
			}

    		if(Serial.isConnected())
    		{
    			
    			toogleDisableStates(true);
    			
    			startReading=true;

				try 
				{
					Serial.getSerialPort().addEventListener(new SerialReader());
				} 
				catch (TooManyListenersException e) 
				{
					txtInfo.setText("An error occurred");
				}
	            Serial.getSerialPort().notifyOnDataAvailable(true);
	            
				try 
				{
					inStream=Serial.getSerialPort().getInputStream();
					inLine=new BufferedReader(new InputStreamReader(Serial.getSerialPort().getInputStream(), StandardCharsets.ISO_8859_1));
				} 
				catch (IOException e) 
				{
					txtInfo.setText("IO error occured");
				}
    			
    			if(!serialWriter.isAlive())
    			{	
    				serialWriter.start();
    				
    			    AnimationTimer timer = new AnimationTimer() 
    			   	{

			            @Override
			            public void handle(long now) 
			            {
			                if ((now - lastUpdate.get() > minUpdateInterval)&&!check) 
			                {
			                  // runtime.gc();
			                  // System.gc();

			                   final String message = messageQueue.poll();
			                   if (message != null) 
			                   {

			                	   if(txtTerminal.get(txtTerminal.size()-1).equals("RX-> "))
			                		   txtTerminal.set(txtTerminal.size()-1,message);
			                	   else
			                		   txtTerminal.add(message);
			                	  
			                	   numOfRX++;
			               		   lblTransmisions.setText("transmisions = "+Integer.toString(numOfRX));
			               		   byteCount=0;
			               		   //lblByteCount.setText("byte count = 0");
			               		
			                	   RXstring=new StringBuilder("RX-> ");
			                	   txtTerminal.add(RXstring.toString());
			                	   terminal.scrollTo(terminal.getItems().size() - 1);
			                   }
			           
			                   if(ok&&!wait)
			                   {
			                	   		StringBuilder r=new StringBuilder("");
			                	   		try 
			                	   		{
			                	   			if(itemReadLine.isSelected())
			                	   			{
			                	   				buffer=Convert.StringToByteArray(inLine.readLine(), Converter.ASCII);
			                	   				bufferSIZE=Converter.getLength();
			                	   			}
			                	   			else
			                	   			{
			                	   				bufferSIZE=inStream.read(buffer);

			                	   			}
											
										} 
			                	   		catch (IOException e) 
			                	   		{
											txtInfo.setText("Read line error");
											//btnDisconnect.fire()
										}
			                	   		
	                	   				r.append(Convert.ByteArrayToString(buffer,bufferSIZE,formatR));
	                	   				byteCount+=bufferSIZE;
			                	    	bufferSIZE=0;

	    			           		//	wait=false;
	    			           					
    			                	   	lblByteCount.setText("byte count = "+Integer.toString(byteCount));
	    			       				if(formatR!=Converter.ASCII)
	    			       					txtTerminal.set(txtTerminal.size()-1,RXstring.append('-').append(r).toString());
	    			       				else
	    			       					txtTerminal.set(txtTerminal.size()-1,RXstring.append(r).toString());
	    			       				terminal.scrollTo(terminal.getItems().size() - 1);
	    			       				ok=false;
    			                   } 
    			                   lastUpdate.set(now);
    			              }
    			         }

    				};

    			    timer.start();
    			}
    			txtInfo2.setText(
    				cmbPorts.getValue()+", "+
    				cmbBaud.getValue()+", "+
    				cmbDataBits.getValue()+", "+
    				cmbStopBits.getValue()+", "+
    				cmbParity.getValue()//+", "+
    	//			cmbFlowControl.getValue()
    				);
    			
    		}
    	}
    	

    }

    @FXML
    void onDisconnectClick(ActionEvent event) {
    	
    	
    	Serial.getSerialPort().removeEventListener();
    	
    	
    	try 
    	{
			inLine.close();
			inStream.close();
		} 
    	catch (IOException e) 
    	{
			
		}
    	
    	if(Serial.isConnected())
    	{   		
			for(int i=0;i<=lastAdded;i++)
			{
				HBox hbox=(HBox) vbox.getChildren().get(2*i);
				TextField txt=(TextField) hbox.getChildren().get(1);
				txt.textProperty().removeListener(listenForChanges);
			}
			port=null;
    		startReading=false;
    		txtInfo2.setText(" ");
    		toogleDisableStates(false);
    		
    		runtime.gc();
    		System.gc();
    	}
    	
    	
    }
    
    public void toogleDisableStates(boolean b)
    {

			btnConnect.setDisable(b);
			btnDisconnect.setDisable(!b);
			menuOpen.setDisable(b);
			btnOpen.setDisable(b);
			btnScan.setDisable(b);
			cmbPorts.setDisable(b);
			cmbBaud.setDisable(b);
			cmbDataBits.setDisable(b);
			cmbStopBits.setDisable(b);
			cmbParity.setDisable(b);
			cmbTimer.setDisable(b);
			lblByteCount.setVisible(b);
			lblTransmisions.setVisible(b);
	//		cmbFlowControl.setDisable(b);

    	
    }
   
    @FXML
    void onSendClick(ActionEvent e) {
    	
    	
    	String txtSend="";

	    if(Serial.isConnected())
	    {
    		if(e.getSource() instanceof Button)
	    	{
	    		if(e.getSource()==btnSend)
	    		{
	    	    	if(formatT==Converter.ASCII)
	    	    	{
	    	    		txtSend=txtWrite.getText();
	    	    		TXdataDisplay=txtSend;
	    	    	}
	    	    	else
	    	    	{
	    	    		String s=txtWrite.getText();
	    	    		byte b[]=Convert.StringToByteArray(s,formatT);
	    	    		TXdataDisplay=Convert.ByteArrayToString(b, Converter.getLength(), formatT);
	    	    		txtWrite.setText(TXdataDisplay);
	    	    		txtSend=Convert.ByteArrayToStringASCII(b, Converter.getLength());
	    	    	}	
	    			
	    			TXdata=txtSend;
	    		}
	    		else if (e.getSource()==btnCR) Serial.write(13);	
	    		else if (e.getSource()==btnLF) Serial.write(10);
	    		else if (e.getSource()==btnCRLF) 
	    		{	
	    			Serial.write(13);
	    			Serial.write(10);
	    		}
	    		else
	    		{
	    			
	    			String s=((Button)e.getSource()).getId().toString();
	    			int i=Integer.parseInt(s.substring(7));
	    			HBox hbox=(HBox) vbox.getChildren().get(2*i);
	    	    	TextField txt=(TextField) hbox.getChildren().get(1);
	    	    	if(formatT==Converter.ASCII)
	    	    	{
	    	    		txtSend=txt.getText();
	    	    		TXdataDisplay=txtSend;
	    	    	}
	    	    	else
	    	    	{
	    	    		s=txt.getText();
	    	    		byte b[]=Convert.StringToByteArray(s,formatT);
	    	    		TXdataDisplay=Convert.ByteArrayToString(b, Converter.getLength(), formatT);
	    	    		txt.setText(TXdataDisplay);
	    	    		txtSend=Convert.ByteArrayToStringASCII(b, Converter.getLength());
	    	    	
	    	    	}	
	    	    	TXdata=txtSend;
	
	    		}
	    		
	    	}
	

	    }

    }
    
    public Button getScanButton()
    {
    	return btnScan;
    }

    void setTimer(String s)
    {
    	switch(s)
    	{
    	case "Single transmit":
    		multiplier=99;
    		break;
    	case "Periodically at 0.1 s":
    		multiplier=100;
    		break;
    	case "Periodically at 0.2 s":
    		multiplier=200;
    		break;
    	case "Periodically at 0.5 s":
    		multiplier=500;
    		break;
    	case "Periodically at 1 s":
    		multiplier=1000;
    		break;
    	case "Periodically at 2 s":
    		multiplier=2000;
    		break;
    	case "Periodically at 5 s":
    		multiplier=5000;
    		break;
    	case "Periodically at 10 s":
    		multiplier=10000;
    		break;
    	}
    }

    public class SerialWriter implements Runnable 
    {

    	private SerialPortModel Serial=new SerialPortModel();
    	private final BlockingQueue<String> messageQueue ;
    	
    	public SerialWriter(SerialPortModel s, BlockingQueue<String> messageQueue)
    	{
    		Serial=s;
    		this.messageQueue=messageQueue;
    		
    	}
		@Override
		synchronized public void run() {
			
			while(true)
			{
			

				try
				{	

					if(startReading)
					{
						if(!TXdata.isEmpty()) 
						{	
								wait=true;
								StringBuilder TXstring=new StringBuilder(TXdata);
								StringBuilder TXstringDisplay=new StringBuilder(TXdataDisplay);
								TXstringDisplay.insert(0,"TX-> ");
								
								if(itemCR.isSelected()&&itemLF.isSelected())
								{
									Serial.write(TXstring.append("\r\n").toString());
								
								}
								else if(itemCR.isSelected()) 
								{
									Serial.write(TXstring.append("\r").toString());
							
								}
								else if(itemLF.isSelected()) 
								{
									Serial.write(TXstring.append("\n").toString());
						
								}
								else 
								{
									Serial.write(TXstring.toString());
								}
								
								messageQueue.put(TXstringDisplay.toString());

								if(multiplier==99) 
								{
									TXdata="";
								}	
								
								wait=false;
						}	
					}
					else
					{	
						if(Serial.isConnected())
						{
							String s=Serial.disconnect();
							TXdata="";
							Platform.runLater(()->txtInfo.setText(s));
						}
						
					}
					
					Thread.sleep(multiplier);
				} 
				catch (InterruptedException e) 
				{
					
				}		
			}		
		}
    }

    public class CheckForPort implements Runnable 
    {
    	
    	private SerialPortModel Serial;

    	
    	public CheckForPort(SerialPortModel s)
    	{
    		Serial=s;
    	}
    	@Override
    	synchronized public void run() 
    	{
			while(true)
			{
	    		try
		    	{	
	    			Thread.sleep(500);
	    		}
				catch (InterruptedException e) 
				{
					
				}
    			if(check)
    			{

    				//System.out.println("check "+ check);
    				
	  		 		Platform.runLater(new Runnable(){
	  		 			@Override
		  		 		synchronized public void run() 
		  		 		{
		  		 			if(port!=null&&!Serial.getChecked(port))
				    		{
		  		 				startReading=false;
			    				btnDisconnect.fire();
			    				btnScan.fire();
				    					
					    	}
			  		 		else btnScan.fire();
			    		}
	  		 		});
    				check=false;
	    		}
    			
  				
    			if(startReading)
	    		{
    				if(Serial.getMessage().contains("error")) 
    				{
						
    					Platform.runLater(()->
    					{
    						txtInfo.setText(Serial.getMessage());
	    					startReading=false;
	    					btnDisconnect.fire();
	    					Serial.disconnect();
							TXdata="";
    					}
    					);
    				}
    			}


			}
    	}


    	
    }
    
    public static class SerialReader implements SerialPortEventListener 
    {
   
        public SerialReader ()
        {
        }
        
        public void serialEvent(SerialPortEvent e) 
        {
            try
            {
            	ok=true;
            }
            catch ( Exception ex )
            {
                System.exit(-1);
            }             
        }

    }
    public class textChange implements ChangeListener<String>
    {

    	boolean del=false;

		@Override
		public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) 
		{
			if(formatT!=Converter.ASCII)
			{
				int changedIndex=0;
				int caretPosition=0;
				for(int i=0;i<=lastAdded;i++)
				{
					if(observable.hashCode()==txtHashCodes[i])
						changedIndex=i;
				}
			
				
				HBox hbox=(HBox) vbox.getChildren().get(2*changedIndex);
		    	TextField txt=(TextField) hbox.getChildren().get(1);
		    	caretPosition=txt.getCaretPosition();		
	
			
	
				if(caretPosition<(newValue.length()-1))del=true;
				else
				{
					del=false;
					if(oldValue.length()>newValue.length())del=true;
				}
	
				String s;
				if(!del)
				{
					s=processFormatRegex(this.setRegex(formatT), newValue, oldValue, Converter.SEPARATOR, formatT);
					Platform.runLater(()->
					{
						txt.textProperty().set(s);
						txt.end();
					}
					);
	
				}
				else
				{
					s=processFormatRegexDEL(this.setRegexDEL(formatT), newValue, oldValue, Converter.SEPARATOR, formatT);
					Platform.runLater(()->
					{
						int c=txt.getCaretPosition();
						txt.textProperty().set(s);
						txt.positionCaret(c);
					}
					);
					
	
				}
			}

		}

		
		public String setRegex(int f)
		{
	        String r="";
			if(f==Converter.DEC) r="[0-9]{0,}(-)?"; 	
			if(f==Converter.HEX) r="[0-9A-Fa-f]{0,}(-)?";		
			if(f==Converter.BIN) r="[0-1]{0,}(-)?";  
			return r;	
		}
		
		public String setRegexDEL(int f)
		{
           String r="";
		   if(f==Converter.DEC) r="[0-9]{4,}";   	
		   if(f==Converter.HEX) r="[0-9A-Fa-f]{3,}";		
		   if(f==Converter.BIN) r="[0-1]{9,}";   
		   return r;
	
		}
		
		public String processFormatRegexDEL(String regex, String newValue, String oldValue, char separator, int format)
		{
			Pattern checkRegex= Pattern.compile(regex);
			Matcher regexMatcher=checkRegex.matcher(newValue);
			String newString=newValue;

				while(regexMatcher.find())
				{
					if(regexMatcher.group().length()!=0)
					{
						newString=newValue.substring(0,regexMatcher.start());
						newString+=toByte(regexMatcher.group().substring(0,Converter.getNumOfDigits(format)), format);
						newString+=newValue.substring(regexMatcher.end(),newValue.length());
					}
				}
				return newString;
		}
		
		public String processFormatRegex(String regex, String newValue, String oldValue, char separator, int format)
		{
			Pattern checkRegex= Pattern.compile(regex);
			Matcher regexMatcher=checkRegex.matcher(newValue);
			String newString="";

				while(regexMatcher.find())
				{
					if(regexMatcher.group().length()!=0)
					{
						
						if(regexMatcher.group().length()<Converter.getNumOfDigits(format))
						{
							if(newValue.charAt(regexMatcher.end()-1)==separator)
							{
								String s="";
								for(int i=0;i<=(Converter.getNumOfDigits(format)-regexMatcher.group().length());i++)
								{
									s+="0";
								}
								if(regexMatcher.group().length()==1)
									newString+=s+separator;
								else
									newString+=s+regexMatcher.group().substring(0,regexMatcher.group().length()-1)+separator;
								
							}
							else
								newString+=regexMatcher.group().substring(0,regexMatcher.group().length());
						}
						else if(regexMatcher.group().length()==Converter.getNumOfDigits(format))
						{
							if(newValue.charAt(regexMatcher.end()-1)==separator)
								newString+="0"+regexMatcher.group().substring(0,regexMatcher.group().length()-1)+separator;
							else
							{
								newString+=toByte(regexMatcher.group().substring(0,regexMatcher.group().length()), format)+separator;
							}
						}
						else if(regexMatcher.group().length()>Converter.getNumOfDigits(format))
						{
							if(newValue.charAt(regexMatcher.start()+Converter.getNumOfDigits(format))==separator)
									newString+=toByte(regexMatcher.group().substring(0,Converter.getNumOfDigits(format)), format)+separator;
							else
							{
							    String s="";
							    int count=0;
								for(int i=0;i<(regexMatcher.group().length()-1);i++)
								{
								    s+=regexMatcher.group().charAt(i);
								    count++;
								    if(count==Converter.getNumOfDigits(format))
								    {
								    	count=0;
								    	newString+=toByte(s, format)+separator;
								    	s="";
								    }
								}
								if(!s.isEmpty())newString+=s+separator;
								
							}
						}
						
					}
				}
				return newString;
		}
		
		public String toByte(String string, int format)
		{
			int i=Integer.parseInt(string, format);
			if(i>255) string="255";
			return string.toUpperCase();	
		}

		
    }
   
   public class HotplugListener implements UsbServicesListener
   {
    	public void usbDeviceAttached(UsbServicesEvent event) 
    	{ 
    		check=true;
    	}
    	public void usbDeviceDetached(UsbServicesEvent event) 
    	{ 
    		check=true;
    	} 
    } 

/*
   public class SerialOwnershipHandler implements CommPortOwnershipListener
   {
       String id;

       public SerialOwnershipHandler(String id) 
       {
           this.id = id;
       }
       
       public void ownershipChange(int type) 
       {
           switch (type) 
           {
               case CommPortOwnershipListener.PORT_OWNED:
                   System.out.println("PORT OWNED");
                   break;
               case CommPortOwnershipListener.PORT_UNOWNED:
                   System.out.println("PORT RELESED");
                   break;
               case CommPortOwnershipListener.PORT_OWNERSHIP_REQUESTED:
            	   System.out.println("PORT OWNERSHIP REQUESTED");
                   break;
                   
           }
           
       }
   }*/


    
}
