package com.example.rayzi.socket;

public interface CallHandler {

    void onCallRequest(Object[] args);

    void onCallConform(Object[] args);

    void onCallAnswer(Object[] args);

    void onCallRecieve(Object[] args);

    void onCallCancel(Object[] args);

}
