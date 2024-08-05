package dev.omerdanismaz.Cryptograph.models;

import dev.omerdanismaz.Cryptograph.enums.EUserStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "dbtUsers")
public class UserModel
{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long dbfUserId;

    @Column(length = 32, nullable = false)
    private String dbfUserFirstName;

    @Column(length = 32, nullable = false)
    private String dbfUserLastName;

    @Column(length = 64, nullable = false, unique = true)
    private String dbfUserEmail;

    @Column(length = 150, nullable = false)
    private String dbfUserPassword;

    @Column(length = 200, nullable = false)
    private String dbfUserEncryptionKey;

    @Column(nullable = false)
    private Date dbfUserCreatedOn;

    @Column(nullable = false)
    private Date dbfUserLoggedOn;

    @Column(nullable = false)
    private EUserStatus dbfUserStatus;
}
