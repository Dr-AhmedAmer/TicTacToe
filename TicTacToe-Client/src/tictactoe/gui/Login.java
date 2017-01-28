package tictactoe.gui;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.effect.DropShadow;
import javafx.scene.effect.Reflection;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.stage.Stage;
 
public class Login extends Application {
 
 String user = "JavaFX2";
 String pw = "password";
 String checkUser, checkPw;
 
    public static void main(String[] args) {
        launch(args);
    }
     
    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("TicTacToe Login");
        
        BorderPane bp = new BorderPane();
        bp.setPadding(new Insets(10,50,50,50));
        
        //Adding HBox
        HBox hb = new HBox();
        hb.setPadding(new Insets(20,20,20,30));
        
        //Adding GridPane
        GridPane gridPane = new GridPane();
        gridPane.setPadding(new Insets(20,20,20,20));
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
        gridPane.add(btnLogin, 0, 2,2,2);
        gridPane.add(lblMessage, 1, 3);
        
                         
      
        bp.setTop(hb);
        bp.setCenter(gridPane);  
        
     Scene scene = new Scene(bp);
     primaryStage.setScene(scene);
     primaryStage.show();
    }
}