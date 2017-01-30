package tictactoe.gui;

import com.sun.java.swing.plaf.windows.resources.windows;
import com.sun.javafx.stage.WindowHelper;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;
import tictactoe.client.network.SessionManager;
import tictactoe.models.Player;

public class Login extends Application {

	private Stage sta;
	private SessionManager.AuthListener authListener = new SessionManager.AuthListener() {
		@Override
		public void onSuccess(Player p) {
			try {
				Platform.runLater(new Runnable() {
					@Override
					public void run() {
						System.out.println("7asal");
						System.err.println(p.getEmail());
						Game g = new Game();
						try {
							g.start(sta);
						} catch (Exception ex) {
							Logger.getLogger(Login.class.getName()).log(Level.SEVERE, null, ex);
						}
					}
				});

			} catch (Exception ex) {
				Logger.getLogger(Login.class.getName()).log(Level.SEVERE, null, ex);
			}

		}

		@Override
		public void onFailure() {
			System.err.println("ma7asalsh");
		}
	};
	private SessionManager sMan = SessionManager.getInstance();

	String user;
	String pw;
	String checkUser, checkPw;

	public static void main(String[] args) {
		launch(args);

	}

	@Override
	public void start(Stage primaryStage) {
		this.sta = primaryStage;
		primaryStage.setTitle("TicTacToe Login");

		sMan.setAuthListener(authListener);

		BorderPane bp = new BorderPane();
		bp.setPadding(new Insets(10, 50, 50, 50));

		//Adding HBox
		HBox hb = new HBox();
		hb.setPadding(new Insets(20, 20, 20, 30));

		//Adding GridPane
		GridPane gridPane = new GridPane();
		gridPane.setPadding(new Insets(20, 20, 20, 20));
		gridPane.setHgap(5);
		gridPane.setVgap(5);

		//Implementing Nodes for GridPane
		Label lblUserName = new Label("Username");
		TextField txtUserName = new TextField();
		Label lblPassword = new Label("Password");
		PasswordField pf = new PasswordField();
		Button btnLogin = new Button("Login");
		btnLogin.setMaxWidth(Double.MAX_VALUE);
		Label lblMessage = new Label();

		//Adding Nodes to GridPane layout
		gridPane.add(lblUserName, 0, 0);
		gridPane.add(txtUserName, 1, 0);
		gridPane.add(lblPassword, 0, 1);
		gridPane.add(pf, 1, 1);
		gridPane.add(btnLogin, 0, 2, 2, 2);
		gridPane.add(lblMessage, 1, 3);

		btnLogin.setOnAction(e -> {
			user = txtUserName.getText();
			pw = pf.getText();
			sMan.login(user, pw);
		});
                
		bp.setTop(hb);
		bp.setCenter(gridPane);
                
                
		Scene scene = new Scene(bp);
		primaryStage.setScene(scene);
		primaryStage.show();
	}

    @Override
    public void stop() throws Exception {
        
        sMan.stop();
        
        super.stop(); //To change body of generated methods, choose Tools | Templates.
    }
        
        
}
