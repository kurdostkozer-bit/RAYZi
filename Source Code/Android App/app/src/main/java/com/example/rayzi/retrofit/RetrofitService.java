package com.example.rayzi.retrofit;

import com.example.rayzi.audioLive.LiveStreamRoot;
import com.example.rayzi.audioLive.ThemeRoot;
import com.example.rayzi.dummyModels.CreateUserStripe;
import com.example.rayzi.modelclass.AdsRoot;
import com.example.rayzi.modelclass.BannerRoot;
import com.example.rayzi.modelclass.BlockUnblockRoot;
import com.example.rayzi.modelclass.BlockedUserListRoot;
import com.example.rayzi.modelclass.CallRequestRoot;
import com.example.rayzi.modelclass.ChatListRoot;
import com.example.rayzi.modelclass.ChatTopicRoot;
import com.example.rayzi.modelclass.ChatUserListRoot;
import com.example.rayzi.modelclass.CoinRecordRoot;
import com.example.rayzi.modelclass.CoinSellerDataRoot;
import com.example.rayzi.modelclass.CoinSellerHistoryRoot;
import com.example.rayzi.modelclass.CoinSellerRoot;
import com.example.rayzi.modelclass.ComplainRoot;
import com.example.rayzi.modelclass.DiamondPlanRoot;
import com.example.rayzi.modelclass.FollowUnfollowResponse;
import com.example.rayzi.modelclass.GiftCategoryRoot;
import com.example.rayzi.modelclass.GiftRoot;
import com.example.rayzi.modelclass.GuestProfileRoot;
import com.example.rayzi.modelclass.GuestUsersListRoot;
import com.example.rayzi.modelclass.HeshtagsRoot;
import com.example.rayzi.modelclass.HistoryListRoot;
import com.example.rayzi.modelclass.IpAddressRoot_e;
import com.example.rayzi.modelclass.LevelRoot;
import com.example.rayzi.modelclass.LiveSummaryRoot;
import com.example.rayzi.modelclass.MyRecordTopDataRoot;
import com.example.rayzi.modelclass.PostCommentRoot;
import com.example.rayzi.modelclass.PostRoot;
import com.example.rayzi.modelclass.ReactionRoot;
import com.example.rayzi.modelclass.RecordRoot;
import com.example.rayzi.modelclass.ReedemListRoot;
import com.example.rayzi.modelclass.ReliteRoot;
import com.example.rayzi.modelclass.RestResponse;
import com.example.rayzi.modelclass.SearchLocationRoot;
import com.example.rayzi.modelclass.SettingRoot;
import com.example.rayzi.modelclass.SongRoot;
import com.example.rayzi.modelclass.StickerRoot;
import com.example.rayzi.modelclass.StripePaymentRoot2_e;
import com.example.rayzi.modelclass.SvgaListRoot;
import com.example.rayzi.modelclass.UpdateLiveTime;
import com.example.rayzi.modelclass.UploadImageRoot;
import com.example.rayzi.modelclass.UserRoot;
import com.example.rayzi.modelclass.VipPlanRoot;
import com.example.rayzi.pkRoot.PkAudioLiveUserRoot;
import com.google.gson.JsonObject;

import java.util.Map;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.PATCH;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.PartMap;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface RetrofitService {

    @GET("json")
    Call<IpAddressRoot_e> getIp();

    @GET("/setting")
    Call<SettingRoot> getSettings();

    @POST("/user/loginSignup")
    Call<UserRoot> createUser(@Body JsonObject jsonObject);

    @GET("/user/profile")
    Call<UserRoot> getUser(@Query("userId") String type);

    @POST("/history/income/seeAd")
    Call<UserRoot> addDiamondFromAds(@Body JsonObject jsonObject);


    @POST("/user/getUser")
    Call<GuestProfileRoot> getGuestUser(@Body JsonObject jsonObject);

    @Multipart
    @POST("/user/update")
    Call<UserRoot> updateUser(@PartMap Map<String, RequestBody> partMap,
                              @Part MultipartBody.Part requestBody, @Part MultipartBody.Part coverImage);

    @POST("/follow")
    Call<RestResponse> followUser(@Body JsonObject jsonObject);

    @POST("/unFollow")
    Call<RestResponse> unFollowUser(@Body JsonObject jsonObject);


    @POST("/follower/followUnfollow")
    Call<FollowUnfollowResponse> toggleFollowUnfollow(@Body JsonObject jsonObject);


    @GET("/banner")
    Call<BannerRoot> getBanner(@Query("type") String type);

    @POST("/user/checkUsername")
    Call<RestResponse> checkUserName(@Query("username") String username, @Query("userId") String userId);

    @POST("/follower/followerList")
    Call<GuestUsersListRoot> getFollowrsList(@Body JsonObject jsonObject);

    @POST("/follower/followingList")
    Call<GuestUsersListRoot> getFollowingList(@Body JsonObject jsonObject);


    @POST("/user/user/search")
    Call<GuestUsersListRoot> searchUser(@Body JsonObject jsonObject);

    @GET("/coinPlan")
    Call<DiamondPlanRoot> getDiamondsPlan();


    @GET("/vipPlan")
    Call<VipPlanRoot> getVipPlan();


    @GET("/v1/forward")
    Call<SearchLocationRoot> searchLocation(@Query("access_key") String key, @Query("query") String value);

    @GET("/hashtag")
    Call<HeshtagsRoot> searchHashtag(@Query("value") String keyword);

    @Multipart
    @POST("/post/uploadPost")
    Call<RestResponse> uploadPost(@PartMap Map<String, RequestBody> partMap,
                                  @Part MultipartBody.Part requestBody
    );

    @Multipart
    @POST("/video/uploadRelite")
    Call<RestResponse> uploadRelite(@PartMap Map<String, RequestBody> partMap,
                                    @Part MultipartBody.Part requestBody1,
                                    @Part MultipartBody.Part requestBody2,
                                    @Part MultipartBody.Part requestBody3
    );

    @GET("/song")
    Call<SongRoot> getSongs();

    @GET("/post/getPopularLatestPost")
    Call<PostRoot> getPostList(@Query("userId") String uId, @Query("type") String type,
                               @Query("start") int start, @Query("limit") int limit);

    @GET("/post/user")
    Call<PostRoot> getUserPostList(@Query("userId") String uId,
                                   @Query("start") int start, @Query("limit") int limit);

    @GET("/post/getFollowingPost")
    Call<PostRoot> getFollowingPost(@Query("userId") String uId, @Query("type") String type,
                                    @Query("start") int start, @Query("limit") int limit);


    @GET("/video/getRelite")
    Call<ReliteRoot> getRelites(@Query("userId") String uId, @Query("type") String type,
                                @Query("start") int start, @Query("limit") int limit);


    @GET("/favorite/likeUnlike")
    Call<RestResponse> toggleLikePost(@Query("userId") String userId, @Query("postId") String postId);

    @GET("/favorite/likeUnlike")
    Call<RestResponse> toggleLikeReel(@Query("userId") String userId, @Query("videoId") String videoId);

    @GET("/comment")
    Call<PostCommentRoot> getPostCommentList(@Query("userId") String uId, @Query("postId") String postId,
                                             @Query("start") int start, @Query("limit") int limit);

    @GET("/favorite")
    Call<PostCommentRoot> getPostLikeList(@Query("userId") String uId, @Query("postId") String postId,
                                          @Query("start") int start, @Query("limit") int limit);

    @GET("/comment")
    Call<PostCommentRoot> getReliteCommentList(@Query("userId") String uId, @Query("videoId") String postId,
                                               @Query("start") int start, @Query("limit") int limit);

    @GET("/likes")
    Call<PostCommentRoot> getReliteLikeList(@Query("userId") String uId, @Query("videoId") String postId,
                                            @Query("start") int start, @Query("limit") int limit);

    @POST("/comment")
    Call<RestResponse> addComment(@Body JsonObject jsonObject);

    @PATCH("liveUser/live")
    Call<com.example.rayzi.modelclass.LiveStreamRoot> makelivestreamUser(@Body JsonObject jsonObject);

    @PATCH("liveUser/live")
    Call<LiveStreamRoot> makeliveUser();

    @Multipart
    @PATCH("liveUser/live")
    Call<LiveStreamRoot> makeLiveUser(@PartMap Map<String, RequestBody> partMap,
                                      @Part MultipartBody.Part requestBody);

    @GET("/liveUser")
    Call<PkAudioLiveUserRoot> getLiveUsersList(@Query("userId") String uId, @Query("type") String type, @Query("keyword") String keyword, @Query("start") int start, @Query("limit") int limit);

    @GET("/liveUser/fakeLiveUser")
    Call<PkAudioLiveUserRoot> getFakeLiveList(@Query("start") int start, @Query("limit") int limit);

    /*   @GET("/liveUser")     //old method
       Call<NormalLiveUserRoot> getLiveUsersList(@Query("userId") String uId, @Query("type") String type);*/
    @GET("/getStreamingSummary")
    Call<LiveSummaryRoot> getLiveSummary(@Query("liveStreamingId") String liveStreamingId);


    @Multipart
    @POST("/complain")
    Call<RestResponse> addSupport(
            @PartMap Map<String, RequestBody> partMap,
            @Part MultipartBody.Part requestBody);

    @GET("/complain/userList")
    Call<ComplainRoot> getComplains(@Query("userId") String userid);


    @POST("/redeem")
    Call<RestResponse> cashOutDiamonds(@Body JsonObject jsonObject);


    @POST("/history/convertRcoinToDiamond")
    Call<UserRoot> convertRcoinToDiamond(@Body JsonObject jsonObject);

    @GET("/redeem/user")
    Call<ReedemListRoot> getReedemHistotry(@Query("userId") String userid);

    @POST("/user/addReferralCode")
    Call<UserRoot> reedemReferalCode(@Body JsonObject jsonObject);

    @GET("/giftCategory")
    Call<GiftCategoryRoot> getGiftCategory();

    @GET("/gift/{cId}")
    Call<GiftRoot> getGiftsByCategory(@Path("cId") String categoryId);

    @POST("/coinPlan/purchase/googlePlay")
    Call<UserRoot> callPurchaseApiGooglePayDiamond(@Body JsonObject jsonObject);

    @POST("/vipPlan/purchase/googlePlay")
    Call<UserRoot> callVIPPurchaseApiGooglePayDiamond(@Body JsonObject jsonObject);

    @POST("/vipPlan/purchase/googlePlay")
    Call<UserRoot> callPurchaseApiGooglePayVip(@Body JsonObject jsonObject);


    @POST("/coinPlan/purchase/stripe")
    Call<StripePaymentRoot2_e> setStripeDiamonds(@Body JsonObject jsonObject);

    @POST("/coinPlan/purchase/stripe")
    Call<UserRoot> purchsePlanStripeDiamons(@Body JsonObject jsonObject);

    @POST("/vipPlan/purchase/stripe")
    Call<StripePaymentRoot2_e> setStripeVip(@Body JsonObject jsonObject);

    @POST("/vipPlan/purchase/stripe")
    Call<UserRoot> purchsePlanStripeVip(@Body JsonObject jsonObject);


    @GET("/history/diamondRcoinTotal")
    Call<CoinRecordRoot> getCoinRecord(@Query("userId") String userId,
                                       @Query("startDate") String startDate,
                                       @Query("endDate") String endDate);


    @GET("/history/diamondRcoinHistory")
    Call<HistoryListRoot> getCoinHostory(@Query("userId") String userId,
                                         @Query("startDate") String startDate,
                                         @Query("endDate") String endDate,
                                         @Query("type") String type,
                                         @Query("start") int start, @Query("limit") int limit);

    @POST("/chatTopic/createRoom")
    Call<ChatTopicRoot> createChatRoom(@Body JsonObject jsonObject);

    @GET("/chat/getOldChat")
    Call<ChatListRoot> getOldChats(@Query("topicId") String chatRoomId,
                                   @Query("start") int start, @Query("limit") int limit);

    @GET("/chatTopic/chatList")
    Call<ChatUserListRoot> getChatUserList(@Query("userId") String userId,
                                           @Query("start") int start, @Query("limit") int limit);

    @Multipart
    @POST("/chat/uploadImage")
    Call<UploadImageRoot> uploadChatImage(
            @PartMap Map<String, RequestBody> partMap,
            @Part MultipartBody.Part requestBody);

    @DELETE("/chat/deleteMessage")
    Call<RestResponse> deleteChat(@Query("chatId") String chatId);

    @DELETE("/comment")
    Call<RestResponse> deleteComment(@Query("commentId") String chatId);

    @POST("/user/online")
    Call<RestResponse> makeOnlineUser(@Body JsonObject jsonObject);

    @POST("/history/call")
    Call<CallRequestRoot> makeCallRequest(@Body JsonObject jsonObject);

    @POST("/notification")
    Call<UserRoot> changeUserNotificationSetting(@Body JsonObject jsonObject);

    @GET("/user/random")
    Call<GuestProfileRoot> getRandomUser(@Query("userId") String userId);

    @POST("/report")
    Call<RestResponse> reportThisUser(@Body JsonObject jsonObject);

    @GET("/sticker")
    Call<StickerRoot> getStickers();

    @GET("/level")
    Call<LevelRoot> getLevels();

    @GET("/advertisement")
    Call<AdsRoot> getAds();

    @DELETE("/post/deletePost")
    Call<RestResponse> deletePost(@Query("postId") String postId);

    @DELETE("/video/deleteRelite")
    Call<RestResponse> deleteRelite(@Query("videoId") String videoId);

    @GET("/theme")
    Call<ThemeRoot> getTheme();

    @GET("/liveUser/getTime")
    Call<UpdateLiveTime> updateLiveTime(@Query("liveUserId") String liveUserId, @Query("liveStreamingId") String liveStreamingId);

    @GET("/coinSeller")
    Call<CoinSellerRoot> getCoinSellerList();


    @POST("/history/live")
    Call<RecordRoot> getMyRecord(@Body JsonObject jsonObject);

    @POST("/history/liveAnalytic")
    Call<MyRecordTopDataRoot> getMyRecordTopData(@Body JsonObject jsonObject);

    @GET("/svga/get")
    Call<SvgaListRoot> getSvgaList(@Query("userId") String userId, @Query("type") String type, @Query("start") int start, @Query("limit") int limit);

    @POST("svga/purchase")
    Call<UserRoot> purchaseSvga(@Query("type") String type, @Body JsonObject jsonObject);

    @POST("svga/select")
    Call<UserRoot> selectSvga(@Body JsonObject jsonObject);

    @GET("/history/sendGiftFakeHost")
    Call<UserRoot> getCoin(@Query("senderUserId") String senderUserId, @Query("coin") int coin, @Query("receiverUserId") String receiverUserId, @Query("type") String type);

    @POST("/coinPlan/stripe/createCustomer")
    Call<CreateUserStripe> getStripeCustomer(@Body JsonObject jsonObject);

    @GET("/user/checkPlan")
    Call<RestResponse> checkUserPlan(@Query("userId") String userId);

    @GET("/coinSeller/getCoinSellerUser")
    Call<CoinSellerDataRoot> getMyCoinSellerData(@Query("userId") String userId);

    @PATCH("/coinSeller/coinByCoinSeller")
    Call<CoinSellerDataRoot> sendCoinToUser(@Body JsonObject jsonObject);

    @GET("/coinSellerHistory/historyOfCoinSellerToUser")
    Call<CoinSellerHistoryRoot> getTopupHistory(@Query("userId") String key);

    @Multipart
    @POST("/hostRequest/createRequest")
    Call<RestResponse> addHostRequest(@PartMap Map<String, RequestBody> partMap, @Part MultipartBody.Part requestBody);

    @GET("/reaction/getReaction")
    Call<ReactionRoot> getReactions();

    @Multipart
    @PATCH("/liveUser/updateRoomImage")
    Call<RestResponse> updateRoomImage(@PartMap Map<String, RequestBody> partMap,
                                       @Part MultipartBody.Part requestBody);

    @GET("liveUser/checkLive")
    Call<LiveStreamRoot> checkUserLiveOrNot(@Query("userId") String userId);

    @PATCH("/liveUser/updatePrivateCode")
    Call<RestResponse> updatePasscode(@Query("privateCode") String privateCode,
                                      @Query("liveUserId") String liveUserId);

    @DELETE("/liveUser/terminateAudioSession")
    Call<RestResponse> deleteRoom(@Query("userId") String userId);


    @GET("hostLiveHistory/hostLive")
    Call<RestResponse> getHostApi(@Query("hostId") String hostId,
                                  @Query("liveType") String liveType,
                                  @Query("date") String date);


    @PATCH("/liveuser/broadcastAlertSound")
    Call<RestResponse> getNotification(@Query("userId") String userId);

    @DELETE("chatTopic/deleteAllChatsAndTopics")
    Call<RestResponse> deleteAllChat(@Query("userId") String userId);

    @POST("block/blockOrUnblockUser")
    Call<BlockUnblockRoot> BlockUser(@Query("userId") String userId,
                                     @Query("toUserId") String toUserId);

    @GET("/block/getBlockedUsers")
    Call<BlockedUserListRoot> getBlockUser(@Query("userId") String userId);


}
