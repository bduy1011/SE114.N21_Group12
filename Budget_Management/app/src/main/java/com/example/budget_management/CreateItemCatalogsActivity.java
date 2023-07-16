package com.example.budget_management;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.GridLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.example.budget_management.Model.Catalog;
import com.example.budget_management.Other.TouchableWrapper;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

public class CreateItemCatalogsActivity extends AppCompatActivity {
    private final int AMOUNT_ITEM_CATALOG = 16;
    private final int REQUEST_CODE_ICON_CATEGORY = 12;
    private final int REQUEST_CODE_COLOR_CATEGORY = 13;
    private FirebaseAuth mAuth;
    private DatabaseReference mIncomeCategoryDatabase;
    private DatabaseReference mExpenseCategoryDatabase;
    private ImageView ivMainItemCreating;
    private TextView tvNameItem;
    private RadioGroup rgTypeItem;
    private GridLayout gridLayoutIcon;
    private GridLayout gridLayoutColor;
    private Button btnAddItemCategory;
    private ArrayList<LinearLayout> mLinearLayoutIcon;
    private ArrayList<ImageButton> mImageButtonColor;
    private ArrayList<Integer> mIconCatalog;
    private LinearLayout mSelectedLinearLayoutIcon;
    private ImageButton mSelectedImageButtonIcon;
    private ImageButton mSelectedImageButtonColor;
    private String mNameSelectedItemCatalog, mTypeSelectedItemCatalog, mColorSelectedItemCatalog;
    private int mIconSelectedItemCatalog = 0;

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_ICON_CATEGORY && resultCode == RESULT_OK) {
            int selectedIcon = data.getIntExtra("SelectedIcon", 0);
            mIconSelectedItemCatalog = selectedIcon;
            boolean flag = false;
            for (int i : getImageResources())
                if (i == mIconSelectedItemCatalog) {
                    flag = true;
                    break;
                }
            if (flag) {
                setBackgroundPreviousSelectedIcon();
                mSelectedLinearLayoutIcon = mLinearLayoutIcon.get(getImageResources().indexOf(selectedIcon));
                mSelectedImageButtonIcon = (ImageButton) mSelectedLinearLayoutIcon.getTag();
                setBackgroundCurrentSelectedIcon(mColorSelectedItemCatalog);
            }
            else {
                setBackgroundPreviousSelectedIcon();
                mSelectedImageButtonIcon = null;
                mSelectedLinearLayoutIcon = null;
            }
        } else if (requestCode == REQUEST_CODE_COLOR_CATEGORY && resultCode == RESULT_OK) {
            String selectedColor = data.getStringExtra("SelectedColor");
            mColorSelectedItemCatalog = selectedColor;
            setBackgroundCurrentSelectedIcon(mColorSelectedItemCatalog);
        }
        updateMainItemCreating(mIconSelectedItemCatalog, mColorSelectedItemCatalog);
        checkEnableButton();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_item_catalogs);

        init();

        mIconCatalog = getImageResources();

        createMainItemCreating(R.drawable.ic_category, "#a4b7b1");

        createGridViewIcon(mIconCatalog, AMOUNT_ITEM_CATALOG);

        createGridViewColor();

        createListenerControl();
    }
    private void init() {
        ivMainItemCreating = findViewById(R.id.imageViewMainItemCreating);
        tvNameItem = findViewById(R.id.textViewNameItem);
        rgTypeItem = findViewById(R.id.radioGroupType);
        gridLayoutIcon = findViewById(R.id.gridLayoutIcon);
        gridLayoutColor = findViewById(R.id.gridLayoutColor);
        btnAddItemCategory = findViewById(R.id.btnAddItemCategory);
        btnAddItemCategory.setAlpha(0.5f);
        btnAddItemCategory.setEnabled(false);

        mIconCatalog = new ArrayList<>();
        mLinearLayoutIcon = new ArrayList<>();
        mImageButtonColor = new ArrayList<>();

        mAuth = FirebaseAuth.getInstance();
        FirebaseUser mUser = mAuth.getCurrentUser();
        String uid = mUser.getUid();
        mIncomeCategoryDatabase = FirebaseDatabase.getInstance().getReference().child("IncomeCatalogs").child(uid);
        mExpenseCategoryDatabase = FirebaseDatabase.getInstance().getReference().child("ExpenseCatalogs").child(uid);
        mIncomeCategoryDatabase.keepSynced(true);
        mExpenseCategoryDatabase.keepSynced(true);
    }
    private void createGridViewIcon(ArrayList<Integer> mIconCatalog, int amountItem) {
        // Thiết lập số cột của GridLayout là 4
        int countColumn = 4;
        gridLayoutIcon.setColumnCount(countColumn);

        // Tính toán chiều rộng cột cho các thiết bị có độ rộng màn hình khác nhau
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

            ImageButton imageButton = new ImageButton(this);

            linearLayout.setTag(imageButton);
            mLinearLayoutIcon.add(linearLayout);

            if (i != amountItem - 1) {
                imageButton.setImageResource(mIconCatalog.get(i));
            }
            else imageButton.setImageResource(R.drawable.ic_dots);
            imageButton.setScaleType(ImageButton.ScaleType.CENTER_INSIDE);

            String customColor;
            if (i != amountItem - 1) {
                customColor = "#a4b7b1";
            }
            else {
                customColor = "#fdc22a";
            }

            // Tạo một Drawable từ code Java với màu sắc mới
            GradientDrawable drawableImageButton = new GradientDrawable();
            drawableImageButton.setShape(GradientDrawable.OVAL);
            drawableImageButton.setColor(Color.parseColor(customColor));
            imageButton.setBackground(drawableImageButton);
            if (i != amountItem - 1)
                imageButton.setTag(getImageResources().get(i));

            if (i != amountItem - 1) {
                // Thêm sự kiện click cho ImageButton
                imageButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mIconSelectedItemCatalog = (int) ((ImageButton) v).getTag();

                        setBackgroundPreviousSelectedIcon();

                        mSelectedImageButtonIcon = (ImageButton) v;
                        mSelectedLinearLayoutIcon = linearLayout;

                        // Thiết lập Background cho topic đang chọn
                        setBackgroundCurrentSelectedIcon(mColorSelectedItemCatalog);

                        updateMainItemCreating(mIconSelectedItemCatalog, mColorSelectedItemCatalog);

                        checkEnableButton();
                    }
                });
            }
            else {
                // Thêm sự kiện click cho ImageButton
                imageButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        sendIntent(mIconSelectedItemCatalog);
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
    private void createGridViewColor() {
        int countColumn = 8;

        gridLayoutColor.setColumnCount(countColumn);

        int screenWidthPx = getResources().getDisplayMetrics().widthPixels - 50;
        int columnWidthPx = screenWidthPx / countColumn;
        int widthItem = screenWidthPx / 6;

        int amount = 8;

        for (int i = 0; i < amount; i++) {
            ImageButton imageButton = new ImageButton(this);
            imageButton.setScaleType(ImageButton.ScaleType.CENTER_INSIDE);
            String customColor;
            if (i != amount - 1) {
                customColor = getColorByIndex(i);
            } else {
                customColor = getColorByIndex(1000);
                imageButton.setImageResource(R.drawable.ic_add_1);
            }

            if (i != amount - 1) {
                imageButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if(mSelectedImageButtonColor != null) {
                            mSelectedImageButtonColor.setImageResource(0);
                        }
                        mColorSelectedItemCatalog = (String) ((ImageButton) v).getTag();
                        mSelectedImageButtonColor = (ImageButton) v;
                        mSelectedImageButtonColor.setImageResource(R.drawable.ic_tick);
                        updateMainItemCreating(mIconSelectedItemCatalog, mColorSelectedItemCatalog);
                        setBackgroundCurrentSelectedIcon(mColorSelectedItemCatalog);
                        checkEnableButton();
                    }
                });
            } else {
                imageButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(v.getContext(), ColorCatalogActivity.class);
                        if (mColorSelectedItemCatalog != null) {
                            intent.putExtra("CurrentResColor", mColorSelectedItemCatalog);
                        }
                        startActivityForResult(intent, REQUEST_CODE_COLOR_CATEGORY);
                    }
                });
            }

            float percent = 0.5f;
            int with = (int) (widthItem * percent);
            int marginH = (int) (widthItem * (percent / 4));
            LinearLayout.LayoutParams paramsImageButton = new LinearLayout.LayoutParams(with, with);
            paramsImageButton.setMargins(marginH, 20, marginH, 20);
            imageButton.setLayoutParams(paramsImageButton);

            GradientDrawable drawableImageButton = new GradientDrawable();
            drawableImageButton.setShape(GradientDrawable.OVAL);
            drawableImageButton.setColor(Color.parseColor(customColor));
            imageButton.setBackground(drawableImageButton);

            imageButton.setTag(customColor);

            mImageButtonColor.add(imageButton);

            gridLayoutColor.addView(imageButton);
        }
    }
    private void setBackgroundCurrentSelectedIcon(String color) {
        if (mSelectedLinearLayoutIcon != null && mSelectedImageButtonIcon != null) {
            GradientDrawable drawableLinearLayout = new GradientDrawable();
            drawableLinearLayout.setShape(GradientDrawable.RECTANGLE);
            drawableLinearLayout.setCornerRadius(20);
            drawableLinearLayout.setColor(Color.WHITE);
            drawableLinearLayout.setStroke(4, Color.DKGRAY);
            mSelectedLinearLayoutIcon.setBackground(drawableLinearLayout);

            GradientDrawable drawableImageButton = new GradientDrawable();
            drawableImageButton.setShape(GradientDrawable.OVAL);
            if (!TextUtils.isEmpty(color))
                drawableImageButton.setColor(Color.parseColor(color));
            else
                drawableImageButton.setColor(Color.parseColor("#a4b7b1"));
            mSelectedImageButtonIcon.setBackground(drawableImageButton);
        }
    }
    private void setBackgroundPreviousSelectedIcon() {
        // Đặt màu trắng cho LinearLayout đã chọn trước đó
        if (mSelectedLinearLayoutIcon != null) {
            mSelectedLinearLayoutIcon.setBackgroundColor(Color.WHITE);
        }
        if (mSelectedImageButtonIcon != null) {
            GradientDrawable drawableImageButton = new GradientDrawable();
            drawableImageButton.setShape(GradientDrawable.OVAL);
            drawableImageButton.setColor(Color.parseColor("#a4b7b1"));
            mSelectedImageButtonIcon.setBackground(drawableImageButton);
        }
    }
    private boolean isCheckFillFullInform() {
        if (!TextUtils.isEmpty(mNameSelectedItemCatalog)) {
            if (!TextUtils.isEmpty(mTypeSelectedItemCatalog)) {
                if (mIconSelectedItemCatalog != 0) {
                    if (!TextUtils.isEmpty(mColorSelectedItemCatalog)) {
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
    private void checkEnableButton() {
        if (!TextUtils.isEmpty(mNameSelectedItemCatalog))
            if (!TextUtils.isEmpty(mTypeSelectedItemCatalog))
                if (mIconSelectedItemCatalog != 0)
                    if (!TextUtils.isEmpty(mColorSelectedItemCatalog)) {
                        btnAddItemCategory.setAlpha(1f);
                        btnAddItemCategory.setEnabled(true);
                        return;
                    }
        btnAddItemCategory.setAlpha(0.5f);
        btnAddItemCategory.setEnabled(false);
    }
    private ArrayList<Integer> getImageResources() {
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
    private void sendIntent(int currentResIcon) {
        Intent intent = new Intent(this, IconCatalogActivity.class);
        intent.putExtra("CurrentResIcon", currentResIcon);
        startActivityForResult(intent, REQUEST_CODE_ICON_CATEGORY);
    }
    public String getColorByIndex(int index) {
        String color;

        switch (index) {
            case 0:
                color = "#80cf5c";
                break;
            case 1:
                color = "#5162f6";
                break;
            case 2:
                color = "#f1b109";
                break;
            case 3:
                color = "#eb54c8";
                break;
            case 4:
                color = "#36d9d8";
                break;
            case 5:
                color = "#de2020";
                break;
            case 6:
                color = "#9d7ef3";
                break;
            default:
                color = "#a4b7b1";
                break;
        }
        return color;
    }
    private void createListenerControl() {
        TouchableWrapper touchableWrapper = findViewById(R.id.touchableWrapper);
        touchableWrapper.setTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                int action = motionEvent.getAction();
                if (action == MotionEvent.ACTION_DOWN) {
                    tvNameItem.clearFocus();
                    hideKeyboard();
                }
                return false;
            }
        });
        tvNameItem.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // Không cần thực hiện gì trước khi thay đổi văn bản
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // Thực hiện hành động khi văn bản đang được thay đổi
                mNameSelectedItemCatalog = s.toString();
                checkEnableButton();
            }

            @Override
            public void afterTextChanged(Editable s) {
                // Không cần thực hiện gì sau khi thay đổi văn bản
                mNameSelectedItemCatalog = s.toString();
                checkEnableButton();
            }
        });
        rgTypeItem.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                // Xử lý sự kiện khi có RadioButton được chọn
                if (checkedId == R.id.radioButtonExpense) {
                    mTypeSelectedItemCatalog = "Chi phí";
                } else if (checkedId == R.id.radioButtonIncome) {
                    mTypeSelectedItemCatalog = "Thu nhập";
                }
                checkEnableButton();
            }
        });
        btnAddItemCategory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isCheckFillFullInform()) {
                    String id = mIncomeCategoryDatabase.push().getKey();
                    Catalog catalog = new Catalog(mNameSelectedItemCatalog, mColorSelectedItemCatalog, mTypeSelectedItemCatalog, mIconSelectedItemCatalog);
                    if (catalog.getType() == "Chi phí");
                    mExpenseCategoryDatabase.child(id).setValue(catalog);
                    if (catalog.getType() == "Thu nhập");
                    mIncomeCategoryDatabase.child(id).setValue(catalog);

                    Intent intent = new Intent(view.getContext(), AddExpenseActivity.class);
                    String name = catalog.getName();
                    String color = catalog.getColor();
                    String type = catalog.getType();
                    int icon = catalog.getIcon();
                    intent.putExtra("name", name);
                    intent.putExtra("color", color);
                    intent.putExtra("type", type);
                    intent.putExtra("icon", icon);
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                    startActivity(intent);
                }
            }
        });
    }
    private void createMainItemCreating(int image, String color) {
        GradientDrawable background = new GradientDrawable();
        background.setShape(GradientDrawable.OVAL);
        background.setColor(Color.parseColor(color));
        ivMainItemCreating.setBackground(background);
        ivMainItemCreating.setScaleType(ImageButton.ScaleType.CENTER_INSIDE);

        ivMainItemCreating.setImageResource(image);
    }
    private Drawable imageChangedSize(int image) {
        Drawable drawable = getResources().getDrawable(image);

        // Kích thước gốc của ảnh
        int originalWidth = drawable.getIntrinsicWidth();
        int originalHeight = drawable.getIntrinsicHeight();

        // Kích thước mới sau khi thay đổi
        int newWidth, newHeight;

        // Tính toán kích thước mới
        if (originalWidth >= originalHeight) {
            // Khi chiều rộng lớn hơn hoặc bằng chiều cao
            newWidth = 70;
            newHeight = (int) (originalHeight * (70.0f / originalWidth));
        } else {
            // Khi chiều cao lớn hơn chiều rộng
            newWidth = (int) (originalWidth * (70.0f / originalHeight));
            newHeight = 70;
        }

        // Thay đổi kích thước ảnh
        Bitmap resizedBitmap = Bitmap.createScaledBitmap(
                drawableToBitmap(drawable),
                newWidth,
                newHeight,
                true
        );

        return new BitmapDrawable(getResources(), resizedBitmap);
    }
    private Bitmap drawableToBitmap(Drawable drawable) {
        if (drawable instanceof BitmapDrawable) {
            return ((BitmapDrawable) drawable).getBitmap();
        }

        Bitmap bitmap = Bitmap.createBitmap(
                drawable.getIntrinsicWidth(),
                drawable.getIntrinsicHeight(),
                Bitmap.Config.ARGB_8888
        );
        Canvas canvas = new Canvas(bitmap);
        drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
        drawable.draw(canvas);
        return bitmap;
    }
    private void updateMainItemCreating(int image, String color) {
        GradientDrawable background = new GradientDrawable();
        background.setShape(GradientDrawable.OVAL);
        if (image != 0) {
            ivMainItemCreating.setImageDrawable(imageChangedSize(image));
        }
        if (color != null) {
            background.setColor(Color.parseColor(color));
        }
        else background.setColor(Color.parseColor("#a4b7b1"));

        ivMainItemCreating.setBackground(background);
    }
    private void hideKeyboard() {
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(tvNameItem.getWindowToken(), 0);
    }
}