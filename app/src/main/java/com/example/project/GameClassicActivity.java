package com.example.project;

import android.os.Bundle;
import android.widget.GridLayout;
import android.widget.ImageButton;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

public class GameClassicActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.game_classic);
        GridLayout gridBoard = findViewById(R.id.gridBoard);

        // set up game
        for (int row = 0; row < 5; row++) {
            for (int col = 0; col < 5; col++) {

                ImageButton tile = new ImageButton(this);
                tile.setImageResource(R.drawable.unrevealed_tile);

                GridLayout.LayoutParams params =
                        new GridLayout.LayoutParams();

                params.width = 100;
                params.height = 100;

                tile.setLayoutParams(params);

                tile.setPadding(0, 0, 0, 0);
                tile.setBackground(null);
                tile.setScaleType(ImageView.ScaleType.FIT_XY);
                gridBoard.addView(tile);
            }
        }
    }
}
