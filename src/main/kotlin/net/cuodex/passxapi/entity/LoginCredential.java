package net.cuodex.passxapi.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;
import org.hibernate.Hibernate;
import org.hibernate.annotations.NotFound;
import org.hibernate.annotations.NotFoundAction;

import javax.persistence.*;
import java.util.Objects;


@Entity
@RequiredArgsConstructor
@Table(name = "credentials")
public class LoginCredential {
    @Id
    @Column(name = "id", nullable = false)
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Getter @Setter
    private Long id;

    @Column(length = 60)
    @Getter @Setter
    private String title, url, username, email, password;

    @Column(length = 120)
    @Getter @Setter
    private String description;


    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @NotFound(action = NotFoundAction.IGNORE)
    @JoinColumn(name = "user_account_id", referencedColumnName = "id", nullable = false)
    @JsonIgnore
    @Getter @Setter
    private UserAccount userAccount;


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        LoginCredential that = (LoginCredential) o;
        return id.equals(that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "LoginCredential{" +
                "id=" + id +
                ", title='" + title + '\'' +
                ", url='" + url + '\'' +
                ", description='" + description + '\'' +
                ", username='" + username + '\'' +
                ", email='" + email + '\'' +
                ", password='" + password + '\'' +
                '}';
    }
}
