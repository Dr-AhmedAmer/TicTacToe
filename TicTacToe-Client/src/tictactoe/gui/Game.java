package tictactoe.gui;

import com.sun.javafx.scene.control.skin.LabeledText;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
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

	ListView<String> listView = new ListView<String>();
	private SessionManager sMan = SessionManager.getInstance();
	private GameManager gMan = GameManager.getInstance();
	private boolean playable = true;
	private boolean turnX = true;
	private Tile[][] board = new Tile[3][3];
	private List<Combo> combos = new ArrayList<>();
	private HashMap<String,Integer> playersmap=new HashMap<String,Integer>();
	private final Image OFFLINE = new Image("file:offline.png");
	private final Image ONLINE = new Image("file:online.png");
	private Image[] listOfImages = {ONLINE, OFFLINE};

	private SessionManager.GameControlListener gameControlListener = new SessionManager.GameControlListener() {
		@Override
		public void onGameRequest(int senderId) {

		}

		@Override
		public void onGameResponse(int senderId, int response) {

		}

		@Override
		public void onPlayerList(List<Player> players) {
			System.out.println(".onPlayerList()");
			System.out.println(players.size());
			if (players == null) {
				System.out.println("Empty");
			} else {
				ObservableList<Player> playerslist = FXCollections.observableArrayList(players);
				playerslist.forEach(p -> {
					listView.getItems().add(p.getDisplayName());
					playersmap.put(p.getDisplayName(), p.getId());
					
				});
				listView.setOnMouseClicked((MouseEvent event) -> {
						if (event.getButton().equals(MouseButton.PRIMARY)) {
							if (event.getClickCount() == 2) {
								
								sMan.sendInvite(playersmap.get(listView.getSelectionModel()
                                                    .getSelectedItem()));//your code here  
								System.out.println("id" + playersmap.get(listView.getSelectionModel()
                                                    .getSelectedItem()));
							}
						}

					});

				//				listView.setCellFactory(param -> new ListCell<String>() {
				//					private ImageView imageView = new ImageView();
				//
				//					@Override
				//					public void updateItem(String name, boolean empty) {
				//						super.updateItem(name, empty);
				//						if (empty) {
				//							setText(null);
				//							setGraphic(null);
				//						} else {
				//							if (name.equals("ONLINE")) {
				//								imageView.setImage(listOfImages[0]);
				//							} else if (name.equals("OFFLINE")) {
				//								imageView.setImage(listOfImages[1]);
				//							}
				//							setText(name);
				//							setGraphic(imageView);
				//						}
				//					}
				//				});
			}

		}
	};

	private Pane boardPane = new Pane();
	BorderPane root = new BorderPane();
	private GameManager.GameListener gameListener = new GameManager.GameListener() {
		@Override
		public void onGameMove(int x, int y) {
//			if (turnX) {
//				return;
//			}
			board[x][y].drawO();
			turnX = true;
			checkState();

		}

		@Override
		public void onGameEnd(String winner) {

		}
	};

	private Parent createContent() {
		boardPane.setPrefSize(600, 600);

		for (int i = 0; i < 3; i++) {
			for (int j = 0; j < 3; j++) {
				Tile tile = new Tile();
				tile.setTranslateX(j * 200);
				tile.setTranslateY(i * 200);
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

		boardPane.getChildren().add(listView);
		root.setCenter(boardPane);
		root.setRight(listView);

		return root;
	}

	@Override
	public void start(Stage primaryStage) throws Exception {

		gMan.setGameListener(gameListener);
		sMan.setGameControlListener(gameControlListener);
		sMan.sendListPlayers();
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
			Rectangle border = new Rectangle(200, 200);
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
					if (!turnX) {
						return;
					}
					gMan.move(this.x, this.y);
					System.out.println(this.x + " " + this.y);
					drawX();
					turnX = false;
					checkState();
				} else if (event.getButton() == MouseButton.SECONDARY) {

				}
			});
		}

		public double getCenterX() {
			return getTranslateX() + 100;
		}

		public double getCenterY() {
			return getTranslateY() + 100;
		}

		public String getValue() {
			return text.getText();
		}

		private void drawX() {
			text.setText("X");
		}

		private void drawO() {
			text.setText("O");
		}
	}

	public static void main(String[] args) {
		launch(args);

	}

}
