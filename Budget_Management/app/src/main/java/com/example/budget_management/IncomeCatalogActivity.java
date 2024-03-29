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

public class IncomeCatalogActivity extends AppCompatActivity {
    private final int REQUEST_TO_CREATE_ITEM_CATALOG = 10;
    private GridLayout gridLayout;
    private ArrayList<Catalog> mCatalogIncome;
    private ArrayList<LinearLayout> mLinearLayouts;
    private LinearLayout mSelectedLinearLayoutCatalog;
    private TextView mSelectedTextView;
    private Catalog mCatalogFromIncomeCatalog;
    private FirebaseAuth mAuth;
    private DatabaseReference mIncomeCategoryDatabase;
    private String key;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_income_catalog);

        init();
    }

    private void init() {
        mLinearLayouts = new ArrayList<>();

        mCatalogIncome = new ArrayList<>();

        gridLayout = findViewById(R.id.gridLayout);

        mAuth = FirebaseAuth.getInstance();
        FirebaseUser mUser = mAuth.getCurrentUser();
        String uid = mUser.getUid();
        mIncomeCategoryDatabase = FirebaseDatabase.getInstance().getReference().child("IncomeCatalogs").child(uid);
        mIncomeCategoryDatabase.keepSynced(true);

        mIncomeCategoryDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                mCatalogIncome.clear();
                for (DataSnapshot data : dataSnapshot.getChildren()) {
                    Catalog catalog = data.getValue(Catalog.class);
                    mCatalogIncome.add(catalog);
                }
                createGridViewCatalog(mCatalogIncome);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Xử lý lỗi nếu có
            }
        });
    }
    private void createGridViewCatalog(ArrayList<Catalog> mCatalogIncome) {
        // Thiết lập số cột của GridLayout là 4
        gridLayout.setColumnCount(4);

        // Tính toán chiều rộng cột cho các thiết bị có độ rộng màn hình khác nhau
        int countColumn = 4;
        int screenWidthPx = getResources().getDisplayMetrics().widthPixels - 50;
        int columnWidthPx = screenWidthPx / countColumn;
        int widthItem = screenWidthPx / 6;

        for (int i = 0; i < mCatalogIncome.size() + 1; i++) {
            if (i == mCatalogIncome.size() && key == "add") {
                return;
            }
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

            if (i != mCatalogIncome.size()) {
                imageButton.setImageResource(getFileFromDrawable(mCatalogIncome.get(i).getIcon()));
            }
            else imageButton.setImageResource(R.drawable.ic_extend_catalog);
            imageButton.setScaleType(ImageButton.ScaleType.CENTER_INSIDE);

            int customColor;
            if (i != mCatalogIncome.size()) {
                customColor = Color.parseColor(mCatalogIncome.get(i).getColor());
            }
            else customColor = Color.parseColor("#a4b7b1");

            // Tạo một Drawable từ code Java với màu sắc mới
            GradientDrawable drawableImageButton = new GradientDrawable();
            drawableImageButton.setShape(GradientDrawable.OVAL);
            drawableImageButton.setColor(customColor);
            imageButton.setBackground(drawableImageButton);
            imageButton.setTag(customColor);

            // Thêm sự kiện click cho ImageButton
            if(i != mCatalogIncome.size()) {
                imageButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        setBackgroundPreviousSelectedCatalog();

                        // Lấy màu của ImageButton được click
                        int selectedColor = (int) v.getTag();

                        // Lấy tên topic của icon được chọn
                        ViewGroup viewGroup = (ViewGroup) v.getParent();
                        TextView textView = (TextView) viewGroup.getChildAt(1);

                        // Thiết lập Background cho topic đang chọn
                        setBackgroundCurrentSelectedCatalog(linearLayout, selectedColor, textView);

                        // Lưu trữ topic được chọn
                        saveSelectedTopic(linearLayout, textView);

                        int position = mLinearLayouts.indexOf(mSelectedLinearLayoutCatalog);
                        if (position < mCatalogIncome.size()) {
                            Intent intent = new Intent(v.getContext(), AddIncomeActivity.class);
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

            if (i == mCatalogIncome.size()) {
                int smallerSize = (int) (widthItem * 0.7);
                LinearLayout.LayoutParams smallerParams = new LinearLayout.LayoutParams(smallerSize, smallerSize);
                smallerParams.setMargins(0, (int) (widthItem * 0.15), 0, 20 + (int) (widthItem * 0.16));
                imageButton.setLayoutParams(smallerParams);
            }

            // Thêm ImageButton vào LinearLayout
            linearLayout.addView(imageButton);

            // Thêm TextView vào LinearLayout
            TextView textView = new TextView(this);
            if (i != mCatalogIncome.size()) {
                textView.setText(mCatalogIncome.get(i).getName());
            }
            else textView.setText("Tạo");
            textView.setTextColor(Color.BLACK);
            textView.setGravity(Gravity.CENTER);
            linearLayout.setTag(textView);
            linearLayout.addView(textView);

            if (i != mCatalogIncome.size()) {
                // Đăng ký sự kiện click cho LinearLayout
                linearLayout.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        // Nếu LinearLayout đã chọn trước đó khác với LinearLayout hiện tại
                        if (mSelectedLinearLayoutCatalog != v) {
                            setBackgroundPreviousSelectedCatalog();

                            // Lấy màu của ImageButton được click
                            int selectedColor = 100;
                            String tmp = textView.getText().toString();
                            for (int i = 0; i < mCatalogIncome.size(); i++) {
                                if (tmp == mCatalogIncome.get(i).getName())
                                    selectedColor = Color.parseColor(mCatalogIncome.get(i).getColor());
                            }

                            // Thiết lập Background cho topic đang chọn
                            setBackgroundCurrentSelectedCatalog((LinearLayout) v, selectedColor, textView);

                            // Lưu trữ topic được chọn
                            saveSelectedTopic((LinearLayout) v, textView);

                            int position = mLinearLayouts.indexOf(mSelectedLinearLayoutCatalog);
                            if (position < mCatalogIncome.size()) {
                                Intent intent = new Intent();
                                intent.putExtra("SelectedExtendIcon", position);
                                setResult(RESULT_OK, intent);
                            }
                            finish();
                        }
                    }
                });
            }

            // Thêm LinearLayout vào GridLayout
            gridLayout.addView(linearLayout);
        }
        int position = -1;
        // Set background cho topic được chọn trước đó
        mCatalogFromIncomeCatalog = receiveIntent();
        for (int i = 0; i < mCatalogIncome.size(); i++) {
            if (mCatalogIncome.get(i).getName().equals(mCatalogFromIncomeCatalog.getName())
                    && mCatalogIncome.get(i).getColor().equals(mCatalogFromIncomeCatalog.getColor())
                    && mCatalogIncome.get(i).getType().equals(mCatalogFromIncomeCatalog.getType())
                    && mCatalogIncome.get(i).getIcon().equals(mCatalogFromIncomeCatalog.getIcon())) {
                position = i;
                break;
            }
        }
        if (position != -1 && position <= mCatalogIncome.size()) {
            if(mSelectedLinearLayoutCatalog != null) {
                setBackgroundPreviousSelectedCatalog();
            }
            mSelectedLinearLayoutCatalog = mLinearLayouts.get(position);
            int selectedColor = Color.parseColor(mCatalogIncome.get(position).getColor());
            mSelectedTextView = (TextView) mSelectedLinearLayoutCatalog.getTag();
            setBackgroundCurrentSelectedCatalog(mSelectedLinearLayoutCatalog, selectedColor, mSelectedTextView);
        }
    }
    private void setBackgroundCurrentSelectedCatalog(LinearLayout linearLayout, int selectedColor, TextView textView) {
        // Đặt màu cho tên Topic hiện tại
        textView.setTextColor(Color.WHITE);

        // Đặt màu nền cho tên Topic hiện tại
        GradientDrawable drawableLinearLayout = new GradientDrawable();
        drawableLinearLayout.setShape(GradientDrawable.RECTANGLE);
        drawableLinearLayout.setCornerRadii(new float[]{25, 25, 25, 25, 25, 25, 25, 25});
        drawableLinearLayout.setColor(selectedColor);
        linearLayout.setBackground(drawableLinearLayout);
    }
    private void setBackgroundPreviousSelectedCatalog() {
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
        String icon = intent.getStringExtra("icon");
        Catalog catalog = new Catalog(name, color, type, icon);
        return catalog;
    }
    private void sendIntent() {
        Intent intent = new Intent(this, CreateItemCatalogsActivity.class);
        intent.putExtra("type", "Income");
        startActivityForResult(intent, REQUEST_TO_CREATE_ITEM_CATALOG);
    }
    private int getFileFromDrawable(String fileName) {
        int drawableId = getResources().getIdentifier(fileName, "drawable", getPackageName());
        return drawableId;
    }
}