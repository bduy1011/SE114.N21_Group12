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

import java.util.ArrayList;

public class ExpenseCatalogActivity extends AppCompatActivity {
    private final int REQUEST_CODE_EXPENSE_CATALOG = 10;
    private final int REQUEST_CODE_CREATE_ITEM_CATALOG = 11;
    private GridLayout gridLayout;
    private ArrayList<String> mCatalogExpense;
    private ArrayList<LinearLayout> mLinearLayouts;
    private LinearLayout mSelectedLinearLayoutCatalog;
    private TextView mSelectedTextView;
    private int mPositionIconFromExpenseCatalog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_expense_catalog);

        init();

        createGridViewCatalog(mCatalogExpense);
    }

    private void init() {
        mLinearLayouts = new ArrayList<>();

        mCatalogExpense = new ArrayList<String>();
        mCatalogExpense.add("Ăn uống");
        mCatalogExpense.add("Đi lại");
        mCatalogExpense.add("Quà tặng");
        mCatalogExpense.add("Giải trí");
        mCatalogExpense.add("Học tập");
        mCatalogExpense.add("Sức khỏe");
        mCatalogExpense.add("Quần áo");
        mCatalogExpense.add("Quần áo1");
        mCatalogExpense.add("Quần áo2");
        mCatalogExpense.add("Quần áo3");
        mCatalogExpense.add("Quần áo4");
        mCatalogExpense.add("Quần áo5");
        mCatalogExpense.add("Khác");

        gridLayout = findViewById(R.id.gridLayout);
    }

    private void createGridViewCatalog(ArrayList<String> mCatalogExpense) {
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
                imageButton.setImageResource(getImageResource(i));
            }
            else imageButton.setImageResource(R.drawable.ic_extend_catalog);
            imageButton.setScaleType(ImageButton.ScaleType.CENTER_INSIDE);

            int customColor;
            if (i != mCatalogExpense.size()) {
                customColor = getTintColor(i);
            }
            else customColor = Color.LTGRAY;

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
                        // Lấy vị trí của LinearLayout trong danh sách
                        int currentPosition = mLinearLayouts.indexOf(linearLayout);
                        pushIntentToExpenseCatalogActivity(currentPosition);
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
                textView.setText(mCatalogExpense.get(i));
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
                                if (tmp == mCatalogExpense.get(i))
                                    selectedColor = getTintColor(i);
                            }

                            // Thiết lập Background cho topic đang chọn
                            setBackgroundCurrentSelectedIcon((LinearLayout) v, selectedColor, textView);

                            // Lưu trữ topic được chọn
                            saveSelectedTopic((LinearLayout) v, textView);

                            int position = mLinearLayouts.indexOf(mSelectedLinearLayoutCatalog);
                            if (position < mCatalogExpense.size()) {
                                Intent intent = new Intent(v.getContext(), AddExpenseActivity.class);
                                intent.putExtra("SelectedExtendIcon", position);
                                setResult(RESULT_OK, intent);
                                finish();
                            }
                            else {
                                Intent intent = new Intent(v.getContext(), CreateItemCatalogsActivity.class);
                                startActivityForResult(intent, REQUEST_CODE_EXPENSE_CATALOG);
                            }


                        }
                    }
                });
            }

            // Thêm LinearLayout vào GridLayout
            gridLayout.addView(linearLayout);
        }

        // Set background cho topic được chọn trước đó
        mPositionIconFromExpenseCatalog = getPositionIconFromExpenseCatalog();
        if (mPositionIconFromExpenseCatalog <= mCatalogExpense.size()) {
            if(mSelectedLinearLayoutCatalog != null) {
                setBackgroundPreviousSelectedIcon();
            }
            mSelectedLinearLayoutCatalog = mLinearLayouts.get(mPositionIconFromExpenseCatalog);
            int selectedColor = getTintColor(mPositionIconFromExpenseCatalog);
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
    private int getImageResource(int index) {
        switch (index) {
            case 0:
                return R.drawable.icon_foodanddrink_1;
            case 1:
                return R.drawable.icon_transportation_1;
            case 2:
                return R.drawable.icon_shopping_2;
            case 3:
                return R.drawable.icon_shopping_3;
            case 4:
                return R.drawable.icon_education_1;
            case 5:
                return R.drawable.icon_health_1;
            case 6:
                return R.drawable.icon_shopping_1;
            case 7:
                return R.drawable.icon_other_1;
            case 8:
                return R.drawable.icon_accounts_1;
            case 9:
                return R.drawable.icon_shopping_5;
            case 10:
                return R.drawable.icon_accounts_5;
            case 11:
                return R.drawable.icon_shopping_9;
            case 12:
                return R.drawable.icon_shopping_13;
            default:
                return 0;
        }
    }
    private int getTintColor(int index) {
        switch (index) {
            case 0:
                return Color.parseColor("#FFC107");
            case 1:
                return Color.parseColor("#2196F3");
            case 2:
                return Color.parseColor("#673AB7");
            case 3:
                return Color.parseColor("#F44336");
            case 4:
                return Color.parseColor("#4CAF50");
            case 5:
                return Color.parseColor("#9C27B0");
            case 6:
                return Color.parseColor("#FF5722");
            case 7:
                return Color.parseColor("#607D80");
            default:
                return Color.parseColor("#607D8B");
        }
    }
    private int getPositionIconFromExpenseCatalog() {
        Intent intent = getIntent();
        int position = intent.getIntExtra("PreviousSelectedIcon", 10000);
        return position;
    }
    private void pushIntentToExpenseCatalogActivity(int currentPosition) {
        if (currentPosition == mCatalogExpense.size()) {
            Intent intent = new Intent(this, CreateItemCatalogsActivity.class);
            startActivityForResult(intent, REQUEST_CODE_CREATE_ITEM_CATALOG);
        }
    }
}