package dev.omerdanismaz.Cryptograph.controllers;

import dev.omerdanismaz.Cryptograph.enums.ESessionAttributes;
import dev.omerdanismaz.Cryptograph.iservices.INoteService;
import dev.omerdanismaz.Cryptograph.iservices.ISessionService;
import dev.omerdanismaz.Cryptograph.iservices.IUserService;
import dev.omerdanismaz.Cryptograph.models.NoteModel;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Date;
import java.util.List;

import static dev.omerdanismaz.Cryptograph.functions.SessionOperations.*;
import static dev.omerdanismaz.Cryptograph.functions.SessionOperations.setDecryptedNotes;

@Controller
@AllArgsConstructor
public class NoteController
{
    private final IUserService userService;
    private final ISessionService sessionService;
    private final INoteService noteService;

    @GetMapping("/notes")
    public String notesGET(Model model,
                           HttpServletRequest httpServletRequest,
                           HttpServletResponse httpServletResponse)
    {
        initializeSession(httpServletRequest, httpServletResponse, userService, sessionService);

        if(isUserGuest(httpServletRequest))
        {
            return "redirect:/login";
        }

        List<NoteModel> decryptedNotes = getDecryptedNotes(httpServletRequest, noteService);

        model.addAttribute("CSRFToken",
                getSessionAttribute(httpServletRequest, ESessionAttributes.CSRF_TOKEN));
        model.addAttribute("decryptedNotes", decryptedNotes);

        return "/app/note/notes";
    }

    @GetMapping("/addNote")
    public String addNoteGET(Model model,
                             HttpServletRequest httpServletRequest,
                             HttpServletResponse httpServletResponse)
    {
        initializeSession(httpServletRequest, httpServletResponse, userService, sessionService);

        if(isUserGuest(httpServletRequest))
        {
            return "redirect:/login";
        }

        model.addAttribute("CSRFToken",
                getSessionAttribute(httpServletRequest, ESessionAttributes.CSRF_TOKEN));
        model.addAttribute("messageOne",
                "Note name is empty or too long. (Maximum Acceptable Length: 64)");
        model.addAttribute("messageTwo",
                "Note content is empty or too long. (Maximum Acceptable Length: 1024)");

        return "/app/note/addNote";
    }

    @PostMapping("/addNote")
    public String addNotePOST(@RequestParam("CSRFToken") String CSRFToken,
                              @RequestParam("noteName") String noteName,
                              @RequestParam("noteContent") String noteContent,
                              HttpServletRequest httpServletRequest)
    {
        if(isUserGuest(httpServletRequest))
        {
            return "redirect:/";
        }

        if(!isCSRFTokenValid(httpServletRequest, CSRFToken))
        {
            return "redirect:/";
        }

        if(noteName.isEmpty() || noteName.length() > 64)
        {
            return "redirect:/";
        }

        if(noteContent.isEmpty() || noteContent.length() > 1024)
        {
            return "redirect:/";
        }

        NoteModel noteModel = NoteModel
                .builder()
                .dbfNoteName(noteName)
                .dbfNoteContent(noteContent)
                .build();

        noteService.createNote(noteModel, httpServletRequest);

        Long userId = Long.parseLong(getSessionAttribute(httpServletRequest, ESessionAttributes.USER_ID));

        List<NoteModel> decryptedNotes = noteService.readAllNotesByUserId(userId, httpServletRequest);
        setDecryptedNotes(httpServletRequest, decryptedNotes);

        return "redirect:/notes";
    }

    @PostMapping("/viewNote")
    public String viewNotePOST(@RequestParam("CSRFToken") String CSRFToken,
                               @RequestParam("noteId") String noteId,
                               Model model,
                               HttpServletResponse httpServletResponse,
                               HttpServletRequest httpServletRequest)
    {
        initializeSession(httpServletRequest, httpServletResponse, userService, sessionService);

        if(isUserGuest(httpServletRequest))
        {
            return "redirect:/";
        }

        if(!isCSRFTokenValid(httpServletRequest, CSRFToken))
        {
            return "redirect:/";
        }

        if(noteId.isEmpty())
        {
            return "redirect:/";
        }

        NoteModel searchedNote = noteService.readNoteById(Long.parseLong(noteId), httpServletRequest);

        Long userId = Long.parseLong(getSessionAttribute(httpServletRequest, ESessionAttributes.USER_ID));

        if(searchedNote.getDbfNoteUserId().equals(userId))
        {
            model.addAttribute("noteName", searchedNote.getDbfNoteName());
            model.addAttribute("noteContent", searchedNote.getDbfNoteContent());
            model.addAttribute("noteCreatedOn", searchedNote.getDbfNoteCreatedOn());
            model.addAttribute("noteUpdatedOn", searchedNote.getDbfNoteUpdatedOn());
            model.addAttribute("CSRFToken", CSRFToken);
            model.addAttribute("noteId", noteId);
        }

        return "/app/note/viewNote";
    }

    @PostMapping("/updateNote")
    public String updateNotePOST(@RequestParam("CSRFToken") String CSRFToken,
                                 @RequestParam("noteId") String noteId,
                                 Model model,
                                 HttpServletRequest httpServletRequest)
    {
        if(isUserGuest(httpServletRequest))
        {
            return "redirect:/";
        }

        if(!isCSRFTokenValid(httpServletRequest, CSRFToken))
        {
            return "redirect:/";
        }

        if(noteId.isEmpty())
        {
            return "redirect:/";
        }

        NoteModel searchedNote = noteService.readNoteById(Long.parseLong(noteId), httpServletRequest);

        Long userId = Long.parseLong(getSessionAttribute(httpServletRequest, ESessionAttributes.USER_ID));

        if(searchedNote.getDbfNoteUserId().equals(userId))
        {
            model.addAttribute("CSRFToken", CSRFToken);
            model.addAttribute("noteId", noteId);
            model.addAttribute("noteName", searchedNote.getDbfNoteName());
            model.addAttribute("noteContent", searchedNote.getDbfNoteContent());
            model.addAttribute("messageOne",
                    "Note name is empty or too long. (Maximum Acceptable Length: 64)");
            model.addAttribute("messageTwo",
                    "Note content is empty or too long. (Maximum Acceptable Length: 1024)");
        }

        return "/app/note/updateNote";
    }

    @PostMapping("/updateNoteProcess")
    public String updateNoteProcessPOST(@RequestParam("CSRFToken") String CSRFToken,
                                        @RequestParam("noteId") String noteId,
                                        @RequestParam("newNoteName") String newNoteName,
                                        @RequestParam("newNoteContent") String newNoteContent,
                                        HttpServletRequest httpServletRequest)
    {
        if(isUserGuest(httpServletRequest))
        {
            return "redirect:/";
        }

        if(!isCSRFTokenValid(httpServletRequest, CSRFToken))
        {
            return "redirect:/";
        }

        if(noteId.isEmpty()
                || newNoteName.isEmpty() || newNoteContent.isEmpty())
        {
            return "redirect:/";
        }

        NoteModel searchedNote = noteService.readNoteById(Long.parseLong(noteId), httpServletRequest);

        Long userId = Long.parseLong(getSessionAttribute(httpServletRequest, ESessionAttributes.USER_ID));

        if(searchedNote.getDbfNoteUserId().equals(userId))
        {
            searchedNote.setDbfNoteName(newNoteName);
            searchedNote.setDbfNoteContent(newNoteContent);
            searchedNote.setDbfNoteUpdatedOn(new Date());
            noteService.updateNote(searchedNote, httpServletRequest);

            List<NoteModel> decryptedNotes = noteService.readAllNotesByUserId(userId, httpServletRequest);

            setDecryptedNotes(httpServletRequest, decryptedNotes);
        }

        return "redirect:/notes";
    }

    @PostMapping("/deleteNote")
    public String deleteNotePOST(@RequestParam("CSRFToken") String CSRFToken,
                                 @RequestParam("noteId") String noteId,
                                 HttpServletRequest httpServletRequest)
    {
        if(isUserGuest(httpServletRequest))
        {
            return "redirect:/";
        }

        if(!isCSRFTokenValid(httpServletRequest, CSRFToken))
        {
            return "redirect:/";
        }

        if(noteId.isEmpty())
        {
            return "redirect:/";
        }

        NoteModel searchedNote = noteService.readNoteById(Long.parseLong(noteId), httpServletRequest);

        Long userId = Long.parseLong(getSessionAttribute(httpServletRequest, ESessionAttributes.USER_ID));

        if(searchedNote.getDbfNoteUserId().equals(userId))
        {
            noteService.deleteNote(searchedNote);

            List<NoteModel> decryptedNotes = noteService.readAllNotesByUserId(userId, httpServletRequest);

            setDecryptedNotes(httpServletRequest, decryptedNotes);
        }

        return "redirect:/notes";
    }
}
