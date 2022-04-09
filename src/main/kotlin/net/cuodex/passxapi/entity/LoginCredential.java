package net.cuodex.passxapi.entity;

import lombok.Data;

import javax.persistence.*;

@Data
@Entity
@Table(name = "credentials")
public class LoginCredential {
    @Id
    @Column(name = "id", nullable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(length = 60)
    private String title, username, email, password, url, description;

    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "user_account_id", referencedColumnName = "id")
    private UserAccount userAccount;
}
