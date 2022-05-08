package net.cuodex.passxapi.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;


@Entity
@Table(name = "credentials")
public class LoginCredential {
    @Id
    @Column(name = "id", nullable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Getter
    private Long id;

    @Column(length = 60)
    @Getter @Setter
    private String title, url, description, username, email, password;

    @ManyToOne(cascade = CascadeType.PERSIST)
    @JoinColumn(name = "user_account_id", referencedColumnName = "id")
    @JsonIgnore
    @Getter @Setter
    private UserAccount userAccount;

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
                ", userAccount=" + userAccount +
                '}';
    }
}
