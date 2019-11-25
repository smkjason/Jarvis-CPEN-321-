package com.example.jarvis;

import android.content.Intent;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import androidx.test.rule.ActivityTestRule;

import static androidx.test.espresso.Espresso.closeSoftKeyboard;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static androidx.test.espresso.Espresso.onView;


public class GroupChatActivityTest {
    public static final String USER_NAME = "jarviscpen321";
    public static final String PASSWORD = "Jarvis321";

    @Rule
    public final ActivityTestRule<GroupChatActivity> activityActivityTestRule =
            new ActivityTestRule<>(GroupChatActivity.class, true, false);

    @Before
    public void setUp() throws Exception {

        onView(withId(R.id.sign_in_button))
                .perform(click());
    }

    @Test
    public void sendmsgtest()
    {

        activityActivityTestRule.launchActivity(null);
        onView(withId(R.id.edittext_chatbox))
                .perform(typeText("Are you seeing this message?"));
        closeSoftKeyboard();
        onView(withId(R.id.button_chatbox_send)).perform(click());
        onView(withText("Are you seeing this message?")).check(matches(isDisplayed()));
    }

    @After
    public void tearDown() throws Exception {
    }
}