
package tictactoe.helpers;

import java.util.List;
import org.hibernate.Criteria;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.criterion.Restrictions;
import tictactoe.models.Player;
import tictactoe.network.messages.RegisterMessage;


public class AuthHelper {
    
    public static int register(RegisterMessage msg){
        
        boolean validEmail = Validator.validateEmail(msg.getEmail());
        boolean validName = Validator.validateName(msg.getDisplayName());
        boolean validPass = Validator.validatePassword(msg.getPassword());
        
        if(!validEmail || !validName || !validPass )
            return 1;
        
        Session session = DBManager.getInstance().openSession();
        Criteria cr = session.createCriteria(Player.class);
        cr.add(Restrictions.eq("email", msg.getEmail()));
        List results = cr.list();
        
        if(results.size() > 0)
            return 1;
        
        int validInsert = 0;
        Transaction tx  = null;
     
        try{
            tx = session.beginTransaction();
            Player player = new Player();
            player.setDisplayName(msg.getDisplayName());
            player.setEmail(msg.getEmail());
            player.setPassword(msg.getPassword());
            session.save(player); 
            tx.commit();
        }catch (HibernateException e) {
            validInsert = 1;
            if (tx!=null) tx.rollback();
            e.printStackTrace(); 
        }finally {
              session.close(); 
        }
        
        return validInsert;
    }
}
