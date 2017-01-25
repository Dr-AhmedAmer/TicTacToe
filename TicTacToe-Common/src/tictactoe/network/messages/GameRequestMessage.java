
package tictactoe.network.messages;

public class GameRequestMessage extends Message {
    
    private int senderId;
    private int reciverId;

    public int getSenderId() {
        return senderId;
    }

    public void setSenderId(int senderId) {
        this.senderId = senderId;
    }

    public int getReciverId() {
        return reciverId;
    }

    public void setReciverId(int reciverId) {
        this.reciverId = reciverId;
    }
    
}
