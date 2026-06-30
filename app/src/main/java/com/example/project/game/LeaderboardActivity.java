package com.example.project.game;

import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.ImageButton;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.project.R;
import com.example.project.utils.ImageUtils;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.imageview.ShapeableImageView;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import java.util.ArrayList;
import java.util.List;

import Model.LeaderBoard;
import com.google.firebase.auth.FirebaseAuth;

public class LeaderboardActivity extends AppCompatActivity {

    private static final String TAG = "DEBUG_LEADERBOARD";

    private AutoCompleteTextView spinnerMode, spinnerDiff;
    private RecyclerView rvLeaderboard;
    private TextView tvEmptyMessage;
    private MaterialCardView layoutMyRank;
    private TextView tvMyRank, tvMyPlayerId, tvMyScore, tvMyTime;
    private LeaderboardAdapter adapter;
    private final List<LeaderBoard> leaderboardList = new ArrayList<>();

    private final FirebaseFirestore db = FirebaseFirestore.getInstance();
    private String selectedMode = "";
    private String selectedDiff = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_leaderboard);

        initViews();
        loadModesFromFirestore();
    }

    private void initViews() {
        spinnerMode = findViewById(R.id.spinnerMode);
        spinnerDiff = findViewById(R.id.spinnerDiff);
        rvLeaderboard = findViewById(R.id.rvLeaderboard);
        tvEmptyMessage = findViewById(R.id.tvEmptyMessage);

        layoutMyRank = findViewById(R.id.layoutMyRank);
        tvMyRank = findViewById(R.id.tvMyRank);
        tvMyPlayerId = findViewById(R.id.tvMyPlayerId);
        tvMyScore = findViewById(R.id.tvMyScore);
        tvMyTime = findViewById(R.id.tvMyTime);

        rvLeaderboard.setLayoutManager(new LinearLayoutManager(this));
        adapter = new LeaderboardAdapter(leaderboardList);
        rvLeaderboard.setAdapter(adapter);
        adapter.setOnItemClickListener(item -> {
            showUserProfileDialog(item.getUserId());
        });
        layoutMyRank.setOnClickListener(v -> {
            String currentUid = FirebaseAuth.getInstance().getCurrentUser() != null
                    ? FirebaseAuth.getInstance().getCurrentUser().getUid()
                    : "HNUNJojeicQ6CIgmmNxIA3S4gOk2";
            showUserProfileDialog(currentUid);
        });
        ImageButton btnBack = findViewById(R.id.btnBack);
        btnBack.setOnClickListener(v -> finish());
    }
    private void showUserProfileDialog(String userId) {
        BottomSheetDialog bottomSheetDialog =
                new BottomSheetDialog(this);

        View view = getLayoutInflater().inflate(R.layout.dialog_user_info, null);
        bottomSheetDialog.setContentView(view);

        ShapeableImageView imgDialogAvatar = view.findViewById(R.id.imgDialogAvatar);
        TextView txtDialogName = view.findViewById(R.id.txtDialogName);
        android.widget.Button btnDialogClose = view.findViewById(R.id.btnDialogClose);

        btnDialogClose.setOnClickListener(v -> bottomSheetDialog.dismiss());

        txtDialogName.setText("Đang tải...");

        db.collection("users").document(userId).get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        String name = documentSnapshot.getString("name");
                        String base64Avatar = documentSnapshot.getString("avatar");

                        txtDialogName.setText(name != null && !name.isEmpty() ? name : "Chưa đặt tên");

                        if (base64Avatar != null && !base64Avatar.isEmpty()) {
                            android.graphics.Bitmap bitmap = ImageUtils.base64ToBitmap(base64Avatar);
                            if (bitmap != null) {
                                imgDialogAvatar.setImageBitmap(bitmap);
                            }
                        } else {
                            imgDialogAvatar.setImageResource(R.drawable.default_avatar);
                        }
                    } else {
                        txtDialogName.setText("Người dùng không tồn tại");
                    }
                })
                .addOnFailureListener(e -> {
                    txtDialogName.setText("Lỗi tải thông tin");
                    Log.e(TAG, "Error loading detailed user info", e);
                });

        bottomSheetDialog.show();
    }
    private void loadModesFromFirestore() {
        db.collection("Leaderboards")
                .whereEqualTo("isActive", true)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    List<String> modes = new ArrayList<>();
                    for (DocumentSnapshot doc : queryDocumentSnapshots.getDocuments()) {
                        modes.add(doc.getId());
                    }

                    if (modes.isEmpty()) {
                        updateUI(true);
                        return;
                    }

                    ArrayAdapter<String> modeAdapter = new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, modes);
                    spinnerMode.setAdapter(modeAdapter);

                    selectedMode = modes.get(0);
                    spinnerMode.setText(selectedMode, false);

                    loadDifficultiesDynamically(selectedMode);

                    spinnerMode.setOnItemClickListener((parent, view, position, id) -> {
                        selectedMode = parent.getItemAtPosition(position).toString();
                        resetDiffAndLeaderboard();
                        loadDifficultiesDynamically(selectedMode);
                    });
                })
                .addOnFailureListener(e -> Log.e(TAG, "Error loading modes", e));
    }

    private void loadDifficultiesDynamically(String mode) {
        List<String> potentialDiffs = List.of("easy", "medium", "hard");
        List<String> activeDiffs = new ArrayList<>();
        final int[] remainingChecks = {potentialDiffs.size()};

        for (String diff : potentialDiffs) {
            db.collection("Leaderboards")
                    .document(mode)
                    .collection(diff)
                    .limit(1)
                    .get()
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful() && task.getResult() != null && !task.getResult().isEmpty()) {
                            activeDiffs.add(diff);
                        }

                        remainingChecks[0]--;
                        if (remainingChecks[0] == 0) {
                            updateDiffSpinner(activeDiffs);
                        }
                    });
        }
    }

    private void updateDiffSpinner(List<String> diffs) {
        if (diffs.isEmpty()) {
            updateUI(true);
            return;
        }

        ArrayAdapter<String> diffAdapter = new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, diffs);
        spinnerDiff.setAdapter(diffAdapter);

        selectedDiff = diffs.get(0);
        spinnerDiff.setText(selectedDiff, false);

        loadLeaderboardRecords(selectedMode, selectedDiff);

        spinnerDiff.setOnItemClickListener((parent, view, position, id) -> {
            selectedDiff = parent.getItemAtPosition(position).toString();
            loadLeaderboardRecords(selectedMode, selectedDiff);
        });
    }

    private void loadLeaderboardRecords(String mode, String diff) {
        if (mode.isEmpty() || diff.isEmpty()) return;

        db.collection("Leaderboards")
                .document(mode)
                .collection(diff)
                .orderBy("score", Query.Direction.DESCENDING)
                .orderBy("completedTime", Query.Direction.ASCENDING)
                .limit(100)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    leaderboardList.clear();
                    List<LeaderBoard> items = queryDocumentSnapshots.toObjects(LeaderBoard.class);

                    if (items.isEmpty()) {
                        updateUI(true);
                        adapter.notifyDataSetChanged();
                        return;
                    }

                    leaderboardList.addAll(items);
                    updateUI(false);
                    adapter.notifyDataSetChanged();

                    final int[] remainingUsers = {items.size()};
                    for (int i = 0; i < items.size(); i++) {
                        final int index = i;
                        LeaderBoard record = items.get(index);

                        db.collection("users").document(record.getUserId()).get()
                                .addOnSuccessListener(userDoc -> {
                                    if (userDoc.exists()) {
                                        String name = userDoc.getString("name");
                                        if (name != null && !name.isEmpty()) {
                                            leaderboardList.get(index).setPlayerName(name);
                                        }
                                    }
                                })
                                .addOnCompleteListener(task -> {
                                    remainingUsers[0]--;
                                    if (remainingUsers[0] == 0) {
                                        adapter.notifyDataSetChanged();
                                        checkAndShowMyRank();
                                    }
                                });
                    }
                })
                .addOnFailureListener(e -> Log.e(TAG, "Error loading records", e));
    }

    private void updateUI(boolean isEmpty) {
        if (isEmpty) {
            rvLeaderboard.setVisibility(View.GONE);
            tvEmptyMessage.setVisibility(View.VISIBLE);
            layoutMyRank.setVisibility(View.GONE);
        } else {
            rvLeaderboard.setVisibility(View.VISIBLE);
            tvEmptyMessage.setVisibility(View.GONE);
            checkAndShowMyRank();
        }
    }

    private void checkAndShowMyRank() {
        String currentUid = FirebaseAuth.getInstance().getCurrentUser() != null
                ? FirebaseAuth.getInstance().getCurrentUser().getUid()
                : "HNUNJojeicQ6CIgmmNxIA3S4gOk2";

        boolean currentNotFound = true;

        for (int i = 0; i < leaderboardList.size(); i++) {
            LeaderBoard item = leaderboardList.get(i);

            if (item.getUserId().equals(currentUid)) {
                currentNotFound = false;
                int myRankPosition = i + 1;

                tvMyRank.setText(String.valueOf(myRankPosition));

                String displayName = item.getPlayerName() != null ? item.getPlayerName() : item.getUserId();
                tvMyPlayerId.setText(String.format("%s (Bạn)", displayName));

                tvMyScore.setText(String.format("%d Pts", item.getScore()));
                tvMyTime.setText(String.format("⏱️ %02d:%02d", item.getCompletedTime() / 60, item.getCompletedTime() % 60));

                if (tvMyRank.getBackground() instanceof GradientDrawable) {
                    GradientDrawable rankBg = (GradientDrawable) tvMyRank.getBackground();
                    switch (myRankPosition) {
                        case 1:
                            rankBg.setColor(Color.parseColor("#F9AB00"));
                            break;
                        case 2:
                            rankBg.setColor(Color.parseColor("#9AA0A6"));
                            break;
                        case 3:
                            rankBg.setColor(Color.parseColor("#DE8544"));
                            break;
                        default:
                            rankBg.setColor(Color.parseColor("#757575"));
                            break;
                    }
                }

                layoutMyRank.setVisibility(View.VISIBLE);
                break;
            }
        }

        if (currentNotFound) {
            layoutMyRank.setVisibility(View.GONE);
        }
    }

    private void resetDiffAndLeaderboard() {
        selectedDiff = "";
        spinnerDiff.setText("");
        leaderboardList.clear();
        adapter.notifyDataSetChanged();
    }
}