//package tictactoe.gui;
//
//import java.util.ArrayList;
//import java.util.List;
//
//import javafx.animation.KeyFrame;
//import javafx.animation.KeyValue;
//import javafx.animation.Timeline;
//import javafx.application.Application;
//import javafx.collections.FXCollections;
//import javafx.collections.ObservableList;
//import javafx.geometry.Pos;
//import javafx.scene.Parent;
//import javafx.scene.Scene;
//import javafx.scene.control.ListCell;
//import javafx.scene.control.ListView;
//import javafx.scene.image.Image;
//import javafx.scene.image.ImageView;
//import javafx.scene.input.MouseButton;
//import javafx.scene.input.MouseEvent;
//import javafx.scene.layout.HBox;
//import javafx.scene.layout.Pane;
//import javafx.scene.layout.StackPane;
//import javafx.scene.paint.Color;
//import javafx.scene.shape.Line;
//import javafx.scene.shape.Rectangle;
//import javafx.scene.text.Font;
//import javafx.scene.text.Text;
//import javafx.stage.Stage;
//import javafx.util.Duration;
//import tictactoe.models.Player;
//
//public class Register extends Application {
//
//	ListView<String> listView = new ListView<>();
//	private String[] avatars = {"man1.png", "man2.png", "man3.png", "man4.png", "girl1.png", "girl2.png", "girl3.png", "girl4.png"};
//
//	@Override
//	public void start(Stage primaryStage) throws Exception {
//		ObservableList<String> playerslist = FXCollections.observableArrayList(avatars);
//		playerslist.forEach(img -> {
//			listView.getItems().add(img);
//		});
//		listView.setOnMouseClicked((MouseEvent event) -> {
//			if (event.getButton().equals(MouseButton.PRIMARY)) {
//				System.out.println(listView.getSelectionModel().getSelectedItem());
//			}
//		});
//
//		listView.setCellFactory(parm -> new ListCell<String>() {
//			private final ImageView imageView = new ImageView();
//
//			@Override
//			public void updateItem(String img, boolean empty) {
//				super.updateItem(img, empty);
//				if (empty) {
//					setText(null);
//					setGraphic(null);
//				} else {
//
//					ImageView avatarImageView = new ImageView();
//					Image image = new Image(getClass().getClassLoader().getResource("images/avatars/" + img).toString(), 30,30, true, true);
//					avatarImageView.setImage(image);
//					setGraphic(avatarImageView);
//
//				}
//			}
//		});
//		
//		primaryStage.setScene(new Scene()));
//		primaryStage.show();
//	}
//
//	public static void main(String[] args) {
//		launch(args);
//	}
//}
