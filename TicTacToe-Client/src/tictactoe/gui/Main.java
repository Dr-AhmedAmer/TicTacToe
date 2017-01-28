package tictactoe.gui;
	
import javafx.scene.control.MenuBar;
import java.util.ArrayList;
import java.util.List;

import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.SeparatorMenuItem;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;


public class Main extends Application {


    @Override
    public void start(Stage primaryStage) throws Exception {
	BorderPane root =new BorderPane();
	Menu game = new Menu("_Game");
        game.setMnemonicParsing(true); 
        MenuItem newItem = new MenuItem("New");
        MenuItem openItem = new MenuItem("Open");
	MenuItem saveItem = new MenuItem("Save");
        MenuItem exitItem = new MenuItem("Exit");
        game.getItems().addAll(newItem, openItem, saveItem, new SeparatorMenuItem(), exitItem);
	
	Menu player = new Menu("_Player");
        game.setMnemonicParsing(true); 
        MenuItem registerItem = new MenuItem("Register");
        MenuItem signItem = new MenuItem("Signin");
	MenuItem listItem = new MenuItem("List");
        MenuItem chatItem = new MenuItem("Chat");
        player.getItems().addAll(registerItem,signItem,listItem,chatItem);
	
	MenuBar bar=new MenuBar();
	bar.getMenus().addAll(game,player);
	
	VBox mainbtns=new VBox();
	Button login=new Button("Login");
	Button register=new Button("Register");
	login.setMaxWidth(Double.MAX_VALUE);
	register.setMaxWidth(Double.MAX_VALUE);
	mainbtns.getChildren().addAll(login,register);
	mainbtns.setAlignment(Pos.CENTER);
	mainbtns.setSpacing(10);
	mainbtns.setPadding(new Insets(0, 20, 10, 20));
	root.setTop(bar);
	root.setCenter(mainbtns);
	
	
	    
        primaryStage.setScene(new Scene(root,400,500));
	primaryStage.setTitle("TicTacToe");
        primaryStage.show();
    }

    
    public static void main(String[] args) {
        launch(args);
    }
}