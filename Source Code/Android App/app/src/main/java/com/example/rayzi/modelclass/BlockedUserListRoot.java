package com.example.rayzi.modelclass;

import java.util.List;
import com.google.gson.annotations.SerializedName;

public class BlockedUserListRoot{

	@SerializedName("blockedUsers")
	private List<BlockedUsersItem> blockedUsers;

	@SerializedName("message")
	private String message;

	@SerializedName("status")
	private boolean status;

	@SerializedName("total")
	private int total;

	public int getTotal() {
		return total;
	}

	public List<BlockedUsersItem> getBlockedUsers(){
		return blockedUsers;
	}

	public String getMessage(){
		return message;
	}

	public boolean isStatus(){
		return status;
	}

	public static class BlockedUsersItem{

		@SerializedName("_id")
		private String id;

		@SerializedName("toUserId")
		private ToUserId toUserId;

		public String getId(){
			return id;
		}

		public ToUserId getToUserId(){
			return toUserId;
		}
	}

	public static class ToUserId{

		@SerializedName("country")
		private String country;

		@SerializedName("image")
		private String image;

		@SerializedName("countryFlagImage")
		private String countryFlagImage;

		@SerializedName("name")
		private String name;

		@SerializedName("_id")
		private String id;

		public String getCountry(){
			return country;
		}

		public String getImage(){
			return image;
		}

		public String getCountryFlagImage(){
			return countryFlagImage;
		}

		public String getName(){
			return name;
		}

		public String getId(){
			return id;
		}
	}
}