package com.example.kienpt.note.utils;

import com.example.kienpt.note.models.Note;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class ListUtil {
    // Order by time created
    public static List<Note> orderByCreatedTime(List<Note> listNote) {
        String DATE_FORMAT = "dd/MM/yyyy HH:mm:ss";
        SimpleDateFormat formatter = new SimpleDateFormat(DATE_FORMAT);
        for (int i = 0; i < listNote.size() - 1; i++) {
            for (int j = i + 1; j < listNote.size(); j++) {
                try {
                    Date createdTimeOfNoteA = formatter.parse(listNote.get(i).getCreatedTime());
                    Date createdTimeOfNoteB = formatter.parse(listNote.get(j).getCreatedTime());
                    if (createdTimeOfNoteA.before(createdTimeOfNoteB)) {
                        Note mediate = listNote.get(j);
                        listNote.set(j, listNote.get(i));
                        listNote.set(i, mediate);
                    }
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }
        }
        return listNote;
    }
}
