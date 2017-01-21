
package tictactoe.helpers;

import java.util.List;
import org.hibernate.Criteria;
import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.criterion.Restrictions;
import tictactoe.models.Player;
import tictactoe.network.messages.AuthMessage;
import tictactoe.network.messages.RegisterMessage;


public class AuthHelper {
    
    public static ResultObject<Player> register(RegisterMessage msg){
        
        ResultObject<Player> result = new ResultObject<>();
        
        boolean validEmail = Validator.validateEmail(msg.getEmail());
        boolean validName = Validator.validateName(msg.getDisplayName());
        boolean validPass = Validator.validatePassword(msg.getPassword());
        
        if(!validEmail)
            result.addError(new Error("Invalid Email"));
        
        if(!validName)
            result.addError(new Error("Invalid Name"));
        
        if(!validPass)
            result.addError(new Error("Invalid Password"));
        
        if(!validEmail || !validName || !validPass )
            return result;
        
        Session session = DBManager.getInstance().openSession();
        Criteria cr = session.createCriteria(Player.class);
        cr.add(Restrictions.eq("email", msg.getEmail()));
        List results = cr.list();
        
        if(results.size() > 0){
            
            result.addError(new Error("Email already exists"));
            return result;
        }
        
        Transaction tx  = null;
     
        try{
            tx = session.beginTransaction();
            Player player = new Player();
            player.setDisplayName(msg.getDisplayName());
            player.setEmail(msg.getEmail());
            player.setPassword(msg.getPassword());
            session.save(player); 
            tx.commit();
            
            result.setResult(player);
            
        }catch (HibernateException e) {
            
            if (tx!=null) tx.rollback();
            e.printStackTrace();
            
            result.addError(new Error(e.getMessage()));
            
        }finally {
              session.close(); 
        }
        
        return result;
    }
    
    public static ResultObject<Player> logIn(AuthMessage msg){
        
        ResultObject<Player> result = new ResultObject<>();
        
        boolean validEmail = Validator.validateEmail(msg.getUserName());
        boolean validPass = Validator.validatePassword(msg.getPassword());
        
        if(!validEmail)
            result.addError(new Error("Invalid Email"));
        
        if(!validPass)
            result.addError(new Error("Invalid Password"));
        
        if(!validEmail || !validPass )
            return result;
        
        Session session = DBManager.getInstance().openSession();
        Query query = session.createQuery("from Player p where p.email = :QueryEmail and p.password = :QueryPassword");
        query.setParameter("QueryEmail",msg.getUserName());
        query.setParameter("QueryPassword",msg.getPassword());
        List list = query.list();
   
        if(list.size() > 0){
            
            result.setResult((Player)list.get(0));            
            
        }else{
            
            result.addError(new Error("User doens't exist"));
        }
        
        return result;
            
    }
}
