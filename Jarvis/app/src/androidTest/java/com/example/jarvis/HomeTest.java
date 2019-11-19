package com.example.jarvis;


import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.rule.ActivityTestRule;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.closeSoftKeyboard;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;


@RunWith(AndroidJUnit4.class)
public class HomeTest {

    @Rule
    public ActivityTestRule<Home> mActivityRule =
            new ActivityTestRule<>(Home.class);

    @Test
    public void gotoViewProfile() {
        //press the view profile button.
        onView(withId(R.id.view_profile_bttn)).perform(click());

        // This view is in view, no need to tell Espresso.
        onView(withId(R.id.username)).check(matches(withText("Jarvis Robo")));
    }
}
