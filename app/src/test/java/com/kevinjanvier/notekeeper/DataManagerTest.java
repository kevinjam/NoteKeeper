package com.kevinjanvier.notekeeper;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

public class DataManagerTest {


    static DataManager sDataManager;


    @AfterClass
    public static void classSetUp() throws Exception{
        sDataManager = DataManager.getInstance();
    }



    @Before
    public void setUp() throws Exception{
       sDataManager = DataManager.getInstance();
        sDataManager.getNotes().clear();
        sDataManager.initializeExampleNotes();
    }

    @Test
    public void createNewNote() throws Exception {

        final CourseInfo course = sDataManager.getCourse("android_async");
        final String noteTitle = "Test note title";
        final String noteText = "This is the body of my test note";


        int noteIndex = sDataManager.createNewNote();
        NoteInfo newNote = sDataManager.getNotes().get(noteIndex);
        newNote.setCourse(course);
        newNote.setTitle(noteTitle);
        newNote.setText(noteText);

        NoteInfo compareNote = sDataManager.getNotes().get(noteIndex);
        //check individual note
        assertEquals(compareNote.getCourse(), course);
        assertEquals(compareNote.getTitle(), noteTitle);
        assertEquals(compareNote.getText(), noteText);

       // assertSame(newNote, compareNote);
    }

    @Test
    public void createNewNoteOneStepCreation(){
        final CourseInfo course = sDataManager.getCourse("android_async");
        final String noteTitle = "Test note title";
        final String noteText ="This is the body of my test note";
        int noteIndex = sDataManager.createNewNote(course, noteTitle, noteText);

        NoteInfo compareNote = sDataManager.getNotes().get(noteIndex);
        assertEquals(compareNote.getCourse(), course);
        assertEquals(compareNote.getTitle(), noteTitle);
        assertEquals(compareNote.getText(), noteText);

    }

}