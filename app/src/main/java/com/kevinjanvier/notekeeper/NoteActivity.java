package com.kevinjanvier.notekeeper;

import android.content.ContentUris;
import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;

import java.util.List;

public class NoteActivity extends AppCompatActivity {

    public static final String NOTE_POSITION = "com.kevinjanvier.notekeeper.NOTE_POSITION";
    public static final String ORIGINAL_NOTE_COURSE_ID = "com.kevinjanvier.notekeeper.COURSE_ID";
    public static final String ORIGINAL_NOTE_TITLE = "com.kevinjanvier.notekeeper.NOTE_TITLE";
    public static final String ORIGINAL_NOTE_TEXT = "com.kevinjanvier.notekeeper.NOTE_TEXT";
    public static final int POSTION_NOT_SET = -1;
    private NoteInfo mNote;
    private boolean isNewNote;
    private Spinner mSpinnerCourses;
    private EditText mTextNoteTitle;
    private EditText mTextNoteText;
    private int mNotePosition;
    private boolean isCanceling;
    private String mOriginalNoteCourseId;
    private String mOriginNoteTitle;
    private String mOriginNoteText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_note);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mSpinnerCourses = findViewById(R.id.spinner_courses);

        List<CourseInfo> courses = DataManager.getInstance().getCourses();
        ArrayAdapter<CourseInfo> adapterCourses =
                new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, courses);
        adapterCourses.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mSpinnerCourses.setAdapter(adapterCourses);

        readDisplayValues();
        if (savedInstanceState == null) {
            savenewOriginNoeValues();

        } else {
            restoreOriginalNoteValues(savedInstanceState);
        }


        mTextNoteTitle = findViewById(R.id.text_note_text);
        mTextNoteText = findViewById(R.id.text_note_title);

        if (!isNewNote) {
            displayNote(mSpinnerCourses, mTextNoteTitle, mTextNoteText);
        }


    }

    private void restoreOriginalNoteValues(Bundle savedInstanceState) {
        mOriginalNoteCourseId = savedInstanceState.getString(ORIGINAL_NOTE_COURSE_ID);
        mOriginNoteText = savedInstanceState.getString(ORIGINAL_NOTE_TEXT);
        mOriginNoteTitle = savedInstanceState.getString(ORIGINAL_NOTE_TITLE);
    }


    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString(ORIGINAL_NOTE_COURSE_ID, mOriginalNoteCourseId);
        outState.putString(ORIGINAL_NOTE_TITLE, mOriginNoteTitle);
        outState.putString(ORIGINAL_NOTE_TEXT, mOriginNoteText);
    }

    private void savenewOriginNoeValues() {
        if (isNewNote)
            return;

        mOriginalNoteCourseId = mNote.getCourse().getCourseId();
        mOriginNoteTitle = mNote.getTitle();
        mOriginNoteText = mNote.getText();


    }


    @Override
    protected void onPause() {
        super.onPause();

        if (isCanceling) {
            if (isNewNote) {
                DataManager.getInstance().removeNote(mNotePosition);
            } else {
                storePreviousNoteValues();
            }
        } else {
            saveNote();
        }
    }

    private void storePreviousNoteValues() {
        CourseInfo course = DataManager.getInstance().getCourse(mOriginalNoteCourseId);
        mNote.setCourse(course);
        mNote.setTitle(mOriginNoteTitle);
        mNote.setText(mOriginNoteText);
    }

    /**
     * Save notes
     */
    private void saveNote() {

        mNote.setCourse((CourseInfo) mSpinnerCourses.getSelectedItem());
        mNote.setTitle(mTextNoteTitle.getText().toString());
        mNote.setText(mTextNoteText.getText().toString());
    }

    private void displayNote(Spinner spinnerCourses, EditText textNoteTitle, EditText textNoteText) {
        List<CourseInfo> courseInfos = DataManager.getInstance().getCourses();
        int courseIndex = courseInfos.indexOf(mNote.getCourse());
        spinnerCourses.setSelection(courseIndex);


        textNoteTitle.setText(mNote.getTitle());
        textNoteText.setText(mNote.getText());


    }

    private void readDisplayValues() {
        Intent intent = getIntent();
        int position = intent.getIntExtra(NOTE_POSITION, POSTION_NOT_SET);

//        isNewNote = mNote == null;
        isNewNote = position == POSTION_NOT_SET;
        if (isNewNote) {
            createNewNote();
        } else {
            mNote = DataManager.getInstance().getNotes().get(position);
        }
    }

    /**
     * Create new Notes
     */
    private void createNewNote() {
        DataManager dm = DataManager.getInstance();
        mNotePosition = dm.createNewNote();
        mNote = dm.getNotes().get(mNotePosition);


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_note, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_sent_mail) {

            sendEmail();
            return true;
        } else if (id == R.id.cancel) {
            isCanceling = true;
            finish();
        } else if (id == R.id.action_next) {
            moveNext();
        }else if (id== R.id.set_remider){
            showReminderNotification();
        }

        return super.onOptionsItemSelected(item);
    }

    private void showReminderNotification() {
        String noteText =mTextNoteText.getText().toString();
        String noteTitle =mTextNoteTitle.getText().toString();
//        ContentUris.parseId(mNote)
        NoteReminderNotification.notify(this, noteText, noteTitle, 0);
    }


    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        MenuItem item = menu.findItem(R.id.action_next);
        int lastNoteIndex = DataManager.getInstance().getNotes().size() -1;
        item.setEnabled(mNotePosition < lastNoteIndex);
        return super.onPrepareOptionsMenu(menu);
    }

    private void moveNext() {
        ++mNotePosition;
        mNote = DataManager.getInstance().getNotes().get(mNotePosition);

        savenewOriginNoeValues();
        displayNote(mSpinnerCourses, mTextNoteTitle, mTextNoteText);

        invalidateOptionsMenu();
    }

    private void sendEmail() {
        CourseInfo course = (CourseInfo) mSpinnerCourses.getSelectedItem();
        String subject = mTextNoteTitle.getText().toString();
        String text = "Checkout what i learned in the Pluralsight course \"" +
                course.getTitle() + "\"\n" + mTextNoteText.getText().toString();

        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("message/rfc2022");
        intent.putExtra(Intent.EXTRA_SUBJECT, subject);
        intent.putExtra(Intent.EXTRA_TEXT, text);
        startActivity(intent);


    }
}
