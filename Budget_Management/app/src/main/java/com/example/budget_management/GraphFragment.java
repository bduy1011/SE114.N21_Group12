package com.example.budget_management;

import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.budget_management.Model.Data;
import com.github.mikephil.charting.animation.Easing;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.formatter.PercentFormatter;
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
    private Button thisWeekBtn;
    private Button thisMonthBtn;
    private Button thisYearBtn;
    private Boolean isDayClick = false;
    private Boolean isWeekClick = false;
    private Boolean isMonthClick = false;
    private Boolean isYearClick = false;

    GraphFragment() {

    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View myview = inflater.inflate(R.layout.fragment_graph, container, false);


        pieChart = myview.findViewById(R.id.pieChart);
        toDayBtn = myview.findViewById(R.id.today_btn);
        thisWeekBtn = myview.findViewById(R.id.week_btn);
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
                LoadPieChart();
            }
        });
        thisWeekBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isWeekClick = true;
                LoadPieChart();
            }
        });
        thisMonthBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isMonthClick = true;
                LoadPieChart();
            }
        });
        thisYearBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isYearClick = true;
                LoadPieChart();
            }
        });
        return myview;
    }
    private void LoadPieChart(){
        mIncomeDatabase.addValueEventListener(new ValueEventListener() {
            int expense = 0;
            int income = 0;
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot mysnap: snapshot.getChildren()){
                    Data data= mysnap.getValue(Data.class);
                    SimpleDateFormat dateFormat = new SimpleDateFormat("MMMM d, yyyy", Locale.ENGLISH);
                    try {
                        Date date = dateFormat.parse(data.getDate());

                        Calendar calendar = Calendar.getInstance();
                        int currentYear = calendar.get(Calendar.YEAR); // Lấy năm hiện tại
                        int currentMonth = calendar.get(Calendar.MONTH); // Lấy tháng hiện tại
                        int currentDay = calendar.get(Calendar.DAY_OF_MONTH); // Lấy ngày hiện tại
                        calendar.setTime(date);
                        int year = calendar.get(Calendar.YEAR); // Lấy năm của ngày nhập thông tin
                        int month = calendar.get(Calendar.MONTH); // Lấy tháng của ngày nhập thông tin
                        int day = calendar.get(Calendar.DAY_OF_MONTH); // Lấy ngày của ngày nhập thông tin
                        if(day == currentDay && month == currentMonth && year == currentYear && isDayClick) {
                            income += data.getAmount();
                        } else if (month == currentMonth && year == currentYear && isMonthClick) {
                            income +=data.getAmount();
                        } else if (year == currentYear && isYearClick) {
                            income += data.getAmount();
                        }
                        else{

                        }
                    } catch (ParseException e) {
                        throw new RuntimeException(e);
                    }
                }
                mExpenseDatabase.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        for(DataSnapshot mysnap: snapshot.getChildren()){
                            Data data= mysnap.getValue(Data.class);
                            SimpleDateFormat dateFormat = new SimpleDateFormat("MMMM d, yyyy", Locale.ENGLISH);
                            try {
                                Date date = dateFormat.parse(data.getDate());

                                Calendar calendar = Calendar.getInstance();
                                int currentYear = calendar.get(Calendar.YEAR); // Lấy năm hiện tại
                                int currentMonth = calendar.get(Calendar.MONTH); // Lấy tháng hiện tại
                                int currentDay = calendar.get(Calendar.DAY_OF_MONTH); // Lấy ngày hiện tại
                                calendar.setTime(date);
                                int year = calendar.get(Calendar.YEAR); // Lấy năm của ngày nhập thông tin
                                int month = calendar.get(Calendar.MONTH); // Lấy tháng của ngày nhập thông tin
                                int day = calendar.get(Calendar.DAY_OF_MONTH); // Lấy ngày của ngày nhập thông tin
                                if(day == currentDay && month == currentMonth && year == currentYear && isDayClick) {
                                    expense += data.getAmount();
                                } else if (month == currentMonth && year == currentYear && isMonthClick) {
                                    expense +=data.getAmount();
                                } else if (year == currentYear && isYearClick) {
                                    expense += data.getAmount();
                                }
                                else {

                                }
                            } catch (ParseException e) {
                                throw new RuntimeException(e);
                            }
                        }
                        ArrayList<PieEntry> entries = new ArrayList<>();
                        entries.add(new PieEntry((float) expense, "Doanh thu"));
                        entries.add(new PieEntry((float) income, "Chi phí"));

                        PieDataSet dataSet = new PieDataSet(entries,"");

                        dataSet.setValueFormatter(new PercentFormatter(pieChart));
                        dataSet.setValueTextSize(16f);

                        // Thiết lập màu cho các phần tử trong biểu đồ tròn
                        ArrayList<Integer> colors = new ArrayList<>();

                        int incomeColor = Color.parseColor("#F91115"); //Green but lighter
                        int expenseColor = Color.parseColor("#00CC66");//Red but lighter
                        colors.add(expenseColor);
                        colors.add(incomeColor);
                        dataSet.setColors(colors);

                        // Tạo PieData từ PieDataSet
                        PieData pieData = new PieData(dataSet);

                        // Thiết lập các thuộc tính cho PieChart
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

                        isDayClick = false;
                        isMonthClick = false;
                        isYearClick = false;
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
}
