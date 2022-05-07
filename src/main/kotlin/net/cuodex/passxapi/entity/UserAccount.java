package net.cuodex.passxapi.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "users", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"username"})
})
public class UserAccount {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Getter
    private long id;
    @Getter @Setter
    private String username;
    @Getter @Setter
    private String email;

    @JsonIgnore
    @Getter @Setter
    private String passwordTest;
    @Getter @Setter
    private String createdAt;
    @Getter @Setter
    private String lastSeen;

    @JsonIgnore
    @OneToMany(mappedBy = "userAccount", fetch = FetchType.EAGER)
    @Getter @Setter
    private Set<LoginCredential> loginCredentials = new HashSet<>();

    public void addCredential(LoginCredential loginCredential) {
        loginCredentials.add(loginCredential);
    }

}
