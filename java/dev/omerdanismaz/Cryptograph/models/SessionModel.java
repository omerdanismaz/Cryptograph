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
@Table(name = "dbtSessions")
public class SessionModel
{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long dbfSessionId;

    @Column(nullable = false)
    private Long dbfSessionUserId;

    @Column(length = 64, nullable = false)
    private String dbfSessionToken;

    @Column(nullable = false)
    private Date dbfSessionExpiration;

    @Column(nullable = false)
    private Date dbfSessionCreatedOn;
}
