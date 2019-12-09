package com.example.andrlab2_kovalenko;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;

public class DBUtils {

    private static final String TAG = DBUtils.class.getSimpleName();

    private static List<Note> getNotesFormDB(Context context) {

        DBHelper dbHelper = new DBHelper(context);
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        Cursor c = db.query("notes", null, null, null, null, null, null);

        List<Note> notes = new LinkedList<>();

        if (c.moveToFirst()) {

            // определяем номера столбцов по имени в выборке
            int idIndex = c.getColumnIndex("id");
            int nameIndex = c.getColumnIndex("name");
            int descriptionIndex = c.getColumnIndex("description");
            int dateIndex = c.getColumnIndex("date");
            int imageIndex = c.getColumnIndex("image");
            int importanceIndex = c.getColumnIndex("importance");

            do {

                int id = c.getInt(idIndex);
                String name = c.getString(nameIndex);
                String description = c.getString(descriptionIndex);
                long date = c.getLong(dateIndex);
                String image = c.getString(imageIndex);
                String importance = c.getString(importanceIndex);


                Note note = new Note(
                        name,
                        description,
                        Importance.valueOf(importance),
                        new Date(date),
                        image
                );

                note.guid = Integer.toString(id);

                Log.i(TAG, "Read from database: " + note.toString());

                notes.add(note);

            } while (c.moveToNext());
        } else {
            Log.d(TAG, "0 rows");
        }

        c.close();

        return notes;
    }

    static List<Note> readNotes(Context context) {
        return getNotesFormDB(context);
    }

    static void appendNotes(Context context, List<Note> notes) {
        for (Note note : notes) {
            appendNote(context, note);
        }
    }

    static void writeNotes(Context context, List<Note> notes) {
        for (Note note : notes) {
            appendNote(context, note);
        }
    }

    static void appendNote(Context context, Note note) {

        DBHelper dbHelper = new DBHelper(context);
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        ContentValues cv = new ContentValues();

        cv.put("name", note.name);
        cv.put("description", note.description);
        cv.put("date", note.end.getTime());
        cv.put("image", note.image);
        cv.put("importance", note.importance.toString());

        long rowId = db.insert("notes", null, cv);
        Log.d(TAG, "Row inserted, ID = " + rowId);

    }

    static Note findNote(Context context, String id) {
        List<Note> notes = readNotes(context);
        Objects.requireNonNull(notes);

        int index = -1;

        for (int i = 0; i < notes.size(); i++) {
            if (notes.get(i).guid.equals(id)) {
                index = i;
                break;
            }
        }

        if (index != -1) {
            return notes.get(index);
        } else {
            Log.e(TAG, "findNote: error, note not found");
            return null;
        }
    }

    static void updateNote(Context context, Note note) {
        DBHelper dbHelper = new DBHelper(context);
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        ContentValues cv = new ContentValues();

        cv.put("name", note.name);
        cv.put("description", note.description);
        cv.put("date", note.end.getTime());
        cv.put("image", note.image);
        cv.put("importance", note.importance.toString());

        long rowId = db.update("notes", cv, "id = ?", new String[] { note.guid });
        Log.d(TAG, "Row updated, ID = " + rowId);
    }

    static void deleteNote(Context context, Note note) {
        DBHelper dbHelper = new DBHelper(context);
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        long rowId = db.delete("notes", "id = ?", new String[] { note.guid });
        Log.d(TAG, "Row deleted, ID = " + rowId);
    }

}
