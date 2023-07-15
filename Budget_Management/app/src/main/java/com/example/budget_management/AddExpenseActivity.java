package com.example.budget_management;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.text.InputType;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.EditText;
import android.widget.GridLayout;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.budget_management.Model.Catalog;
import com.example.budget_management.Model.Data;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class AddExpenseActivity extends AppCompatActivity {
    private final int AMOUNT_ITEM_CATALOG = 8;
    private final int REQUEST_CODE_EXPENSE_CATALOG = 10;
    //Firebase
    private FirebaseAuth mAuth;
    private DatabaseReference mExpenseDatabase;
    private GridLayout gridLayout;
    private LinearLayout mSelectedLinearLayoutCatalog;
    private TextView mSelectedTextView;
    private Catalog mSelectedCatalog;
    private ArrayList<Catalog> mCatalogExpense;
    private ArrayList<LinearLayout> mLinearLayouts;
    private LinearLayout linearLayout1, linearLayout2, linearLayout3;
    private TextView textDate1, textDate2, textDate3;
    private TextView textDescription1, textDescription2, textDescription3;
    private ImageButton imageButtonCalendar;
    private Date mSelectedDate;
    private Date date3;
    private EditText mEditTextDescription;
    private EditText mEditTextMoney;
    private Button btnAdd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_expense);

        init();

        ArrayList<Catalog> tmpCatalogExpense = new ArrayList<>(mCatalogExpense);
        createGridViewCatalog(tmpCatalogExpense, AMOUNT_ITEM_CATALOG);

        createLinearLayoutDate();

        createImageButtonCalendar();

        createButtonAddClick();
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            int positionFromExpenseCatalogActivity = data.getIntExtra("SelectedExtendIcon", 10000);
            if (positionFromExpenseCatalogActivity < mCatalogExpense.size()) {
                setBackgroundPreviousSelectedIcon();
                ArrayList<Catalog> tmpCatalogExpense = new ArrayList<>(mCatalogExpense);
                createGridViewCatalog(changedCatalogExpense(tmpCatalogExpense, positionFromExpenseCatalogActivity), AMOUNT_ITEM_CATALOG);
            }
        }
    }
    private void init() {
        mLinearLayouts = new ArrayList<>();

        Catalog catalog = new Catalog("Ăn uống", "#FFC107", "Chi phí", getImageResource(0));
        Catalog catalog1 = new Catalog("Đi lại", "#2196F3", "Chi phí", getImageResource(1));
        Catalog catalog2 = new Catalog("Quà tặng", "#673AB7", "Chi phí", getImageResource(2));
        Catalog catalog3 = new Catalog("Giải trí", "#F44336", "Chi phí", getImageResource(3));
        Catalog catalog4 = new Catalog("Học tập", "#4CAF50", "Chi phí", getImageResource(4));
        Catalog catalog5 = new Catalog("Sức khỏe", "#9C27B0", "Chi phí", getImageResource(5));
        Catalog catalog6 = new Catalog("Quần áo", "#FF5722", "Chi phí", getImageResource(6));

        mCatalogExpense = new ArrayList<Catalog>();
        mCatalogExpense.add(catalog);
        mCatalogExpense.add(catalog1);
        mCatalogExpense.add(catalog2);
        mCatalogExpense.add(catalog3);
        mCatalogExpense.add(catalog4);
        mCatalogExpense.add(catalog5);
        mCatalogExpense.add(catalog6);

        gridLayout = findViewById(R.id.gridLayout);

        linearLayout1 = findViewById(R.id.linearLayout1);
        linearLayout2 = findViewById(R.id.linearLayout2);
        linearLayout3 = findViewById(R.id.linearLayout3);

        textDate1 = findViewById(R.id.textDate1);
        textDate2 = findViewById(R.id.textDate2);
        textDate3 = findViewById(R.id.textDate3);

        textDescription1 = findViewById(R.id.textDescription1);
        textDescription2 = findViewById(R.id.textDescription2);
        textDescription3 = findViewById(R.id.textDescription3);

        imageButtonCalendar = findViewById(R.id.imgBtnCalendar);

        mEditTextDescription = findViewById(R.id.editDescription);

        mEditTextMoney = findViewById(R.id.editMoney);
        mEditTextMoney.setInputType(InputType.TYPE_CLASS_NUMBER);

        btnAdd = findViewById(R.id.btnAddNoteExpense);

        // Chọn ngày hôm nay
        setSelectedLinearLayoutDate(1);

        // Lấy ngày hôm nay
        Calendar calendar = Calendar.getInstance();
        // Trừ đi một ngày
        calendar.add(Calendar.DAY_OF_MONTH, -2);
        // Lấy ngày hôm kia
        Date previousDate2 = calendar.getTime();
        // Gán ngày hôm kia cho biến date3
        date3 = previousDate2;
        //Database
        mAuth= FirebaseAuth.getInstance();
        //New fix
        FirebaseUser mUser=mAuth.getCurrentUser();
        String uid=mUser.getUid();

        mExpenseDatabase= FirebaseDatabase.getInstance().getReference().child("ExpenseData").child(uid);

        mExpenseDatabase.keepSynced(true);
    }
    private void createGridViewCatalog(ArrayList<Catalog> mCatalogExpense, int amountItem) {
        // Thiết lập số cột của GridLayout là 4
        gridLayout.setColumnCount(4);

        // Tính toán chiều rộng cột cho các thiết bị có độ rộng màn hình khác nhau
        int countColumn = 4;
        int screenWidthPx = getResources().getDisplayMetrics().widthPixels - 50;
        int columnWidthPx = screenWidthPx / countColumn;
        int widthItem = screenWidthPx / 6;

        mLinearLayouts.clear();

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
            linearLayout.setBackground(drawableLinearLayout);

            GridLayout.LayoutParams params = new GridLayout.LayoutParams();
            params.width = columnWidthPx;
            params.height = GridLayout.LayoutParams.WRAP_CONTENT;
            params.columnSpec = GridLayout.spec(i % countColumn);
            params.rowSpec = GridLayout.spec(i / countColumn);
            linearLayout.setLayoutParams(params);
            linearLayout.setPadding(0, 10, 0, 20);

            mLinearLayouts.add(linearLayout);

            // Thêm ImageButton vào LinearLayout
            ImageButton imageButton = new ImageButton(this);
            if (i != amountItem - 1) {
                imageButton.setImageResource(mCatalogExpense.get(i).getIcon());
            }
            else imageButton.setImageResource(R.drawable.ic_extend_catalog);
            imageButton.setScaleType(ImageButton.ScaleType.CENTER_INSIDE);

            int customColor;
            if (i != amountItem - 1) {
                customColor = Color.parseColor(mCatalogExpense.get(i).getColor());
            }
            else customColor = Color.LTGRAY;

            // Tạo một Drawable với màu sắc mới cho ImageButton
            GradientDrawable drawableImageButton = new GradientDrawable();
            drawableImageButton.setShape(GradientDrawable.OVAL);
            drawableImageButton.setColor(customColor);
            imageButton.setBackground(drawableImageButton);
            imageButton.setTag(customColor);

            // Thêm sự kiện click cho ImageButton
            if(i != amountItem - 1) {
                imageButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        setBackgroundPreviousSelectedIcon();

                        // Lấy tên topic của icon được chọn
                        ViewGroup viewGroup = (ViewGroup) v.getParent();
                        TextView textView = (TextView) viewGroup.getChildAt(1);
                        // Lấy màu của ImageButton được click
                        String tmp = textView.getText().toString();
                        for (int i = 0; i < mCatalogExpense.size(); i++) {
                            if (tmp == mCatalogExpense.get(i).getName()) {
                                mSelectedCatalog = mCatalogExpense.get(i);
                            }
                        }

                        // Thiết lập Background cho topic đang chọn
                        setBackgroundCurrentSelectedIcon(linearLayout, mSelectedCatalog.getColor(), textView);

                        // Lấy vị trí của LinearLayout trong danh sách
                        int currentPosition = mLinearLayouts.indexOf(linearLayout) + 1;
                        pushIntentToExpenseCatalogActivity(currentPosition);

                        // Lưu trữ topic được chọn
                        saveSelectedTopic(linearLayout, textView);
                    }
                });
            }
            else {
                imageButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        // Lấy vị trí của LinearLayout trong danh sách
                        int currentPosition = mLinearLayouts.indexOf(linearLayout) + 1;
                        pushIntentToExpenseCatalogActivity(currentPosition);
                    }
                });
            }

            // Thiết lập kích thước của ImageButton
            LinearLayout.LayoutParams paramsImageButton = new LinearLayout.LayoutParams(widthItem, widthItem);
            paramsImageButton.setMargins(0, 0, 0, 20);
            imageButton.setLayoutParams(paramsImageButton);

            if (i == amountItem - 1) {
                int smallerSize = (int) (widthItem * 0.7);
                LinearLayout.LayoutParams smallerParams = new LinearLayout.LayoutParams(smallerSize, smallerSize);
                smallerParams.setMargins(0, (int) (widthItem * 0.15), 0, 20 + (int) (widthItem * 0.16));
                imageButton.setLayoutParams(smallerParams);
            }

            // Thêm ImageButton vào LinearLayout
            linearLayout.addView(imageButton);

            // Thêm TextView vào LinearLayout
            TextView textView = new TextView(this);
            if (i != amountItem - 1) {
                textView.setText(mCatalogExpense.get(i).getName());
            }
            else textView.setText("Xem thêm");
            textView.setTextColor(Color.BLACK);
            textView.setGravity(Gravity.CENTER);
            linearLayout.setTag(textView);
            linearLayout.addView(textView);

            // Thêm sự kiện click cho LinearLayout
            if (i != amountItem - 1) {
                linearLayout.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        setBackgroundPreviousSelectedIcon();

                        // Lấy màu của ImageButton được click
                        String tmp = textView.getText().toString();
                        for (int i = 0; i < mCatalogExpense.size(); i++) {
                            if (tmp == mCatalogExpense.get(i).getName()) {
                                mSelectedCatalog = mCatalogExpense.get(i);
                            }
                        }

                        // Thiết lập Background cho topic đang chọn
                        setBackgroundCurrentSelectedIcon((LinearLayout) v, mSelectedCatalog.getColor(), textView);

                        // Lưu trữ topic được chọn
                        saveSelectedTopic((LinearLayout) v, textView);
                    }
                });
            }

            // Thêm LinearLayout vào GridLayout
            gridLayout.addView(linearLayout);
        }
    }
    private void setBackgroundCurrentSelectedIcon(LinearLayout linearLayout, String selectedColor, TextView textView) {
        // Đặt màu cho tên Topic hiện tại
        textView.setTextColor(Color.WHITE);

        // Đặt màu nền cho tên Topic hiện tại
        GradientDrawable drawableLinearLayout = new GradientDrawable();
        drawableLinearLayout.setShape(GradientDrawable.RECTANGLE);
        drawableLinearLayout.setCornerRadii(new float[]{25, 25, 25, 25, 25, 25, 25, 25});
        drawableLinearLayout.setColor(Color.parseColor(selectedColor));
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
    private void createLinearLayoutDate() {
        // Lấy ngày hiện tại
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM", Locale.getDefault());
        String currentDate = dateFormat.format(calendar.getTime());

        // Thiết lập ngày hiện tại cho textDate1
        textDate1.setText(currentDate);

        // Thiết lập ngày trước đó cho textDate2
        calendar.add(Calendar.DAY_OF_MONTH, -1);
        String previousDate2 = dateFormat.format(calendar.getTime());
        textDate2.setText(previousDate2);

        // Thiết lập ngày trước đó cho textDate3
        calendar.add(Calendar.DAY_OF_MONTH, -1);
        String previousDate3 = dateFormat.format(calendar.getTime());
        textDate3.setText(previousDate3);

        // Thiết lập sự kiện click cho LinearLayout 1
        linearLayout1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setSelectedLinearLayoutDate(1);
            }
        });

        // Thiết lập sự kiện click cho LinearLayout 2
        linearLayout2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setSelectedLinearLayoutDate(2);
            }
        });

        // Thiết lập sự kiện click cho LinearLayout 3
        linearLayout3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setSelectedLinearLayoutDate(3);
            }
        });
    }
    private void createImageButtonCalendar() {
        imageButtonCalendar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Dialog dialog = new Dialog(AddExpenseActivity.this);
                dialog.setContentView(R.layout.dialog_calendar);

                // Định nghĩa giao diện bo góc
                GradientDrawable shape = new GradientDrawable();
                shape.setShape(GradientDrawable.RECTANGLE);
                shape.setCornerRadii(new float[] { 20, 20, 20, 20, 20, 20, 20, 20 });
                shape.setColor(Color.WHITE);

                // Áp dụng giao diện bo góc cho dialog
                dialog.getWindow().setBackgroundDrawable(shape);

                CalendarView calendarView = dialog.findViewById(R.id.calendarView);

                // Chuyển đổi Date thành ngày, tháng, năm riêng biệt
                Calendar calendarSetSelectedDate = Calendar.getInstance();
                calendarSetSelectedDate.setTime(mSelectedDate);
                int daySelected = calendarSetSelectedDate.get(Calendar.DAY_OF_MONTH);
                int monthSelected = calendarSetSelectedDate.get(Calendar.MONTH);
                int yearSelected = calendarSetSelectedDate.get(Calendar.YEAR);

                // Set ngày được chọn lên CalendarView
                calendarView.setDate(calendarSetSelectedDate.getTimeInMillis(), true, true);

                // Lấy ngày hôm nay
                Calendar calendar = Calendar.getInstance();
                int currentYear = calendar.get(Calendar.YEAR);
                int currentMonth = calendar.get(Calendar.MONTH);
                int currentDayOfMonth = calendar.get(Calendar.DAY_OF_MONTH);
                String currentDate = currentDayOfMonth + "/" + (currentMonth + 1);

                // Lấy ngày hôm qua
                calendar.add(Calendar.DAY_OF_MONTH, -1);
                currentMonth = calendar.get(Calendar.MONTH);
                currentDayOfMonth = calendar.get(Calendar.DAY_OF_MONTH);
                String previousDate = currentDayOfMonth + "/" + (currentMonth + 1);

                // Thiết lập ngày hôm nay cho TextView
                TextView textView = dialog.findViewById(R.id.textView);
                textView.setText(daySelected + "/" + (monthSelected + 1) + "/" + yearSelected);

                // Thiết lập giới hạn cho calendarView
                calendarView.setMaxDate(System.currentTimeMillis());

                calendarView.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
                    @Override
                    public void onSelectedDayChange(@NonNull CalendarView view, int year, int month, int dayOfMonth) {
                        textView.setText(daySelected + "/" + (monthSelected + 1) + "/" + yearSelected);
                        String tmp = dayOfMonth + "/" + (month + 1);
                        if (tmp.equals(currentDate)) {
                            setSelectedLinearLayoutDate(1);
                        }
                        else if (tmp.equals(previousDate)) {
                            setSelectedLinearLayoutDate(2);
                        }
                        else {
                            textDate3.setText(tmp);
                            textDescription3.setText("Đã chọn");

                            // Gán giá trị cho biến date3
                            Calendar selectedDateCalendar = Calendar.getInstance();
                            selectedDateCalendar.set(Calendar.YEAR, year);
                            selectedDateCalendar.set(Calendar.MONTH, month);
                            selectedDateCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                            date3 = selectedDateCalendar.getTime();

                            setSelectedLinearLayoutDate(3);
                        }
                        dialog.dismiss();
                    }
                });

                Button btnCancel = dialog.findViewById(R.id.btnCancel);
                btnCancel.setTextColor(Color.parseColor("#008000"));
                btnCancel.setBackgroundResource(R.drawable.button_background);
                btnCancel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                    }
                });
                dialog.show();
            }
        });
    }
    private void createButtonAddClick() {
        btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String id = mExpenseDatabase.push().getKey();

                // Chư kt điều kiện
                String tmpAmmount = mEditTextMoney.getText().toString().trim();
                int amount = Integer.parseInt(tmpAmmount);

                String type = mSelectedTextView.getText().toString().trim();

                String note = mEditTextDescription.getText().toString().trim();

                String colorCatalog = mSelectedCatalog.getColor();

                int iconCatalog = mSelectedCatalog.getIcon();

                SimpleDateFormat sdf = new SimpleDateFormat("MMM d, yyyy", Locale.ENGLISH);
                String mDate = sdf.format(mSelectedDate);

                Data data=new Data(amount,type,note,id,mDate, colorCatalog, iconCatalog);
                mExpenseDatabase.child(id).setValue(data);
                finish();
            }
        });
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
                return R.drawable.ic_add;
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
                return Color.parseColor("#607D8B");
            case 8:
                return Color.LTGRAY;
            default:
                return Color.parseColor("#FFFFFF");
        }
    }
    private GradientDrawable getGradientDrawableLinearLayoutDate() {
        // Tạo một GradientDrawable với góc bo và màu nền tùy chỉnh
        GradientDrawable gradientDrawable = new GradientDrawable();
        gradientDrawable.setShape(GradientDrawable.RECTANGLE);
        gradientDrawable.setCornerRadius(16); // Độ cong của góc bo, thay đổi giá trị radius theo nhu cầu
        gradientDrawable.setColor(Color.parseColor("#2E6930")); // Màu nền, thay đổi mã màu theo nhu cầu
        return gradientDrawable;
    }
    private void setSelectedLinearLayoutDate(int index) {
        // Lấy ngày hôm nay
        Calendar calendar = Calendar.getInstance();
        Date currentDate = calendar.getTime();
        switch (index) {
            case 1:
                linearLayout1.setBackground(getGradientDrawableLinearLayoutDate());

                textDate1.setTextColor(Color.WHITE);
                textDescription1.setTextColor(Color.WHITE);
                linearLayout2.setBackgroundColor(Color.WHITE);
                textDate2.setTextColor(Color.BLACK);
                textDescription2.setTextColor(Color.BLACK);
                linearLayout3.setBackgroundColor(Color.WHITE);
                textDate3.setTextColor(Color.BLACK);
                textDescription3.setTextColor(Color.BLACK);

                // Gán ngày hôm nay cho biến mdateSelected
                mSelectedDate = currentDate;
                break;
            case 2:
                linearLayout2.setBackground(getGradientDrawableLinearLayoutDate());

                linearLayout1.setBackgroundColor(Color.WHITE);
                textDate1.setTextColor(Color.BLACK);
                textDescription1.setTextColor(Color.BLACK);
                textDate2.setTextColor(Color.WHITE);
                textDescription2.setTextColor(Color.WHITE);
                linearLayout3.setBackgroundColor(Color.WHITE);
                textDate3.setTextColor(Color.BLACK);
                textDescription3.setTextColor(Color.BLACK);

                // Trừ đi một ngày
                calendar.add(Calendar.DAY_OF_MONTH, -1);
                // Lấy ngày hôm trước
                Date previousDate = calendar.getTime();
                // Gán ngày hôm trước cho biến mdateSelected
                mSelectedDate = previousDate;
                break;
            case 3:
                linearLayout3.setBackground(getGradientDrawableLinearLayoutDate());

                linearLayout1.setBackgroundColor(Color.WHITE);
                textDate1.setTextColor(Color.BLACK);
                textDescription1.setTextColor(Color.BLACK);
                linearLayout2.setBackgroundColor(Color.WHITE);
                textDate2.setTextColor(Color.BLACK);
                textDescription2.setTextColor(Color.BLACK);
                textDate3.setTextColor(Color.WHITE);
                textDescription3.setTextColor(Color.WHITE);

                mSelectedDate = date3;
                break;
        }
    }
    private void pushIntentToExpenseCatalogActivity(int currentPosition) {
        if (currentPosition == AMOUNT_ITEM_CATALOG) {
            Intent intent = new Intent(this, ExpenseCatalogActivity.class);
            if (mSelectedLinearLayoutCatalog != null) {
                int previousPosition = mLinearLayouts.indexOf(mSelectedLinearLayoutCatalog);
                intent.putExtra("PreviousSelectedIcon", previousPosition);
            }
            startActivityForResult(intent, REQUEST_CODE_EXPENSE_CATALOG);
        }
    }
    public ArrayList<Catalog> changedCatalogExpense(ArrayList<Catalog> catalogExpense, int index) {
        if (index >= 0 && index < catalogExpense.size()) {
            // Xóa phần tử tại vị trí index
            catalogExpense.remove(index);
            // Chèn phần tử vào vị trí đầu tiên
            catalogExpense.add(0, mCatalogExpense.get(index));
        }
        return catalogExpense;
    }
}