package com.example.budget_management;

import androidx.appcompat.app.AppCompatActivity;

import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.GridLayout;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.TextView;

import java.lang.reflect.Field;
import java.util.ArrayList;

public class IconCatalogActivity extends AppCompatActivity {
    private LinearLayout linearLayoutMain;
    private ArrayList<String> mIconCategoryList;
    private ArrayList<LinearLayout> mLinearLayoutIcon;
    private LinearLayout mSelectedLinearLayoutIcon;
    private Button btnSelect;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_icon_catalog);

        init();

        createGridViewItemCategoryList(mIconCategoryList);
    }

    private void init() {
        linearLayoutMain = findViewById(R.id.mainLinearLayout);
        btnSelect = findViewById(R.id.btnSelect);

        mLinearLayoutIcon = new ArrayList<>();

        mIconCategoryList = new ArrayList<>();
        mIconCategoryList.add("Tài chính");
        mIconCategoryList.add("Di chuyển");
        mIconCategoryList.add("Mua sắm");
        mIconCategoryList.add("Đồ ăn thức uống");
        mIconCategoryList.add("Nhà cửa");
        mIconCategoryList.add("Sức khỏe");
        mIconCategoryList.add("Làm đẹp");
        mIconCategoryList.add("Giải trí");
        mIconCategoryList.add("Tài khoản");
        mIconCategoryList.add("Thể thao");
        mIconCategoryList.add("Thư giãn");
        mIconCategoryList.add("Giáo dục");
        mIconCategoryList.add("Gia đình");
        mIconCategoryList.add("Khác");

        if (mSelectedLinearLayoutIcon == null) {
            btnSelect.setAlpha(0.5f);
            btnSelect.setEnabled(false);
        }
        else {
            btnSelect.setAlpha(1f);
            btnSelect.setEnabled(true);
        }
    }

    private void createGridViewItemCategoryList(ArrayList<String> mIconCategoryList) {
        for (String category : mIconCategoryList) {
            TextView textView = new TextView(this);
            textView.setText(category);
            textView.setTextColor(Color.BLACK);
            linearLayoutMain.addView(textView);
            String directoryName = null;
            ArrayList<Integer> mIconCategory = new ArrayList<>();
            switch(category) {
                case "Tài chính":
                    directoryName = "icon_finances";
                    break;
                case "Di chuyển":
                    directoryName = "icon_transportation";
                    break;
                case "Mua sắm":
                    directoryName = "icon_shopping";
                    break;
                case "Đồ ăn thức uống":
                    directoryName = "icon_foodanddrink";
                    break;
                case "Nhà cửa":
                    directoryName = "icon_home";
                    break;
                case "Sức khỏe":
                    directoryName = "icon_health";
                    break;
                case "Làm đẹp":
                    directoryName = "icon_beauty";
                    break;
                case "Giải trí":
                    directoryName = "icon_entertainment";
                    break;
                case "Tài khoản":
                    directoryName = "icon_accounts";
                    break;
                case "Thể thao":
                    directoryName = "icon_workout";
                    break;
                case "Thư giãn":
                    directoryName = "icon_relaxation";
                    break;
                case "Giáo dục":
                    directoryName = "icon_education";
                    break;
                case "Gia đình":
                    directoryName = "icon_family";
                    break;
                case "Khác":
                    directoryName = "icon_other";
                    break;
            }
            if (directoryName != null) {
                mIconCategory = getImageResourcesFromDirectory(directoryName);
                linearLayoutMain.addView(createGridViewItem(mIconCategory));
            }
        }
    }

    private GridLayout createGridViewItem(ArrayList<Integer> mIconCategory) {
        GridLayout gridLayoutIcon = new GridLayout(this);
        // Thiết lập số cột của GridLayout là 4
        gridLayoutIcon.setColumnCount(4);

        // Tính toán chiều rộng cột cho các thiết bị có độ rộng màn hình khác nhau
        int countColumn = 4;
        int screenWidthPx = getResources().getDisplayMetrics().widthPixels - 50;
        int columnWidthPx = screenWidthPx / countColumn;
        int widthItem = screenWidthPx / 6;

        for (int i = 0; i < mIconCategory.size(); i++) {
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
            linearLayout.setPadding(0, 25, 0, 25);

            // Thêm LinearLayout vào danh sách mLinearLayouts
            mLinearLayoutIcon.add(linearLayout);

            // Thêm ImageButton vào LinearLayout
            ImageButton imageButton = new ImageButton(this);

            imageButton.setImageResource(mIconCategory.get(i));
            imageButton.setScaleType(ImageButton.ScaleType.CENTER_INSIDE);

            int customColor = Color.LTGRAY;

            // Tạo một Drawable từ code Java với màu sắc mới
            GradientDrawable drawableImageButton = new GradientDrawable();
            drawableImageButton.setShape(GradientDrawable.OVAL);
            drawableImageButton.setColor(customColor);
            imageButton.setBackground(drawableImageButton);
            imageButton.setTag(customColor);

            // Thêm sự kiện click cho ImageButton
            imageButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    btnSelect.setAlpha(1f);
                    btnSelect.setEnabled(true);

                    setBackgroundPreviousSelectedIcon();

                    // Thiết lập Background cho topic đang chọn
                    setBackgroundCurrentSelectedIcon(linearLayout);

                    // Lưu trữ topic được chọn
                    saveSelectedTopic(linearLayout);
                }
            });

            // Thiết lập kích thước của ImageButton
            LinearLayout.LayoutParams paramsImageButton = new LinearLayout.LayoutParams(widthItem, widthItem);
            paramsImageButton.setMargins(0, 25, 0, 25);
            imageButton.setLayoutParams(paramsImageButton);

            // Thêm ImageButton vào LinearLayout
            linearLayout.addView(imageButton);

            // Thêm LinearLayout vào GridLayout
            gridLayoutIcon.addView(linearLayout);
        }

        GridLayout.LayoutParams layoutParams = new GridLayout.LayoutParams();
        layoutParams.setMargins(0, 0, 0, 50);
        gridLayoutIcon.setLayoutParams(layoutParams);

        return gridLayoutIcon;
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
        if (mSelectedLinearLayoutIcon != null) {
            mSelectedLinearLayoutIcon.setBackgroundColor(Color.WHITE);
        }
    }
    private void saveSelectedTopic(LinearLayout linearLayout) {
        mSelectedLinearLayoutIcon = linearLayout;
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
}