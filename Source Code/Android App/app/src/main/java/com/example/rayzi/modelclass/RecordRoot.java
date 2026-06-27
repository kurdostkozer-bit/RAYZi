
package com.example.rayzi.modelclass;

import java.util.List;

import com.google.gson.annotations.Expose;


@SuppressWarnings("unused")
public class RecordRoot {

    @Expose
    private List<History> history;
    @Expose
    private Long income;
    @Expose
    private String message;
    @Expose
    private Boolean status;
    @Expose
    private Long total;

    public List<History> getHistory() {
        return history;
    }

    public void setHistory(List<History> history) {
        this.history = history;
    }

    public Long getIncome() {
        return income;
    }

    public void setIncome(Long income) {
        this.income = income;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Boolean getStatus() {
        return status;
    }

    public void setStatus(Boolean status) {
        this.status = status;
    }

    public Long getTotal() {
        return total;
    }

    public void setTotal(Long total) {
        this.total = total;
    }


    @SuppressWarnings("unused")
    public static class History {

        @Expose
        private String _id;
        @Expose
        private Long comments;
        @Expose
        private String duration;
        @Expose
        private String endTime;
        @Expose
        private Long fans;
        @Expose
        private Long gifts;
        @Expose
        private Long rCoin;
        @Expose
        private String startTime;

        public boolean isAudio() {
            return audio;
        }

        @Expose
        private boolean audio;
        @Expose
        private Long user;

        public String get_id() {
            return _id;
        }

        public void set_id(String _id) {
            this._id = _id;
        }

        public Long getComments() {
            return comments;
        }

        public void setComments(Long comments) {
            this.comments = comments;
        }

        public String getDuration() {
            return duration;
        }

        public void setDuration(String duration) {
            this.duration = duration;
        }

        public String getEndTime() {
            return endTime;
        }

        public void setEndTime(String endTime) {
            this.endTime = endTime;
        }

        public Long getFans() {
            return fans;
        }

        public void setFans(Long fans) {
            this.fans = fans;
        }

        public Long getGifts() {
            return gifts;
        }

        public void setGifts(Long gifts) {
            this.gifts = gifts;
        }

        public Long getRCoin() {
            return rCoin;
        }

        public void setRCoin(Long rCoin) {
            this.rCoin = rCoin;
        }

        public String getStartTime() {
            return startTime;
        }

        public void setStartTime(String startTime) {
            this.startTime = startTime;
        }

        public Long getUser() {
            return user;
        }

        public void setUser(Long user) {
            this.user = user;
        }

    }
}
