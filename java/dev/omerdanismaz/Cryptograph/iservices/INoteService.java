package dev.omerdanismaz.Cryptograph.iservices;

import dev.omerdanismaz.Cryptograph.models.NoteModel;
import jakarta.servlet.http.HttpServletRequest;

import java.util.List;

public interface INoteService
{
    void createNote(NoteModel noteModel, HttpServletRequest httpServletRequest);
    NoteModel readNoteById(Long noteId, HttpServletRequest httpServletRequest);
    List<NoteModel> readAllNotesByUserId(Long userId, HttpServletRequest httpServletRequest);
    void updateNote(NoteModel noteModel, HttpServletRequest httpServletRequest);
    void deleteNote(NoteModel noteModel);
}
