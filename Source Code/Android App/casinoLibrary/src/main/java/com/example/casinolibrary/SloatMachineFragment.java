package com.example.casinolibrary;

import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.core.content.res.ResourcesCompat;

import androidx.lifecycle.ViewModelProvider;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.media.MediaPlayer;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;


import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

public class SloatMachineFragment extends BottomSheetDialogFragment {

    private static final String TAG = "SloatMachineFragment";

    private SloatMachineViewModel mViewModel;
    private int comboNumber = 7;
    private int coef1 = 72;
    private int coef2 = 142;
    private int coef3 = 212;
    private int position1 = 5;
    private int position2 = 5;
    private int position3 = 5;
    private int[] slot = {1, 2, 3, 4, 5, 6, 7};

    private RecyclerView rv1;
    private RecyclerView rv2;
    private RecyclerView rv3;
    private SpinnerAdapter adapter;
    private CustomLayoutManager layoutManager1;
    private CustomLayoutManager layoutManager2;
    private CustomLayoutManager layoutManager3;

    private ImageButton spinButton;
    private ImageButton plusButton;
    private ImageButton minusButton;


    private TextView myCoins;
    private TextView lines;
    private TextView bet;



    private boolean firstRun;



    private Game_Logic gameLogic;

    private boolean soundOn, soundOn1;
    private SharedPreferences pref;
    public MediaPlayer mp,win,bgsound;
    private int countads;
    public static final String PREFS_NAME = "FirstRun";
    private int currentUserCoin=0;

    public OnSlotMachineGameListner getOnSlotMachineGameListner() {
        return onSlotMachineGameListner;
    }

    public void setOnSlotMachineGameListner(OnSlotMachineGameListner onSlotMachineGameListner) {
        this.onSlotMachineGameListner = onSlotMachineGameListner;
    }

    public OnSlotMachineGameListner onSlotMachineGameListner;

    private int playmusic, playsound;
    private ImageView music_off, music_on , soundon, soundoff;
    private View view;

    @Override
    public void onStart() {
        super.onStart();
        Dialog dialog = getDialog();
        if (dialog != null) {
            View bottomSheet = dialog.findViewById(R.id.design_bottom_sheet);
            bottomSheet.getLayoutParams().height = 1300;
        }
        View view = getView();
        view.post(() -> {
            View parent = (View) view.getParent();
            CoordinatorLayout.LayoutParams params = (CoordinatorLayout.LayoutParams) (parent).getLayoutParams();
            CoordinatorLayout.Behavior behavior = params.getBehavior();
            BottomSheetBehavior bottomSheetBehavior = (BottomSheetBehavior) behavior;
            bottomSheetBehavior.setPeekHeight(view.getMeasuredHeight());
        });
    }

    @Override
    public void setStyle(int style, int theme) {
        super.setStyle(style, theme);

    }

    @Override
    public void onDismiss(@NonNull DialogInterface dialog) {
        super.onDismiss(dialog);
        Log.d(TAG, "onDismiss: ");
        if (mp!=null){
            mp.pause();

        }
        if (bgsound!=null){
            bgsound.pause();
        }
    }

    @Override
    public int getTheme() {
        return R.style.CustomBottomSheetDialogTheme;
    }

    public static SloatMachineFragment newInstance() {
        return new SloatMachineFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        view=inflater.inflate(R.layout.fragment_sloat_machine, container, false);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mViewModel = new ViewModelProvider(this).get(SloatMachineViewModel.class);
        final Typeface typeface = ResourcesCompat.getFont(getActivity(), R.font.font);


        bgsound = MediaPlayer.create(getActivity(),R.raw.bg_music);
        bgsound.setLooping(true);
        mp = MediaPlayer.create(getActivity(), R.raw.spin);
        win = MediaPlayer.create(getActivity(), R.raw.win);

        pref = getActivity().getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        firstRun = pref.getBoolean("firstRun", true);



        if (firstRun) {
            playmusic = 1;
            playsound = 1;
            SharedPreferences.Editor editor = pref.edit();
            editor.putBoolean("firstRun", false);
            editor.apply();
        } else {
            playmusic= pref.getInt("music", 1);
            playsound = pref.getInt("sound", 1);
            checkmusic();

        }

        Log.d("MUSIC",String.valueOf(playmusic));



        //Initializations
        gameLogic = new Game_Logic();

        spinButton = view.findViewById(R.id.spinButton);
        plusButton = view.findViewById(R.id.plusButton);
        minusButton = view.findViewById(R.id.minusButton);


        myCoins = view.findViewById(R.id.myCoins);
        myCoins.setTypeface(typeface);
        bet = view.findViewById(R.id.bet);
        bet.setTypeface(typeface);
        adapter = new SpinnerAdapter();

        //RecyclerView settings
        rv1 = view.findViewById(R.id.spinner1);
        rv2 = view.findViewById(R.id.spinner2);
        rv3 = view.findViewById(R.id.spinner3);
        rv1.setHasFixedSize(true);
        rv2.setHasFixedSize(true);
        rv3.setHasFixedSize(true);


        layoutManager1 = new CustomLayoutManager(getActivity());
        layoutManager1.setScrollEnabled(false);
        rv1.setLayoutManager(layoutManager1);
        layoutManager2 = new CustomLayoutManager(getActivity());
        layoutManager2.setScrollEnabled(false);
        rv2.setLayoutManager(layoutManager2);
        layoutManager3 = new CustomLayoutManager(getActivity());
        layoutManager3.setScrollEnabled(false);
        rv3.setLayoutManager(layoutManager3);

        rv1.setAdapter(adapter);
        rv2.setAdapter(adapter);
        rv3.setAdapter(adapter);
        rv1.scrollToPosition(position1);
        rv2.scrollToPosition(position2);
        rv3.scrollToPosition(position3);

        gameLogic.setMyCoins(currentUserCoin);
        gameLogic.setBet(5);
        gameLogic.setJackpot(0);

        updateText();

        //RecyclerView listeners
        rv1.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                switch (newState) {
                    case RecyclerView.SCROLL_STATE_IDLE:
                        rv1.scrollToPosition(gameLogic.getPosition(0));
                        layoutManager1.setScrollEnabled(false);
                }
            }
        });

        rv2.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                switch (newState) {
                    case RecyclerView.SCROLL_STATE_IDLE:
                        rv2.scrollToPosition(gameLogic.getPosition(1));
                        layoutManager2.setScrollEnabled(false);
                }
            }
        });

        rv3.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                switch (newState) {
                    case RecyclerView.SCROLL_STATE_IDLE:
                        rv3.scrollToPosition(gameLogic.getPosition(2));
                        layoutManager3.setScrollEnabled(false);
                        updateText();
                        if (gameLogic.getHasWon()) {
                            if(playsound == 1){
                                win.start();
                            }
                            countads ++;


                            LayoutInflater inflater = getLayoutInflater();
                            View layout = inflater.inflate(R.layout.win_splash,
                                    (ViewGroup) view.findViewById(R.id.win_splash));
                            TextView winCoins = layout.findViewById(R.id.win_coins);
                            winCoins.setTypeface(typeface);
                            winCoins.setText(gameLogic.getPrize());
                            Toast toast = new Toast(getActivity());
                            toast.setDuration(Toast.LENGTH_SHORT);
                            toast.setGravity(Gravity.CENTER_VERTICAL, 0, 0);
                            toast.setView(layout);
                            toast.show();
                            gameLogic.setHasWon(false);

onSlotMachineGameListner.onUserWin(Integer.parseInt(gameLogic.getPrize()));
onSlotMachineGameListner.onUserCoinUpdated(Integer.parseInt(gameLogic.getMyCoins()));
                        }else {
onSlotMachineGameListner.onUserLoss(Integer.parseInt(gameLogic.getBet()));
                            onSlotMachineGameListner.onUserCoinUpdated(Integer.parseInt(gameLogic.getMyCoins()));
                        }
                }
            }
        });

        //Button listeners
        spinButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(playsound == 1){
                    mp.start();
                }
                layoutManager1.setScrollEnabled(true);
                layoutManager2.setScrollEnabled(true);
                layoutManager3.setScrollEnabled(true);
                gameLogic.getSpinResults();
                position1 = gameLogic.getPosition(0) + coef1;
                position2 = gameLogic.getPosition(1) + coef2;
                position3 = gameLogic.getPosition(2) + coef3;
                rv1.smoothScrollToPosition(position1);
                rv2.smoothScrollToPosition(position2);
                rv3.smoothScrollToPosition(position3);

                onSlotMachineGameListner.onUserBet(Integer.parseInt(gameLogic.getBet()));
                onSlotMachineGameListner.onUserCoinUpdated(Integer.parseInt(gameLogic.getMyCoins()));
            }
        });

        plusButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(playsound == 1){
                    mp.start();
                }
                gameLogic.betUp();
                updateText();
            }
        });

        minusButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(playsound == 1){
                    mp.start();
                }
                gameLogic.betDown();
                updateText();
            }
        });

     /*   settingsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(playsound == 1){
                    mp.start();
                }
                ShowSettingsDialog();
            }
        });
*/

    }
    private void checkmusicdraw(){
        if (playmusic == 1){
            music_on.setVisibility(View.VISIBLE);
            music_off.setVisibility(View.INVISIBLE);
        }
        else {
            music_on.setVisibility(View.INVISIBLE);
            music_off.setVisibility(View.VISIBLE);
        }
    }

    private void checksounddraw(){
        if (playsound == 1){
            soundon.setVisibility(View.VISIBLE);
            soundoff.setVisibility(View.INVISIBLE);
        }
        else {
            soundon.setVisibility(View.INVISIBLE);
            soundoff.setVisibility(View.VISIBLE);
        }
    }




    private void updateText() {
        //jackpot.setText(gameLogic.getJackpot());
        myCoins.setText(gameLogic.getMyCoins());
        bet.setText(gameLogic.getBet());



    }


    private void checkmusic(){
        if (playmusic == 1){
            bgsound.start();
        }
        else {
            bgsound.pause();
        }

    }

    public void setLocalUserCoin(int diamond) {
        this.currentUserCoin=diamond;
      //  updateText();
    }

    private class ItemViewHolder extends RecyclerView.ViewHolder {

        ImageView pic;

        public ItemViewHolder(View itemView) {
            super(itemView);
            pic = itemView.findViewById(R.id.spinner_item);
        }
    }

    private class SpinnerAdapter extends RecyclerView.Adapter<ItemViewHolder> {

        @NonNull
        @Override
        public ItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            LayoutInflater layoutInflater = LayoutInflater.from(getActivity());
            View view = layoutInflater.inflate(R.layout.spinner_item_game, parent, false);
            return new ItemViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull ItemViewHolder holder, int position) {
            int i = position < 7 ? position : position % comboNumber;
            switch (slot[i]) {
                case 1:
                    holder.pic.setImageResource(R.drawable.combination_1);
                    break;
                case 2:
                    holder.pic.setImageResource(R.drawable.combination_2);
                    break;
                case 3:
                    holder.pic.setImageResource(R.drawable.combination_3);
                    break;
                case 4:
                    holder.pic.setImageResource(R.drawable.combination_4);
                    break;
                case 5:
                    holder.pic.setImageResource(R.drawable.combination_5);
                    break;
                case 6:
                    holder.pic.setImageResource(R.drawable.combination_6);
                    break;
                case 7:
                    holder.pic.setImageResource(R.drawable.combination_7);
                    break;
            }

        }

        @Override
        public int getItemCount() {
            return Integer.MAX_VALUE;
        }
    }

    public interface OnSlotMachineGameListner{
        public void onUserCoinUpdated(int updatedCoin);
        public void onUserWin(int winCoin);
        public void onUserBet(int betCoin);
        public void onUserLoss(int lossCoin);
    }
}