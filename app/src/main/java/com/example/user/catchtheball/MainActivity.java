package com.example.user.catchtheball;

import android.content.Intent;
import android.graphics.Point;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Display;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends AppCompatActivity {

    private TextView scoreLabel,startLabel;
    private ImageView box,orange,black,pink;
    private MainActivity mainActivity;

    //Size
    private int frameHeight;
    private int boxSize;
    private int screenWidth;
    private int screenHeight;


    //Position
    private int boxY;
    private int orangeX;
    private int orangeY;
    private int blackX;
    private int blackY;
    private int pinkX;
    private int pinkY;

    //Speed
    private int boxSpeed;
    private int orangeSpeed;
    private int blackSpeed;
    private int pinkSpeed;

    //Score
    private int score = 0;

    //Initialize class
    private Handler handler = new Handler();
    private Timer timer = new Timer();
    private SoundPlayer soundPlayer;

    //Status check
    private boolean action_flg = false;
    private boolean start_flg = false;

    @Override

    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        soundPlayer = new SoundPlayer(this);

        scoreLabel = (TextView) findViewById(R.id.scoreLabel);
        startLabel = (TextView) findViewById(R.id.startLabel);

        box = (ImageView) findViewById(R.id.box);
        orange = (ImageView) findViewById(R.id.orange);
        black = (ImageView) findViewById(R.id.black);
        pink = (ImageView) findViewById(R.id.pink);

        //Get screen size
        WindowManager wm = getWindowManager();
        Display disp = wm.getDefaultDisplay();
        Point size = new Point();
        disp.getSize(size);

        screenWidth = size.x;
        screenHeight = size.y;

        //Now
        //Nexus-4 width:768 height:1184
        //Speed box:20 orange:12 pink:20 black:16

        boxSpeed = Math.round(screenHeight / 60F); // 1184 / 60 = 19.733... => 20
        orangeSpeed = Math.round(screenWidth / 60F); // 768 / 60 = 12.8... => 13
        pinkSpeed = Math.round(screenWidth / 36F); // 768 / 36 = 21.333... => 21
        blackSpeed = Math.round(screenWidth / 45F); // 768 / 45 = 17.06... => 17

        Log.v("SPEED_BOX", + boxSpeed + "");
        Log.v("SPEED_ORANGE", + orangeSpeed + "");
        Log.v("SPEED_PINK", + pinkSpeed + "");
        Log.v("SPEED_BLACK", + blackSpeed + "");

        //Move to out of screen
        orange.setX(-80);
        orange.setY(-80);
        black.setX(-80);
        black.setY(-80);
        pink.setX(-80);
        pink.setY(-80);

        scoreLabel.setText("Score : 0");
    }

    public void changePos() {

        hitCheck();

        //Orange ball
        orangeX -= orangeSpeed;
        if (orangeX < 0) {

            orangeX = screenWidth + 20;
            orangeY = (int) Math.floor(Math.random() * (frameHeight - orange.getHeight()));
        }
        orange.setX(orangeX);
        orange.setY(orangeY);

        //Black ball
        blackX -= blackSpeed;
        if (blackX < 0) {

            blackX = screenWidth + 10;
            blackY = (int) Math.floor(Math.random() * (frameHeight - black.getHeight()));
        }
        black.setX(blackX);
        black.setY(blackY);

        //Pink ball
        pinkX -= pinkSpeed;
        if (pinkX < 0) {

            pinkX = screenWidth + 5000;
            pinkY = (int) Math.floor(Math.random() * (frameHeight - pink.getHeight()));
        }
        pink.setX(pinkX);
        pink.setY(pinkY);

        //Move box
        if (action_flg == true) {

            //Touching
            boxY -= boxSpeed;
        } else {

            //Releasing
            boxY += 20;
        }

        //Check box position
        if (boxY < 0)
            boxY = 0;
        if (boxY > frameHeight - boxSize)
            boxY = frameHeight - boxSize;
        box.setY(boxY);

        scoreLabel.setText("Score : " + score);
    }

    public void hitCheck() {

        //If the center of the ball is in the box, it counts as a hit.
        //Orange
        int orangeCenterX = orangeX + orange.getWidth() / 2;
        int orangeCenterY = orangeY + orange.getHeight() / 2;

        //0 <= orangeCenterX <= boxWidth
        //boxY <= orangeCenter <= boxY + boxHeight
        if (0 <= orangeCenterX && orangeCenterX <= boxSize && boxY <= orangeCenterY && orangeCenterY <= boxY + boxSize) {

            score += 10;
            orangeX = -10;
            soundPlayer.playHitSound();
        }

        //Pink
        int pinkCenterX = pinkX + pink.getWidth() / 2;
        int pinkCenterY = pinkY + pink.getHeight() / 2;

        //0 <= pinkCenterX <= boxWidth
        //boxY <= pinkCenter <= boxY + boxHeight
        if (0 <= pinkCenterX && pinkCenterX <= boxSize && boxY <= pinkCenterY && pinkCenterY <= boxY + boxSize) {

            score += 30;
            pinkX = -10;
            soundPlayer.playHitSound();
        }

        //Pink
        int blackCenterX = blackX + black.getWidth() / 2;
        int blackCenterY = blackY + black.getHeight() / 2;

        //0 <= blackCenterX <= boxWidth
        //boxY <= blackCenter <= boxY + boxHeight
        if (0 <= blackCenterX && blackCenterX <= boxSize && boxY <= blackCenterY && blackCenterY <= boxY + boxSize) {

            //Stop timer
            timer.cancel();
            timer = null;
            soundPlayer.playOverSound();

            //Show Result
            Intent intent = new Intent(getApplicationContext(), ResultActivity.class);
            intent.putExtra("SCORE", score);
            startActivity(intent);
        }
    }

    public boolean onTouchEvent(MotionEvent me) {

        if (start_flg == false) {

            start_flg = true;

            //Why get frame height and box height here?
            //Because the UI has not been set on the screen in method onCreate();!!

            FrameLayout frame = (FrameLayout) findViewById(R.id.frame);
            frameHeight = frame.getHeight();

            boxY = (int) box.getY();

            //The box is a squire(height and width are the same.)
            boxSize = box.getHeight();

            startLabel.setVisibility(View.GONE);

            timer.schedule(new TimerTask() {
                @Override
                public void run() {
                    handler.post(new Runnable() {
                        @Override
                        public void run() {

                            changePos();
                        }
                    });
                }
            }, 0, 20);
        }
        else {

            if(me.getAction() == MotionEvent.ACTION_DOWN) {

                action_flg = true;
            }
            else if(me.getAction() == MotionEvent.ACTION_UP) {

                action_flg = false;
            }
        }

        return true;
    }

    @Override

    public boolean dispatchKeyEvent(KeyEvent event) {

        if (event.getAction() == KeyEvent.ACTION_DOWN) {

            switch (event.getKeyCode()) {

                case KeyEvent.KEYCODE_BACK:

                    return true;
            }
        }

        return super.dispatchKeyEvent(event);
    }
}
