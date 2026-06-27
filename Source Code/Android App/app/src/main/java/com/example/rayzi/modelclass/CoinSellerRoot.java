package com.example.rayzi.modelclass;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class CoinSellerRoot {

    @SerializedName("message")
    private String message;

    @SerializedName("status")
    private boolean status;

    @SerializedName("coinSeller")
    private List<CoinSellerItem> coinSeller;

    public String getMessage() {
        return message;
    }

    public boolean isStatus() {
        return status;
    }

    public List<CoinSellerItem> getCoinSeller() {
        return coinSeller;
    }

    public static class CoinSellerItem {

        @SerializedName("lastLogin")
        private String lastLogin;

        @SerializedName("image")
        private String image;

        @SerializedName("spendCoin")
        private int spendCoin;

        @SerializedName("mobileNumber")
        private String mobileNo;

        @SerializedName("receiveCoin")
        private int receiveCoin;

        @SerializedName("isShow")
        private boolean isShow;

        @SerializedName("isDisable")
        private boolean isDisable;

        @SerializedName("createdAt")
        private String createdAt;

        @SerializedName("password")
        private String password;

        @SerializedName("countryCode")
        private String countryCode;

        @SerializedName("name")
        private String name;

        @SerializedName("_id")
        private String id;

        @SerializedName("email")
        private String email;

        @SerializedName("coin")
        private int coin;

        @SerializedName("updatedAt")
        private String updatedAt;

        public String getLastLogin() {
            return lastLogin;
        }

        public String getImage() {
            return image;
        }

        public int getSpendCoin() {
            return spendCoin;
        }

        public String getMobileNo() {
            return mobileNo;
        }

        public int getReceiveCoin() {
            return receiveCoin;
        }

        public boolean isIsShow() {
            return isShow;
        }

        public boolean isIsDisable() {
            return isDisable;
        }

        public String getCreatedAt() {
            return createdAt;
        }

        public String getPassword() {
            return password;
        }

        public String getCountryCode() {
            return countryCode;
        }

        public String getName() {
            return name;
        }

        public String getId() {
            return id;
        }

        public String getEmail() {
            return email;
        }

        public int getCoin() {
            return coin;
        }

        public String getUpdatedAt() {
            return updatedAt;
        }
    }
}