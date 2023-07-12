package com.example.budget_management;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.GridLayout;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import java.lang.reflect.Field;
import java.util.ArrayList;

public class CreateItemCatalogsActivity extends AppCompatActivity {
    private TextView tvNameItem;
    private RadioGroup rgTypeItem;

    private GridLayout gridLayoutIcon;
    private ArrayList<LinearLayout> mLinearLayoutIcon;

    private ArrayList<Integer> mIconCatalog;

    private Button btnAdd;

    private LinearLayout mSelectedLinearLayoutCatalog;

    private String mNameItemCatalog, mTypeItemCatalog, mIconItemCatalog, mColorItemCatalog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_item_catalogs);

        init();

        rgTypeItem.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                // Xử lý sự kiện khi có RadioButton được chọn
                if (checkedId == R.id.radioButtonExpense) {
                    mTypeItemCatalog = "Chi phí";
                } else if (checkedId == R.id.radioButtonIncome) {
                    mTypeItemCatalog = "Thu nhập";
                }
            }
        });

        /*btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isCheckFillFullInfor()) {
                    //....
                }
            }
        });*/

        //String directoryName = "icon";
        //mIconCatalog = getImageResourcesFromDirectory(directoryName);

        createGridViewCatalog(mIconCatalog);

    }

    private void init() {
        mLinearLayoutIcon = new ArrayList<>();

        gridLayoutIcon = findViewById(R.id.gridLayout);
        rgTypeItem = findViewById(R.id.radioGroupType);

        mIconCatalog = new ArrayList<>();

        //btnAdd = findViewById(R.id.btnThem);
    }

    private void createGridViewCatalog(ArrayList<Integer> mIconCatalog) {
        // Thiết lập số cột của GridLayout là 4
        gridLayoutIcon.setColumnCount(4);

        // Tính toán chiều rộng cột cho các thiết bị có độ rộng màn hình khác nhau
        int countColumn = 4;
        int screenWidthPx = getResources().getDisplayMetrics().widthPixels - 50;
        int columnWidthPx = screenWidthPx / countColumn;
        int widthItem = screenWidthPx / 6;

        for (int i = 0; i < mIconCatalog.size() + 1; i++) {
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
            linearLayout.setPadding(0, 20, 0, 20);

            // Thêm LinearLayout vào danh sách mLinearLayouts
            mLinearLayoutIcon.add(linearLayout);

            // Thêm ImageButton vào LinearLayout
            ImageButton imageButton = new ImageButton(this);

            if (i != mIconCatalog.size()) {
                imageButton.setImageResource(mIconCatalog.get(i));
            }
            else imageButton.setImageResource(R.drawable.dots_icon);
            imageButton.setScaleType(ImageButton.ScaleType.CENTER_INSIDE);

            int customColor;
            if (i != mIconCatalog.size()) {
                customColor = Color.LTGRAY;
            }
            else {
                customColor = Color.parseColor("#FFA500");
            }

            // Tạo một Drawable từ code Java với màu sắc mới
            GradientDrawable drawableImageButton = new GradientDrawable();
            drawableImageButton.setShape(GradientDrawable.OVAL);
            drawableImageButton.setColor(customColor);
            imageButton.setBackground(drawableImageButton);
            imageButton.setTag(customColor);

            if (i != mIconCatalog.size()) {
                // Thêm sự kiện click cho ImageButton
                imageButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        setBackgroundPreviousSelectedIcon();

                        // Thiết lập Background cho topic đang chọn
                        setBackgroundCurrentSelectedIcon(linearLayout);

                        // Lưu trữ topic được chọn
                        saveSelectedTopic(linearLayout);
                    }
                });
            }
            else {
                // Thêm sự kiện click cho ImageButton
                imageButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        // Lấy vị trí của LinearLayout trong danh sách
                        //int currentPosition = mLinearLayouts.indexOf(linearLayout) + 1;
                        pushIntentToExpenseCatalogActivity(/*currentPosition*/);
                    }
                });
            }

            // Thiết lập kích thước của ImageButton
            LinearLayout.LayoutParams paramsImageButton = new LinearLayout.LayoutParams(widthItem, widthItem);
            paramsImageButton.setMargins(0, 20, 0, 20);
            imageButton.setLayoutParams(paramsImageButton);

            if (i == mIconCatalog.size()) {
                int smallerSize = (int) (widthItem * 0.7);
                LinearLayout.LayoutParams smallerParams = new LinearLayout.LayoutParams(smallerSize, smallerSize);
                smallerParams.setMargins(0, 20 + (int) (widthItem * 0.15), 0, 20 + (int) (widthItem * 0.15));
                imageButton.setLayoutParams(smallerParams);
            }

            // Thêm ImageButton vào LinearLayout
            linearLayout.addView(imageButton);

            // Thêm LinearLayout vào GridLayout
            gridLayoutIcon.addView(linearLayout);
        }
    }
    private void setBackgroundCurrentSelectedIcon(LinearLayout linearLayout) {
        // Đặt màu nền cho tên Topic hiện tại
        GradientDrawable drawableLinearLayout = new GradientDrawable();
        drawableLinearLayout.setShape(GradientDrawable.RECTANGLE);
        drawableLinearLayout.setCornerRadius(20);
        drawableLinearLayout.setColor(Color.WHITE);
        drawableLinearLayout.setStroke(4, Color.DKGRAY);
        linearLayout.setBackground(drawableLinearLayout);
    }
    private void setBackgroundPreviousSelectedIcon() {
        // Đặt màu trắng cho LinearLayout đã chọn trước đó
        if (mSelectedLinearLayoutCatalog != null) {
            mSelectedLinearLayoutCatalog.setBackgroundColor(Color.WHITE);
        }
    }
    private void saveSelectedTopic(LinearLayout linearLayout) {
        mSelectedLinearLayoutCatalog = linearLayout;
    }

    private boolean isCheckFillFullInfor() {
        if (mNameItemCatalog != null && mNameItemCatalog.trim() != "") {
            if (mTypeItemCatalog != null && mTypeItemCatalog.trim() != "") {
                if (mIconItemCatalog != null && mIconItemCatalog.trim() != "") {
                    // chưa check màu
                    return true;
                }
                else {
                    Toast.makeText(this, "Bạn chưa chọn icon cho danh mục!", Toast.LENGTH_SHORT).show();
                    return false;
                }
            }
            else {
                Toast.makeText(this, "Bạn chưa chọn loại danh mục!", Toast.LENGTH_SHORT).show();
                return false;
            }
        }
        else {
            Toast.makeText(this, "Bạn chưa nhập tên danh mục!", Toast.LENGTH_SHORT).show();
            return false;
        }
    }
    private ArrayList<Integer> getImageResourcesFromDirectory(String directoryName) {
        ArrayList<Integer> resourceList = new ArrayList<>();
        Resources resources = getResources(); // Lấy đối tượng Resources

        Field[] fields = R.drawable.class.getFields();
        for (Field field : fields) {
            try {
                String name = field.getName();
                if (name.startsWith(directoryName + "_")) {
                    int resourceId = resources.getIdentifier(name, "drawable", getPackageName());
                    resourceList.add(resourceId);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return resourceList;
    }
    private void pushIntentToExpenseCatalogActivity(/*int currentPosition*/) {
        //if (currentPosition == AMOUNT_ITEM_CATALOG) {
        Intent intent = new Intent(this, IconCatalogActivity.class);
            /*if (mSelectedLinearLayoutCatalog != null) {
                int previousPosition = mLinearLayouts.indexOf(mSelectedLinearLayoutCatalog);
                intent.putExtra("PreviousSelectedIcon", previousPosition);
            }*/
        //startActivityForResult(intent, REQUEST_CODE_ICON_CATEGORY);
        startActivity(intent);
        //}
    }
}