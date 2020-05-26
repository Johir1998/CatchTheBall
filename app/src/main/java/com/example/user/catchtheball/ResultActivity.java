package com.example.user.catchtheball;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.TextView;

public class ResultActivity extends AppCompatActivity {

    private TextView scoreLabel;
    private TextView highScoreLabel;

    @Override

    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result);

        scoreLabel = (TextView) findViewById(R.id.scoreLabel);
        highScoreLabel = (TextView) findViewById(R.id.highScoreLabel);

        int Score = getIntent().getIntExtra("SCORE", 0);
        scoreLabel.setText("Score : " + Score + "");

        SharedPreferences settings = getSharedPreferences("GAME_DATA", Context.MODE_PRIVATE);
        int highScore = settings.getInt("HIGH_SCORE", 0);

        if (Score > highScore) {

            highScoreLabel.setText("High Score : " + Score);

            //Save HighScore
            SharedPreferences.Editor editor = settings.edit();
            editor.putInt("HIGH_SCORE", Score);
            editor.commit();
        } else {

            highScoreLabel.setText("High Score : " + highScore);
        }
    }

    public void tryAgain(View view) {
        startActivity(new Intent(getApplicationContext(), StartActivity.class));
    }

    //Disable Return Button

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
