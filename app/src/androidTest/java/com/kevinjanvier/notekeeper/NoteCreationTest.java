package com.kevinjanvier.notekeeper;

import androidx.test.espresso.ViewInteraction;
import androidx.test.rule.ActivityTestRule;
import androidx.test.runner.AndroidJUnit4;

import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.closeSoftKeyboard;
import static androidx.test.espresso.action.ViewActions.pressBack;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.matcher.ViewMatchers.withId;



/**
 * Android Test for Creating a note
 */
@RunWith(AndroidJUnit4.class)
public class NoteCreationTest {

    static DataManager sDatamanager;

    @BeforeClass
    public static void classSetUp() throws Exception{
        sDatamanager = DataManager.getInstance();
    }

    @Rule
    public ActivityTestRule<NoteListActivity> mActivityActivityTestRule =
            new ActivityTestRule<>(NoteListActivity.class);

    @Test
    public void createNewNote(){
        final CourseInfo course = sDatamanager.getCourse("java_lang");
        //ViewInteraction fabNewNote = onView(withId(R.id.fab));
        //fabNewNote.perform(click());

        onView(withId(R.id.fab)).perform(click());
        onView(withId(R.id.text_note_title)).perform(typeText("Test note Title"));
        onView(withId(R.id.text_note_text)).perform(typeText("This is the body"),
                closeSoftKeyboard());

        pressBack();

    }



}