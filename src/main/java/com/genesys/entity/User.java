package com.genesys.entity;

import com.genesys.enums.user_enums.UserRole;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.UuidGenerator;

import java.util.UUID;

@Entity
@Table(name = "users")
@NoArgsConstructor


public class User {

    @Id
    @UuidGenerator(style = UuidGenerator.Style.VERSION_7)
    private UUID id;

    @Column(unique = true,nullable = false)
    private String username;
    @Column(nullable = false)
    private String password;
    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private UserRole role;
    @Column(nullable = false)
    private boolean active=true;

    public User(String username, String password, UserRole role){
        this.username=username;
        this.password=password;
        this.role = role;
    }

    public boolean getActive(){return active;}
    public UUID getId(){return id;}
    public String getUsername(){
        return username;
    }

    public String getPassword(){
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public UserRole getRole(){
        return role;
    }

    public boolean isActive(){
        return active;
    }

    public void deactivate(){
        this.active=false;
    }







}
