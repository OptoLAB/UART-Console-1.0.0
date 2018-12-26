/*
 * Author: J. Bajic, 2018.
 */

import javafx.application.Application;
import javafx.application.Platform;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ToolBar;
import javafx.scene.image.Image;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

public class MySerialGUIFX extends Application  {

	public static void main(String[] args)
	{
			Application.launch(args);
	}
	
	@Override
	public void start(Stage stage) throws Exception {
		Pane panel= (Pane) FXMLLoader.load(MySerialGUIFX.class.getResource("/resources/FXML/SerialGUIFXML.fxml"));
		Scene scena=new Scene(panel);
		VBox v=(VBox) panel.getChildren().get(0);
		ToolBar t=(ToolBar) v.getChildren().get(1);
		Button scan=(Button) t.getItems().get(4);
	
		
		panel.autosize();
		stage.setTitle("UART Console 1.0.0");
		stage.getIcons().add(new Image("/resources/icon.png"));
		stage.sizeToScene();
		//stage.setResizable(false);
		scena.getStylesheets().add("/resources/css/MyAppStyle2.css");
		stage.setScene(scena);
		stage.setMinHeight(500);
		stage.setMinWidth(770);
		scan.fire(); // scan ports and set up gui on start
		stage.show();
		
		
	

		
		stage.setOnCloseRequest(new EventHandler<WindowEvent>() {
            @Override
            public void handle(WindowEvent t) {
                Platform.exit();
                System.exit(0);
            }
		});
		
	}

}