package net.cuodex.passxapi.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;

@Data
@Entity
@Table(name = "users", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"username"})
})
public class UserAccount {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    private String username;
    private String email;

    @JsonIgnore
    private String passwordTest;
    private String createdAt;
    private String lastSeen;

    @JsonIgnore
    @OneToMany(mappedBy = "userAccount")
    private Set<LoginCredential> loginCredentials = new HashSet<>();

}
