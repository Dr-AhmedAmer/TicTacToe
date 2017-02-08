package tictactoe.gui;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXComboBox;
import com.jfoenix.controls.JFXListView;
import com.jfoenix.controls.JFXPasswordField;
import com.jfoenix.controls.JFXTextArea;
import com.jfoenix.controls.JFXTextField;
import com.jfoenix.validation.RequiredFieldValidator;
import de.jensd.fx.glyphs.fontawesome.*;
import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import tictactoe.emoji.*;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.application.HostServices;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.embed.swing.SwingFXUtils;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Orientation;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.SnapshotParameters;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ScrollPane;
import javafx.scene.effect.DropShadow;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.image.WritableImage;
import javafx.scene.input.KeyCode;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundImage;
import javafx.scene.layout.BackgroundPosition;
import javafx.scene.layout.BackgroundRepeat;
import javafx.scene.layout.BackgroundSize;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.FontPosture;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.stage.Popup;
import javafx.stage.Stage;
import javafx.util.Duration;
import javax.imageio.ImageIO;
import tictactoe.basic.BasicUtils;
import tictactoe.client.game.GameManager;
import tictactoe.client.network.SessionManager;
import tictactoe.models.Player;
import tictactoe.network.messages.AuthResultMessage;

public class Game extends Application {
	
	JFXTextArea notification = new JFXTextArea();	
	JFXListView<Player> listView = new JFXListView<>();
	JFXButton chatEmojBtn = new JFXButton();
	JFXButton chatSend = new JFXButton();
	JFXTextArea chatText = new JFXTextArea();
	JFXListView chatList = new JFXListView();
	Label winsc = new Label();
	Label losesc = new Label();
	int losenum = 0;
	int winnum = 0;
	private Player player;
	private SessionManager sMan = SessionManager.getInstance();
	private GameManager gMan = GameManager.getInstance();
	private boolean playable = true;
	private boolean yourTurn = false;
	private boolean sender = false;
	private String playerSymbol;
	private String opponentSymbol;
	private Color opponentColor = Color.rgb(41, 128, 185);
	private Color playerColor = Color.rgb(192, 57, 43);
	private Tile[][] board = new Tile[3][3];
	private List<Combo> combos = new ArrayList<>();
	Scene login;
	Scene signup;
	Scene game;
	Stage thestage;
	String user;
	String pw;
	int opponentId;
	Label lblMessage = new Label();
	StackPane rootstack = new StackPane();
	ImageView wait = new ImageView(new Image(getClass().getClassLoader().getResource("images/wait.gif").toString()));
	Text responsetxt = new Text();
	VBox reqvb = new VBox();
	JFXButton aiplay = new JFXButton("Play with computer");
	JFXButton playagain = new JFXButton("Play Again");
	Line line = new Line();
	JFXComboBox filter = new JFXComboBox();
	HBox chatHbox = new HBox();
	HostServices services = this.getHostServices();
	private SessionManager.GameControlListener gameControlListener = new SessionManager.GameControlListener() {
		@Override
		public void onGameRequest(int senderId) {
			Platform.runLater(() -> {
				Popup requestpop = new Popup();
				yesNoPopup(requestpop, "Player sends you request\nWhat you gonna do?",
						"Accept", "Reject",
						(event) -> {
							opponentId = senderId;
							sMan.sendResponse(senderId, 0);
							resetGame();
							requestpop.hide();
						},
						(event) -> {
							sMan.sendResponse(senderId, 1);
							requestpop.hide();
						});
				requestpop.show(thestage, thestage.getX() + 100, thestage.getY() + 200);
			});
		}
		
		@Override
		public void onGameResponse(int senderId, int response, String symbol) {
			
			Platform.runLater(() -> {
				rootstack.getChildren().remove(reqvb);
				responsetxt.setFont(Font.font(responsetxt.getFont().toString(), FontWeight.BOLD, 24));
				Popup notify = notifyPopup(responsetxt);
				if (response == 0) {
					rootstack.getChildren().remove(wait);
					responsetxt.setText("Player Accepted");
					responsetxt.setFill(Color.GREEN);
					if (symbol.equals("X")) {
						yourTurn = true;
						opponentSymbol = "O";
					} else {
						opponentSymbol = "X";
					}
					playerSymbol = symbol;
					gMan.startGame();
					
				} else {
					rootstack.getChildren().remove(wait);
					responsetxt.setFill(Color.RED);
					responsetxt.setText("Player Rejected");
					
				}
				if (sender) {
					notify.show(thestage, thestage.getX() + 100, thestage.getY() + 200);
				}
			});
		}
		
		@Override
		public void onPlayerList(List<Player> players) {
			Platform.runLater(() -> {
				
				if (players == null) {
					System.out.println("Empty");
				} else {
					listView.getItems().clear();
					genrateListView(players);
					Popup oflinPop = new Popup();
					yesNoPopup(oflinPop, "Sory player is offline,\nbut you can play with computer",
							"Yes", "No",
							(event) -> {
								sMan.sendAIInvite(player.getId());
								oflinPop.hide();
							}, (event) -> {
								oflinPop.hide();
							});
					Popup playPop = new Popup();
					yesNoPopup(playPop, "Sory player is bussy now,\nbut you can play with computer",
							"Yes", "No",
							(event) -> {
								sMan.sendAIInvite(player.getId());
								playPop.hide();
							}, (event) -> {
								playPop.hide();
							});
					listView.setOnMouseClicked((MouseEvent event) -> {
						if (event.getButton().equals(MouseButton.PRIMARY)) {
							if (event.getClickCount() == 2) {
								if (listView.getSelectionModel().getSelectedItem().getStatus().equals("idle")) {
									rootstack.getChildren().add(wait);
									sMan.sendInvite(listView.getSelectionModel().getSelectedItem().getId());
									sender = true;
									resetGame();
									
								} else if (listView.getSelectionModel().getSelectedItem().getStatus().equals("play")) {
									playPop.show(thestage, thestage.getX() + 100, thestage.getY() + 200);
								} else if (listView.getSelectionModel().getSelectedItem().getStatus().equals("offln")) {
									oflinPop.show(thestage, thestage.getX() + 100, thestage.getY() + 200);
								}
								
							}
						}
					});
				}
				
			});
			
		}
		
	};
	
	private void yesNoPopup(Popup pop, String message, String okstr, String nostr, EventHandler<ActionEvent> okaction, EventHandler<ActionEvent> noaction) {
		JFXButton popupOk = new JFXButton();
		JFXButton popupNo = new JFXButton();
		VBox popupContainer = new VBox();
		HBox popupBtnContainer = new HBox();
		Label popupmessage = new Label();
		popupOk.setText(okstr);
		popupNo.setText(nostr);
		popupmessage.setText(message);
		popupmessage.setGraphic(new FontAwesomeIconView(FontAwesomeIcon.WARNING));
		popupOk.getStyleClass().add("button-raised");
		popupNo.getStyleClass().add("button-raised-red");
		popupOk.setGraphic(new FontAwesomeIconView(FontAwesomeIcon.THUMBS_UP));
		popupNo.setGraphic(new FontAwesomeIconView(FontAwesomeIcon.THUMBS_DOWN));
		popupBtnContainer.getChildren().setAll(popupOk, popupNo);
		popupBtnContainer.setSpacing(10);
		popupBtnContainer.setAlignment(Pos.CENTER);
		popupContainer.getChildren().setAll(popupmessage, popupBtnContainer);
		popupContainer.setEffect(new DropShadow(5, Color.BLACK));
		popupContainer.setAlignment(Pos.CENTER);
		popupContainer.setId("pop");
		pop.getContent().setAll(popupContainer);
		popupOk.setOnAction(okaction);
		popupNo.setOnAction(noaction);
	}
	
	private Popup notifyPopup(Node message) {
		Popup notifypop = new Popup();
		JFXButton popupOk = new JFXButton();
		VBox popupContainer = new VBox();
		HBox popupBtnContainer = new HBox();
		popupOk.setText("Close");
		popupOk.getStyleClass().add("button-raised");
		popupOk.setGraphic(new FontAwesomeIconView(FontAwesomeIcon.CLOSE));
		popupBtnContainer.getChildren().setAll(popupOk);
		popupBtnContainer.setSpacing(10);
		popupBtnContainer.setAlignment(Pos.CENTER);
		popupContainer.getChildren().setAll(message, popupBtnContainer);
		popupContainer.setEffect(new DropShadow(5, Color.BLACK));
		popupContainer.setAlignment(Pos.CENTER);
		popupContainer.setId("notifypop");
		popupOk.setOnAction((event) -> {
			notifypop.hide();
		});
		notifypop.getContent().setAll(popupContainer);
		return notifypop;
	}
	
	private SessionManager.AuthListener authListener = new SessionManager.AuthListener() {
		@Override
		public void onSuccess(Player p) {
			Platform.runLater(() -> {
				player = p;
				game = createGameRoot();
				game.getStylesheets().add(BasicUtils.getResourceUrl(Game.class, "style.css"));
				thestage.setScene(game);
			});
		}
		
		@Override
		public void onFailure(AuthResultMessage msg) {
			Platform.runLater(() -> {
				if (msg.getErrors().size() > 0) {
					for (tictactoe.helpers.Error err : msg.getErrors()) {
						lblMessage.setText(err.getDescription());
					}
				}
			});
			
		}
	};
	
	private GameManager.GameListener gameListener = new GameManager.GameListener() {
		@Override
		public void onGameMove(int x, int y) {
			board[x][y].drawMove(opponentSymbol);
			checkState();
			yourTurn = true;
		}
		
		@Override
		public void onGameEnd(String winner) {
			Platform.runLater(() -> {
				responsetxt.setFont(Font.font(responsetxt.getFont().toString(), FontWeight.BOLD, 60));
				playagain.getStyleClass().add("button-raised");
				reqvb.setAlignment(Pos.CENTER);
				playagain.setOnAction((event) -> {
					if (opponentId == 0) {
						sMan.sendAIInvite(player.getId());
					} else {
						sMan.sendInvite(opponentId);
					}
					resetGame();
					rootstack.getChildren().remove(reqvb);
				});
				if (winner.equals("Winner")) {
					responsetxt.setFill(Color.GREEN);
					winsc.setText(String.valueOf(++winnum));
				} else if (winner.equals("Looser")) {
					responsetxt.setFill(Color.RED);
					losesc.setText(String.valueOf(++losenum));
				} else {
					responsetxt.setFill(Color.BLACK);
				}
				responsetxt.setText(winner);
				reqvb.getChildren().setAll(responsetxt, playagain);
				reqvb.setOnMouseClicked(null);
				rootstack.getChildren().add(reqvb);
				sender = false;
				chatHbox.setDisable(true);
			});
			
		}
		
		@Override
		public void onGameChatTextMessage(Player sender, String content) {
			
			Platform.runLater(() -> {
				HBox hBox = new HBox();
				VBox vBox = new VBox();
				hBox.setSpacing(3);
				vBox.setSpacing(3);
				Text name = new Text(sender.getDisplayName());
				Text messeget = new Text(content);
				messeget.setFont(Font.font(messeget.getFont().toString(), FontWeight.LIGHT, FontPosture.ITALIC, 14));
				ImageView pictureImageView = new ImageView();
				Image image = new Image(getClass().getClassLoader().getResource("images/avatars/" + sender.getImage()).toString(), 20, 20, true, true);
				pictureImageView.setImage(image);
				
				hBox.getChildren().addAll(pictureImageView, name);
				hBox.setAlignment(Pos.CENTER_LEFT);
				vBox.setStyle("-fx-background-color:rgba(52, 152, 219,1.0);-fx-padding:5px;-fx-border-radius:10");
				vBox.getChildren().addAll(hBox, messeget);
				chatList.getItems().add(vBox);
			});
			
		}
	};
	
	public void setgMan(GameManager gMan) {
		this.gMan = gMan;
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
					if (player.getStatus().equals("idle")) {
						notification.clear();
						notification.setText(notification.getText() + "\n   s" + player.getDisplayName() + " is online");
					}
					hBox.getChildren().addAll(pictureImageView, ptvb);
					hBox.setAlignment(Pos.CENTER_LEFT);
					
					setGraphic(hBox);
					
				}
				
			}
		});
	}
	
	private Scene createLoginRoot() {
		Text welcome = new Text("Welcome to our TicTacToe Game");
		welcome.setFont(Font.font(responsetxt.getFont().toString(), FontWeight.BOLD, 18));
		welcome.setFill(Color.WHITESMOKE);
		welcome.setEffect(new DropShadow(5, Color.BLACK));
		BorderPane bp = new BorderPane();
		bp.setPadding(new Insets(30));
		bp.setBackground(new Background(new BackgroundImage(new Image(BasicUtils.getResourceUrl(Game.class, "images.jpg")), BackgroundRepeat.NO_REPEAT, BackgroundRepeat.NO_REPEAT, BackgroundPosition.CENTER, BackgroundSize.DEFAULT)));

		//Adding GridPane
		GridPane gridPane = new GridPane();
		gridPane.setPadding(new Insets(20, 20, 20, 20));
		gridPane.setHgap(5);
		gridPane.setVgap(10);

		//Implementing Nodes for GridPane
		JFXTextField txtEmail = new JFXTextField();
		txtEmail.setPromptText("Email");
		JFXPasswordField pf = new JFXPasswordField();
		pf.setPromptText("Password");
		JFXButton btnLogin = new JFXButton("Login");
		btnLogin.getStyleClass().add("button-raised");
		btnLogin.setMaxWidth(Double.MAX_VALUE);
		JFXButton btnSignUp = new JFXButton("Sign Up");
		btnSignUp.setMaxWidth(Double.MAX_VALUE);
		btnSignUp.getStyleClass().add("button-raised");
		lblMessage.getStyleClass().add("lblMessage");
		//Adding Nodes to GridPane layout
		gridPane.add(welcome, 0, 0, 2, 2);
		gridPane.add(txtEmail, 0, 4, 2, 2);
		gridPane.add(pf, 0, 8, 2, 2);
		gridPane.add(btnLogin, 0, 12, 2, 2);
		gridPane.add(btnSignUp, 0, 14, 2, 2);
		gridPane.add(lblMessage, 0, 16, 2, 2);
		
		RequiredFieldValidator validator = new RequiredFieldValidator();
		validator.setMessage("Input Required");
		validator.setIcon(new FontAwesomeIconView(FontAwesomeIcon.WARNING));
		txtEmail.getValidators().add(validator);
		txtEmail.focusedProperty().addListener((o, oldVal, newVal) -> {
			if (!newVal) {
				txtEmail.validate();
				pf.resetValidation();
				
			}
		});
		RequiredFieldValidator pfvalidator = new RequiredFieldValidator();
		pfvalidator.setMessage("Input Required");
		pfvalidator.setIcon(new FontAwesomeIconView(FontAwesomeIcon.WARNING));
		pf.getValidators().add(pfvalidator);
		pf.focusedProperty().addListener((o, oldVal, newVal) -> {
			if (!newVal) {
				pf.validate();
				txtEmail.resetValidation();
				lblMessage.setText("");
			}
		});
		txtEmail.focusedProperty().addListener((arg0, oldValue, newValue) -> {
			if (!newValue) {
				if (!txtEmail.getText().matches("[A-Za-z0-9._%-]+\\@[A-Za-z]+\\.[A-Za-z]+.")) {
					lblMessage.setText("invalid Email");
				}
			}
		});
		btnLogin.setOnAction(e -> {
			user = txtEmail.getText();
			pw = pf.getText();
			sMan.login(user, pw);
		});
		btnSignUp.setOnAction((event) -> {
			signup = createSignUPRoot();
			signup.getStylesheets().add(BasicUtils.getResourceUrl(Game.class, "style.css"));
			thestage.setScene(signup);
		});
		gridPane.setAlignment(Pos.CENTER);
		bp.setCenter(gridPane);
		return new Scene(bp, 650, 650);
		
	}
	String avatar;
	
	private Scene createSignUPRoot() {
		String avatars[] = {"man1.png", "man2.png", "man3.png", "man4.png", "girl1.png", "girl2.png", "girl3.png", "girl4.png"};
		BorderPane bp = new BorderPane();
		bp.setPadding(new Insets(10, 50, 50, 50));
		bp.setBackground(new Background(new BackgroundImage(new Image(BasicUtils.getResourceUrl(Game.class, "images.jpg")), BackgroundRepeat.NO_REPEAT, BackgroundRepeat.NO_REPEAT, BackgroundPosition.CENTER, BackgroundSize.DEFAULT)));

		//Adding GridPane
		GridPane gridPane = new GridPane();
		gridPane.setPadding(new Insets(20, 20, 20, 20));
		gridPane.setHgap(5);
		gridPane.setVgap(10);

		//Implementing Nodes for GridPane
		JFXTextField txtUserName = new JFXTextField();
		JFXTextField txtEmail = new JFXTextField();
		JFXPasswordField pf = new JFXPasswordField();
		JFXButton btnReg = new JFXButton("Register");
		JFXButton btnback = new JFXButton("Back");
		btnback.setGraphic(new FontAwesomeIconView(FontAwesomeIcon.HOME));
		btnReg.setGraphic(new FontAwesomeIconView(FontAwesomeIcon.SIGN_IN));
		
		btnReg.setMaxWidth(Double.MAX_VALUE);
		btnback.setMaxWidth(Double.MAX_VALUE);
		
		btnReg.getStyleClass().add("button-raised");
		btnback.getStyleClass().add("button-raised");
		JFXListView<String> avatarsList = new JFXListView<>();
		avatarsList.setOrientation(Orientation.HORIZONTAL);
		avatarsList.setPrefSize(290, 30);
		for (String ava : avatars) {
			avatarsList.getItems().add(ava);
		}
		avatarsList.setCellFactory(parm -> new ListCell<String>() {
			private final ImageView imageView = new ImageView();
			
			@Override
			public void updateItem(String ava, boolean empty) {
				super.updateItem(ava, empty);
				if (empty) {
					setText(null);
					setGraphic(null);
				} else {
					ImageView avaImageView = new ImageView();
					Image image = new Image(getClass().getClassLoader().getResource("images/avatars/" + ava).toString(), 20, 20, true, true);
					avaImageView.setImage(image);
					setGraphic(avaImageView);
				}
			}
		});
		
		avatarsList.setOnMouseClicked((MouseEvent event) -> {
			if (event.getButton().equals(MouseButton.PRIMARY)) {
				avatar = avatarsList.getSelectionModel().getSelectedItem();
			}
		});
		
		txtEmail.setPromptText("Email");
		pf.setPromptText("Password");
		txtUserName.setPromptText("User Name");
		//Adding Nodes to GridPane layout
		gridPane.add(txtUserName, 0, 0, 2, 2);
		gridPane.add(txtEmail, 0, 4, 2, 2);
		gridPane.add(pf, 0, 8, 2, 2);
		gridPane.add(avatarsList, 0, 12, 2, 2);
		gridPane.add(btnReg, 0, 14, 2, 2);
		gridPane.add(btnback, 0, 16, 2, 2);
		gridPane.add(lblMessage, 0, 18, 2, 2);
		btnback.setOnAction((event) -> {
			thestage.setScene(login);
		});
		RequiredFieldValidator pfvalidator = new RequiredFieldValidator();
		pfvalidator.setMessage("Input Required");
		pfvalidator.setIcon(new FontAwesomeIconView(FontAwesomeIcon.WARNING));
		pf.getValidators().add(pfvalidator);
		pf.focusedProperty().addListener((o, oldVal, newVal) -> {
			if (!newVal) {
				txtUserName.resetValidation();
				txtEmail.resetValidation();
				pf.validate();
				lblMessage.setText("");
			}
		});
		RequiredFieldValidator usrvalidator = new RequiredFieldValidator();
		usrvalidator.setMessage("Input Required");
		usrvalidator.setIcon(new FontAwesomeIconView(FontAwesomeIcon.WARNING));
		txtUserName.getValidators().add(usrvalidator);
		txtUserName.focusedProperty().addListener((o, oldVal, newVal) -> {
			
			if (!newVal) {
				txtUserName.validate();
				txtEmail.resetValidation();
				pf.resetValidation();
				lblMessage.setText("");
			}
			
		});
		
		RequiredFieldValidator mailvalidator = new RequiredFieldValidator();
		mailvalidator.setMessage("Input Required");
		mailvalidator.setIcon(new FontAwesomeIconView(FontAwesomeIcon.WARNING));
		txtEmail.getValidators().add(mailvalidator);
		txtEmail.focusedProperty().addListener((o, oldVal, newVal) -> {
			if (!newVal) {
				txtEmail.validate();
				txtUserName.resetValidation();
				pf.resetValidation();
			}
		});
		txtEmail.focusedProperty().addListener((arg0, oldValue, newValue) -> {
			if (!newValue) {
				if (!txtEmail.getText().matches("[A-Za-z0-9._%-]+\\@[A-Za-z]+\\.[A-Za-z]+.")) {
					lblMessage.setText("invalid Email");
					txtEmail.setText("");
				}
			}
			
		});
		
		btnReg.setOnAction(e -> {
			String name = txtUserName.getText();
			String email = txtEmail.getText();
			String pass = pf.getText();
			if (avatar == null) {
				lblMessage.setText("Choose an avatar first");
			} else {
				lblMessage.setText("");
				sMan.register(email, pass, name, avatar);
				
			}
			
		});
		gridPane.setAlignment(Pos.CENTER);
		bp.setCenter(gridPane);
		return new Scene(bp, 650, 650);
		
	}
	
	Pane boardPane = new Pane();
	
	private Scene createGameRoot() {
		
		BorderPane root = new BorderPane();
		boardPane.setPrefSize(450, 450);
		root.setPrefSize(650, 650);
		HBox countitle = new HBox();
		HBox counters = new HBox();
		counters.setSpacing(20);
		losesc.setText(String.valueOf(losenum));
		winsc.setText(String.valueOf(winnum));
		Label wins = new Label("Wins");
		Label loses = new Label("Loses");
		wins.getStyleClass().add("labelc");
		loses.getStyleClass().add("labelc");
		winsc.getStyleClass().add("labelwin");
		losesc.getStyleClass().add("labellose");
		countitle.getChildren().addAll(wins, loses);
		counters.getChildren().addAll(winsc, losesc);
		counters.setAlignment(Pos.CENTER);
		countitle.setAlignment(Pos.CENTER);
		countitle.setPrefWidth(200);
		counters.setPrefWidth(200);
		for (int i = 0; i < 3; i++) {
			for (int j = 0; j < 3; j++) {
				Tile tile = new Tile();
				tile.setTranslateX(j * 150);
				tile.setTranslateY(i * 150);
				tile.setX(j);
				tile.setY(i);
				tile.setDisable(true);
				
				boardPane.getChildren().add(tile);
				board[j][i] = tile;
			}
		}

		// horizontal
		for (int y = 0; y < 3; y++) {
			combos.add(new Combo(board[0][y], board[1][y], board[2][y]));
		}

		// vertical
		for (int x = 0; x < 3; x++) {
			combos.add(new Combo(board[x][0], board[x][1], board[x][2]));
		}

		// diagonals
		combos.add(new Combo(board[0][0], board[1][1], board[2][2]));
		combos.add(new Combo(board[2][0], board[1][1], board[0][2]));
		
		listView.setPrefSize(200, 425);
		
		VBox chatVbox = new VBox();
		chatHbox.setPrefSize(450, 30);
		
		chatText.setPrefSize(350, 30);
		
		chatSend.setPrefWidth(70);
		chatSend.setGraphic(new FontAwesomeIconView(FontAwesomeIcon.SEND));
		chatEmojBtn.setGraphic(new FontAwesomeIconView(FontAwesomeIcon.SMILE_ALT));
		chatSend.setPrefHeight(Double.MAX_VALUE);
		
		filter.getItems().setAll("Online", "Offline", "All");
		filter.getSelectionModel().selectFirst();
		filter.setPrefWidth(200);
		
		chatEmojBtn.setPrefWidth(30);
		chatEmojBtn.setPrefHeight(Double.MAX_VALUE);
		chatHbox.getChildren().addAll(chatText, chatSend, chatEmojBtn);
		ScrollPane chatScroll = new ScrollPane();
		chatScroll.setPrefSize(600, 150);
		chatScroll.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
		chatScroll.setVbarPolicy(ScrollPane.ScrollBarPolicy.ALWAYS);
		
		chatScroll.setContent(chatList);
		chatList.setPrefSize(600, 150);
		
		chatHbox.setDisable(true);
		VBox playersvb = new VBox();
		filter.setPrefWidth(200);
		playersvb.setPrefWidth(200);
		aiplay.setPrefWidth(200);
		aiplay.getStyleClass().add("button-raised");
		notification.getStyleClass().add("notification");
		playersvb.getChildren().addAll(filter, listView, aiplay, countitle, counters, notification);
		rootstack.getChildren().add(boardPane);
		rootstack.setAlignment(Pos.CENTER);
		chatVbox.getChildren().addAll(rootstack, chatScroll, chatHbox);
		chatSend.setOnAction(e -> {
			sMan.sendChatMessage(player, chatText.getText());
			HBox hBox = new HBox();
			VBox vBox = new VBox();
			hBox.setSpacing(3);
			vBox.setSpacing(3);
			Text name = new Text("Me");
			Text messeget = new Text(chatText.getText());
			chatText.clear();
			messeget.setFont(Font.font(messeget.getFont().toString(), FontWeight.LIGHT, FontPosture.ITALIC, 14));
			ImageView pictureImageView = new ImageView();
			Image image = new Image(getClass().getClassLoader().getResource("images/avatars/" + player.getImage()).toString(), 20, 20, true, true);
			pictureImageView.setImage(image);
			
			hBox.getChildren().addAll(pictureImageView, name);
			hBox.setAlignment(Pos.CENTER_LEFT);
			vBox.setStyle("-fx-background-color:rgba(231, 76, 60,1.0);-fx-padding:5px;-fx-border-radius:10");
			vBox.getChildren().addAll(hBox, messeget);
			chatList.getItems().add(vBox);
		});
		
		chatEmojBtn.setOnAction((event) -> {
			EmojiUtil.showEmojiPopup(chatVbox, true, e -> {
				chatText.setText(chatText.getText() + e.getEmoji());
			});
		});
		aiplay.setOnAction((event) -> {
			rootstack.getChildren().remove(reqvb);
			rootstack.getChildren().add(wait);
			sMan.sendAIInvite(player.getId());
			opponentId = 0;
			resetGame();
			sender = true;
			chatHbox.setDisable(false);
		});
		
		root.setCenter(chatVbox);
		root.setRight(playersvb);
		return new Scene(root, 650, 650);
	}
	
	public void resetGame() {
		
		for (int i = 0; i < 3; i++) {
			for (int j = 0; j < 3; j++) {
				board[j][i].clear();
				board[j][i].setDisable(false);
			}
		}
		boardPane.getChildren().remove(line);
		playable = true;
		chatHbox.setDisable(false);
	}
	
	@Override
	public void start(Stage primaryStage) throws Exception {
		thestage = primaryStage;
		sMan.setAuthListener(authListener);
		gMan.setGameListener(gameListener);
		sMan.setGameControlListener(gameControlListener);
		
		login = createLoginRoot();
		login.getStylesheets().add(BasicUtils.getResourceUrl(Game.class, "style.css"));
		primaryStage.setResizable(false);
		primaryStage.setScene(login);
		primaryStage.show();
	}
	
	private void checkState() {
		for (Combo combo : combos) {
			if (combo.isComplete()) {
				playable = false;
				playWinAnimation(combo);
				break;
			}
		}
	}
	
	private void playWinAnimation(Combo combo) {
		Platform.runLater(() -> {
			
			line.setFill(playerColor);
			line.setStrokeWidth(5);
			line.setStartX(combo.tiles[0].getCenterX());
			line.setStartY(combo.tiles[0].getCenterY());
			line.setEndX(combo.tiles[0].getCenterX());
			line.setEndY(combo.tiles[0].getCenterY());
			
			boardPane.getChildren().add(line);
			
			Timeline timeline = new Timeline();
			timeline.getKeyFrames().add(new KeyFrame(Duration.seconds(1),
					new KeyValue(line.endXProperty(), combo.tiles[2].getCenterX()),
					new KeyValue(line.endYProperty(), combo.tiles[2].getCenterY())));
			timeline.play();
		});
		
	}
	
	private class Combo {
		
		private Tile[] tiles;
		
		public Combo(Tile... tiles) {
			this.tiles = tiles;
		}
		
		public boolean isComplete() {
			if (tiles[0].getValue().isEmpty()) {
				return false;
			}
			return tiles[0].getValue().equals(tiles[1].getValue())
					&& tiles[0].getValue().equals(tiles[2].getValue());
		}
	}
	
	private class Tile extends StackPane {
		
		private Text text = new Text();
		Color clr;
		private int x;
		private int y;
		
		public int getX() {
			return x;
		}
		
		public void setX(int x) {
			this.x = x;
		}
		
		public int getY() {
			return y;
		}
		
		public void setY(int y) {
			this.y = y;
		}
		
		public Tile() {
			Rectangle border = new Rectangle(150, 150);
			border.setFill(null);
			border.setStroke(Color.BLACK);
			
			text.setFont(Font.font(72));
			
			setAlignment(Pos.CENTER);
			getChildren().addAll(border, text);
			setOnMouseEntered((event) -> {
				if (this.text.getText().isEmpty()) {
					border.setFill(Color.LIGHTGRAY);
					border.setStroke(Color.DARKGRAY);
				}
			});
			setOnMouseExited((event) -> {
				if (this.text.getText().isEmpty()) {
					border.setFill(null);
					border.setStroke(Color.BLACK);
				}
			});
			setOnMouseClicked(event -> {
				
				if (!playable) {
					return;
				}
				
				if (event.getButton() == MouseButton.PRIMARY) {
					if (yourTurn && this.text.getText().isEmpty()) {
						drawMove(playerSymbol);
						checkState();
						border.setFill(null);
						border.setStroke(Color.BLACK);
						gMan.move(this.x, this.y);
						yourTurn = false;
					} else {
						return;
					}
				}
			});
		}
		
		public double getCenterX() {
			return getTranslateX() + 75;
		}
		
		public double getCenterY() {
			return getTranslateY() + 75;
		}
		
		public String getValue() {
			return text.getText();
		}
		
		public void clear() {
			text.setText("");
		}
		
		private void drawMove(String turn) {
			if (turn.equals("X")) {
				text.setFill(playerColor);
				text.setText("X");
			} else {
				text.setFill(opponentColor);
				text.setText("O");
			}
		}
	}
	
	public Parent createShare() {
		VBox container = new VBox();
		Platform.runLater(() -> {
			JFXButton facebook = new JFXButton("Facebook", new ImageView(new Image(getClass().getClassLoader().getResource("images/fb.png").toString(), 20, 20, true, true)));
			JFXButton twitter = new JFXButton("Twitter", new ImageView(new Image(getClass().getClassLoader().getResource("images/tw.png").toString(), 20, 20, true, true)));
			HBox sharehb = new HBox();
			sharehb.getChildren().addAll(facebook, twitter);
			container.getChildren().add(sharehb);
			WritableImage image = boardPane.snapshot(new SnapshotParameters(), null);
			facebook.setOnAction((event) -> {
				try {
					File file = new File("snap.png");
					ImageIO.write(SwingFXUtils.fromFXImage(image, null), "png", file);
					services.showDocument("https://www.facebook.com/sharer/sharer.php?u=" + file.toURI().toURL().toString());
				} catch (MalformedURLException ex) {
					Logger.getLogger(Game.class.getName()).log(Level.SEVERE, null, ex);
				} catch (IOException ex) {
					Logger.getLogger(Game.class.getName()).log(Level.SEVERE, null, ex);
				}
			});
			
		});
		return container;
	}
	
	public static void main(String[] args) {
		launch(args);
		
	}
	
	public void stop() throws Exception {
		
		sMan.stop();
		
		super.stop(); //To change body of generated methods, choose Tools | Templates.
	}
}
