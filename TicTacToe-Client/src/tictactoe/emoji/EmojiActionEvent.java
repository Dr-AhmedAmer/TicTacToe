package tictactoe.emoji;

import javafx.event.Event;
import javafx.event.EventType;

public class EmojiActionEvent extends Event {

    public static final EventType<EmojiActionEvent> EMOJI_ACTION =
            new EventType<EmojiActionEvent>(Event.ANY, "EMOJI-ACTION");
    private Emoji emoji;

    public EmojiActionEvent(Emoji emoji) {
        super(EMOJI_ACTION);
        this.emoji = emoji;
    }

    public Emoji getEmoji() {
        return emoji;
    }
}
