package com.example.budget_management;

import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
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

public class AddIncomeActivity extends AppCompatActivity {
    //Firebase
    private FirebaseAuth mAuth;
    private DatabaseReference mIncomeDatabase;
    private DatabaseReference mExpenseDatabase;
    private GridLayout gridLayout;
    private LinearLayout mSelectedLinearLayoutCatalog;
    private TextView mSelectedTextView;
    private ArrayList<String> mCatalogExpense;
    private ArrayList<LinearLayout> mLinearLayouts;
    private LinearLayout linearLayout1, linearLayout2, linearLayout3;
    private TextView textDate1, textDate2, textDate3;
    private TextView textDescription1, textDescription2, textDescription3;
    private ImageButton imageButtonCalendar;
    private Date mSelectedDate;
    private Date date3;
    private EditText mEditTextDescription;
    private EditText mEditTextMoney;
    private Button btnThem;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_add_income_and_expense);

        init();

        createGridViewCatalog();

        createLinearLayoutDate();

        createImageButtonCalendar();

        createButtonThemClick();
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
        mCatalogExpense.add("Khác");

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

        btnThem = findViewById(R.id.btnThem);

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

        mIncomeDatabase= FirebaseDatabase.getInstance().getReference().child("IncomeData").child(uid);
        mExpenseDatabase= FirebaseDatabase.getInstance().getReference().child("ExpenseData").child(uid);

        mIncomeDatabase.keepSynced(true);
        mExpenseDatabase.keepSynced(true);
    }
    private void createGridViewCatalog() {
        // Thiết lập số cột của GridLayout là 4
        gridLayout.setColumnCount(4);

        // Tính toán chiều rộng cột cho các thiết bị có độ rộng màn hình khác nhau
        int countColumn = 4;
        int screenWidthPx = getResources().getDisplayMetrics().widthPixels - 50;
        int columnWidthPx = screenWidthPx / countColumn;
        int widthItem = screenWidthPx / 6;

        for (int i = 0; i < 8; i++) {
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

            imageButton.setImageResource(getImageResource(i));
            // Thiết lập ScaleType
            imageButton.setScaleType(ImageButton.ScaleType.CENTER_INSIDE);

            // Lấy màu sắc tùy chỉnh dựa trên chỉ số i
            int customColor = getTintColor(i);

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
                    // Lấy màu sắc của GradientDrawable
                    int selectedColor = (int) v.getTag();
                    // Thay đổi màu sắc của LinearLayout và TextView tương ứng
                    if (mSelectedLinearLayoutCatalog != null) {
                        mSelectedLinearLayoutCatalog.setBackgroundColor(Color.WHITE);
                    }
                    if (mSelectedTextView != null) {
                        mSelectedTextView.setTextColor(Color.BLACK);
                    }
                    GradientDrawable drawableLinearLayout = new GradientDrawable();
                    drawableLinearLayout.setShape(GradientDrawable.RECTANGLE);
                    drawableLinearLayout.setCornerRadii(new float[]{25, 25, 25, 25, 25, 25, 25, 25});
                    drawableLinearLayout.setColor(selectedColor);
                    // Đặt màu cho LinearLayout được click
                    linearLayout.setBackground(drawableLinearLayout);
                    // Lấy ViewGroup chứa TextView từ LinearLayout cha của ImageButton
                    ViewGroup viewGroup = (ViewGroup) v.getParent();
                    TextView textView = (TextView) viewGroup.getChildAt(1);
                    // Đặt màu cho TextView được click
                    textView.setTextColor(Color.WHITE);
                    // Lưu trữ LinearLayout và TextView được click
                    mSelectedLinearLayoutCatalog = linearLayout;
                    mSelectedTextView = textView;
                }
            });

            // Thiết lập kích thước của ImageButton
            LinearLayout.LayoutParams paramsImageButton = new LinearLayout.LayoutParams(widthItem, widthItem);
            paramsImageButton.setMargins(0, 0, 0, 20);
            imageButton.setLayoutParams(paramsImageButton);

            // Thêm ImageButton vào LinearLayout
            linearLayout.addView(imageButton);

            // Thêm TextView vào LinearLayout
            TextView textView = new TextView(this);
            textView.setText(mCatalogExpense.get(i));
            textView.setTextColor(Color.BLACK);
            textView.setGravity(Gravity.CENTER);
            linearLayout.setTag(textView);
            linearLayout.addView(textView);

            // Đăng ký sự kiện click cho LinearLayout
            linearLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // Nếu LinearLayout đã chọn trước đó khác với LinearLayout hiện tại
                    if (mSelectedLinearLayoutCatalog != v) {
                        // Đặt màu trắng cho LinearLayout đã chọn trước đó
                        if (mSelectedLinearLayoutCatalog != null) {
                            mSelectedLinearLayoutCatalog.setBackgroundColor(Color.WHITE);
                        }
                        // Đặt màu đen cho TextView đã chọn trước đó
                        if (mSelectedTextView != null) {
                            mSelectedTextView.setTextColor(Color.BLACK);
                        }
                        // Lưu trữ LinearLayout hiện tại vào biến mSelectedLinearLayout
                        mSelectedLinearLayoutCatalog = (LinearLayout) v;
                        mSelectedTextView = textView;
                        // Lấy màu của ImageButton được click
                        int selectedColor = 100;
                        String tmp = textView.getText().toString();
                        for (int i = 0; i < 8; i++) {
                            if (tmp == mCatalogExpense.get(i)) selectedColor = getTintColor(i);
                        }
                        // Đặt màu cho tên Topic hiện tại
                        textView.setTextColor(Color.WHITE);
                        // Đặt màu cho LinearLayout hiện tại
                        v.setBackgroundColor(Color.WHITE);
                        GradientDrawable drawableLinearLayout = new GradientDrawable();
                        drawableLinearLayout.setShape(GradientDrawable.RECTANGLE);
                        drawableLinearLayout.setCornerRadii(new float[]{25, 25, 25, 25, 25, 25, 25, 25});
                        drawableLinearLayout.setColor(selectedColor);
                        v.setBackground(drawableLinearLayout);
                    }
                }
            });

            // Thêm LinearLayout vào GridLayout
            gridLayout.addView(linearLayout);
        }
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
                Dialog dialog = new Dialog(AddIncomeActivity.this);
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
    private void createButtonThemClick() {
        btnThem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String id=mIncomeDatabase.push().getKey();

                String tmAmmount = mEditTextMoney.getText().toString().trim();
                String tmtype = mSelectedTextView.getText().toString().trim();
                String tmnote = mEditTextDescription.getText().toString().trim();
                int inamount=Integer.parseInt(tmAmmount);

                SimpleDateFormat sdf = new SimpleDateFormat("MMM d, yyyy", Locale.ENGLISH);
                String mDate = sdf.format(mSelectedDate);

                Data data=new Data(inamount,tmtype,tmnote,id,mDate);
                mIncomeDatabase.child(id).setValue(data);
                finish();
            }
        });
    }
    private int getImageResource(int index) {
        switch (index) {
            case 0:
                return R.drawable.ic_expense_fastfood;
            case 1:
                return R.drawable.ic_expense_bus;
            case 2:
                return R.drawable.ic_expense_gift;
            case 3:
                return R.drawable.ic_expense_game;
            case 4:
                return R.drawable.ic_expense_book;
            case 5:
                return R.drawable.ic_expense_health;
            case 6:
                return R.drawable.ic_expense_shirt;
            case 7:
                return R.drawable.ic_expense_question;
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
}