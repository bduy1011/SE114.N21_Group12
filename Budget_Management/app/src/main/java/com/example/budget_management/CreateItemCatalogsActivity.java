package com.example.budget_management;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
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

import com.example.budget_management.Model.Catalog;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

public class CreateItemCatalogsActivity extends AppCompatActivity {
    private final int AMOUNT_ITEM_CATALOG = 16;
    private final int RESPONSE_CODE_ICON_CATALOG = 12;
    private final int REQUEST_TO_ICON_CATEGORY = 13;
    private FirebaseAuth mAuth;
    private DatabaseReference mIncomeCategoryDatabase;
    private DatabaseReference mExpenseCategoryDatabase;

    private TextView tvNameItem;
    private RadioGroup rgTypeItem;

    private GridLayout gridLayoutIcon;
    private ArrayList<LinearLayout> mLinearLayoutIcon;

    private GridLayout gridLayoutColor;
    private ArrayList<LinearLayout> mLinearLayoutColor;

    private ArrayList<Integer> mIconCatalog;

    private Button btnAddItemCategory;

    private LinearLayout mSelectedLinearLayoutCatalog;

    private String mNameItemCatalog, mTypeItemCatalog;
    private int mIconItemCatalog = 0, mColorItemCatalog = 0;

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

//        if (requestCode == RESPONSE_CODE_ICON_CATALOG && resultCode == RESULT_OK) {
//            int positionFromExpenseCatalogActivity = data.getIntExtra("SelectedExtendIcon", 10000);
//            if (positionFromExpenseCatalogActivity < mCatalogExpense.size()) {
//                setBackgroundPreviousSelectedIcon();
//                ArrayList<String> tmpCatalogExpense = new ArrayList<>(mCatalogExpense);
//                createGridViewCatalog(changedCatalogExpense(tmpCatalogExpense, positionFromExpenseCatalogActivity), AMOUNT_ITEM_CATALOG - 1);
//            }
//        }
    }

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

        btnAddItemCategory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isCheckFillFullInfor()) {
                    String id = mIncomeCategoryDatabase.push().getKey();
                    Catalog catalog = new Catalog(mNameItemCatalog, mColorItemCatalog, mTypeItemCatalog, mIconItemCatalog);
                    if (catalog.getType() == "Chi phí")
                        mExpenseCategoryDatabase.child(id).setValue(catalog);
                    if (catalog.getType() == "Thu nhập")
                        mIncomeCategoryDatabase.child(id).setValue(catalog);
                }
            }
        });

        mIconCatalog = getImageResourcesFromDirectory();

        createGridViewCatalog(mIconCatalog, AMOUNT_ITEM_CATALOG);
    }

    private void init() {
        mLinearLayoutIcon = new ArrayList<>();

        gridLayoutIcon = findViewById(R.id.gridLayoutIcon);

        rgTypeItem = findViewById(R.id.radioGroupType);

        mIconCatalog = new ArrayList<>();

        btnAddItemCategory = findViewById(R.id.btnAddItemCategory);

        mAuth = FirebaseAuth.getInstance();
        //New fix
        FirebaseUser mUser = mAuth.getCurrentUser();
        String uid = mUser.getUid();

        mIncomeCategoryDatabase = FirebaseDatabase.getInstance().getReference().child("IncomeCatalogs").child(uid);
        mExpenseCategoryDatabase = FirebaseDatabase.getInstance().getReference().child("ExpenseCatalogs").child(uid);

        mIncomeCategoryDatabase.keepSynced(true);
        mExpenseCategoryDatabase.keepSynced(true);
    }

    private void createGridViewCatalog(ArrayList<Integer> mIconCatalog, int amountItem) {
        // Thiết lập số cột của GridLayout là 4
        gridLayoutIcon.setColumnCount(4);

        // Tính toán chiều rộng cột cho các thiết bị có độ rộng màn hình khác nhau
        int countColumn = 4;
        int screenWidthPx = getResources().getDisplayMetrics().widthPixels - 50;
        int columnWidthPx = screenWidthPx / countColumn;
        int widthItem = screenWidthPx / 6;

        for (int i = 0; i < amountItem; i++) {
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

            if (i != amountItem - 1) {
                imageButton.setImageResource(mIconCatalog.get(i));
            }
            else imageButton.setImageResource(R.drawable.dots_icon);
            imageButton.setScaleType(ImageButton.ScaleType.CENTER_INSIDE);

            int customColor;
            if (i != amountItem - 1) {
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

            if (i != amountItem - 1) {
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

//                XỬ LÝ HÀM GỬI ĐI ĐẾN TRANG DANH SÁCH ICON (30P)
//                XỬ LÝ HÀM TRẢ VỀ TRANG TẠO ITEM (30P)
//                TẠO BẢNG MÀU (LÂU) 2 TIẾNG THÔI
//                => TẠO ITEM LÊN FIREBASE (30P)
//
//                => 3H30P (DẬY 9H, 10H LÀM - 12H30 => 1H30 ĐI)
//
//                TRƯỚC KHI ĐI CF
//
//                CÒN ĐI CF LÀ GIÚP PHÁT THÔI

                // Thêm sự kiện click cho ImageButton
                imageButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        int currentResIcon = 0;
                        Drawable drawable = imageButton.getDrawable();
                        if (drawable != null) {
                            Drawable.ConstantState constantState = drawable.getConstantState();
                            if (constantState != null) {
                                currentResIcon = constantState.hashCode();
                            }
                        }
                        pushIntentToExpenseCatalogActivity(currentResIcon);
                    }
                });
            }

            // Thiết lập kích thước của ImageButton
            LinearLayout.LayoutParams paramsImageButton = new LinearLayout.LayoutParams(widthItem, widthItem);
            paramsImageButton.setMargins(0, 20, 0, 20);
            imageButton.setLayoutParams(paramsImageButton);

            if (i == amountItem - 1) {
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
                if (mIconItemCatalog != 0) {
                    if (mColorItemCatalog != 0) {
                        return true;
                    }
                    else {
                        Toast.makeText(this, "Bạn chưa chọn màu cho danh mục!", Toast.LENGTH_SHORT).show();
                        return false;
                    }
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
    private ArrayList<Integer> getImageResourcesFromDirectory() {
        ArrayList<Integer> resourceList = new ArrayList<>();
        resourceList.add(R.drawable.icon_other_1);
        resourceList.add(R.drawable.icon_transportation_3);
        resourceList.add(R.drawable.icon_shopping_19);
        resourceList.add(R.drawable.icon_foodanddrink_4);
        resourceList.add(R.drawable.icon_entertainment_10);
        resourceList.add(R.drawable.icon_other_11);
        resourceList.add(R.drawable.icon_transportation_5);
        resourceList.add(R.drawable.icon_beauty_8);
        resourceList.add(R.drawable.icon_finances_10);
        resourceList.add(R.drawable.icon_finances_5);
        resourceList.add(R.drawable.icon_family_11);
        resourceList.add(R.drawable.icon_transportation_13);
        resourceList.add(R.drawable.icon_family_5);
        resourceList.add(R.drawable.icon_foodanddrink_2);
        resourceList.add(R.drawable.icon_family_6);

        return resourceList;
    }
    private void pushIntentToExpenseCatalogActivity(int currentResIcon) {
        Intent intent = new Intent(this, IconCatalogActivity.class);
        intent.putExtra("CurrentResIcon", currentResIcon);
        startActivityForResult(intent, REQUEST_TO_ICON_CATEGORY);
    }
}