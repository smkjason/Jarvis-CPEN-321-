package com.example.jarvis;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import androidx.test.espresso.Espresso;
import androidx.test.espresso.contrib.PickerActions;
import androidx.test.rule.ActivityTestRule;

import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.matcher.ViewMatchers.isClickable;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static org.junit.Assert.*;

public class CreateEventTest {

    @Rule
    public ActivityTestRule<CreateEvent> mActivityTestRule = new ActivityTestRule<>(CreateEvent.class);

    private String eventName = "BBQ";

    @Before
    public void setUp() throws Exception {
    }

    @Test
    public void testuserinputscenario(){
        Espresso.onView(withId(R.id.name_of_event)).perform(typeText("Espresso Christmas"));
        Espresso.onView(withId(R.id.tvDate)).perform(PickerActions.setDate(2019, 12, 25));
        Espresso.closeSoftKeyboard();
        Espresso.onView(withId(R.id.create_event_bttn)).perform(click());
    }

    @After
    public void tearDown() throws Exception {
    }
}