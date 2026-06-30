package com.example.project.game;

import android.app.AlertDialog;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.DisplayMetrics;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.GridLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.project.R;
import com.example.project.game.NewGameDialog;
import com.google.firebase.firestore.MetadataChanges;

import java.util.Locale;

import Model.Game;
import Model.Session;

public class GameClassicActivity extends AppCompatActivity {
    FrameLayout boardContainer;
    GridLayout gridBoard;
    TextView txtCoinsHeader;

    private ScaleGestureDetector scaleDetector;
    private float scaleFactor = 1.0f;
    private float lastX;
    private float lastY;
    private boolean isDragging = false;
    private boolean isScaling = false;
    private boolean flagMode = false;
    private ImageButton[][] tiles;

    Game game;

    private Runnable timerRunnable;
    private Handler handler = new Handler(Looper.getMainLooper());

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.game_classic);

        int rows = getIntent().getIntExtra("rows", 9);
        int cols = getIntent().getIntExtra("columns", 9);
        int mines = getIntent().getIntExtra("mines", 10);
        String diff = getIntent().getStringExtra("diff");

        game = new Game();
        game.setUpGame(rows, cols, mines, diff);
        tiles = new ImageButton[rows][cols];

        ImageView btnReset = findViewById(R.id.btnReset);
        btnReset.setOnClickListener(v -> {
            NewGameDialog dialog = new NewGameDialog();
            dialog.show(getSupportFragmentManager(), "NEW_GAME_DIALOG");
        });

        ImageView btnFlag = findViewById(R.id.btnFlag);
        btnFlag.setOnClickListener(v -> {
            flagMode = !flagMode;
            // Đổi giao diện nút cờ (không bắt buộc)
            btnFlag.setAlpha(flagMode ? 0.5f : 1f);
            // Cập nhật toàn bộ bàn cờ
            updateBoard();
        });

        // gợi ý mở ô trên bàn
        ImageView btnHint = findViewById(R.id.btnHint);
        btnHint.setOnClickListener(v -> {
            if (game.isLose() || game.isWin() || game.isFirstHit()) {
                return;
            }

            int[] hint = game.getHint();
            if (hint != null) {
                openTile(hint[0], hint[1]);
            }
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

                tiles[row][col] = tile;
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
                tile.setOnClickListener(v -> {
                    if (flagMode)
                        toggleFlag(finalRow, finalCol);
                    else
                        openTile(finalRow, finalCol);
                });

                tile.setOnLongClickListener(v -> {
                    if (flagMode)
                        openTile(finalRow, finalCol);
                    else
                        toggleFlag(finalRow, finalCol);

                    return true;
                });
                gridBoard.addView(tile);
            }
        }

        // set up flags
        TextView txtBombs = findViewById(R.id.txtBombs);
        txtBombs.setText(String.format(Locale.getDefault(), "%03d", game.getFlags()));

        // set up time
        TextView txtTime = findViewById(R.id.txtTime);

        timerRunnable = new Runnable() {
            @Override
            public void run() {
                game.increaseTime();
                txtTime.setText(String.format(Locale.getDefault(), "%03d", Math.min(game.getTime(), 999)));
                handler.postDelayed(this, 1000);
            }
        };
        // hiển thị số tiền khi vừa vào game
        txtCoinsHeader = findViewById(R.id.txtCoinsHeader);
        if (Session.user != null && Session.user.uid != null) {
            com.google.firebase.firestore.FirebaseFirestore.getInstance().collection("UserCoins").document(Session.user.uid).addSnapshotListener((value, error) -> {
                if(value != null && value.exists()&& value.contains("coins")){
                    // kéo xu trên mạng về nạp vào RAM
                    Session.coins = value.getLong("coins").intValue();
                    // hiển thị lên Header
                    txtCoinsHeader.setText(String.format(Locale.getDefault(), "%03d", Session.coins));
                }
            });
        }else{
            // nếu chơi offline không đăng nhập
            txtCoinsHeader.setText(String.format(Locale.getDefault(),"%03d",Session.coins));
        }

    }

    private void openTile(int row, int col) {
        // nếu game đã thắng hoặc thua thì không xử lý click nữa
        if(game.isLose() || game.isWin()){
            return;
        }
        if (game.isFirstHit()) {
            game.setUpBombs(row, col);
            timerRunnable.run();
        }
        if (game.isRevealed(row, col)) {
            game.chord(row, col);
        } else {
            game.hitTile(row, col);
        }
        updateBoard();

        if (game.isLose()) {
            // Dừng đồng hồ tính giờ lại
            handler.removeCallbacks(timerRunnable);
            // cập nhật lại số xu mới lên Header
            txtCoinsHeader.setText(String.format(Locale.getDefault(),"%03d",Session.coins));

            // Hiện Dialog thua cuộc
            new android.app.AlertDialog.Builder(this)
                    .setTitle("💥 GAME OVER")
                    .setMessage("Bạn đã dẫm phải mìn!")
                    .setCancelable(false)
                    .setPositiveButton("Chơi lại", (dialog, which) -> recreate())
                    .setNegativeButton("Thoát", (dialog, which) -> finish())
                    .show();
        }

        if (game.isWin()) {
            // Dừng đồng hồ tính giờ lại
            handler.removeCallbacks(timerRunnable);
            // cập nhật lại số xu mới lên Header
            txtCoinsHeader.setText(String.format(Locale.getDefault(),"%03d",Session.coins));

            // Hiện Dialog Chiến thắng kèm thời gian hoàn thành
            new android.app.AlertDialog.Builder(this)
                    .setTitle("🎉 CHIẾN THẮNG!")
                    .setMessage("Thời gian của bạn: " + game.getTime() + " giây.")
                    .setCancelable(false)
                    .setPositiveButton("Ván mới", (dialog, which) -> recreate())
                    .setNegativeButton("Menu", (dialog, which) -> finish())
                    .show();
        }
    }

    private void toggleFlag(int row, int col) {
        game.toggleFlag(row, col);
        TextView txtBombs = findViewById(R.id.txtBombs);
        txtBombs.setText(String.format(Locale.getDefault(),
                "%03d", game.getFlags()));
        updateBoard();
    }

    private void updateBoard() {
        int rows = tiles.length;
        int cols = tiles[0].length;
        for (int row = 0; row < rows; row++) {
            for (int col = 0; col < cols; col++) {
                ImageButton tile = tiles[row][col];
                if (game.isFlagged(row, col)) {
                    tile.setImageResource(R.drawable.flag_tile);
                } else if (!game.isRevealed(row, col)) {
                    if (flagMode) {
                        tile.setImageResource(R.drawable.unrevealed_tile_flag);
                    } else {
                        tile.setImageResource(R.drawable.unrevealed_tile);
                    }
                } else {
                    int value = game.getValue(row, col);
                    switch (value) {
                        case -3:
                            tile.setImageResource(R.drawable.flag_tile_wrong);
                            break;

                        case -2:
                            tile.setImageResource(R.drawable.bomb_tile_triggered);
                            break;

                        case -1:
                            tile.setImageResource(R.drawable.bomb_tile);
                            break;

                        case 0:
                            tile.setImageResource(R.drawable.revealed_tile);
                            break;

                        case 1:
                            tile.setImageResource(R.drawable.revealed_tile_1);
                            break;

                        case 2:
                            tile.setImageResource(R.drawable.revealed_tile_2);
                            break;

                        case 3:
                            tile.setImageResource(R.drawable.revealed_tile_3);
                            break;

                        case 4:
                            tile.setImageResource(R.drawable.revealed_tile_4);
                            break;

                        case 5:
                            tile.setImageResource(R.drawable.revealed_tile_5);
                            break;

                        case 6:
                            tile.setImageResource(R.drawable.revealed_tile_6);
                            break;

                        case 7:
                            tile.setImageResource(R.drawable.revealed_tile_7);
                            break;

                        case 8:
                            tile.setImageResource(R.drawable.revealed_tile_8);
                            break;
                    }
                }
            }
        }
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
                if (!isScaling && scaleFactor > 1.0f && ev.getPointerCount() == 1 && isDragging) {
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
