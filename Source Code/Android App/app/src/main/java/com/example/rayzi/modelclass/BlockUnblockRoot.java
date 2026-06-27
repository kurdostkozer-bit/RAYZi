package com.example.rayzi.modelclass;

import com.google.gson.annotations.SerializedName;

public class BlockUnblockRoot{

	@SerializedName("isBlocked")
	private boolean isBlocked;

	@SerializedName("message")
	private String message;

	@SerializedName("status")
	private boolean status;

	public boolean isIsBlocked(){
		return isBlocked;
	}

	public String getMessage(){
		return message;
	}

	public boolean isStatus(){
		return status;
	}
}