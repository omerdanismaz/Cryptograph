package dev.omerdanismaz.Cryptograph.models;

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
@Table(name = "dbtAccounts")
public class AccountModel
{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long dbfAccountId;

    @Column(nullable = false)
    private Long dbfAccountUserId;

    @Column(length = 200, nullable = false)
    private String dbfAccountName;

    @Column(length = 200, nullable = false)
    private String dbfAccountUsername;

    @Column(length = 200, nullable = false)
    private String dbfAccountPassword;

    @Column(nullable = false)
    private Date dbfAccountCreatedOn;

    @Column(nullable = false)
    private Date dbfAccountUpdatedOn;
}
