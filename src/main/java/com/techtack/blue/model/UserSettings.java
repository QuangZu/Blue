package com.techtack.blue.model;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "user_settings")
@Data
public class UserSettings {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    
    @OneToOne
    @JoinColumn(name = "user_id")
    private User user;
    
    private boolean lightModeEnabled = false;
    
    public UserSettings() {
    }
    
    public UserSettings(User user) {
        this.user = user;
    }
}