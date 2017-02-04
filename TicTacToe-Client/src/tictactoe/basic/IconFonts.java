package tictactoe.basic;

import javafx.scene.Parent;
import javafx.scene.Scene;

public class IconFonts {

    public static void addToScene(Scene scene) {
        scene.getStylesheets().add(IconFonts.class.getClassLoader().getResource("assets/fonts.css").toExternalForm());
    }

    public static void addToParent(Parent parent) {
        parent.getStylesheets().add(IconFonts.class.getClassLoader().getResource("assets/fonts.css").toExternalForm());
    }
}
