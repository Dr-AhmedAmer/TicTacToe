/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package tictactoe.models;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.PostUpdate;
import javax.persistence.Table;
import org.codehaus.jackson.annotate.JsonIgnore;
import org.hibernate.Session;



/**
 *
 * @author ahmed
 */

@Entity
@Table(name = "players")
public class Player {
    
//    private SessionManager sMan = SessionManager.getInstance();
    public final static String STATUS_OFFLINE = "offln";
    public final static String STATUS_IDLE = "idle";
    public final static String STATUS_PLAYING = "play";
    
    @Id @GeneratedValue 
    @Column(name = "id")
    private int id;
    
    @Column(name = "display_name")
    private String displayName;
    
    @Column(name = "email")
    private String email;
    
    @JsonIgnore
    @Column(name = "password")
    private String password;
    
    @Column(name = "points")
    private int points;
    
    @Column(name = "status")
    private String status;

    @Column(name = "image")
    private String image;
    
//    @PostUpdate
//    public void refreshPlayerList(){
//        
//    }

    public String getImage() {
	return image;
    }

    public void setImage(String image) {
    this.image = image;
    }
	
	
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public String getEmail() {
        
        String email = this.email;
        if (email != null){
            email = email.toLowerCase();
        }
        return email;
    }

    public void setEmail(String email) {
        if (email != null){
            email = email.toLowerCase();
        }
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public int getPoints() {
        return points;
    }
    

    public void setPoints(int points) {
        this.points = points;
    }
    
    public void addPoints(int points){
        this.points+=points;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }       
    
}
