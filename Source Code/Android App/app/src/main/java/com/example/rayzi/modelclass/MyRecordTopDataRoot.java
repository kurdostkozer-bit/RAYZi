package com.example.rayzi.modelclass;

import com.google.gson.annotations.SerializedName;

public class MyRecordTopDataRoot{

	@SerializedName("todayLiveStreaming")
	private String todayLiveStreaming;

	@SerializedName("message")
	private String message;

	@SerializedName("weekLiveStreaming")
	private String weekLiveStreaming;

	@SerializedName("weekAudio")
	private String weekAudio;

	@SerializedName("status")
	private boolean status;

	@SerializedName("todayAudio")
	private String todayAudio;

	public String getTodayLiveStreaming(){
		return todayLiveStreaming;
	}

	public String getMessage(){
		return message;
	}

	public String getWeekLiveStreaming(){
		return weekLiveStreaming;
	}

	public String getWeekAudio(){
		return weekAudio;
	}

	public boolean isStatus(){
		return status;
	}

	public String getTodayAudio(){
		return todayAudio;
	}
}