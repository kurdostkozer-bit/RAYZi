package com.example.rayzi.z_demo;

import com.example.rayzi.R;
import com.example.rayzi.dummyModels.CoinPlan_dummy;
import com.example.rayzi.dummyModels.GiftCategory_dummy;
import com.example.rayzi.dummyModels.GiftRoot_dummy;
import com.example.rayzi.dummyModels.Song_dummy;
import com.example.rayzi.fake.pk.model.FakeGiftRoot;
import com.example.rayzi.modelclass.LiveStramComment;
import com.example.rayzi.modelclass.UserRoot;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Random;

public class Demo_contents {


    public static ArrayList<String> girlsImage = new ArrayList<>(Arrays.asList(
            "https://images.unsplash.com/photo-1581588636584-5c447d2c9d97?q=80&w=1998&auto=format&fit=crop&ixlib=rb-4.0.3&ixid=M3wxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8fA%3D%3D",
            "https://images.unsplash.com/photo-1467632499275-7a693a761056?w=500&auto=format&fit=crop&q=60&ixlib=rb-4.0.3&ixid=M3wxMjA3fDB8MHxzZWFyY2h8MjZ8fGhvdCUyMGJpa2luaXxlbnwwfHwwfHx8MA%3D%3D",
            "https://images.unsplash.com/photo-1606792109910-340f5e672ccd?w=500&auto=format&fit=crop&q=60&ixlib=rb-4.0.3&ixid=M3wxMjA3fDB8MHxzZWFyY2h8Mjh8fGhvdCUyMGJpa2luaXxlbnwwfHwwfHx8MA%3D%3D",
            "https://images.unsplash.com/photo-1520065949650-380765513210?w=500&auto=format&fit=crop&q=60&ixlib=rb-4.0.3&ixid=M3wxMjA3fDB8MHxzZWFyY2h8Njd8fGhvdCUyMGJpa2luaXxlbnwwfHwwfHx8MA%3D%3D",
            "https://images.unsplash.com/photo-1583058905141-deef2de746bb?ixid=MnwxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8&ixlib=rb-1.2.1&auto=format&fit=crop&w=888&q=80"

    ));

  /*  public static List<Sticker_dummy> getStickers() {
        List<Sticker_dummy> stickerDummies = new ArrayList<>();
        stickerDummies.add(new Sticker_dummy(1, "https://muly.starthub.ltd/storage/demo/stickers/tBYh155Uj846jNB.png"));
        stickerDummies.add(new Sticker_dummy(2, "https://muly.starthub.ltd/storage/demo/stickers/5xjouRhyJJul6vG.png"));
        stickerDummies.add(new Sticker_dummy(3, "https://muly.starthub.ltd/storage/demo/stickers/VQsIiRGJb1xyR29.png"));
        stickerDummies.add(new Sticker_dummy(4, "https://muly.starthub.ltd/storage/demo/stickers/uMupGAtXaI2Yzm6.png"));
        stickerDummies.add(new Sticker_dummy(5, "https://muly.starthub.ltd/storage/demo/stickers/6MRpnln3q8DMTuC.png"));
        stickerDummies.add(new Sticker_dummy(6, "https://muly.starthub.ltd/storage/demo/stickers/r6oSVjkVNY9Opww.png"));
        stickerDummies.add(new Sticker_dummy(7, "https://muly.starthub.ltd/storage/demo/stickers/rcKJ3JIuBT6JQkL.png"));
        stickerDummies.add(new Sticker_dummy(8, "https://muly.starthub.ltd/storage/demo/stickers/vtJsNlyEUZvqEQb.png"));
        stickerDummies.add(new Sticker_dummy(9, "https://muly.starthub.ltd/storage/demo/stickers/dvRToewsl0vliMw.png"));
        stickerDummies.add(new Sticker_dummy(10, "https://muly.starthub.ltd/storage/demo/stickers/9N2gUIUDdwTsPAT.png"));

        return stickerDummies;
    }*/

    public static ArrayList<String> boysImage = new ArrayList<>(Arrays.asList(
            "https://images.unsplash.com/photo-1609637082285-1aa1e1a63c16?ixid=MnwxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8&ixlib=rb-1.2.1&auto=format&fit=crop&w=880&q=80",
            "https://images.unsplash.com/photo-1485528562718-2ae1c8419ae2?ixid=MnwxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8&ixlib=rb-1.2.1&auto=format&fit=crop&w=858&q=80",
            "https://images.unsplash.com/photo-1552774021-9ebbb764f03e?ixlib=rb-1.2.1&ixid=MnwxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8&auto=format&fit=crop&w=880&q=80",
            "https://images.unsplash.com/photo-1629189858155-9eb2649ec778?ixid=MnwxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8&ixlib=rb-1.2.1&auto=format&fit=crop&w=880&q=80",
            "https://images.unsplash.com/photo-1570211776086-5836c8b1e75f?ixid=MnwxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8&ixlib=rb-1.2.1&auto=format&fit=crop&w=880&q=80"
    ));

    public static List<Song_dummy> getSongFiles() {
        List<Song_dummy> songDummies = new ArrayList<>();
        songDummies.add(new Song_dummy(1, "Rahogi Meri", "Pritam, Arijit Singh",
                "https://muly.starthub.ltd/storage/demo/covers/BydL9iUJ1wRZAgYpng",
                "https://muly.starthub.ltd/storage/demo/audios/jrmyRx4Uwy3GkVy.aac", 14, ""));

        songDummies.add(new Song_dummy(2, "Coca Cola", "Pritam, Arijit Singh",
                "https://muly.starthub.ltd/storage/demo/covers/ZFpka7K6dxUAQCnpng",
                "https://muly.starthub.ltd/storage/demo/audios/jrmyRx4Uwy3GkVy.aac", 19, ""));

        songDummies.add(new Song_dummy(3, "Savage Love (Laxed - Siren Beat)", "Jawsh 685, Jason Derulo",
                "https://muly.starthub.ltd/storage/demo/covers/ZEybCPyhf0QcUZZpng",
                "https://muly.starthub.ltd/storage/demo/audios/93BahZERK0DOiiq.aac", 28, ""));

        songDummies.add(new Song_dummy(4, "Thumbi Thullal", "A. R. Rahman",
                "https://muly.starthub.ltd/storage/demo/covers/pU59tYWwgzC6Hi5png",
                "https://muly.starthub.ltd/storage/demo/audios/S3XXGz6YoTWwvaZ.aac", getRandomPostCoint(), ""));

        songDummies.add(new Song_dummy(5, "For the Night", "Pop Smoke, Lil Baby & DaBaby",
                "https://muly.starthub.ltd/storage/demo/covers/6XyPuIdF3PJmEICpng",
                "https://muly.starthub.ltd/storage/demo/audios/93BahZERK0DOiiq.aac", getRandomPostCoint(), ""));


        return songDummies;
    }

    public static List<String> girlsBio() {
        List<String> bios = new ArrayList<>();

        String bio1 = "Money can’t buy happiness. But it can buy Makeup!";
        String bio2 = "Sometimes it’s the princess who kills the dragon and saves the prince.";
        String bio3 = "love..dancing.\uD83D\uDE18\uD83D\uDE18\n" +
                "luv ❤my❤ friends\uD83D\uDC48";
        String bio4 = "\uD83D\uDCF7Like Photography✔\n" +
                "\uD83D\uDC01Animal Lover✔\n" +
                "\uD83C\uDF6CChocolate Lover✔\n" +
                "\uD83D\uDE2DFirst Cry On 11th March✔\n" +
                "\uD83D\uDC8AMedical Student✔\n";
        String bio5 = "I’m a princess \uD83D\uDC96, not because I have a Prince, but because my dad is a king \uD83D\uDC51\n";

        bios.add(bio1);
        bios.add(bio2);
        bios.add(bio3);
        bios.add(bio4);
        bios.add(bio5);

        return bios;
    }

    public static List<String> boysBio() {
        List<String> bios = new ArrayList<>();

        String bio1 = "\uD83D\uDCAFOfficial account\uD83D\uDD10\n" +
                "\uD83D\uDCF7Photography\uD83D\uDCF7\n" +
                "\uD83D\uDE18Music lover\uD83C\uDFB6\n" +
                "⚽Sports lover⛳\n" +
                "\uD83D\uDE0DSports bike lover\n";
        String bio2 = "\uD83D\uDC51Attitude Prince\uD83D\uDC51\n" +
                "\uD83E\uDD1DDosto Ki Shan\uD83D\uDC9C\n" +
                "\uD83D\uDC8FGF Ki Jaan♥️\n" +
                "\uD83D\uDC9EMom Ka Ladla\uD83D\uDC93\n" +
                "\uD83D\uDC99Pappa Ka Pyara\uD83D\uDC99";
        String bio3 = "love..dancing.\uD83D\uDE18\uD83D\uDE18\n" +
                "luv ❤my❤ friends\uD83D\uDC48";
        String bio4 = "\uD83D\uDCF7Like Photography✔\n" +
                "\uD83D\uDC01Animal Lover✔\n" +
                "\uD83C\uDF6CChocolate Lover✔\n" +
                "\uD83D\uDE2DFirst Cry On 11th March✔\n" +
                "\uD83D\uDC8AMedical Student✔\n";
        String bio5 = "༺❉MR. Perfect❉༻\n" +
                "\uD83D\uDCA5King OF 22 May\uD83C\uDF1F\n" +
                "\uD83C\uDFB5Music Addicted\uD83C\uDFB6\n" +
                "\uD83D\uDC9C Photography\uD83D\uDCF8\n" +
                "\uD83D\uDC95Heart Hã¢Kër\uD83D\uDC8C";

        bios.add(bio1);
        bios.add(bio2);
        bios.add(bio3);
        bios.add(bio4);
        bios.add(bio5);

        return bios;
    }

    public static List<UserRoot.User> getUsers(boolean isShuffle) {


        List<UserRoot.User> userDummies = new ArrayList<>(Arrays.asList(
                new UserRoot.User(10, "", false, 0, "India", "", false, "female", 10, 0, "", "hiiiii", true, 0, 0, null, "", 0, "null", "", null, "null", "alisha@gmail.com", "", girlsImage.get(0), false, null, null, "", false, "null", 10, 1, 2, "Alisha", "", 19, "Alisha", true, "12345678"),
                new UserRoot.User(0, "", false, 0, "USA", "", false, "female", 10, 0, "", "hello", true, 0, 0, null, "", 0, "null", "", null, "null", "amar@gmail.com", "", girlsImage.get(1), false, null, null, "", false, "null", 10, 1, 2, "Amar", "", 20, "amar", true, "45678912"),
                new UserRoot.User(0, "", false, 0, "India", "", false, "famale", 10, 0, "", "hiii", true, 0, 0, null, "", 0, "null", "", null, "null", "AaliyaMia@gmail.com", "", girlsImage.get(2), false, null, null, "", false, "null", 10, 1, 2, "Aaliya Mia", "", 18, "Aaliya Mia", true, "45678912"),
                new UserRoot.User(0, "", false, 0, "UK", "", false, "female", 10, 0, "", "hiiiii", true, 0, 0, null, "", 0, "null", "", null, "null", "prisha@gmail.com", "", girlsImage.get(3), false, null, null, "", false, "null", 10, 1, 2, "Prisha", "", 25, "Prisha", true, "45678912"),
                new UserRoot.User(0, "", false, 0, "GERMANY", "", false, "male", 10, 0, "", "hiiiii", true, 0, 0, null, "", 0, "null", "", null, "null", "DanielDavidson@gmail.com", "", girlsImage.get(4), false, null, null, "", false, "null", 10, 1, 2, "Daniel Davidson", "", 23, "Daniel Davidson", true, "45678912"),
                new UserRoot.User(0, "", false, 0, "FRANCE", "", false, "male", 10, 0, "", "hiiiii", true, 0, 0, null, "", 0, "null", "", null, "null", "JamesCarter@gmail.com", "", girlsImage.get(0), false, null, null, "", false, "null", 10, 1, 2, "James Carter", "", 23, "James Carter", true, "45678912"),
                new UserRoot.User(0, "", false, 0, "India", "", false, "male", 10, 0, "", "hiiiii", true, 0, 0, null, "", 0, "null", "", null, "null", "muskan@gmail.com", "", girlsImage.get(1), false, null, null, "", false, "null", 10, 1, 2, "Rihan", "", 23, "Rihan", true, "45678912"),
                new UserRoot.User(0, "", false, 0, "GERMANY", "", false, "female", 10, 0, "", "hiiiii", true, 0, 0, null, "", 0, "null", "", null, "null", "lily@gmail.com", "", girlsImage.get(2), false, null, null, "", false, "null", 10, 1, 2, "Lily", "", 23, "Lily", true, "45678912"),
                new UserRoot.User(0, "", false, 0, "USA", "", false, "female", 10, 0, "", "hiiiii", true, 0, 0, null, "", 0, "null", "", null, "null", "Kennedy@gmail.com", "", girlsImage.get(3), false, null, null, "", false, "null", 10, 1, 2, "Kennedy", "", 23, "Kennedy", true, "45678912"),
                new UserRoot.User(0, "", false, 0, "USA", "", false, "male", 10, 0, "", "hiiiii", true, 0, 0, null, "", 0, "null", "", null, "null", "Charlottebailey@gmail.com", "", girlsImage.get(4), false, null, null, "", false, "null", 10, 1, 2, "Charlotte Bailey", "", 23, "Charlotte Bailey", true, "45678912")
        ));
        if (isShuffle) {
            Collections.shuffle(userDummies);
        }
        return userDummies;
    }

    public static int getRandomCoin() {
        Random random = new Random();
        int i = random.nextInt(1000 - 111) + 111;
        return i;
    }

    public static int getRandomPostCoint() {
        Random random = new Random();
        int i = random.nextInt(100 - 11) + 11;
        return i;
    }

    public static List<LiveStramComment> getLiveStreamComment() {
        List<LiveStramComment> liveStramCommentDummies = new ArrayList<>();

        liveStramCommentDummies.add(new LiveStramComment("Please stop looking so hot every time.", getUsers(true).get(0), true, null, "", "comment", ""));
        liveStramCommentDummies.add(new LiveStramComment("Your hotness is just beating me everytim.", getUsers(true).get(0), true, null, "", "comment", ""));
        liveStramCommentDummies.add(new LiveStramComment("Give me your mobile number", getUsers(true).get(0), true, null, "", "comment", ""));
        liveStramCommentDummies.add(new LiveStramComment("Every single part of your body was made according to my spec.", getUsers(true).get(0), true, null, "", "comment", ""));
        liveStramCommentDummies.add(new LiveStramComment("9975537455 it is my mobile number", getUsers(true).get(0), true, null, "", "comment", ""));
        liveStramCommentDummies.add(new LiveStramComment("Please stop looking so hot every time.", getUsers(true).get(0), false, null, "", "comment", ""));
        liveStramCommentDummies.add(new LiveStramComment("Looking very very hot\uD83D\uDD25in summer", getUsers(true).get(0), false, null, "", "comment", ""));
        liveStramCommentDummies.add(new LiveStramComment("Your queenly smiles are what my eyes have been longing to see.", getUsers(true).get(0), false, null, "", "comment", ""));
        liveStramCommentDummies.add(new LiveStramComment("Too hot for me to handle", getUsers(true).get(0), false, null, "", "comment", ""));
        liveStramCommentDummies.add(new LiveStramComment("Every single part of your body was made according to my spec.", getUsers(true).get(0), false, null, "", "comment", ""));
        liveStramCommentDummies.add(new LiveStramComment("I drop my cap for you.", getUsers(true).get(0), false, null, "", "comment", ""));
        liveStramCommentDummies.add(new LiveStramComment("Your hotness is just beating me everytim.", getUsers(true).get(0), false, null, "", "comment", ""));
        liveStramCommentDummies.add(new LiveStramComment("Classy shot and awesome background too.", getUsers(true).get(0), false, null, "", "comment", ""));
        liveStramCommentDummies.add(new LiveStramComment("Hello dear,", getUsers(true).get(0), false, null, "", "comment", ""));
        liveStramCommentDummies.add(new LiveStramComment("Give me your mobile number", getUsers(true).get(0), false, null, "", "comment", ""));
        liveStramCommentDummies.add(new LiveStramComment("9975537455 it is my mobile number", getUsers(true).get(0), false, null, "", "comment", ""));
        Collections.shuffle(liveStramCommentDummies);
        return liveStramCommentDummies;

    }

    public static List<CoinPlan_dummy> getCoinList() {
        List<CoinPlan_dummy> coinPlans = new ArrayList<>();
        coinPlans.add(new CoinPlan_dummy(100, 10, ""));
        coinPlans.add(new CoinPlan_dummy(200, 20, ""));
        coinPlans.add(new CoinPlan_dummy(1000, 90, "10% off"));
        coinPlans.add(new CoinPlan_dummy(10000, 800, "20% off"));
        coinPlans.add(new CoinPlan_dummy(50000, 2500, "50% off"));
        return coinPlans;
    }

    public static List<String> getFemaleVideos() {
        List<String> videos = new ArrayList<>(Arrays.asList(
                "https://dev.digicean.com/storage/1614063597527.mp4",
                "https://dev.digicean.com/storage/1%20(14).mp4",
                "https://dev.digicean.com/storage/1%20(22).mp4",
                "https://dev.digicean.com/storage/1%20(5).mp4",
                "https://dev.digicean.com/storage/1%20(4).mp4"
        ));
        Collections.shuffle(videos);
        return videos;
    }

    public static List<String> getHashtags() {
        List<String> videos = new ArrayList<>(Arrays.asList(
                "#Love", "#Nature", "#Wedding", "#Alone", "#Female", "#Chill", "#Beauty", "#Life", "#Honeymoon", "#Style", "#Happy", "#Smile", "#Music", "#Sunset", "#Sport"
        ));
        Collections.shuffle(videos);
        return videos;
    }

    public static List<String> getLocations() {
        List<String> videos = new ArrayList<>(Arrays.asList(
                "Surat, India", "London, England", "Paris, France", "New York City, United States", "Moscow, Russia",
                "Tokyo, Japan", "Los Angeles, United States", " Barcelona, Spain"
                , "Madrid, Spain", "Rome, Italy", "Doha, Qatar", "Chicago, United States", "Abu Dhabi, UAE"
                , "San Francisco, US", "Amsterdam, Netherlands", "Delhi, India ", "Mumbai, India", "Bangalore, India"
        ));
        Collections.shuffle(videos);
        return videos;
    }

    public static List<GiftCategory_dummy> getGiftCategory() {
        List<GiftRoot_dummy> emoji = new ArrayList<>(Arrays.asList(
                new GiftRoot_dummy(1, R.raw.emoji, 10, GiftRoot_dummy.IMAGE),
                new GiftRoot_dummy(2, R.raw.emoji1, 10, GiftRoot_dummy.IMAGE),
                new GiftRoot_dummy(3, R.raw.emoji2, 10, GiftRoot_dummy.IMAGE),
                new GiftRoot_dummy(6, R.raw.party, 10, GiftRoot_dummy.IMAGE),
                new GiftRoot_dummy(7, R.raw.star, 10, GiftRoot_dummy.IMAGE),
                new GiftRoot_dummy(8, R.raw.wink, 10, GiftRoot_dummy.IMAGE),
                new GiftRoot_dummy(9, R.raw.wow, 10, GiftRoot_dummy.IMAGE)


        ));
        List<GiftRoot_dummy> love = new ArrayList<>(Arrays.asList(
                new GiftRoot_dummy(5, R.raw.heart, 10, GiftRoot_dummy.IMAGE),
                new GiftRoot_dummy(16, R.raw.s116, 10, GiftRoot_dummy.IMAGE),
                new GiftRoot_dummy(13, R.raw.s113, 10, GiftRoot_dummy.IMAGE),
                new GiftRoot_dummy(14, R.raw.s114, 10, GiftRoot_dummy.IMAGE),
                new GiftRoot_dummy(18, R.raw.s118, 10, GiftRoot_dummy.IMAGE),
                new GiftRoot_dummy(33, R.raw.srose1, 10, GiftRoot_dummy.IMAGE)


        ));
        List<GiftRoot_dummy> sticker = new ArrayList<>(Arrays.asList(
                new GiftRoot_dummy(4, R.raw.g_fox, 10, GiftRoot_dummy.IMAGE),
                new GiftRoot_dummy(10, R.raw.s110, 10, GiftRoot_dummy.IMAGE),
                new GiftRoot_dummy(11, R.raw.s111, 10, GiftRoot_dummy.IMAGE),
                new GiftRoot_dummy(15, R.raw.s115, 10, GiftRoot_dummy.IMAGE),
                new GiftRoot_dummy(17, R.raw.s117, 10, GiftRoot_dummy.IMAGE),
                new GiftRoot_dummy(19, R.raw.s119, 10, GiftRoot_dummy.IMAGE),
                new GiftRoot_dummy(20, R.raw.s120, 10, GiftRoot_dummy.IMAGE),
                new GiftRoot_dummy(12, R.raw.s112, 10, GiftRoot_dummy.IMAGE),
                new GiftRoot_dummy(21, R.raw.s121, 10, GiftRoot_dummy.IMAGE),
                new GiftRoot_dummy(22, R.raw.s122, 10, GiftRoot_dummy.IMAGE),
                new GiftRoot_dummy(23, R.raw.s123, 10, GiftRoot_dummy.IMAGE),
                new GiftRoot_dummy(24, R.raw.s124, 10, GiftRoot_dummy.IMAGE),
                new GiftRoot_dummy(25, R.raw.s125, 10, GiftRoot_dummy.IMAGE),
                new GiftRoot_dummy(26, R.raw.s126, 10, GiftRoot_dummy.IMAGE),
                new GiftRoot_dummy(27, R.raw.s127, 10, GiftRoot_dummy.IMAGE),
                new GiftRoot_dummy(28, R.raw.s128, 10, GiftRoot_dummy.IMAGE),
                new GiftRoot_dummy(29, R.raw.s129, 10, GiftRoot_dummy.IMAGE),
                new GiftRoot_dummy(30, R.raw.srose, 10, GiftRoot_dummy.IMAGE),
                new GiftRoot_dummy(31, R.raw.s130, 10, GiftRoot_dummy.IMAGE),
                new GiftRoot_dummy(32, R.raw.s131, 10, GiftRoot_dummy.IMAGE)


        ));
        Collections.shuffle(love);
        Collections.shuffle(emoji);
        Collections.shuffle(sticker);

        List<GiftCategory_dummy> giftCategories = new ArrayList<>();
        giftCategories.add(new GiftCategory_dummy("Sticker", sticker));
        giftCategories.add(new GiftCategory_dummy("Emoji", emoji));
        giftCategories.add(new GiftCategory_dummy("Love", love));

        //sticker

        // Collections.shuffle(list);
        return giftCategories;
    }
    public static List<FakeGiftRoot> getRandomGiftList() {
        List<FakeGiftRoot> giftlist = new ArrayList<>();

        giftlist.add(new FakeGiftRoot(4, R.raw.g_fox, 10, FakeGiftRoot.IMAGE));
        giftlist.add(new FakeGiftRoot(2, R.raw.emoji1, 10, FakeGiftRoot.IMAGE));
        giftlist.add(new FakeGiftRoot(3, R.raw.emoji2, 10, FakeGiftRoot.IMAGE));
        giftlist.add(new FakeGiftRoot(6, R.raw.party, 10, FakeGiftRoot.IMAGE));
        giftlist.add(new FakeGiftRoot(21, R.raw.s121, 20, FakeGiftRoot.IMAGE));
        giftlist.add(new FakeGiftRoot(22, R.raw.s122, 20, FakeGiftRoot.IMAGE));
        giftlist.add(new FakeGiftRoot(23, R.raw.s123, 20, FakeGiftRoot.IMAGE));
        giftlist.add(new FakeGiftRoot(29, R.raw.s129, 20, FakeGiftRoot.IMAGE));
        giftlist.add(new FakeGiftRoot(30, R.raw.srose, 10, FakeGiftRoot.IMAGE));
        giftlist.add(new FakeGiftRoot(31, R.raw.s130, 10, FakeGiftRoot.IMAGE));
        giftlist.add(new FakeGiftRoot(32, R.raw.s131, 10, FakeGiftRoot.IMAGE));

        Collections.shuffle(giftlist);

        return giftlist;
    }
}
