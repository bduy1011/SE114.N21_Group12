package com.example.budget_management;

import android.app.AlertDialog;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.util.Pair;
import androidx.fragment.app.Fragment;

import com.example.budget_management.Model.Data;
import com.github.mikephil.charting.animation.Easing;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.formatter.PercentFormatter;
import com.google.android.material.datepicker.MaterialDatePicker;
import com.google.android.material.datepicker.MaterialPickerOnPositiveButtonClickListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class GraphFragment extends Fragment {
    private FirebaseAuth mAuth;
    private DatabaseReference mIncomeDatabase;
    private DatabaseReference mExpenseDatabase;
    private PieChart pieChart;
    private Button toDayBtn;
    private Button customBtn;
    private Button thisMonthBtn;
    private Button thisYearBtn;
    private Boolean isDayClick = true;

    private Boolean isCustomClick = false;
    private Boolean isMonthClick = false;
    private Boolean isYearClick = false;
    private Date sDate = null;
    private Date eDate = null;

    GraphFragment() {

    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View myview = inflater.inflate(R.layout.fragment_graph, container, false);

        pieChart = myview.findViewById(R.id.pieChart);
        toDayBtn = myview.findViewById(R.id.today_btn);
        customBtn=myview.findViewById(R.id.custom_btn);
        thisMonthBtn = myview.findViewById(R.id.month_btn);
        thisYearBtn = myview.findViewById(R.id.year_btn);

        mAuth = FirebaseAuth.getInstance();
        FirebaseUser mUser = mAuth.getCurrentUser();
        String uid = mUser.getUid();

        mIncomeDatabase = FirebaseDatabase.getInstance().getReference().child("IncomeData").child(uid);
        mExpenseDatabase = FirebaseDatabase.getInstance().getReference().child("ExpenseData").child(uid);

        toDayBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isDayClick = true;
                isMonthClick = false;
                isYearClick = false;
                isCustomClick = false;
                LoadPieChart(null,null);
                setColorButton();
            }
        });
        thisMonthBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isMonthClick = true;
                isDayClick = false;
                isYearClick = false;
                isCustomClick = false;
                LoadPieChart(null, null);
                setColorButton();
            }
        });
        thisYearBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isYearClick = true;
                isDayClick = false;
                isMonthClick = false;
                isCustomClick = false;
                LoadPieChart(null,null);
                setColorButton();
            }
        });
        customBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isCustomClick = true;
                isDayClick = false;
                isMonthClick = false;
                isYearClick = false;
                MaterialDatePicker.Builder<androidx.core.util.Pair<Long, Long>> builder = MaterialDatePicker.Builder.dateRangePicker().setTitleText("Chọn Khoản Thời Gian");
                MaterialDatePicker<Pair<Long, Long>> mtDatePicker = builder.build();
                mtDatePicker.setShowsDialog(true);
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
                        LoadPieChart(sDate, eDate);
                    }
                });
            }
        });
        return myview;
    }


    private void LoadPieChart(@Nullable Date startDate, @Nullable Date endDate){
        mIncomeDatabase.addValueEventListener(new ValueEventListener() {
            int expense = 0;
            int income = 0;
            String typeOfPieChart = "";
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot mysnap : snapshot.getChildren()){

                    Data data= mysnap.getValue(Data.class);

                    SimpleDateFormat dateFormat = new SimpleDateFormat("MMM d, yyyy", Locale.ENGLISH);
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
                            income += data.getAmount();
                        } else if (month == currentMonth && year == currentYear && isMonthClick) {
                            income +=data.getAmount();
                        } else if (year == currentYear && isYearClick) {
                            income += data.getAmount();
                        }
                        else if (isCustomClick && date.compareTo(startDate) >= 0 && date.compareTo(endDate) <= 0) {
                            income += data.getAmount();
                        }
                    } catch (ParseException e) {
                        throw new RuntimeException(e);
                    }
                }
                mExpenseDatabase.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        for(DataSnapshot mysnap : snapshot.getChildren()){
                            Data data= mysnap.getValue(Data.class);
                            SimpleDateFormat dateFormat = new SimpleDateFormat("MMM d, yyyy", Locale.ENGLISH);
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
                                    expense += data.getAmount();
                                } else if (month == currentMonth && year == currentYear && isMonthClick) {
                                    expense +=data.getAmount();
                                } else if (year == currentYear && isYearClick) {
                                    expense += data.getAmount();
                                }
                                else if(startDate != null && endDate != null){
                                    if (isCustomClick && date.compareTo(sDate) >= 0 && date.compareTo(eDate) <= 0) {
                                        expense += data.getAmount();
                                    }
                                }
                            } catch (ParseException e) {
                                throw new RuntimeException(e);
                            }
                        }

                        ArrayList<PieEntry> entries = new ArrayList<>();
                        entries.add(new PieEntry((float) expense, "Doanh thu"));
                        entries.add(new PieEntry((float) income, "Chi phí"));

                        PieDataSet dataSet = new PieDataSet(entries, typeOfPieChart);

                        dataSet.setValueFormatter(new PercentFormatter(pieChart));
                        dataSet.setValueTextSize(16f);

                        ArrayList<Integer> colors = new ArrayList<>();

                        int incomeColor = Color.parseColor("#F91115"); //Green but lighter
                        int expenseColor = Color.parseColor("#00CC66");//Red but lighter
                        colors.add(expenseColor);
                        colors.add(incomeColor);
                        dataSet.setColors(colors);
                        dataSet.setDrawValues(false);

                        PieData pieData = new PieData(dataSet);
                        pieChart.setDrawEntryLabels(false);
                        pieChart.setData(pieData);
                        pieChart.getDescription().setEnabled(false);
                        pieChart.setUsePercentValues(true);
                        pieChart.setDrawHoleEnabled(true);
                        pieChart.setHoleColor(Color.TRANSPARENT);
                        pieChart.setTransparentCircleRadius(40f);
                        pieChart.setEntryLabelColor(Color.BLACK);
                        pieChart.setEntryLabelTextSize(14f);
                        pieChart.animateY(1000, Easing.EaseInOutQuad);
                        pieChart.invalidate();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
    public void setColorButton(){
        if(isDayClick){
            toDayBtn.setTextColor(Color.parseColor("#a8abf7"));
            thisMonthBtn.setTextColor(Color.GRAY);
            thisYearBtn.setTextColor(Color.GRAY);
            customBtn.setTextColor(Color.GRAY);
            toDayBtn.setTypeface(Typeface.DEFAULT_BOLD);
            thisMonthBtn.setTypeface(Typeface.DEFAULT);
            thisYearBtn.setTypeface(Typeface.DEFAULT);
            customBtn.setTypeface(Typeface.DEFAULT);
        }
        if(isMonthClick){
            toDayBtn.setTextColor(Color.GRAY);
            thisMonthBtn.setTextColor(Color.parseColor("#a8abf7"));
            thisYearBtn.setTextColor(Color.GRAY);
            customBtn.setTextColor(Color.GRAY);
            toDayBtn.setTypeface(Typeface.DEFAULT);
            thisMonthBtn.setTypeface(Typeface.DEFAULT_BOLD);
            thisYearBtn.setTypeface(Typeface.DEFAULT);
            customBtn.setTypeface(Typeface.DEFAULT);
        }
        if(isYearClick){
            toDayBtn.setTextColor(Color.GRAY);
            thisMonthBtn.setTextColor(Color.GRAY);
            thisYearBtn.setTextColor(Color.parseColor("#a8abf7"));
            customBtn.setTextColor(Color.GRAY);
            toDayBtn.setTypeface(Typeface.DEFAULT);
            thisMonthBtn.setTypeface(Typeface.DEFAULT);
            thisYearBtn.setTypeface(Typeface.DEFAULT_BOLD);
            customBtn.setTypeface(Typeface.DEFAULT);
        }
        if(isCustomClick){
            toDayBtn.setTextColor(Color.GRAY);
            thisMonthBtn.setTextColor(Color.GRAY);
            thisYearBtn.setTextColor(Color.GRAY);
            customBtn.setTextColor(Color.parseColor("#a8abf7"));
            toDayBtn.setTypeface(Typeface.DEFAULT);
            thisMonthBtn.setTypeface(Typeface.DEFAULT);
            thisYearBtn.setTypeface(Typeface.DEFAULT);
            customBtn.setTypeface(Typeface.DEFAULT_BOLD);
        }
    }
}
