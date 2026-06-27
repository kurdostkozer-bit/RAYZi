package com.example.rayzi.modelclass;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class SettingRoot {

    @SerializedName("message")
    private String message;

    @SerializedName("status")
    private boolean status;

    @SerializedName("setting")
    private Setting setting;

    public String getMessage() {
        return message;
    }

    public boolean isStatus() {
        return status;
    }

    public Setting getSetting() {
        return setting;
    }

    public static class Setting {

        @SerializedName("isAppActive")
        private boolean isAppActive;

        @SerializedName("callCharge")
        private int callCharge;

        @SerializedName("maxSecondForVideo")
        private int maxSecondForVideo;

        @SerializedName("googlePlayEmail")
        private String googlePlayEmail;

        @SerializedName("minRcoinForCashOut")
        private int minRcoinForCashOut;

        @SerializedName("stripeSwitch")
        private boolean stripeSwitch;

        @SerializedName("loginBonus")
        private int loginBonus;

        @SerializedName("privacyPolicyLink")
        private String privacyPolicyLink;

        @SerializedName("agoraKey")
        private String agoraKey;

        @SerializedName("rCoinForDiamond")
        private double rCoinForDiamond;

        @SerializedName("agoraCertificate")
        private String agoraCertificate;

        @SerializedName("googlePlaySwitch")
        private boolean googlePlaySwitch;

        @SerializedName("freeDiamondForAd")
        private int freeDiamondForAd;

        @SerializedName("privacyPolicyText")
        private String privacyPolicyText;

        @SerializedName("currency")
        private String currency;


        @SerializedName("googlePlayKey")
        private String googlePlayKey;

        @SerializedName("referralBonus")
        private int referralBonus;

        @SerializedName("stripeSecretKey")
        private String stripeSecretKey;

        @SerializedName("stripePublishableKey")
        private String stripePublishableKey;

        @SerializedName("maxAdPerDay")
        private int maxAdPerDay;

        @SerializedName("chatCharge")
        private int chatCharge;

        @SerializedName("_id")
        private String id;

        @SerializedName("rCoinForCashOut")
        private int rCoinForCaseOut;

        @SerializedName("paymentGateway")
        private List<String> paymentGateway;

        @SerializedName("version")
        private int version;

        @SerializedName("locationApiKey")
        private String locationApiKey;


        @SerializedName("game")
        private List<Game> game;

        public String getLocationApiKey() {
            return locationApiKey;
        }

        public int getFemaleCallCharge() {
            return femaleCallCharge;
        }

        public int getMaleCallCharge() {
            return maleCallCharge;
        }

        @SerializedName("femaleCallCharge")
        private int femaleCallCharge;

        @SerializedName("maleCallCharge")
        private int maleCallCharge;

        @SerializedName("bothRandomCallRate")
        private int bothRandomCallRate;

        @SerializedName("maleRandomCallRate")
        private int maleRandomCallRate;

        @SerializedName("femaleRandomCallRate")
        private int femaleRandomCallRate;

        public int getBothRandomCallRate() {
            return bothRandomCallRate;
        }

        public int getMaleRandomCallRate() {
            return maleRandomCallRate;
        }

        public int getFemaleRandomCallRate() {
            return femaleRandomCallRate;
        }

        public boolean isIsAppActive() {
            return isAppActive;
        }
        public List<Game> getGame() {
            return game;
        }
        public int getCallCharge() {
            return callCharge;
        }

        public int getMaxSecondForVideo() {
            return maxSecondForVideo;
        }

        public String getGooglePlayEmail() {
            return googlePlayEmail;
        }

        public int getMinRcoinForCashOut() {
            return minRcoinForCashOut;
        }

        public boolean isStripeSwitch() {
            return stripeSwitch;
        }

        public int getLoginBonus() {
            return loginBonus;
        }


        public String getPrivacyPolicyLink() {
            return privacyPolicyLink;
        }

        public String getAgoraKey() {
            return agoraKey;
        }

        public double getRCoinForDiamond() {
            return rCoinForDiamond;
        }

        public String getAgoraCertificate() {
            return agoraCertificate;
        }

        public boolean isGooglePlaySwitch() {
            return googlePlaySwitch;
        }

        public int getFreeDiamondForAd() {
            return freeDiamondForAd;
        }

        public String getPrivacyPolicyText() {
            return privacyPolicyText;
        }

        public String getCurrency() {
            return currency;
        }


        public String getGooglePlayKey() {
            return googlePlayKey;
        }

        public int getReferralBonus() {
            return referralBonus;
        }

        public String getStripeSecretKey() {
            return stripeSecretKey;
        }

        public String getStripePublishableKey() {
            return stripePublishableKey;
        }

        public int getMaxAdPerDay() {
            return maxAdPerDay;
        }

        public int getChatCharge() {
            return chatCharge;
        }

        public String getId() {
            return id;
        }

        public int getRCoinForCaseOut() {
            return rCoinForCaseOut;
        }

        public List<String> getPaymentGateway() {
            return paymentGateway;
        }

        public int getLetestVersonCode() {
            return version;
        }
    }

    public class Game {

        @SerializedName("name")
        private String name;
        @SerializedName("link")
        private String link;
        @SerializedName("_id")
        private String id;
        @SerializedName("createdAt")
        private String createdAt;
        @SerializedName("updatedAt")
        private String updatedAt;
        @SerializedName("image")
        private String image;

        public String getName() {
            return name;
        }

        public String getLink() {
            return link;
        }

        public String getId() {
            return id;
        }

        public String getCreatedAt() {
            return createdAt;
        }

        public String getUpdatedAt() {
            return updatedAt;
        }

        public String getImage() {
            return image;
        }
    }
}