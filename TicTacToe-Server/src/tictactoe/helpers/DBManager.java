
package tictactoe.helpers;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.AnnotationConfiguration;
import org.hibernate.cfg.Configuration;
import tictactoe.models.Player;

public class DBManager {
    public static DBManager instance = null;
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
}
