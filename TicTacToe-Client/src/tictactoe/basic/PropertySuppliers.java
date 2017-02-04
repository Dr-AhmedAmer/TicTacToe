package tictactoe.basic;

import javafx.beans.property.StringProperty;

import java.util.function.Supplier;

public class PropertySuppliers {

    public static Supplier<String> create(StringProperty property) {
        return () -> property.get();
    }
}
