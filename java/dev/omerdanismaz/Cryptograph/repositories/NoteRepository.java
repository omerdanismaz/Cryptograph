package dev.omerdanismaz.Cryptograph.repositories;

import dev.omerdanismaz.Cryptograph.models.NoteModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface NoteRepository extends JpaRepository<NoteModel, Long>
{
    List<NoteModel> findByDbfNoteUserId(Long noteUserId);
}
