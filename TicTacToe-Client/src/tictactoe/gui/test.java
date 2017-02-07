/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package tictactoe.gui;

import com.jfoenix.controls.JFXHamburger;
import com.jfoenix.controls.JFXListView;
import com.jfoenix.controls.JFXPopup;
import com.jfoenix.controls.JFXPopup.PopupHPosition;
import com.jfoenix.controls.JFXPopup.PopupVPosition;
import com.jfoenix.controls.JFXRippler;
import com.jfoenix.controls.JFXRippler.RipplerMask;
import com.jfoenix.controls.JFXRippler.RipplerPos;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

/**
 *
 * @author salamaashoush
 */
public class test extends Application {
	
	@Override
public void start(Stage primaryStage) throws Exception {
    JFXHamburger show = new JFXHamburger();
    show.setPadding(new Insets(10, 5, 10, 5));
    JFXRippler r = new JFXRippler(show, RipplerMask.CIRCLE, RipplerPos.BACK);
    JFXListView<Label> list = new JFXListView<Label>();
    for (int i = 1; i < 5; i++) list.getItems().add(new Label("Item " + i));
    AnchorPane container = new AnchorPane();
    container.getChildren().add(r);
    AnchorPane.setLeftAnchor(r, 200.0);
    AnchorPane.setTopAnchor(r, 210.0);
    StackPane main = new StackPane();
    main.getChildren().add(container);
    JFXPopup popup = new JFXPopup();
    popup.setContent(list);
    popup.setPopupContainer(main);
    popup.setSource(r);
    r.setOnMouseClicked(( e) -> popup.show(PopupVPosition.TOP, PopupHPosition.LEFT));
    final Scene scene = new Scene(main, 800, 800);
    primaryStage.setTitle("JFX Popup Demo");
    primaryStage.setScene(scene);
    primaryStage.setResizable(false);
    primaryStage.show();
}

	/**
	 * @param args the command line arguments
	 */
	public static void main(String[] args) {
		launch(args);
	}
	
}
