package com.example.jarvis;

import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.rule.ActivityTestRule;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;

@RunWith(AndroidJUnit4.class)
public class ViewProfileTest {
    @Rule
    public ActivityTestRule<ViewProfile> mActivityRule =
            new ActivityTestRule<>(ViewProfile.class);


    @Test
    public void checkUserName() {
        onView(withId(R.id.username)).check(matches(withText("Jarvis Robo")));
    }

    @Test
    public void checkUserEmail() {
        onView(withId(R.id.usergmail)).check(matches(withText("jarviscpen321@gmail.com")));
    }
}
