package com.example.jarvis;

public class FriendItem {
    private int mImageResource;
    private String mFriend;

    public FriendItem(int imageResource, String friend){
        mImageResource = imageResource;
        mFriend = friend;
    }

    public int getImageResource() {
        return mImageResource;
    }

    public String getFriend(){
        return mFriend;
    }

}
