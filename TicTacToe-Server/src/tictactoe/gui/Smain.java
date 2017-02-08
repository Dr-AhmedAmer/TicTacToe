/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package tictactoe.gui;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXComboBox;
import com.jfoenix.controls.JFXListView;
import java.util.List;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
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
import javafx.scene.text.Font;
import javafx.scene.text.FontPosture;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.stage.WindowEvent;
import tictactoe.helpers.PlayerHelper;
import tictactoe.models.Player;
import tictactoe.network.Server;
import tictactoe.network.SessionManager;

/**
 *
 * @author lina
 */
public class Smain extends Application implements SessionManager.PlayerStatusListener {

	JFXComboBox filter = new JFXComboBox();
	HBox btnhb = new HBox();
	VBox listvb = new VBox();
	BorderPane root = new BorderPane();
	JFXButton startbtn = new JFXButton("Start");
	JFXButton stopbtn = new JFXButton("Stop");
	JFXButton closebtn = new JFXButton("Close");
	JFXListView<Player> listView = new JFXListView<>();
	private SessionManager sMan = SessionManager.getInstance();

	@Override
	public void start(Stage primaryStage) {

		sMan.setPlayerStatusListener(this);
		listView.setPrefHeight(550);
		startbtn.getStyleClass().add("button-raised");
		stopbtn.getStyleClass().add("button-raised-red");
		closebtn.getStyleClass().add("button-raised-gray");
		btnhb.getChildren().addAll(startbtn, stopbtn,closebtn);
		btnhb.setAlignment(Pos.CENTER);
		listvb.setPrefHeight(Double.MAX_VALUE);
		filter.getItems().setAll("Online", "Offline", "All");
		filter.getSelectionModel().selectFirst();
		filter.setPrefWidth(Double.MAX_VALUE);
		listvb.getChildren().addAll(filter, listView);
		root.setCenter(listvb);
		root.setBottom(btnhb);

		startbtn.setOnAction((ActionEvent event) -> {
			Server.start(8000);
			genrateListView(PlayerHelper.getAllPlayers().getResults());
		});
		stopbtn.setOnAction((ActionEvent event) -> {
			Server.stop();
		});
		closebtn.setOnAction((event) -> {
			Server.stop();
			System.exit(0);
		});
		primaryStage.setOnCloseRequest((WindowEvent event) -> {
			event.consume();
		});
		Scene scene = new Scene(root, 300, 600);
		scene.getStylesheets().add(getClass().getClassLoader().getResource("assets/style.css").toString());
		primaryStage.setResizable(false);
		primaryStage.setTitle("Server");
		primaryStage.setScene(scene);
		primaryStage.show();
	}

	private void genrateListView(List<Player> players) {
		listView.getStyleClass().add("mylistview");
		ObservableList<Player> playerslist = FXCollections.observableArrayList(players);
		filter.setOnAction((event) -> {
			switch (filter.getSelectionModel().getSelectedItem().toString()) {
				case "Online":
					listView.getItems().clear();
					playerslist.filtered((p) -> p.getStatus().equals("play") || p.getStatus().equals("idle")).forEach((p) -> {
						listView.getItems().add(p);
					});
					break;
				case "Offline":
					listView.getItems().clear();
					playerslist.filtered((p) -> p.getStatus().equals("offln")).forEach((p) -> {
						listView.getItems().add(p);
					});
					break;
				case "All":
					listView.getItems().clear();
					playerslist.forEach(p -> {
						listView.getItems().add(p);
					});
					break;
				default:
					break;
			}
		});
		switch (filter.getSelectionModel().getSelectedItem().toString()) {
			case "Online":
				listView.getItems().clear();
				playerslist.filtered((p) -> p.getStatus().equals("play") || p.getStatus().equals("idle")).forEach((p) -> {
					listView.getItems().add(p);
				});
				break;
			case "Offline":
				listView.getItems().clear();
				playerslist.filtered((p) -> p.getStatus().equals("offln")).forEach((p) -> {
					listView.getItems().add(p);
				});
				break;
			case "All":
				listView.getItems().clear();
				playerslist.forEach(p -> {
					listView.getItems().add(p);
				});
				break;
			default:
				break;
		}

		listView.setCellFactory(parm -> new ListCell<Player>() {
			@Override
			public void updateItem(Player player, boolean empty) {
				super.updateItem(player, empty);
				if (empty) {
					setText(null);
					setGraphic(null);
				} else {

					HBox hBox = new HBox();
					hBox.setSpacing(3);
					VBox ptvb = new VBox();
					ptvb.setAlignment(Pos.CENTER);
					Text pt = new Text(String.valueOf(player.getPoints()));
					pt.setFont(Font.font(pt.getFont().toString(), FontWeight.BLACK, FontPosture.ITALIC, 10));
					Text name = new Text(player.getDisplayName());
					ImageView statusImageView = new ImageView();
					Image statusImage = new Image(getClass().getClassLoader().getResource("images/" + player.getStatus() + ".png").toString(), 16, 16, true, true);
					statusImageView.setImage(statusImage);
					ptvb.getChildren().addAll(statusImageView, pt, name);
					ImageView pictureImageView = new ImageView();
					Image image = new Image(getClass().getClassLoader().getResource("images/avatars/" + player.getImage()).toString(), 50, 50, true, true);
					pictureImageView.setImage(image);
					hBox.getChildren().addAll(pictureImageView, ptvb);
					hBox.setAlignment(Pos.CENTER_LEFT);

					setGraphic(hBox);

				}

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
		if (list != null) {
			listView.getItems().clear();
			this.genrateListView(list);
		} else {
			listView.getItems().clear();
			list = PlayerHelper.getAllPlayers().getResults();
			this.genrateListView(list);
		}
	}

}
