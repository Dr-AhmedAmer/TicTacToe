/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package tictactoe.gui;
import java.util.List;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Orientation;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import tictactoe.helpers.PlayerHelper;
import tictactoe.helpers.ResultList;
import tictactoe.models.Player;
import tictactoe.network.Server;
import tictactoe.network.SessionManager;

/**
 *
 * @author lina
 */
public class Smain extends Application implements SessionManager.PlayerStatusListener  {
    
    
    ListView<Player> listView = new ListView<>();
    private SessionManager sMan = SessionManager.getInstance();
    

    @Override
    public void start(Stage primaryStage) {
        
        sMan.setPlayerStatusListener(this);
        Label lport =new Label("Port");
        TextField port= new TextField();
        VBox vboxin = new VBox(10);
        vboxin.minHeight(80);
         
        VBox vbox = new VBox(10);
        
        BorderPane root =new BorderPane();
        Button btn = new Button();
        btn.setText("start server");
        
        vbox.getChildren().addAll(lport,port,vboxin,btn);
        root.setMargin(vbox, new Insets(80,100,0,100));
        
        root.setCenter(vbox);
        
        
        
        
        btn.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
               Server.start(Integer.parseInt(port.getText()));
                Platform.runLater(() -> {
                    
                genrateListView(PlayerHelper.getAllPlayers().getResults());
                root.setMargin(listView, new Insets(50,100,50,100));
                root.setCenter(listView);
                    
                    
                });
            
            }
        });
        
        
        Scene scene = new Scene(root, 400, 500);
        
        primaryStage.setTitle("server");
        primaryStage.setScene(scene);
        primaryStage.show();
    }
    
    
    
    private void genrateListView(List<Player> players) {
                listView.getItems().clear();
		ObservableList<Player> playerslist = FXCollections.observableArrayList(players);
		playerslist.forEach(p -> {
			listView.getItems().add(p);
		});
		listView.setCellFactory(parm -> new ListCell<Player>() {
			private final ImageView imageView = new ImageView();

			@Override
			public void updateItem(Player player, boolean empty) {
                            
                            Platform.runLater(() -> {
                                super.updateItem(player, empty);
				if (empty) {
					setText(null);
					setGraphic(null);
				} else {
					HBox hBox = new HBox();
					hBox.setSpacing(3);
					Text name = new Text(player.getDisplayName());

					ImageView statusImageView = new ImageView();
					Image statusImage = new Image(getClass().getClassLoader().getResource("images/" + player.getStatus() + ".png").toString(), 16, 16, true, true);
					statusImageView.setImage(statusImage);


					hBox.getChildren().addAll( statusImageView, name);
					hBox.setAlignment(Pos.CENTER_LEFT);

					setGraphic(hBox);

				}
                                
                            });
				
			}
		});
	}

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void onPlayerStatusChange(List<Player> list) {
        System.out.println("onPlayerStatusChange Called");
        this.genrateListView(list);
    }
    
}
