package com.example.rayzi.modelclass;

import com.google.gson.annotations.SerializedName;

public class CoinSellerDataRoot {

	@SerializedName("message")
	private String message;

	@SerializedName("status")
	private boolean status;

	@SerializedName("data")
	private CoinSeller coinSeller;

	public String getMessage(){
		return message;
	}

	public boolean isStatus(){
		return status;
	}

	public CoinSeller getCoinSeller(){
		return coinSeller;
	}

	public static class CoinSeller{

		@SerializedName("isDisable")
		private boolean isDisable;

		@SerializedName("createdAt")
		private String createdAt;

		@SerializedName("spendCoin")
		private int spendCoin;

		@SerializedName("_id")
		private String id;

		@SerializedName("userId")
		private String userId;

		@SerializedName("uniqueId")
		private int uniqueId;

		@SerializedName("coin")
		private int coin;

		@SerializedName("updatedAt")
		private String updatedAt;

		public boolean isIsDisable(){
			return isDisable;
		}

		public String getCreatedAt(){
			return createdAt;
		}

		public int getSpendCoin(){
			return spendCoin;
		}

		public String getId(){
			return id;
		}

		public String getUserId(){
			return userId;
		}

		public int getUniqueId(){
			return uniqueId;
		}

		public int getCoin(){
			return coin;
		}

		public String getUpdatedAt(){
			return updatedAt;
		}
	}
}