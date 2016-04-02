package com.momana.bhromo.memoir.database;

import android.content.Context;
import android.content.SharedPreferences;

import com.google.gson.Gson;
import com.momana.bhromo.memoir.model.Note;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.UUID;

public class NotesDatabase {

    private static final String TABLE_NOTES = "notes_table";
    private static final String LIST_NOTES = "notes_list";
    private static final String PASSWORD = "password";

    private Gson gson;
    private Context context;

    private static NotesDatabase instance;
    public static NotesDatabase getInstance(Context context) {
        if (instance == null) {
            instance = new NotesDatabase(context);
        }
        return instance;
    }

    private NotesDatabase(Context context) {
        // Initialize context
        this.context = context;
        // Initialize shared preferences
        SharedPreferences preferences = context.getSharedPreferences(TABLE_NOTES, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        // Initialize Gson
        if (gson == null) {
            gson = new Gson();
        }
        // Initialize notes
        if (!preferences.contains(LIST_NOTES)) {
            // First boot, create dummy notes
            notes = getDummyNotes();
            String json = gson.toJson(new NotesWrapper(notes));
            editor.putString(LIST_NOTES, json);
            editor.apply();
        } else {
            // Load notes from storage
            String json = preferences.getString(LIST_NOTES, "");
            notes = gson.fromJson(json, NotesWrapper.class).getNotes();
        }
    }
    private ArrayList<Note> getDummyNotes() {
        notes = new ArrayList<>();
        notes.add(new Note(UUID.randomUUID().toString(), "Welcome to Memoir", "<div align=\"left\">" +
                "<font style=\"color:#4caf50\"><b><u>Memoir</u></b></font> is your personal digital diary application. " +
                "Make your unforgettable moments more special, capture your memories and write " +
                "your <font style=\"color:#f44336\"><i>life story</i></font> here...</div>"));
        notes.add(new Note(UUID.randomUUID().toString(), "Editing Features", "<b>Bold<br/>\n" +
                "</b><i>Italic<br/>\n" +
                "</i><u>Underline<br/>\n" +
                "</u><strike>Strike</strike><strike><br/>\n" +
                "</strike>Superscript: x<sup>2</sup> + y<sup>2</sup><br/>\n" +
                "Subscript: H<sub>2</sub>O<br/>\n" +
                "<font style=\"font-size:32px\">Font Size</font><font style=\"font-size:18px\"><br/>\n" +
                "</font><font style=\"font-size:18px\"><font style=\"color:#ff5722\">Text Color<br/>\n" +
                "</font></font><font style=\"font-size:18px\"><font style=\"background-color:#8bc34a\">Text Highlight<br/>\n" +
                "</font></font><font style=\"font-size:18px\"><br/>\n" +
                "</font><font style=\"font-size:18px\">Left Aligned<br/>\n" +
                "</font><div align=\"center\"><font style=\"font-size:18px\">Center Aligned<br/>\n" +
                "</font></div><div align=\"right\"><font style=\"font-size:18px\">Right Aligned<br/>\n" +
                "</font></div><div align=\"left\"><font style=\"font-size:18px\"><br/>\n" +
                "</font></div><div align=\"left\"><font style=\"font-size:18px\"><u>Numberer List</u><br/>\n" +
                "</font></div><ol><li><div align=\"left\"><font style=\"font-size:18px\">Item A<br/>\n" +
                "</font></div></li><li><div align=\"left\"><font style=\"font-size:18px\">Item B<br/>\n" +
                "</font></div></li></ol><div align=\"left\"><font style=\"font-size:18px\"><br/>\n" +
                "</font></div><div align=\"left\"><font style=\"font-size:18px\"><u>Bullet List</u><br/>\n" +
                "</font></div><ul><li><div align=\"left\"><font style=\"font-size:18px\">Item A<br/>\n" +
                "</font></div></li><li><div align=\"left\"><font style=\"font-size:18px\">Item B</font></div></li></ul>"));
        notes.add(new Note(UUID.randomUUID().toString(), "Credits", "<div align=\"center\"><font style=\"font-size:32px\">" +
                "<font style=\"color:#4caf50\"><b>Memoir</b></font></font></div><div align=\"center\"><font style=\"color:#9e9e9e\">" +
                "<b>v1.0</b></font></div><br/><br/>\n<div align=\"center\"><font style=\"font-size:24px\"><font style=\"color:#4caf50\">" +
                "<b>Made By: Group 34</b></font></font></div><div align=\"center\">Ronak Manglani [83]</div><div align=\"center\">" +
                "Bhagwan Motiyani [89]</div><div align=\"center\">Mohit Nagpal [95]</div><br/>\n<div align=\"center\">" +
                "<font style=\"font-size:24px\"><font style=\"color:#4caf50\"><b>Project Guide</b></font></font></div>" +
                "<div align=\"center\">Mrs. Anagha Durugkar</div>"));
        return notes;
    }

    public boolean isPasswordEnabled() {
        SharedPreferences preferences = context.getSharedPreferences(TABLE_NOTES, Context.MODE_PRIVATE);
        return preferences.contains(PASSWORD);
    }
    public boolean isPasswordCorrect(String password) {
        SharedPreferences preferences = context.getSharedPreferences(TABLE_NOTES, Context.MODE_PRIVATE);
        return preferences.getString(PASSWORD, "").equals(password);
    }

    private ArrayList<Note> notes;
    public ArrayList<Note> getNotes() {
        return notes;
    }
    public ArrayList<Note> getNotesOfDate(Date date) {
        SimpleDateFormat fmt = new SimpleDateFormat("yyyyMMdd");
        ArrayList<Note> dateNotes = new ArrayList<>();
        for (int i = 0; i < notes.size(); i++) {
            boolean isSameDay = fmt.format(date).equals(fmt.format(notes.get(i).calendar.getTime()));
            if (isSameDay) {
                dateNotes.add(notes.get(i));
            }
        }
        return dateNotes;
    }
    public Note getNoteByID(String noteId) {
        for (int i = 0; i < notes.size(); i++) {
            if (notes.get(i).id.equals(noteId)) {
                return notes.get(i);
            }
        }
        return null;
    }
    public void addNote(Note note) {
        notes.add(0, note);
        commit();
    }
    public void deleteNoteAt(int position) {
        notes.remove(position);
        commit();
    }
    public void deleteNoteWithId(String noteId) {
        for (int i = 0; i < notes.size(); i++) {
            if (notes.get(i).id.equals(noteId)) {
                notes.remove(i);
                break;
            }
        }
        commit();
    }
    public void commit() {
        String json = gson.toJson(new NotesWrapper(notes));
        SharedPreferences.Editor editor = context.getSharedPreferences(TABLE_NOTES, Context.MODE_PRIVATE).edit();
        editor.putString(LIST_NOTES, json);
        editor.commit();
    }
}
