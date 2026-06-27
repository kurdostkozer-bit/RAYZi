package com.example.rayzi.socket;

public interface AudioRoomHandler {

    void onLiveEndByEnd(Object[] args);

    void onComment(Object[] args);

    void onGift(Object[] args);

    void onView(Object[] args);

    void onAddRequested(Object[] args);

    void onDeclineInvite(Object[] args);

    void onAddParticipants(Object[] args);

    void onLessParticipants(Object[] args);

    void onMuteSeat(Object[] args);

    void onLockSeat(Object[] args);

    void onAllSeatLock(Object[] args);

    void onChangeTheme(Object[] args);

    void onSeat(Object[] args);

    void onBlock(Object[] args);

    void onGetUser(Object[] args);

    void onGetUser2(Object[] args);

    void onInvite(Object[] args);

    void onLiveEnd(Object[] argr);

    void onReactionReceived(Object[] args1);

    void onRoomNameChange(Object[] args);

    void onWelcomeMessage(Object[] args);

    void onRoomImageChange(Object[] args);

    void onUserCoinUpdate(Object[] args);

    void onBanned(Object[] args);

    void onBannedUserlist(Object[] args);

    void onBlockuseralert(Object[] args);

    void onHostEnter(Object[] args);

    void onAudioLiveHostRemove(Object[] args);

    void onTotalRoomcoins(Object[] args);

}
