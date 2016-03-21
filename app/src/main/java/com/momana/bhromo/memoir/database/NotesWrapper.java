package com.momana.bhromo.memoir.database;

import com.momana.bhromo.memoir.model.Note;

import java.util.ArrayList;

public class NotesWrapper {

    private ArrayList<Note> notes;

    public NotesWrapper(ArrayList<Note> notes) {
        this.notes = notes;
    }

    public ArrayList<Note> getNotes() {
        return notes;
    }
}
