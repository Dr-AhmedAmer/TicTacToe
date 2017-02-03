
package tictactoe.helpers;

import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.cfg.AnnotationConfiguration;
import org.hibernate.cfg.Configuration;
import tictactoe.models.Player;

public class DBManager {
    private static DBManager instance = null;
    private SessionFactory factory;
            
    private DBManager(){
        if(this.factory == null){
            this.factory = new AnnotationConfiguration().configure()
                    .addAnnotatedClass(Player.class).buildSessionFactory();
        }
    }
    
    public synchronized static DBManager getInstance()
    {
        if (instance == null){
            instance = new DBManager();
        }
        return instance;
    }
    
    public Session openSession(){
        return this.factory.openSession();
    }
    
    public <T> void update(T obj){
        
        Session s = this.openSession();
        Transaction tx = null;
        
        try{
            tx = s.beginTransaction();
            s.update(obj);
            tx.commit();
        }catch(HibernateException e){
            if(tx != null){
                tx.rollback();
            }
            e.printStackTrace();
        }finally{
            s.close();
        }
        
    }
}
