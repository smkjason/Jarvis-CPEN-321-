package com.example.jarvis;

import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.rule.ActivityTestRule;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isClickable;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;

@RunWith(AndroidJUnit4.class)
public class MapActivityTest {

    @Rule
    public ActivityTestRule<Home> mActivityRule =
            new ActivityTestRule<>(Home.class);


//    @Test
//    public void checkIfButtonsClickable() {
//        onView(withId(R.id.view_profile_bttn)).check(matches(isClickable()));
//        onView(withId(R.id.Map_bttn)).check(matches(isClickable()));
//        onView(withId(R.id.go_to_chatroom_bttn)).check(matches(isClickable()));
//        onView(withId(R.id.create_event_bttn)).check(matches(isClickable()));
//    }
//
//    @Test
//    public void checkIfButtonsDisplayed() {
//        onView(withId(R.id.view_profile_bttn)).check(matches(isDisplayed()));
//        onView(withId(R.id.Map_bttn)).check(matches(isDisplayed()));
//        onView(withId(R.id.go_to_chatroom_bttn)).check(matches(isDisplayed()));
//        onView(withId(R.id.create_event_bttn)).check(matches(isDisplayed()));
//    }
}
