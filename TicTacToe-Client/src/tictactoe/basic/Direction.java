package tictactoe.basic;

public enum Direction {
    FORWARD, BACKWARD;

    public Direction getOppositDirection() {
        if(this.equals(FORWARD)) {
            return BACKWARD;
        } else {
            return FORWARD;
        }
    }
}
