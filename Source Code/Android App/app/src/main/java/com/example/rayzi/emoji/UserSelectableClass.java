package com.example.rayzi.emoji;

import com.example.rayzi.pkRoot.PkAudioLiveUserRoot;

public class UserSelectableClass {

    public  PkAudioLiveUserRoot.UsersItem.SeatItem seatItem;
    public  boolean isSelected=false;

    public UserSelectableClass(PkAudioLiveUserRoot.UsersItem.SeatItem seatItem) {
        this.seatItem = seatItem;

    }

    public PkAudioLiveUserRoot.UsersItem.SeatItem getSeatItem() {
        return seatItem;
    }

    public void setSeatItem(PkAudioLiveUserRoot.UsersItem.SeatItem seatItem) {
        this.seatItem = seatItem;
    }

    public boolean isSelected() {
        return isSelected;
    }

    public void setSelected(boolean selected) {
        isSelected = selected;
    }
}
