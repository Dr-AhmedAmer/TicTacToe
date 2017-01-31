package tictactoe.gui;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Scanner;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.SeparatorMenuItem;
import javafx.scene.control.TextArea;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.util.Duration;
import tictactoe.client.game.GameManager;
import tictactoe.client.network.SessionManager;
import tictactoe.models.Player;

public class Game extends Application {

	ListView<Player> listView = new ListView<>();
	private SessionManager sMan = SessionManager.getInstance();
	private GameManager gMan = GameManager.getInstance();
	private boolean playable = true;
	private boolean yourTurn = true;
	private String symbol="X";
    private int response;
	private Tile[][] board = new Tile[3][3];
	private List<Combo> combos = new ArrayList<>();
	private SessionManager.GameControlListener gameControlListener = new SessionManager.GameControlListener() {
		@Override
		public void onGameRequest(int senderId) {
                    System.out.println("request from" +senderId);
                    System.out.println("enter response");
                    Scanner sc = new Scanner(System.in);
                    response=sc.nextInt();
                    sMan.sendResponse(senderId, response);

		}

		@Override
		public void onGameResponse(int senderId, int response ,String symbol) {
                    
                    if(response == 0){
                        System.out.println("player accepted");
			gMan.startGame();
                        System.out.println("ur symbol"+" "+symbol);
                    }else{
                        System.out.println("player declined");
                    }
                    
		}

		@Override
		public void onPlayerList(List<Player> players) {
			if (players == null) {
				System.out.println("Empty");
			} else {
				ObservableList<Player> playerslist = FXCollections.observableArrayList(players);
				playerslist.forEach(p -> {
					listView.getItems().add(p);
				});
				listView.setOnMouseClicked((MouseEvent event) -> {
						if (event.getButton().equals(MouseButton.PRIMARY)) {
							if (event.getClickCount() == 2) {
								sMan.sendInvite(listView.getSelectionModel().getSelectedItem().getId());
							}
						}
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
                    Text name = new Text(player.getDisplayName());
					
                    ImageView statusImageView = new ImageView();
                    Image statusImage = new Image(getClass().getClassLoader().getResource("images/" + player.getStatus()+ ".png").toString(), 16, 16,true,true);
                    statusImageView.setImage(statusImage);

                    ImageView pictureImageView = new ImageView();
                    Image image = new Image(getClass().getClassLoader().getResource("images/" + player.getStatus()+ ".png").toString(),50,50,true,true);
                    pictureImageView.setImage(image);

                    hBox.getChildren().addAll(statusImageView, pictureImageView, name);
                    hBox.setAlignment(Pos.CENTER_LEFT);

                    setGraphic(hBox);
                   
                }
            }
        });

			}

		}
	};

	private Pane boardPane = new Pane();
	BorderPane root = new BorderPane();
	private GameManager.GameListener gameListener = new GameManager.GameListener() {
		@Override
		public void onGameMove(int x, int y) {
			if (!yourTurn) {
				System.out.println("from o return if x");
				return;
			}
			System.out.println("x= "+x+", y= "+y);
			board[x][y].drawMove(symbol);
			yourTurn=true;
			symbol="O";
			System.out.println("from o turn x");
			checkState();

		}

		@Override
		public void onGameEnd(String winner) {

		}
	};

	private Parent createContent() {
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
		
		VBox chatVbox =new VBox();
		HBox chatHbox =new HBox();
		chatHbox.setPrefSize(450,30);
		TextArea chatText=new TextArea();
		chatText.setPrefSize(350, 30);
		Button chatSend =new Button("Send");
		chatSend.setPrefWidth(70);
		chatSend.setPrefHeight(Double.MAX_VALUE);
		Button chatEmojBtn=new Button("emoj");
		chatEmojBtn.setPrefWidth(30);
		chatEmojBtn.setPrefHeight(Double.MAX_VALUE);
		
		ImageView chatEmoj=new ImageView();
		
		chatEmojBtn.setGraphic(chatEmoj);
		chatHbox.getChildren().addAll(chatText,chatSend,chatEmojBtn);
		ScrollPane chatScroll =new ScrollPane();
		chatScroll.setPrefSize(600, 150);
		chatScroll.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
		ListView chatList =new ListView();
		chatScroll.setContent(chatList);
		chatList.setPrefSize(600,150);
		chatVbox.getChildren().addAll(boardPane,chatScroll,chatHbox);
		
		
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
		bar.setPrefHeight(20);
		root.setTop(bar);
		root.setCenter(chatVbox);
		root.setRight(listView);

		return root;
	}

	@Override
	public void start(Stage primaryStage) throws Exception {

		gMan.setGameListener(gameListener);
		sMan.setGameControlListener(gameControlListener);
		sMan.sendListPlayers();
		primaryStage.setResizable(false);
		primaryStage.setScene(new Scene(createContent()));
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
		Line line = new Line();
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
//					if(!yourTurn){
//						return;
//					}
					drawMove(symbol);
					gMan.move(this.x, this.y);
					yourTurn = false;
					symbol="X";
					System.out.println("from x turn o "+yourTurn);
					
					checkState();
				} else if (event.getButton() == MouseButton.SECONDARY) {

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
                        text.setText("X");
                    }else{
                        text.setText("O");

                    }
		}

		private void drawO() {
			text.setText("O");
		}
	}

	public static void main(String[] args) {
		launch(args);

	}

}
