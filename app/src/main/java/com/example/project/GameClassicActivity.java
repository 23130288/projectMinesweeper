package com.example.project;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.DisplayMetrics;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.GridLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.project.game.NewGameDialog;

import java.util.Locale;

import Model.Game;

public class GameClassicActivity extends AppCompatActivity {
    FrameLayout boardContainer;
    GridLayout gridBoard;

    private ScaleGestureDetector scaleDetector;
    private float scaleFactor = 1.0f;
    private float lastX;
    private float lastY;
    private boolean isDragging = false;
    private boolean isScaling = false;
    Game game;

    private Runnable timerRunnable;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.game_classic);

        int rows = getIntent().getIntExtra("rows", 9);
        int cols = getIntent().getIntExtra("columns", 9);
        int mines = getIntent().getIntExtra("mines", 10);

        game = new Game();
        game.setUpGame(rows, cols, mines);

        ImageView btnReset = findViewById(R.id.btnReset);
        btnReset.setOnClickListener(v -> {
            NewGameDialog dialog = new NewGameDialog();dialog.show(getSupportFragmentManager(), "NEW_GAME_DIALOG");
        });

        // set up zoom, dragging
        boardContainer = findViewById(R.id.boardContainer);
        gridBoard = findViewById(R.id.gridBoard);
        ImageView backgroundImage = findViewById(R.id.backgroundImage);
        scaleDetector = new ScaleGestureDetector(this, new ScaleGestureDetector.SimpleOnScaleGestureListener() {
            @Override
            public boolean onScaleBegin(@NonNull ScaleGestureDetector detector) {
                isScaling = true;
                return true;
            }
            @Override
            public boolean onScale(@NonNull ScaleGestureDetector detector) {
                scaleFactor *= detector.getScaleFactor();
                scaleFactor = Math.max(1.0f, Math.min(scaleFactor, 4.0f));
                gridBoard.setScaleX(scaleFactor);
                gridBoard.setScaleY(scaleFactor);
                if (scaleFactor > 1.0f) {
                    backgroundImage.setVisibility(View.GONE);
                } else {
                    backgroundImage.setVisibility(View.VISIBLE);
                }
                limitTranslation();
                return true;
            }
            @Override
            public void onScaleEnd(@NonNull ScaleGestureDetector detector) {
                isScaling = false;
            }
        });
        gridBoard.setRowCount(rows);
        gridBoard.setColumnCount(cols);

        DisplayMetrics dm = getResources().getDisplayMetrics();
        int margin = (int) (10 * dm.density * 2);
        int screenWidth = dm.widthPixels - margin;
        int tileSize = screenWidth / cols;
        // set up game
        for (int row = 0; row < rows; row++) {
            for (int col = 0; col < cols; col++) {
                ImageButton tile = new ImageButton(this);
                tile.setImageResource(R.drawable.unrevealed_tile);
                GridLayout.LayoutParams params = new GridLayout.LayoutParams();

                params.width = tileSize;
                params.height = tileSize;

                tile.setLayoutParams(params);
                tile.setPadding(0, 0, 0, 0);
                tile.setBackground(null);
                tile.setScaleType(ImageView.ScaleType.CENTER_CROP);

                int finalRow = row;
                int finalCol = col;
                tile.setOnClickListener(v -> openTile(finalRow, finalCol));
                gridBoard.addView(tile);
            }
        }

        // set up flags
        TextView txtBombs = findViewById(R.id.txtBombs);
        txtBombs.setText(String.format(Locale.getDefault(), "%03d", game.getFlags()));

        // set up time
        TextView txtTime = findViewById(R.id.txtTime);
        Handler handler = new Handler(Looper.getMainLooper());
        timerRunnable = new Runnable() {
            @Override
            public void run() {
                game.increaseTime();
                txtTime.setText(String.format(Locale.getDefault(), "%03d", Math.min(game.getTime(), 999)));
                handler.postDelayed(this, 1000);
            }
        };
    }

    private void openTile(int row, int col) {
        if (game.isFirstHit()) {
            game.setUpBombs(row, col);
            timerRunnable.run();
        }
        game.hitTile(row, col);
    }

    private void limitTranslation() {
        float scaledWidth = gridBoard.getWidth() * scaleFactor;
        float scaledHeight = gridBoard.getHeight() * scaleFactor;
        float maxX = Math.max(0, (scaledWidth - boardContainer.getWidth()) / 2f);
        float maxY = Math.max(0, (scaledHeight - boardContainer.getHeight()) / 2f);
        float x = gridBoard.getTranslationX();
        float y = gridBoard.getTranslationY();
        x = Math.max(-maxX, Math.min(x, maxX));
        y = Math.max(-maxY, Math.min(y, maxY));
        gridBoard.setTranslationX(x);
        gridBoard.setTranslationY(y);
    }


    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        scaleDetector.onTouchEvent(ev);
        switch (ev.getActionMasked()) {
            case MotionEvent.ACTION_DOWN:
                lastX = ev.getRawX();
                lastY = ev.getRawY();
                isDragging = true;
                break;
            case MotionEvent.ACTION_MOVE:
                if (!isScaling && ev.getPointerCount() == 1 && isDragging) {
                    float dx = ev.getRawX() - lastX;
                    float dy = ev.getRawY() - lastY;

                    float newX = gridBoard.getTranslationX() + dx;
                    float newY = gridBoard.getTranslationY() + dy;
                    float scaledWidth = gridBoard.getWidth() * scaleFactor;
                    float scaledHeight = gridBoard.getHeight() * scaleFactor;
                    float maxX = Math.max(0, (scaledWidth - boardContainer.getWidth()) / 2f);
                    float maxY = Math.max(0, (scaledHeight - boardContainer.getHeight()) / 2f);
                    newX = Math.max(-maxX, Math.min(newX, maxX));
                    newY = Math.max(-maxY, Math.min(newY, maxY));

                    gridBoard.setTranslationX(newX);
                    gridBoard.setTranslationY(newY);
                    lastX = ev.getRawX();
                    lastY = ev.getRawY();
                }
                break;
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                isDragging = false;
                break;
        }
        return super.dispatchTouchEvent(ev);
    }
}
