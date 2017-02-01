package tictactoe.gui;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.control.PasswordField;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.SeparatorMenuItem;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.FontPosture;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.util.Duration;
import tictactoe.client.game.GameManager;
import tictactoe.client.network.SessionManager;
import tictactoe.models.Player;

public class Game extends Application {

	ListView<Player> listView = new ListView<>();
	Button chatEmojBtn = new Button("emoj");
	Button chatSend = new Button("Send");
	TextArea chatText = new TextArea();
	ListView chatList = new ListView();
	private Player player;
	private SessionManager sMan = SessionManager.getInstance();
	private GameManager gMan = GameManager.getInstance();
	private boolean playable = true;
	private boolean yourTurn = false;
	private String playerSymbol;
	private String opponentSymbol;
	private Color opponentColor = Color.rgb(41, 128, 185);
	private Color playerColor = Color.rgb(192, 57, 43);
	private int response;
	private Tile[][] board = new Tile[3][3];
	private List<Combo> combos = new ArrayList<>();
	Scene login;
	Scene game;
	Stage thestage;
	String user;
	String pw;
	Label lblMessage = new Label();

	private SessionManager.GameControlListener gameControlListener = new SessionManager.GameControlListener() {
		@Override
		public void onGameRequest(int senderId) {
			System.out.println("request from" + senderId);
			System.out.println("enter response");
			Scanner sc = new Scanner(System.in);
			response = sc.nextInt();
			sMan.sendResponse(senderId, response);

		}

		@Override
		public void onGameResponse(int senderId, int response, String symbol) {

			if (response == 0) {
				System.out.println("player accepted");
				gMan.startGame();
				if (symbol.equals("X")) {
					yourTurn = true;
					opponentSymbol = "O";
				} else {
					opponentSymbol = "X";
				}
				playerSymbol = symbol;

			} else {
				System.out.println("player declined");
			}

		}

		@Override
		public void onPlayerList(List<Player> players) {
			Platform.runLater(() -> {

				if (players == null) {
					System.out.println("Empty");
				} else {
					genrateListView(players);
					listView.setOnMouseClicked((MouseEvent event) -> {
						if (event.getButton().equals(MouseButton.PRIMARY)) {
							if (event.getClickCount() == 2) {
								sMan.sendInvite(listView.getSelectionModel().getSelectedItem().getId());
							}
						}
					});
//				sMan.sendListPlayers();
				}

			});

		}

	};

	private SessionManager.AuthListener authListener = new SessionManager.AuthListener() {
		@Override
		public void onSuccess(Player p) {
			Platform.runLater(() -> {
				player = p;
				game = createGameRoot();
				thestage.setScene(game);
				sMan.sendListPlayers();
			});
		}

		@Override
		public void onFailure() {
			Platform.runLater(() -> {
				lblMessage.setText("this user is not in database");
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
			System.out.println(winner);
		}

		@Override
		public void onGameChatTextMessage(Player sender, String content) {

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

		}
	};

	public void setgMan(GameManager gMan) {
		this.gMan = gMan;
	}

	private void genrateListView(List<Player> players) {
		ObservableList<Player> playerslist = FXCollections.observableArrayList(players);
		playerslist.forEach(p -> {
			listView.getItems().add(p);
		});
		listView.setCellFactory(parm -> new ListCell<Player>() {
			private final ImageView imageView = new ImageView();

			@Override
			public void updateItem(Player player, boolean empty) {
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

					ImageView pictureImageView = new ImageView();
					Image image = new Image(getClass().getClassLoader().getResource("images/avatars/" + player.getImage()).toString(), 50, 50, true, true);
					pictureImageView.setImage(image);

					hBox.getChildren().addAll(pictureImageView, statusImageView, name);
					hBox.setAlignment(Pos.CENTER_LEFT);

					setGraphic(hBox);

				}
			}
		});
	}

	private Scene createLoginRoot() {

		BorderPane bp = new BorderPane();
		bp.setPadding(new Insets(10, 50, 50, 50));

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
		Button btnSignUp = new Button("Sign Up");
		btnSignUp.setMaxWidth(Double.MAX_VALUE);
		

		//Adding Nodes to GridPane layout
		gridPane.add(lblUserName, 0, 0);
		gridPane.add(txtUserName, 1, 0);
		gridPane.add(lblPassword, 0, 1);
		gridPane.add(pf, 1, 1);
		gridPane.add(btnLogin, 0, 2, 2, 2);
		gridPane.add(btnSignUp, 0, 4, 2, 2);
		gridPane.add(lblMessage, 0, 6,2,2);

		btnLogin.setOnAction(e -> {
			if(txtUserName.getText().equals("")){
				lblMessage.setText("Please enter your Name");
				return;
			}else if(pf.getText().equals("")){
				lblMessage.setText("Please enter your Password");
				return;
			}else{
				user = txtUserName.getText();
				pw = pf.getText();
				sMan.login(user, pw);
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

		for (int i = 0; i < 3; i++) {
			for (int j = 0; j < 3; j++) {
				Tile tile = new Tile();
				tile.setTranslateX(j * 150);
				tile.setTranslateY(i * 150);
				tile.setX(j);
				tile.setY(i);

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

		listView.setPrefWidth(200);
		boardPane.getChildren().add(listView);

		VBox chatVbox = new VBox();
		HBox chatHbox = new HBox();
		chatHbox.setPrefSize(450, 30);

		chatText.setPrefSize(350, 30);

		chatSend.setPrefWidth(70);
		chatSend.setPrefHeight(Double.MAX_VALUE);

		chatEmojBtn.setPrefWidth(30);
		chatEmojBtn.setPrefHeight(Double.MAX_VALUE);

		ImageView chatEmoj = new ImageView();

		chatEmojBtn.setGraphic(chatEmoj);
		chatHbox.getChildren().addAll(chatText, chatSend, chatEmojBtn);
		ScrollPane chatScroll = new ScrollPane();
		chatScroll.setPrefSize(600, 150);
		chatScroll.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);

		chatScroll.setContent(chatList);
		chatList.setPrefSize(600, 150);
		chatVbox.getChildren().addAll(boardPane, chatScroll, chatHbox);
		chatSend.setOnAction(e -> {
			sMan.sendChatMessage(player, chatText.getText());
			HBox hBox = new HBox();
			VBox vBox = new VBox();
			hBox.setSpacing(3);
			vBox.setSpacing(3);
			Text name = new Text("Me");
			Text messeget = new Text(chatText.getText());
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
		Menu gamem = new Menu("_Game");
		gamem.setMnemonicParsing(true);
		MenuItem newItem = new MenuItem("New");
		MenuItem openItem = new MenuItem("Open");
		MenuItem saveItem = new MenuItem("Save");
		MenuItem exitItem = new MenuItem("Exit");
		gamem.getItems().addAll(newItem, openItem, saveItem, new SeparatorMenuItem(), exitItem);

		Menu playerm = new Menu("_Player");
		gamem.setMnemonicParsing(true);
		MenuItem registerItem = new MenuItem("Register");
		MenuItem signItem = new MenuItem("Signin");
		MenuItem listItem = new MenuItem("List");
		MenuItem chatItem = new MenuItem("Chat");
		playerm.getItems().addAll(registerItem, signItem, listItem, chatItem);

		MenuBar bar = new MenuBar();
		bar.getMenus().addAll(gamem, playerm);
		bar.setPrefHeight(20);
		root.setTop(bar);
		root.setCenter(chatVbox);
		root.setRight(listView);
		return new Scene(root, 650, 650);
	}

	@Override
	public void start(Stage primaryStage) throws Exception {
		thestage = primaryStage;
		sMan.setAuthListener(authListener);
		gMan.setGameListener(gameListener);
		sMan.setGameControlListener(gameControlListener);

		login = createLoginRoot();
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
			Line line = new Line();
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

			setOnMouseClicked(event -> {

				if (!playable) {
					return;
				}

				if (event.getButton() == MouseButton.PRIMARY) {
					if (yourTurn) {
						drawMove(playerSymbol);
						checkState();
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

	public static void main(String[] args) {
		launch(args);

	}

	public void stop() throws Exception {

		sMan.stop();

		super.stop(); //To change body of generated methods, choose Tools | Templates.
	}
}
