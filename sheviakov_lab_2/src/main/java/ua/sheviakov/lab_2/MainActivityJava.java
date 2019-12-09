package ua.sheviakov.lab_2;

import android.app.Activity;
import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import ua.sheviakov.lab_2.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.regex.Pattern;

public class MainActivityJava extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = MainActivityJava.class.getSimpleName();
    private static final int NEW_NOTE_REQUEST_CODE = 787;
    private static final int EDIT_NOTE_REQUEST_CODE = 786;

    private NotesAdapter adapter;
    private FloatingActionButton add;
    private RecyclerView recycler;
    private List<Note> notes = new ArrayList<>();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        add = findViewById(R.id.action_add);
        add.setOnClickListener(this);

        adapter = new NotesAdapter(
                notes, this::onNoteClicked,
                this::onNoteLongClicked);

        recycler = findViewById(R.id.notes_recycler);
        recycler.setAdapter(adapter);
        recycler.setLayoutManager(new LinearLayoutManager(
                this,
                LinearLayoutManager.VERTICAL,
                false));

        loadDataFromDB();
        handleIntent(getIntent());
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu, menu);

        SearchManager searchManager =
                (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        SearchView searchView =
                (SearchView) menu.findItem(R.id.search).getActionView();
        searchView.setSearchableInfo(
                searchManager.getSearchableInfo(getComponentName()));
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                if (TextUtils.isEmpty(newText)) {
                    searchNote(null);
                }
                return true;
            }
        });
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.filter:
                DialogUtil.showFilterDialog(this, this::filterImportance, this::filterDate);
        }
        return false;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if ((requestCode == NEW_NOTE_REQUEST_CODE || requestCode == EDIT_NOTE_REQUEST_CODE) && resultCode == Activity.RESULT_OK) {
            loadDataFromDB();
        }
    }

    private void loadDataFromDB() {
        List<Note> content = DBUtils.readNotes(this);
        Log.d(TAG, "loadDataFromDB: data " + content);
        if (content != null) {
            this.notes = content;
            adapter.swap(notes);
        } else {
            Toast.makeText(this, R.string.notes_are_empty, Toast.LENGTH_SHORT).show();
        }
    }

    private void onNoteLongClicked(Note note) {
        DialogUtil.showEditDeleteDialog(this, new EditDeleteListener() {
            @Override
            public void deleteClicked() {
                int index = notes.indexOf(note);
                if (index != -1) {
                    notes.remove(index);
                    DBUtils.deleteNote(MainActivityJava.this, note);
                    adapter.removeItem(index);
                } else {
                    Log.e(TAG, "deleteClicked: remove failed no such element");
                }
            }

        });
    }

    public void onNoteClicked(Note note) {
        startActivityForResult(
                EditNoteActivity.makeIntent(
                        MainActivityJava.this,
                        note.guid),
                EDIT_NOTE_REQUEST_CODE);
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        handleIntent(intent);
    }

    private void handleIntent(Intent intent) {
        if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
            String query = intent.getStringExtra(SearchManager.QUERY);
            searchNote(query);
        }
    }

    private void filterImportance(ImportanceFilter filter) {

        if (filter == ImportanceFilter.ALL) {
            adapter.swap(notes);
            return;
        }

        List<Note> filteredNotes = new LinkedList<>();

        for (Note note : notes) {
            switch (filter) {
                case LOW:
                    if (note.importance == Importance.LOW) {
                        filteredNotes.add(note);
                    }
                    break;
                case MEDIUM:
                    if (note.importance == Importance.MEDIUM) {
                        filteredNotes.add(note);
                    }
                    break;
                case HIGH:
                    if (note.importance == Importance.HIGH) {
                        filteredNotes.add(note);
                    }
                    break;
            }
        }

        adapter.swap(filteredNotes);
    }

    private void filterDate(Date date) {

        List<Note> filteredNotes = new LinkedList<>();

        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);

        for (Note note : notes) {
            Calendar noteCalendar = Calendar.getInstance();
            noteCalendar.setTime(note.end);

            boolean isCorrectDay = calendar.get(Calendar.DAY_OF_MONTH) == noteCalendar.get(Calendar.DAY_OF_MONTH);
            boolean isCorrectMonth = calendar.get(Calendar.MONTH) == noteCalendar.get(Calendar.MONTH);
            boolean isCorrectYear = calendar.get(Calendar.YEAR) == noteCalendar.get(Calendar.YEAR);

            if (isCorrectDay && isCorrectMonth && isCorrectYear) {
                filteredNotes.add(note);
            }

        }

        adapter.swap(filteredNotes);
    }

    private void searchNote(String query) {
        if (query == null || query.isEmpty()) {
            adapter.swap(notes);
            return;
        }

        Pattern pattern = Pattern.compile(query, Pattern.CASE_INSENSITIVE);
        List<Note> notes = new ArrayList<>();

        for (int i = 0; i < this.notes.size(); i++) {
            if (pattern.matcher(this.notes.get(i).description).find()) {
                notes.add(this.notes.get(i));
            }
        }

        adapter.swap(notes);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.action_add:
                startActivityForResult(AddNoteActivity.makeIntent(this), NEW_NOTE_REQUEST_CODE);
                break;
        }

    }
}
