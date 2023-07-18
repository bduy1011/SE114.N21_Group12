package com.example.budget_management;

import static android.graphics.Color.parseColor;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.TintTypedArray;
import androidx.core.util.Pair;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.budget_management.Model.Data;
import com.github.mikephil.charting.animation.Easing;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.formatter.PercentFormatter;
import com.google.android.material.datepicker.MaterialDatePicker;
import com.google.android.material.datepicker.MaterialPickerOnPositiveButtonClickListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Random;

public class HomeFragment extends Fragment {

    //Floating Button
    private FloatingActionButton fab_main_btn;
    private FloatingActionButton fab_income_btn;
    private FloatingActionButton fab_expense_btn;
    //Floating button TV
    private TextView fab_income_txt;
    private TextView fab_expense_txt;
    private boolean isOpen=false;
    //Animation
    private Animation FadOpen,FadeClose;
    //Dashboard income and expense result
    private TextView totalIncomeResult;
    private TextView totalExpenseResult;
    //Firebase
    private FirebaseAuth mAuth;
    private DatabaseReference mIncomeDatabase;
    private DatabaseReference mExpenseDatabase;
    //Recycle view
    //private RecyclerView mRecyclerIncome;
    //private RecyclerView mRecyclerExpense;
    private RecyclerView mainRecycleView;
    //Adapter
    IncomeAdapter incomeAdapter;
    ExpenseAdapter expenseAdapter;

    //PieChart
    private PieChart mainChart;
    //Map and type list
    private List<Data> dataList;
    private List<Integer> expenseColorList;
    private List<Integer> incomeColorList;
    private Map<String, Float> typeIncomeAmountMap;
    private Map<String, Float> typeExpenseAmountMap;
    private Map<String, Integer> typeExpenseColorMap;
    private Map<String, Integer> typeExpenseIconMap;
    private Map<String, Integer> typeIncomeColorMap;
    private Map<String, Integer> typeIncomeIconMap;

    private int totalIncomeSum = 0;
    private int totalExpenseSum = 0;
    //Boolean
    private Boolean isIncome = false, isExpense = false;
    private Boolean isDayClick = false, isMonthClick = false, isYearClick = false, isCustomClick = false;
    //Date
    private Date sDate = null;
    private Date eDate = null;
    private MaterialDatePicker mtDatePicker;

    public HomeFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View myview=inflater.inflate(R.layout.fragment_home,container,false);
        totalExpenseResult = myview.findViewById(R.id.expense_set_result);
        totalIncomeResult = myview.findViewById(R.id.income_set_result);
        mAuth=FirebaseAuth.getInstance();
        FirebaseUser mUser=mAuth.getCurrentUser();
        String uid=mUser.getUid();
        mIncomeDatabase= FirebaseDatabase.getInstance().getReference().child("IncomeData").child(uid);
        mExpenseDatabase=
        FirebaseDatabase.getInstance().getReference().child("ExpenseData").child(uid);
        mIncomeDatabase.keepSynced(true);
        mExpenseDatabase.keepSynced(true);

        if(isExpense == isIncome){
            isDayClick = true;
            isIncome = true;
            loadIncomePieChart(sDate, eDate);
        }

        //Connect button to layout
        fab_main_btn=myview.findViewById(R.id.fb_main_plus_btn);
        fab_income_btn=myview.findViewById(R.id.income_Ft_btn);
        fab_expense_btn=myview.findViewById(R.id.expense_Ft_btn);
        //Connect floating text;
        fab_income_txt=myview.findViewById(R.id.income_ft_text);
        fab_expense_txt=myview.findViewById(R.id.expense_ft_text);
        //Total value set
        mainRecycleView = myview.findViewById(R.id.main_recycleview);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        layoutManager.setReverseLayout(true);
        layoutManager.setStackFromEnd(true);
        mainRecycleView.setHasFixedSize(true);
        mainRecycleView.setLayoutManager(layoutManager);
        //Pie Chart
        mainChart = myview.findViewById(R.id.main_chart);
        //Animation connect
        FadOpen= AnimationUtils.loadAnimation(getActivity(),R.anim.fade_open);
        FadeClose=AnimationUtils.loadAnimation(getActivity(),R.anim.fade_close);
        //Button on PieChart
        Button dayBtn = myview.findViewById(R.id.today_btn);
        Button mothBtn = myview.findViewById(R.id.month_btn);
        Button yearBtn = myview.findViewById(R.id.year_btn);
        Button cusBtn = myview.findViewById(R.id.custom_btn);

        dayBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isDayClick = true;
                isMonthClick = false;
                isYearClick = false;
                isCustomClick = false;
                if(isIncome){
                    loadIncomePieChart(sDate,eDate);
                }
                if(isExpense){
                    loadExpensePieChart(sDate,eDate);
                }
            }
        });
        mothBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isDayClick = false;
                isMonthClick = true;
                isYearClick = false;
                isCustomClick = false;
                if(isIncome){
                    loadIncomePieChart(sDate,eDate);
                }
                if(isExpense){
                    loadExpensePieChart(sDate,eDate);
                }
            }
        });
        yearBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isDayClick = false;
                isMonthClick = false;
                isYearClick = true;
                isCustomClick = false;
                if(isIncome){
                    loadIncomePieChart(sDate,eDate);
                }
                if(isExpense){
                    loadExpensePieChart(sDate,eDate);
                }
            }
        });
        cusBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isDayClick = false;
                isMonthClick = false;
                isYearClick = false;
                isCustomClick = true;

                MaterialDatePicker.Builder<androidx.core.util.Pair<Long, Long>> builder = MaterialDatePicker.Builder.dateRangePicker().setTitleText("Select Day Range");
                mtDatePicker = builder.build();
                mtDatePicker.setShowsDialog(true);
                AlertDialog.Builder myDialog = new AlertDialog.Builder(getActivity());

                mtDatePicker.show(getChildFragmentManager(), "DATE_PICKER");
                mtDatePicker.addOnPositiveButtonClickListener(new MaterialPickerOnPositiveButtonClickListener() {
                    @Override
                    public void onPositiveButtonClick(Object selection) {
                        androidx.core.util.Pair<Long, Long> dateRange = (Pair<Long, Long>) selection;
                        Long startDateInMillis = dateRange.first;
                        Long endDateInMillis = dateRange.second;
                        Calendar calendar = Calendar.getInstance();
                        calendar.setTime(new Date(startDateInMillis));
                        calendar.set(Calendar.HOUR_OF_DAY, 0);
                        calendar.set(Calendar.MINUTE, 0);
                        calendar.set(Calendar.SECOND, 0);
                        sDate = calendar.getTime();
                        calendar.setTime(new Date(endDateInMillis));
                        calendar.set(Calendar.HOUR_OF_DAY, 23);
                        calendar.set(Calendar.MINUTE, 59);
                        calendar.set(Calendar.SECOND, 59);
                        eDate = calendar.getTime();

                        if(isIncome){
                            loadIncomePieChart(sDate,eDate);
                        }
                        if(isExpense){
                            loadExpensePieChart(sDate,eDate);
                        }
                    }
                });
            }
        });

        //Load data from begin
        totalExpenseResult.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isExpense = true;
                isIncome = false;
                loadExpensePieChart(sDate,eDate);
            }
        });
        totalIncomeResult.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isIncome = true;
                isExpense = false;
                loadIncomePieChart(sDate,eDate);
            }
        });
        fab_main_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addData();
            }
        });

        return myview;
    }
    //Floating button animation
    private void addData() {
        if (isIncome) {
            incomeDataInsert();
        }
        if(isExpense){
            expenseDataInsert();
        }
    }
    public void incomeDataInsert() {
        Intent intentIncome = new Intent(getContext(), AddIncomeActivity.class);
        startActivity(intentIncome);
    }
    public void expenseDataInsert(){
        Intent intentExpense = new Intent(getContext(), AddExpenseActivity.class);
        startActivity(intentExpense);
    }
    @NonNull
    public static String formatCurrency(int amount) {
        String currency;
        if (amount >= 1000000000) {
            currency = String.format("%.2fB", amount / 1e9);
        } else if (amount >= 1000000) {
            currency = String.format("%.2fM", amount / 1e6);
        } else if (amount >= 1000) {
            currency = String.format("%.2fk", amount / 1e3);
        } else {
            currency = String.format("%dđ", amount);
        }
        return currency;
    }
    @Override
    public  void onStart(){
        super.onStart();
    }

    @Override
    public void onStop() {
        super.onStop();
    }
    private int getColorForType(String color) {
        return 0;
    }
    private void loadIncomePieChart(@Nullable Date startDate, @Nullable Date endDate){
        mIncomeDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Random random = new Random();
                totalIncomeSum = 0;
                dataList = new ArrayList<>();
                incomeColorList = new ArrayList<>();
                typeIncomeIconMap = new HashMap<>();
                typeIncomeColorMap = new HashMap<>();
                typeIncomeAmountMap = new HashMap<>();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    Data data = dataSnapshot.getValue(Data.class);
                    DateFormat dateFormat = new SimpleDateFormat("MMM d, yyyy", Locale.ENGLISH);
                    try {
                        Date date = dateFormat.parse(data.getDate());

                        Calendar calendar = Calendar.getInstance();
                        int currentYear = calendar.get(Calendar.YEAR);
                        int currentMonth = calendar.get(Calendar.MONTH);
                        int currentDay = calendar.get(Calendar.DAY_OF_MONTH);
                        calendar.setTime(date);
                        int year = calendar.get(Calendar.YEAR);
                        int month = calendar.get(Calendar.MONTH);
                        int day = calendar.get(Calendar.DAY_OF_MONTH);
                        if(day == currentDay && month == currentMonth && year == currentYear && isDayClick) {
                            totalIncomeSum += data.getAmount();
                            totalIncomeResult.setText(formatCurrency(totalIncomeSum));
                            dataList.add(data);
                        } else if (month == currentMonth && year == currentYear && isMonthClick) {
                            totalIncomeSum +=data.getAmount();
                            totalIncomeResult.setText(formatCurrency(totalIncomeSum));
                            dataList.add(data);
                        } else if (year == currentYear && isYearClick) {
                            totalIncomeSum += data.getAmount();
                            totalIncomeResult.setText(formatCurrency(totalIncomeSum));
                            dataList.add(data);
                        }
                        else if (isCustomClick && date.compareTo(startDate) > 0 && date.compareTo(endDate) < 0) {
                            totalIncomeSum += data.getAmount();
                            totalIncomeResult.setText(formatCurrency(totalIncomeSum));
                            dataList.add(data);
                        }
                    } catch (ParseException e) {
                        throw new RuntimeException(e);
                    }
                }

                List<PieEntry> entries = new ArrayList<>();
                PieDataSet dataSet = new PieDataSet(null, "Biểu đồ tròn");
                for (Data data : dataList) {
                    String type = data.getType();
                    int color = Color.parseColor(data.getColor());
                    int icon = getFileFromDrawable(data.getIcon());
                    float amount = data.getAmount();
                    if (typeIncomeAmountMap.containsKey(type)) {
                        amount += typeExpenseAmountMap.get(type);
                    }
                    typeIncomeColorMap.put(type, color);
                    typeIncomeAmountMap.put(type, amount);
                    typeIncomeIconMap.put(type, icon);
                }

                for (Map.Entry<String, Float> entry : typeIncomeAmountMap.entrySet()) {
                    String type = entry.getKey();
                    float amount = entry.getValue();
                    incomeColorList.add(typeIncomeColorMap.get(type));
                    dataSet.addColor(typeIncomeColorMap.get(type));
                    entries.add(new PieEntry(amount, type));
                }
                dataSet.setColors(incomeColorList);
                dataSet.setValues(entries);
                PieData incomeData = new PieData(dataSet);
                incomeData.setValueTextSize(12f);
                incomeData.setValueTextColor(Color.BLACK);

                dataSet.setValueFormatter(new PercentFormatter(mainChart));
                dataSet.setSliceSpace(2f);
                dataSet.setValues(entries);
                dataSet.setDrawValues(false);


                mainChart.setCenterText("\tIncome Money\n"+totalIncomeSum + "đ");
                mainChart.setCenterTextColor(Color.GREEN);
                mainChart.setCenterTextSize(20f);
                mainChart.setCenterTextTypeface(Typeface.create(Typeface.DEFAULT_BOLD, Typeface.BOLD));


                mainChart.setDrawHoleEnabled(true);
                mainChart.setHoleColor(Color.TRANSPARENT);
                mainChart.setTransparentCircleRadius(40f);

                mainChart.setEntryLabelColor(Color.BLACK);
                mainChart.setEntryLabelTextSize(14f);
                mainChart.setHoleRadius(65f);

                mainChart.animateY(1000, Easing.EaseInOutQuad);
                mainChart.getDescription().setEnabled(false);
                mainChart.getLegend().setEnabled(false);
                mainChart.setData(incomeData);

                mainChart.setDrawEntryLabels(false);
                mainChart.invalidate();

                incomeAdapter = new IncomeAdapter();
                mainRecycleView.setAdapter(incomeAdapter);
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });

    }
    private void loadExpensePieChart(@Nullable Date startDate, @Nullable Date endDate){
        mExpenseDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Random random = new Random();
                dataList = new ArrayList<>();
                typeExpenseAmountMap = new HashMap<>();
                expenseColorList = new ArrayList<>();
                typeExpenseColorMap = new HashMap<>();
                typeExpenseIconMap = new HashMap<>();
                totalExpenseSum = 0;
                List<PieEntry> entries = new ArrayList<>();
                PieDataSet dataSet = new PieDataSet(null, "");
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    Data data = dataSnapshot.getValue(Data.class);
                    DateFormat dateFormat = new SimpleDateFormat("MMM d, yyyy", Locale.ENGLISH);
                    try {
                        Date date = dateFormat.parse(data.getDate());
                        Calendar calendar = Calendar.getInstance();
                        int currentYear = calendar.get(Calendar.YEAR);
                        int currentMonth = calendar.get(Calendar.MONTH);
                        int currentDay = calendar.get(Calendar.DAY_OF_MONTH);
                        calendar.setTime(date);
                        int year = calendar.get(Calendar.YEAR);
                        int month = calendar.get(Calendar.MONTH);
                        int day = calendar.get(Calendar.DAY_OF_MONTH);
                        if(day == currentDay && month == currentMonth && year == currentYear && isDayClick) {
                            totalExpenseSum += data.getAmount();
                            totalExpenseResult.setText(formatCurrency(totalExpenseSum));
                            dataList.add(data);
                        } else if (month == currentMonth && year == currentYear && isMonthClick) {
                            totalExpenseSum += data.getAmount();
                            totalExpenseResult.setText(formatCurrency(totalExpenseSum));
                            dataList.add(data);
                        } else if (year == currentYear && isYearClick) {
                            totalExpenseSum += data.getAmount();
                            totalExpenseResult.setText(formatCurrency(totalExpenseSum));
                            dataList.add(data);
                        }
                        else if (isCustomClick && date.compareTo(startDate) > 0 && date.compareTo(endDate) < 0) {
                            totalExpenseSum += data.getAmount();
                            totalExpenseResult.setText(formatCurrency(totalExpenseSum));
                            dataList.add(data);
                        }
                    } catch (ParseException e) {
                        throw new RuntimeException(e);
                    }
                }
                for (Data data : dataList) {
                    String type = data.getType();
                    int color = Color.parseColor(data.getColor());
                    int icon = getFileFromDrawable(data.getIcon());
                    float amount = data.getAmount();
                    if (typeExpenseAmountMap.containsKey(type)) {
                        amount += typeExpenseAmountMap.get(type);
                    }
                    typeExpenseColorMap.put(type, color);
                    typeExpenseAmountMap.put(type, amount);
                    typeExpenseIconMap.put(type, icon);
                }
                for (Map.Entry<String, Float> entry : typeExpenseAmountMap.entrySet()) {
                    String type = entry.getKey();
                    float amount = entry.getValue();
                    expenseColorList.add(typeExpenseColorMap.get(type));
                    dataSet.addColor(typeExpenseColorMap.get(type));
                    entries.add(new PieEntry(amount, type));
                }
                dataSet.setColors(expenseColorList);
                dataSet.setValues(entries);
                dataSet.setSliceSpace(2f);
                dataSet.setDrawValues(false);

                PieData expenseData = new PieData(dataSet);
                expenseData.setValueTextSize(12f);
                expenseData.setValueTextColor(Color.BLACK);

                mainChart.setCenterText("\tExpense Money\n"+totalExpenseSum + "đ");
                mainChart.setCenterTextColor(Color.RED);
                mainChart.setCenterTextSize(20f);
                mainChart.setCenterTextTypeface(Typeface.create(Typeface.DEFAULT_BOLD, Typeface.BOLD));


                mainChart.setDrawHoleEnabled(true);
                mainChart.setHoleColor(Color.TRANSPARENT);
                mainChart.setTransparentCircleRadius(40f);

                mainChart.setEntryLabelColor(Color.BLACK);
                mainChart.setEntryLabelTextSize(14f);
                mainChart.setHoleRadius(65f);

                mainChart.animateY(1000, Easing.EaseInOutQuad);
                mainChart.getDescription().setEnabled(false);
                mainChart.getLegend().setEnabled(false);
                mainChart.setData(expenseData);
                mainChart.setDrawEntryLabels(false);
                mainChart.invalidate();

                expenseAdapter = new ExpenseAdapter();
                mainRecycleView.setAdapter(expenseAdapter);

            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
    }
    public class ItemData{
        private String itemType;
        private Float sumOfMoney;
        private int itemIcon;
        private int color;

        public int getColor() {
            return color;
        }

        public void setColor(int color) {
            this.color = color;
        }

        public String getItemType() {
            return itemType;
        }

        public void setItemType(String itemType) {
            this.itemType = itemType;
        }

        public Float getSumOfMoney() {
            return sumOfMoney;
        }

        public void setSumOfMoney(Float sumOfMoney) {
            this.sumOfMoney = sumOfMoney;
        }
        public int getItemIcon(){return this.itemIcon;}
        public void setItemIcon(int itemIcon){
            this.itemIcon = itemIcon;
        }
    }

    private class IncomeAdapter extends RecyclerView.Adapter<IncomeViewHolder> {

        @NonNull
        @Override
        public IncomeViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.main_recycleview_item, parent, false);
            return new IncomeViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull IncomeViewHolder holder, int position) {
            String type = (String) typeIncomeAmountMap.keySet().toArray()[position];
            Float amount = typeIncomeAmountMap.get(type);
            DecimalFormat df = new DecimalFormat("#");
            Float percent = Float.valueOf(df.format((amount*100)/totalIncomeSum));

            holder.setIncomeType(type);
            holder.setIncomeAmmount(amount);
            holder.setIncomePercent(percent.toString() + "%");
        }
        @Override
        public int getItemCount() {
           return typeIncomeAmountMap.size();
        }

        public ItemData getItem(int position){
            ItemData itemData = new ItemData();
            String type = (String) typeIncomeAmountMap.keySet().toArray()[position];
            Float sumOfMoney = typeIncomeAmountMap.get(type);
            int iconItem = typeIncomeIconMap.get(type);

            itemData.setItemIcon(iconItem);
            itemData.setItemType(type);
            itemData.setSumOfMoney(sumOfMoney);
            return itemData;
        }
    }
    private class ExpenseAdapter extends RecyclerView.Adapter<ExpenseViewHolder> {

        @NonNull
        @Override
        public ExpenseViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.main_recycleview_item, parent, false);
            return new ExpenseViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull ExpenseViewHolder holder, int position) {
            String type = (String) typeExpenseAmountMap.keySet().toArray()[position];
            Float amount = typeExpenseAmountMap.get(type);
            Integer color = typeExpenseColorMap.get(type);
            Integer icon = typeExpenseIconMap.get(type);
            DecimalFormat df = new DecimalFormat("#");
            Float percent = Float.parseFloat(df.format((amount * 100) / totalExpenseSum));

            holder.setIcon(icon, color);
            holder.setExpenseType(type);
            holder.setExpenseAmmount(amount);
            holder.setExpensePercent(Float.toString(percent) + "%");
        }
        @Override
        public int getItemCount() {
            return typeExpenseAmountMap.size();
        }

        public ItemData getItem(int position){
            ItemData itemData = new ItemData();
            String type = (String) typeExpenseAmountMap.keySet().toArray()[position];
            Float sumOfMoney = typeExpenseAmountMap.get(type);
            int icon = typeExpenseIconMap.get(type);
            int color = typeExpenseColorMap.get(type);
            itemData.setColor(color);
            itemData.setItemIcon(icon);
            itemData.setItemType(type);
            itemData.setSumOfMoney(sumOfMoney);

            return itemData;
        }

    }
    public class IncomeViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        View mView;
        IncomeFragment incomeFragment = new IncomeFragment();
        public IncomeViewHolder(@NonNull View itemView) {
            super(itemView);
            mView = itemView;
            itemView.setOnClickListener(this);
        }
        private void setIncomeType(String type){
            TextView mType=mView.findViewById(R.id.type_text);
            mType.setText(type);
        }
        private  void setIncomePercent(String percent){
            TextView mPercent=mView.findViewById(R.id.percent_text);
            mPercent.setText(percent);
        }
        private  void setIncomeAmmount(float ammount){
            TextView mAmmount=mView.findViewById(R.id.amount_text);
            DecimalFormat decimalFormat = new DecimalFormat("#");
            String stammount= decimalFormat.format(ammount);
            mAmmount.setText(stammount);
        }
        private void setIcon(int image, int color) {
            ImageView imageView = mView.findViewById(R.id.icon_imageview);
            GradientDrawable background = new GradientDrawable();
            background.setShape(GradientDrawable.OVAL);
            imageView.setScaleType(ImageButton.ScaleType.CENTER_INSIDE);
            if (image != 0) {
                imageView.setImageDrawable(imageChangedSize(image));
            }
            if (color != 0) {
                background.setColor(color);
            }
            else background.setColor(Color.parseColor("#a4b7b1"));

            imageView.setBackground(background);

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

        @Override
        public void onClick(View v) {
            int position = getAbsoluteAdapterPosition();
            if(position != RecyclerView.NO_POSITION){
                ItemData myItemData = incomeAdapter.getItem(position);

                Bundle arg = new Bundle();
                arg.putString("type",myItemData.getItemType());

                incomeFragment.setArguments(arg);
                FragmentManager fragmentManager = ((AppCompatActivity) itemView.getContext()).getSupportFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.replace(R.id.main_frame, incomeFragment);
                fragmentTransaction.addToBackStack(null);
                fragmentTransaction.commit();
            }
        }
    }
    private class ExpenseViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        View mView;
        ExpenseFragment expenseFragment = new ExpenseFragment();
        public ExpenseViewHolder(@NonNull View itemView) {
            super(itemView);
            mView = itemView;
            itemView.setOnClickListener(this);
        }
        private void setExpenseType(String type){
            TextView mType=mView.findViewById(R.id.type_text);
            mType.setText(type);
        }
        private  void setExpensePercent(String percent){
            TextView mPercent=mView.findViewById(R.id.percent_text);
            mPercent.setText(percent);
        }
        private  void setExpenseAmmount(float ammount){
            TextView mAmmount=mView.findViewById(R.id.amount_text);
            DecimalFormat decimalFormat = new DecimalFormat("#");
            String stammount= decimalFormat.format(ammount);
            mAmmount.setText(stammount);
        }
        private void setIcon(int image, int color) {
            ImageView imageView = mView.findViewById(R.id.icon_imageview);
            GradientDrawable background = new GradientDrawable();
            background.setShape(GradientDrawable.OVAL);
            imageView.setScaleType(ImageButton.ScaleType.CENTER_INSIDE);
            if (image != 0) {
                imageView.setImageDrawable(imageChangedSize(image));
            }
            if (color != 0) {
                background.setColor(color);
            }
            else background.setColor(Color.parseColor("#a4b7b1"));

            imageView.setBackground(background);

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
        @Override
        public void onClick(View v) {
            int position = getAbsoluteAdapterPosition();
            if(position != RecyclerView.NO_POSITION){
                ItemData myItemData = expenseAdapter.getItem(position);

                Bundle arg = new Bundle();
                arg.putString("type",myItemData.getItemType());
                arg.putString("icon", getFileNameFromResourceId(myItemData.getItemIcon()));
                arg.putInt("color", myItemData.getColor());

                expenseFragment.setArguments(arg);
                FragmentManager fragmentManager = ((AppCompatActivity) itemView.getContext()).getSupportFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.replace(R.id.main_frame, expenseFragment);
                fragmentTransaction.addToBackStack(null);
                fragmentTransaction.commit();
            }
        }
    }
    private int getFileFromDrawable(String fileName) {
        int drawableId = getResources().getIdentifier(fileName, "drawable", getContext().getPackageName());
        return drawableId;
    }
    private String getFileNameFromResourceId(int resourceId) {
        String resourceTypeName = getResources().getResourceTypeName(resourceId);
        if (!resourceTypeName.equals("drawable")) {
            return null;
        }
        String fileName = getResources().getResourceEntryName(resourceId);
        return fileName;
    }
}