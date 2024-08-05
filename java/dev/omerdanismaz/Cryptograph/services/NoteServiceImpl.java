package dev.omerdanismaz.Cryptograph.services;

import dev.omerdanismaz.Cryptograph.enums.ESessionAttributes;
import dev.omerdanismaz.Cryptograph.iservices.INoteService;
import dev.omerdanismaz.Cryptograph.models.NoteModel;
import dev.omerdanismaz.Cryptograph.repositories.NoteRepository;
import jakarta.servlet.http.HttpServletRequest;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static dev.omerdanismaz.Cryptograph.cryptography.CryptographyOperations.decryptData;
import static dev.omerdanismaz.Cryptograph.cryptography.CryptographyOperations.encryptData;
import static dev.omerdanismaz.Cryptograph.functions.SessionOperations.getSessionAttribute;

@Service
@AllArgsConstructor
public class NoteServiceImpl implements INoteService
{
    private final NoteRepository noteRepository;

    @Override
    public void createNote(NoteModel noteModel, HttpServletRequest httpServletRequest)
    {
        Long userId = Long.parseLong(getSessionAttribute(httpServletRequest,
                ESessionAttributes.USER_ID));
        String userEncryptionKey = getSessionAttribute(httpServletRequest,
                ESessionAttributes.USER_ENCRYPTION_KEY);

        String plainNoteName = noteModel.getDbfNoteName();
        String plainNoteContent = noteModel.getDbfNoteContent();

        String encryptedNoteName = encryptData(plainNoteName, userEncryptionKey);
        String encryptedNoteContent = encryptData(plainNoteContent, userEncryptionKey);

        noteModel.setDbfNoteUserId(userId);
        noteModel.setDbfNoteName(encryptedNoteName);
        noteModel.setDbfNoteContent(encryptedNoteContent);
        noteModel.setDbfNoteCreatedOn(new Date());
        noteModel.setDbfNoteUpdatedOn(new Date());

        noteRepository.save(noteModel);
    }

    @Override
    public NoteModel readNoteById(Long noteId, HttpServletRequest httpServletRequest)
    {
        Optional<NoteModel> searchedNote = noteRepository.findById(noteId);

        String userEncryptionKey = getSessionAttribute(httpServletRequest,
                ESessionAttributes.USER_ENCRYPTION_KEY);

        if(searchedNote.isPresent())
        {
            NoteModel noteModel = searchedNote.get();

            String decryptedNoteName = decryptData(noteModel.getDbfNoteName(), userEncryptionKey);
            String decryptedNoteContent = decryptData(noteModel.getDbfNoteContent(), userEncryptionKey);

            noteModel.setDbfNoteName(decryptedNoteName);
            noteModel.setDbfNoteContent(decryptedNoteContent);

            return noteModel;
        }

        return NoteModel
                .builder()
                .dbfNoteId(0L)
                .dbfNoteUserId(0L)
                .dbfNoteName("EMPTY")
                .dbfNoteContent("EMPTY")
                .dbfNoteCreatedOn(new Date())
                .dbfNoteUpdatedOn(new Date())
                .build();
    }

    @Override
    public List<NoteModel> readAllNotesByUserId(Long userId, HttpServletRequest httpServletRequest)
    {
        List<NoteModel> allNotes = noteRepository.findByDbfNoteUserId(userId);

        String userEncryptionKey = getSessionAttribute(httpServletRequest,
                ESessionAttributes.USER_ENCRYPTION_KEY);

        return allNotes
                .stream()
                .peek(note -> {
                    note.setDbfNoteName(decryptData(note.getDbfNoteName(), userEncryptionKey));
                    note.setDbfNoteContent(decryptData(note.getDbfNoteContent(), userEncryptionKey));
                })
                .sorted(Comparator.comparing(NoteModel::getDbfNoteName))
                .collect(Collectors.toList());
    }

    @Override
    public void updateNote(NoteModel noteModel, HttpServletRequest httpServletRequest)
    {
        Optional<NoteModel> searchedNote = noteRepository.findById(noteModel.getDbfNoteId());

        if(searchedNote.isPresent())
        {
            NoteModel existingNote = searchedNote.get();
            String userEncryptionKey = getSessionAttribute(httpServletRequest,
                    ESessionAttributes.USER_ENCRYPTION_KEY);

            if(noteModel.getDbfNoteName() != null)
            {
                String encryptedNoteName = encryptData(noteModel.getDbfNoteName(), userEncryptionKey);
                existingNote.setDbfNoteName(encryptedNoteName);
            }

            if(noteModel.getDbfNoteContent() != null)
            {
                String encryptedNoteContent = encryptData(noteModel.getDbfNoteContent(), userEncryptionKey);
                existingNote.setDbfNoteContent(encryptedNoteContent);
            }

            existingNote.setDbfNoteUpdatedOn(new Date());

            noteRepository.save(existingNote);
        }
    }

    @Override
    public void deleteNote(NoteModel noteModel)
    {
        Optional<NoteModel> searchedNote = noteRepository.findById(noteModel.getDbfNoteId());
        searchedNote.ifPresent(noteRepository::delete);
    }
}
