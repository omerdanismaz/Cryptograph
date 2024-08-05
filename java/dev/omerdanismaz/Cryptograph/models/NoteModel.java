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
@Table(name = "dbtNotes")
public class NoteModel
{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long dbfNoteId;

    @Column(nullable = false)
    private Long dbfNoteUserId;

    @Column(length = 200, nullable = false)
    private String dbfNoteName;

    @Column(length = 1500, nullable = false)
    private String dbfNoteContent;

    @Column(nullable = false)
    private Date dbfNoteCreatedOn;

    @Column(nullable = false)
    private Date dbfNoteUpdatedOn;
}
