
package tictactoe.helpers;

import java.util.List;
import java.lang.Iterable;
import java.util.Iterator;
import org.hibernate.Criteria;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import tictactoe.models.Player;

public class PlayerHelper {
    
    public static ResultList<Player> getIdlePlayersExceptMe(int meId){
        
        Session session = DBManager.getInstance().openSession();
        Criteria cr = session.createCriteria(Player.class);
//        cr.add(Restrictions.eq("status", Player.STATUS_IDLE));
        cr.addOrder(Order.desc("points"));
        cr.add(Restrictions.ne("id", meId));
        List results = cr.list();
        
        ResultList<Player> resultList = new ResultList<>();
        resultList.setResults(results);
        
        return resultList;
        
    }
    
    public static boolean checkAvaliablePlayer(int medId,int playerId){
        
        boolean result = false;
        
        ResultList<Player> resultList = getIdlePlayersExceptMe(medId);
        
       for(Player player: resultList.getResults()){
           
           if(playerId == player.getId()){
               result = true;
               break;
           }
       }
        return result;    
    }
    
}
