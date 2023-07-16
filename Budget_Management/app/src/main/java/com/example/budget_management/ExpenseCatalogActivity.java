package com.example.budget_management;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridLayout;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.budget_management.Model.Catalog;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class ExpenseCatalogActivity extends AppCompatActivity {
    private final int REQUEST_TO_CREATE_ITEM_CATALOG = 10;
    private GridLayout gridLayout;
    private ArrayList<Catalog> mCatalogExpense;
    private ArrayList<LinearLayout> mLinearLayouts;
    private LinearLayout mSelectedLinearLayoutCatalog;
    private TextView mSelectedTextView;
    private Catalog mCatalogFromExpenseCatalog;
    private FirebaseAuth mAuth;
    private DatabaseReference mExpenseCategoryDatabase;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_expense_catalog);

        init();
    }

    private void init() {
        mLinearLayouts = new ArrayList<>();

        mCatalogExpense = new ArrayList<>();

        gridLayout = findViewById(R.id.gridLayout);

        mAuth = FirebaseAuth.getInstance();
        FirebaseUser mUser = mAuth.getCurrentUser();
        String uid = mUser.getUid();
        mExpenseCategoryDatabase = FirebaseDatabase.getInstance().getReference().child("ExpenseCatalogs").child(uid);
        mExpenseCategoryDatabase.keepSynced(true);

        mExpenseCategoryDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                mCatalogExpense.clear();
                for (DataSnapshot data : dataSnapshot.getChildren()) {
                    Catalog catalog = data.getValue(Catalog.class);
                    mCatalogExpense.add(catalog);
                }
                createGridViewCatalog(mCatalogExpense);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Xử lý lỗi nếu có
            }
        });
    }
    private void createGridViewCatalog(ArrayList<Catalog> mCatalogExpense) {
        // Thiết lập số cột của GridLayout là 4
        gridLayout.setColumnCount(4);

        // Tính toán chiều rộng cột cho các thiết bị có độ rộng màn hình khác nhau
        int countColumn = 4;
        int screenWidthPx = getResources().getDisplayMetrics().widthPixels - 50;
        int columnWidthPx = screenWidthPx / countColumn;
        int widthItem = screenWidthPx / 6;

        for (int i = 0; i < mCatalogExpense.size() + 1; i++) {
            // Tạo LinearLayout mới
            LinearLayout linearLayout = new LinearLayout(this);
            linearLayout.setOrientation(LinearLayout.VERTICAL);
            linearLayout.setGravity(Gravity.CENTER);
            linearLayout.setClickable(true);
            linearLayout.setBackgroundColor(Color.WHITE);

            // Tạo một GradientDrawable mới với góc bo tròn
            GradientDrawable drawableLinearLayout = new GradientDrawable();
            drawableLinearLayout.setShape(GradientDrawable.RECTANGLE);
            drawableLinearLayout.setCornerRadii(new float[]{25, 25, 25, 25, 25, 25, 25, 25});
            drawableLinearLayout.setColor(Color.WHITE);

            // Thiết lập GradientDrawable làm nền cho LinearLayout
            linearLayout.setBackground(drawableLinearLayout);

            GridLayout.LayoutParams params = new GridLayout.LayoutParams();
            params.width = columnWidthPx;
            params.height = GridLayout.LayoutParams.WRAP_CONTENT;
            params.columnSpec = GridLayout.spec(i % countColumn);
            params.rowSpec = GridLayout.spec(i / countColumn);
            linearLayout.setLayoutParams(params);
            linearLayout.setPadding(0, 10, 0, 20);

            // Thêm LinearLayout vào danh sách mLinearLayouts
            mLinearLayouts.add(linearLayout);

            // Thêm ImageButton vào LinearLayout
            ImageButton imageButton = new ImageButton(this);

            if (i != mCatalogExpense.size()) {
                imageButton.setImageResource(mCatalogExpense.get(i).getIcon());
            }
            else imageButton.setImageResource(R.drawable.ic_extend_catalog);
            imageButton.setScaleType(ImageButton.ScaleType.CENTER_INSIDE);

            int customColor;
            if (i != mCatalogExpense.size()) {
                customColor = Color.parseColor(mCatalogExpense.get(i).getColor());
            }
            else customColor = Color.parseColor("#a4b7b1");

            // Tạo một Drawable từ code Java với màu sắc mới
            GradientDrawable drawableImageButton = new GradientDrawable();
            drawableImageButton.setShape(GradientDrawable.OVAL);
            drawableImageButton.setColor(customColor);
            imageButton.setBackground(drawableImageButton);
            imageButton.setTag(customColor);

            // Thêm sự kiện click cho ImageButton
            if(i != mCatalogExpense.size()) {
                imageButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        setBackgroundPreviousSelectedIcon();

                        // Lấy màu của ImageButton được click
                        int selectedColor = (int) v.getTag();

                        // Lấy tên topic của icon được chọn
                        ViewGroup viewGroup = (ViewGroup) v.getParent();
                        TextView textView = (TextView) viewGroup.getChildAt(1);

                        // Thiết lập Background cho topic đang chọn
                        setBackgroundCurrentSelectedIcon(linearLayout, selectedColor, textView);

                        // Lưu trữ topic được chọn
                        saveSelectedTopic(linearLayout, textView);

                        int position = mLinearLayouts.indexOf(mSelectedLinearLayoutCatalog);
                        if (position < mCatalogExpense.size()) {
                            Intent intent = new Intent(v.getContext(), AddExpenseActivity.class);
                            intent.putExtra("SelectedExtendIcon", position);
                            setResult(RESULT_OK, intent);
                        }
                        finish();
                    }
                });
            }
            else {
                imageButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        sendIntent();
                    }
                });
            }

            // Thiết lập kích thước của ImageButton
            LinearLayout.LayoutParams paramsImageButton = new LinearLayout.LayoutParams(widthItem, widthItem);
            paramsImageButton.setMargins(0, 0, 0, 20);
            imageButton.setLayoutParams(paramsImageButton);

            if (i == mCatalogExpense.size()) {
                int smallerSize = (int) (widthItem * 0.7);
                LinearLayout.LayoutParams smallerParams = new LinearLayout.LayoutParams(smallerSize, smallerSize);
                smallerParams.setMargins(0, (int) (widthItem * 0.15), 0, 20 + (int) (widthItem * 0.16));
                imageButton.setLayoutParams(smallerParams);
            }

            // Thêm ImageButton vào LinearLayout
            linearLayout.addView(imageButton);

            // Thêm TextView vào LinearLayout
            TextView textView = new TextView(this);
            if (i != mCatalogExpense.size()) {
                textView.setText(mCatalogExpense.get(i).getName());
            }
            else textView.setText("Tạo");
            textView.setTextColor(Color.BLACK);
            textView.setGravity(Gravity.CENTER);
            linearLayout.setTag(textView);
            linearLayout.addView(textView);

            if (i != mCatalogExpense.size()) {
                // Đăng ký sự kiện click cho LinearLayout
                linearLayout.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        // Nếu LinearLayout đã chọn trước đó khác với LinearLayout hiện tại
                        if (mSelectedLinearLayoutCatalog != v) {
                            setBackgroundPreviousSelectedIcon();

                            // Lấy màu của ImageButton được click
                            int selectedColor = 100;
                            String tmp = textView.getText().toString();
                            for (int i = 0; i < mCatalogExpense.size(); i++) {
                                if (tmp == mCatalogExpense.get(i).getName())
                                    selectedColor = Color.parseColor(mCatalogExpense.get(i).getColor());
                            }

                            // Thiết lập Background cho topic đang chọn
                            setBackgroundCurrentSelectedIcon((LinearLayout) v, selectedColor, textView);

                            // Lưu trữ topic được chọn
                            saveSelectedTopic((LinearLayout) v, textView);

                            int position = mLinearLayouts.indexOf(mSelectedLinearLayoutCatalog);
                            if (position < mCatalogExpense.size()) {
                                Intent intent = new Intent();
                                intent.putExtra("SelectedExtendIcon", position);
                                setResult(RESULT_OK, intent);
                                finish();
                            }
                        }
                    }
                });
            }

            // Thêm LinearLayout vào GridLayout
            gridLayout.addView(linearLayout);
        }
        int position = -1;
        // Set background cho topic được chọn trước đó
        mCatalogFromExpenseCatalog = receiveIntent();
        for (int i = 0; i < mCatalogExpense.size(); i++) {
            if (mCatalogExpense.get(i).getName().equals(mCatalogFromExpenseCatalog.getName())
                    && mCatalogExpense.get(i).getColor().equals(mCatalogFromExpenseCatalog.getColor())
                    && mCatalogExpense.get(i).getType().equals(mCatalogFromExpenseCatalog.getType())
                    && mCatalogExpense.get(i).getIcon() == mCatalogFromExpenseCatalog.getIcon()) {
                position = i;
                break;
            }
        }
        if (position != -1 && position <= mCatalogExpense.size()) {
            if(mSelectedLinearLayoutCatalog != null) {
                setBackgroundPreviousSelectedIcon();
            }
            mSelectedLinearLayoutCatalog = mLinearLayouts.get(position);
            int selectedColor = Color.parseColor(mCatalogExpense.get(position).getColor());
            mSelectedTextView = (TextView) mSelectedLinearLayoutCatalog.getTag();
            setBackgroundCurrentSelectedIcon(mSelectedLinearLayoutCatalog, selectedColor, mSelectedTextView);
        }
    }
    private void setBackgroundCurrentSelectedIcon(LinearLayout linearLayout, int selectedColor, TextView textView) {
        // Đặt màu cho tên Topic hiện tại
        textView.setTextColor(Color.WHITE);

        // Đặt màu nền cho tên Topic hiện tại
        GradientDrawable drawableLinearLayout = new GradientDrawable();
        drawableLinearLayout.setShape(GradientDrawable.RECTANGLE);
        drawableLinearLayout.setCornerRadii(new float[]{25, 25, 25, 25, 25, 25, 25, 25});
        drawableLinearLayout.setColor(selectedColor);
        linearLayout.setBackground(drawableLinearLayout);
    }
    private void setBackgroundPreviousSelectedIcon() {
        // Đặt màu trắng cho LinearLayout đã chọn trước đó
        if (mSelectedLinearLayoutCatalog != null) {
            mSelectedLinearLayoutCatalog.setBackgroundColor(Color.WHITE);
        }
        // Đặt màu đen cho TextView đã chọn trước đó
        if (mSelectedTextView != null) {
            mSelectedTextView.setTextColor(Color.BLACK);
        }
    }
    private void saveSelectedTopic(LinearLayout linearLayout, TextView textView) {
        mSelectedLinearLayoutCatalog = linearLayout;
        mSelectedTextView = textView;
    }
    private Catalog receiveIntent() {
        Intent intent = getIntent();
        String name = intent.getStringExtra("name");
        String color = intent.getStringExtra("color");
        String type = intent.getStringExtra("type");
        int icon = intent.getIntExtra("icon", 10000);
        Catalog catalog = new Catalog(name, color, type, icon);
        return catalog;
    }
    private void sendIntent() {
        Intent intent = new Intent(this, CreateItemCatalogsActivity.class);
        startActivityForResult(intent, REQUEST_TO_CREATE_ITEM_CATALOG);
    }
}