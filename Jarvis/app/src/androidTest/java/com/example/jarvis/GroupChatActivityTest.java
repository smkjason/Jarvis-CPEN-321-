package com.example.jarvis;

import android.util.Log;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import androidx.test.espresso.Espresso;
import androidx.test.rule.ActivityTestRule;
import io.socket.client.Socket;

import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDescendantOfA;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withClassName;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withInputType;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static org.junit.Assert.*;

public class GroupChatActivityTest {

    @Rule
    public final ActivityTestRule<GroupChatActivity> activityActivityTestRule =
            new ActivityTestRule<>(GroupChatActivity.class, true, true);

    @Before
    public void setUp() throws Exception {

        Socket mSocket = ((jarvis) activityActivityTestRule.getActivity().getApplication()).getmSocket();
    }

    @Test
    public void sendmsgtest()
    {
        Espresso.onView(withId(R.id.edittext_chatbox)).perform(typeText("Are you seeing this message?"));
        Espresso.onView(withId(R.id.button_chatbox_send)).perform(click());
        try {
            Thread.sleep(5000);
        }catch(InterruptedException e){
            Log.e("GroupChatTest", "sleeping exception", e);
        }
        Espresso.onView(withText("Are you seeing this message?")).check(matches(isDisplayed()));
    }

    @After
    public void tearDown() throws Exception {
    }
}