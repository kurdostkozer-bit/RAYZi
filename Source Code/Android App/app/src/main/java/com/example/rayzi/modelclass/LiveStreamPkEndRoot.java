package com.example.rayzi.modelclass;

import com.google.gson.annotations.SerializedName;

public class LiveStreamPkEndRoot {

    @SerializedName("tie")
    private Tie tie;

    @SerializedName("winner")
    private Winner winner;

    @SerializedName("data")
    private Data data;

    public Tie getTie() {
        return tie;
    }

    public Winner getWinner() {
        return winner;
    }

    public Data getData() {
        return data;
    }

    public static class Data {

        @SerializedName("country")
        private String country;

        @SerializedName("image")
        private String image;

        @SerializedName("rCoin")
        private int rCoin;

        @SerializedName("pkIdentity")
        private PkIdentity pkIdentity;

        @SerializedName("channel")
        private String channel;

        @SerializedName("agoraUID")
        private int agoraUID;

        @SerializedName("liveStreamingId")
        private String liveStreamingId;

        @SerializedName("isVIP")
        private boolean isVIP;

        @SerializedName("token")
        private String token;

        @SerializedName("liveUserId")
        private String liveUserId;

        @SerializedName("duration")
        private int duration;

        @SerializedName("diamond")
        private int diamond;

        @SerializedName("view")
        private int view;

        @SerializedName("name")
        private String name;

        @SerializedName("_id")
        private String id;

        @SerializedName("isPkMode")
        private boolean isPkMode;

        @SerializedName("pkConfig")
        private PkConfig pkConfig;

        @SerializedName("username")
        private String username;

        public String getCountry() {
            return country;
        }

        public String getImage() {
            return image;
        }

        public int getRCoin() {
            return rCoin;
        }

        public PkIdentity getPkIdentity() {
            return pkIdentity;
        }

        public String getChannel() {
            return channel;
        }

        public int getAgoraUID() {
            return agoraUID;
        }

        public String getLiveStreamingId() {
            return liveStreamingId;
        }

        public boolean isIsVIP() {
            return isVIP;
        }

        public String getToken() {
            return token;
        }

        public String getLiveUserId() {
            return liveUserId;
        }

        public int getDuration() {
            return duration;
        }

        public int getDiamond() {
            return diamond;
        }

        public int getView() {
            return view;
        }

        public String getName() {
            return name;
        }

        public String getId() {
            return id;
        }

        public boolean isIsPkMode() {
            return isPkMode;
        }

        public PkConfig getPkConfig() {
            return pkConfig;
        }

        public String getUsername() {
            return username;
        }

        public static class PkConfig {

            @SerializedName("remoteRank")
            private int remoteRank;

            @SerializedName("host1Image")
            private String host1Image;

            @SerializedName("host1Details")
            private Host1Details host1Details;

            @SerializedName("host2Name")
            private String host2Name;

            @SerializedName("host1Channel")
            private String host1Channel;

            @SerializedName("host2Channel")
            private String host2Channel;

            @SerializedName("host1AgoraUID")
            private int host1AgoraUID;

            @SerializedName("host2AgoraUID")
            private int host2AgoraUID;

            @SerializedName("host2Token")
            private String host2Token;

            @SerializedName("host1Name")
            private String host1Name;

            @SerializedName("isWinner")
            private int isWinner;

            @SerializedName("host2Image")
            private String host2Image;

            @SerializedName("host1LiveId")
            private String host1LiveId;

            @SerializedName("host1Id")
            private String host1Id;

            @SerializedName("host2Details")
            private Host2Details host2Details;

            @SerializedName("host2Id")
            private String host2Id;

            @SerializedName("host2LiveId")
            private String host2LiveId;

            @SerializedName("host1Token")
            private String host1Token;

            @SerializedName("localRank")
            private int localRank;

            public int getRemoteRank() {
                return remoteRank;
            }

            public String getHost1Image() {
                return host1Image;
            }

            public Host1Details getHost1Details() {
                return host1Details;
            }

            public String getHost2Name() {
                return host2Name;
            }

            public String getHost1Channel() {
                return host1Channel;
            }

            public String getHost2Channel() {
                return host2Channel;
            }

            public int getHost1AgoraUID() {
                return host1AgoraUID;
            }

            public int getHost2AgoraUID() {
                return host2AgoraUID;
            }

            public String getHost2Token() {
                return host2Token;
            }

            public String getHost1Name() {
                return host1Name;
            }

            public int getIsWinner() {
                return isWinner;
            }

            public String getHost2Image() {
                return host2Image;
            }

            public String getHost1LiveId() {
                return host1LiveId;
            }

            public String getHost1Id() {
                return host1Id;
            }

            public Host2Details getHost2Details() {
                return host2Details;
            }

            public String getHost2Id() {
                return host2Id;
            }

            public String getHost2LiveId() {
                return host2LiveId;
            }

            public String getHost1Token() {
                return host1Token;
            }

            public int getLocalRank() {
                return localRank;
            }

            public static class Host2Details {

                @SerializedName("image")
                private String image;

                @SerializedName("country")
                private String country;

                @SerializedName("rCoin")
                private int rCoin;

                @SerializedName("name")
                private String name;

                @SerializedName("isVIP")
                private boolean isVIP;

                public String getImage() {
                    return image;
                }

                public String getCountry() {
                    return country;
                }

                public int getRCoin() {
                    return rCoin;
                }

                public String getName() {
                    return name;
                }

                public boolean isIsVIP() {
                    return isVIP;
                }
            }

            public static class Host1Details {

                @SerializedName("image")
                private String image;

                @SerializedName("country")
                private String country;

                @SerializedName("rCoin")
                private int rCoin;

                @SerializedName("name")
                private String name;

                @SerializedName("isVIP")
                private boolean isVIP;

                public String getImage() {
                    return image;
                }

                public String getCountry() {
                    return country;
                }

                public int getRCoin() {
                    return rCoin;
                }

                public String getName() {
                    return name;
                }

                public boolean isIsVIP() {
                    return isVIP;
                }
            }
        }

        public static class PkIdentity {

            @SerializedName("pkId")
            private String pkId;

            @SerializedName("count")
            private int count;

            public String getPkId() {
                return pkId;
            }

            public int getCount() {
                return count;
            }
        }
    }

    public static class Winner {

        @SerializedName("image")
        private String image;

        @SerializedName("country")
        private String country;

        @SerializedName("rCoin")
        private int rCoin;

        @SerializedName("name")
        private String name;

        @SerializedName("isVIP")
        private boolean isVIP;

        public String getImage() {
            return image;
        }

        public String getCountry() {
            return country;
        }

        public int getRCoin() {
            return rCoin;
        }

        public String getName() {
            return name;
        }

        public boolean isIsVIP() {
            return isVIP;
        }
    }

    public static class Tie {

        @SerializedName("isTie")
        private boolean isTie;

        public boolean getIsTie() {
            return isTie;
        }
    }
}