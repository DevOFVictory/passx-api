package net.cuodex.passxapi.entity;

import javax.persistence.*;

@Entity
public class UserAccount {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    private String username;
    private String email;
    private String passwordTest;

    public Long getId() {
        return id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPasswordTest() {
        return passwordTest;
    }

    public void setPasswordTest(String passwordTest) {
        this.passwordTest = passwordTest;
    }
}
