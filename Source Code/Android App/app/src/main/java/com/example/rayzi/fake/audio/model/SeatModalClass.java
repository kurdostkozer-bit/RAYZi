package com.example.rayzi.fake.audio.model;

public class SeatModalClass {

    String seat_id;
    int Image;
    private String name;
    boolean isReserved;

    public SeatModalClass(String seat_id, int image, String name, boolean isReserve) {
        this.seat_id = seat_id;
        Image = image;
        this.name = name;
        isReserved = isReserve;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isReserved() {
        return isReserved;
    }

    public void setReserved(boolean reserved) {
        isReserved = reserved;
    }

    public String getSeat_id() {
        return seat_id;
    }

    public void setSeat_id(String seat_id) {
        this.seat_id = seat_id;
    }

    public int getImage() {
        return Image;
    }

    public void setImage(int image) {
        Image = image;
    }
}
