package com.example.jarvis;

import android.util.Log;
import android.view.View;
import android.widget.DatePicker;

import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.Matchers;
import org.hamcrest.TypeSafeMatcher;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import androidx.test.espresso.Espresso;
import androidx.test.espresso.contrib.PickerActions;
import androidx.test.rule.ActivityTestRule;

import static androidx.test.espresso.Espresso.closeSoftKeyboard;
import static androidx.test.espresso.Espresso.onData;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.withClassName;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.anything;

public class CreateEventTest {

    private static final String TAG = "espressoCE";

    @Rule
    public ActivityTestRule<CreateEvent> mActivityTestRule = new ActivityTestRule<>(CreateEvent.class);

    private String eventName = "BBQ";

    @Before
    public void setUp() throws Exception {
    }

    @Test
    public void testuserinputscenario(){
        int tvDate = R.id.tvDate;
        Espresso.onView(withId(R.id.name_of_event)).perform(typeText("Espresso Christmas"));
        Espresso.closeSoftKeyboard();
        Espresso.onView(withId(tvDate)).perform(click());
        Espresso.onView(withClassName(Matchers.equalTo(DatePicker.class.getName())))
                .perform(PickerActions.setDate(2019, 12, 25));
        closeSoftKeyboard();
//        Espresso.onView(withId(R.id.name_of_event)).check(matches(withText("Espresso Christmas")));
        Espresso.onView(withId(tvDate)).check(matches(withText("25/12/2019")));
    }

    @After
    public void tearDown() throws Exception {
    }

}