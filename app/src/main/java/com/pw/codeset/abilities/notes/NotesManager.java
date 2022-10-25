package com.pw.codeset.abilities.notes;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.pw.codeset.databean.NotesBean;
import com.pw.codeset.utils.SaveFileUtils;
import com.xd.baseutils.utils.NStringUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class NotesManager {

    public static NotesManager mInstance;
    private List<NotesBean> mNotesList;

    public static NotesManager getInstance() {
        if (mInstance == null) {
            synchronized (NotesManager.class) {
                if (mInstance == null) {
                    mInstance = new NotesManager();
                }
            }
        }
        return mInstance;
    }

    NotesManager() {
        init();
    }

    public void init() {
        readNotesListFromFile();
    }

    public void updateNotes(NotesBean notesBean) {
        if (mNotesList == null) {
            mNotesList = new ArrayList<>();
        }
        if (mNotesList.contains(notesBean)) {
            int curNoteIndex = mNotesList.indexOf(notesBean);
            mNotesList.set(curNoteIndex, notesBean);
            saveNotesListToFile();
        }else {
            addNotes(notesBean);
        }
    }

    public void addNotes(NotesBean notesBean) {
        if (mNotesList == null) {
            mNotesList = new ArrayList<>();
        }
        mNotesList.add(notesBean);
        saveNotesListToFile();
    }

    public void deleteNotes(NotesBean notesBean) {
        if (mNotesList == null) {
            mNotesList = new ArrayList<>();
        }
        if (mNotesList.contains(notesBean)) {
            mNotesList.remove(notesBean);
            saveNotesListToFile();
        }
    }

    public List<NotesBean> getNotesList() {
        return mNotesList;
    }

    public NotesBean getNote(String noteId) {
        if (mNotesList == null) {
            mNotesList = new ArrayList<>();
        }
        if (mNotesList.isEmpty()) {
            return null;
        }
        for (int i = 0; i < mNotesList.size(); i++) {
            NotesBean notesBean = mNotesList.get(i);
            if (notesBean != null) {
                if (notesBean.getId().equals(noteId)) {
                    return notesBean;
                }
            }
        }
        return null;
    }

    private void readNotesListFromFile() {
        String noteListStr = SaveFileUtils.getNotesListStr();
        if (NStringUtils.isNotBlank(noteListStr)) {
            mNotesList = new Gson().fromJson(noteListStr,new TypeToken<List<NotesBean>>(){}.getType());
        }else {
            mNotesList = new ArrayList<>();
        }
    }

    private void saveNotesListToFile() {
        String notesStr = "";
        if (mNotesList != null && mNotesList.size() > 0) {
            Collections.sort(mNotesList, new Comparator<NotesBean>() {
                @Override
                public int compare(NotesBean o1, NotesBean o2) {
                    if (o1 == null) {
                        return 1;
                    }
                    return o1.compareTo(o2);
                }
            });
            notesStr = new Gson().toJson(mNotesList);
        }
        SaveFileUtils.saveNotesList(notesStr);
    }
}
