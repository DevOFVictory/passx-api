package net.cuodex.passxapi.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.*;
import org.hibernate.Hibernate;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

@Entity
@RequiredArgsConstructor
@ToString
@Table(name = "users", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"username"})
})
@JsonIgnoreProperties({"hibernateLazyInitializer"})
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
    @Getter @Setter
    private Boolean serverSideEncryption;
    @Getter @Setter
    private String ipAddress;


    @JsonIgnore
    @OneToMany(mappedBy = "userAccount", fetch = FetchType.EAGER, cascade = CascadeType.ALL, orphanRemoval = true)
    @Getter @Setter
    private Set<LoginCredential> loginCredentials = new HashSet<>();

    public void addCredential(LoginCredential loginCredential) {
        loginCredential.setUserAccount(this);
        loginCredentials.add(loginCredential);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
        UserAccount that = (UserAccount) o;
        if (id == 0L)
            return false;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}
