
package tictactoe.network;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.codehaus.jackson.map.ObjectMapper;
import tictactoe.helpers.AuthHelper;
import tictactoe.helpers.ResultObject;
import tictactoe.models.Player;
import tictactoe.network.messages.AuthMessage;
import tictactoe.network.messages.MessageTypes;
import tictactoe.network.messages.RegisterMessage;

public class Session implements Runnable{
    
    private Client client;
    private boolean isStarted;
    private ObjectMapper objectMapper;
    
    public Session(Client client){
        this.client = client;
        this.objectMapper = new ObjectMapper();
    }
    
    public void start(){
        if(!isStarted){
            Thread th = new Thread(this);
            th.start();
            isStarted = true;
        }
    }
    
    public void stop(){
        isStarted = false;
        this.client.close();
    }
    
    public void run(){
        
        while(isStarted){
            
            String type = this.client.recieve();
            String msg = this.client.recieve();
              
                
            if(type != null && msg != null){

            int index = type.indexOf("=");

            if(index >= 0){

                type = type.substring(index +1);

            }


            try {

                switch(type){

                    case MessageTypes.MSG_TYPE_AUTH:

                        AuthMessage authMessage = this.objectMapper.readValue(msg, AuthMessage.class);

                        ResultObject<Player> authResult = AuthHelper.logIn(authMessage);

                        client.send(this.objectMapper.writeValueAsString(authResult));

                        break;

                    case MessageTypes.MSG_TYPE_REG:

                        RegisterMessage registerMessage = this.objectMapper.readValue(msg, RegisterMessage.class);

                        ResultObject<Player> regResult = AuthHelper.register(registerMessage);

                        client.send(this.objectMapper.writeValueAsString(regResult));

                        break;
                }

            } catch (IOException ex) {

                Logger.getLogger(Session.class.getName()).log(Level.SEVERE, null, ex);
            }
                   
            }else{
                
                this.stop();
                System.out.println("client dropped");
                
            }
        }
        
        System.out.println("Session closed");
    }
    
}
