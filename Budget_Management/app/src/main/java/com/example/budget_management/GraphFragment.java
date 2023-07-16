package com.example.budget_management;

import android.app.AlertDialog;
import android.graphics.Color;
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
    private Button customBtn;
    private Button thisMonthBtn;
    private Button thisYearBtn;
    private Boolean isDayClick = false;

    private Boolean isCustomClick = false;
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
                LoadPieChart(null,null);
            }
        });
        thisMonthBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isMonthClick = true;
                LoadPieChart(null, null);
            }
        });
        thisYearBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isYearClick = true;
                LoadPieChart(null,null);
            }
        });
        customBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isCustomClick = true;
                AlertDialog.Builder mydialog=new AlertDialog.Builder(getActivity());
                LayoutInflater inflater=LayoutInflater.from(getActivity());
                View myview=inflater.inflate(R.layout.day_range,null);
                mydialog.setView(myview);

                final AlertDialog dialog=mydialog.create();
                dialog.setCancelable(false);

                Button applyBtn = myview.findViewById(R.id.btn_apply);
                Button cancelBtn = myview.findViewById(R.id.btn_cancel);

                final EditText sDay = myview.findViewById(R.id.start_day_edt);
                final EditText sMonth = myview.findViewById(R.id.start_moth_edt);
                final EditText sYear = myview.findViewById(R.id.start_year_edt);
                final EditText eDay = myview.findViewById(R.id.end_day_edt);
                final EditText eMonth = myview.findViewById(R.id.end_month_edt);
                final EditText eYear = myview.findViewById(R.id.end_year_edt);
                applyBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Date startDate, endDate;
                        String sDayString = sDay.getText().toString();
                        String sMonthString = sMonth.getText().toString();
                        String sYearString = sYear.getText().toString();
                        String eDayString = eDay.getText().toString();
                        String eMonthString = eMonth.getText().toString();
                        String eYearString = eYear.getText().toString();
                        SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy");
                        try {
                            startDate = format.parse(sDayString + "/" + sMonthString + "/" + sYearString);
                            endDate = format.parse(eDayString + "/" + eMonthString + "/" + eYearString);
                            LoadPieChart(startDate, endDate);
                            dialog.dismiss();
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }
                    }
                });
                cancelBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                        isCustomClick = false;
                    }
                });
                dialog.show();
            }
        });
        return myview;
    }


    private void LoadPieChart(@Nullable Date sDate, @Nullable Date eDate){
        mIncomeDatabase.addValueEventListener(new ValueEventListener() {
            int expense = 0;
            int income = 0;
            String typeOfPieChart = "";
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot mysnap: snapshot.getChildren()){

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
                        else if (isCustomClick && date.compareTo(sDate) > 0 && date.compareTo(eDate) < 0) {
                            income += data.getAmount();
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
                            SimpleDateFormat dateFormat = new SimpleDateFormat("MMM d, yyyy", Locale.ENGLISH);
                            try {
                                Date date = dateFormat.parse(data.getDate());

                                Calendar calendar = Calendar.getInstance();
                                int currentYear = calendar.get(Calendar.YEAR); // Láº¥y nÄƒm hiá»‡n táº¡i
                                int currentMonth = calendar.get(Calendar.MONTH); // Láº¥y thÃ¡ng hiá»‡n táº¡i
                                int currentDay = calendar.get(Calendar.DAY_OF_MONTH); // Láº¥y ngÃ y hiá»‡n táº¡i
                                calendar.setTime(date);
                                int year = calendar.get(Calendar.YEAR); // Láº¥y nÄƒm cá»§a ngÃ y nháº­p thÃ´ng tin
                                int month = calendar.get(Calendar.MONTH); // Láº¥y thÃ¡ng cá»§a ngÃ y nháº­p thÃ´ng tin
                                int day = calendar.get(Calendar.DAY_OF_MONTH); // Láº¥y ngÃ y cá»§a ngÃ y nháº­p thÃ´ng tin
                                if(day == currentDay && month == currentMonth && year == currentYear && isDayClick) {
                                    expense += data.getAmount();
                                } else if (month == currentMonth && year == currentYear && isMonthClick) {
                                    expense +=data.getAmount();
                                } else if (year == currentYear && isYearClick) {
                                    expense += data.getAmount();
                                }
                                else if (isCustomClick && date.compareTo(sDate) > 0 && date.compareTo(eDate) < 0) {
                                    expense += data.getAmount();
                                }
                            } catch (ParseException e) {
                                throw new RuntimeException(e);
                            }
                        }

                        ArrayList<PieEntry> entries = new ArrayList<>();
                        entries.add(new PieEntry((float) expense, "Doanh thu"));
                        entries.add(new PieEntry((float) income, "Chi phÃ­"));

                        PieDataSet dataSet = new PieDataSet(entries, typeOfPieChart);

                        dataSet.setValueFormatter(new PercentFormatter(pieChart));
                        dataSet.setValueTextSize(16f);

                        // Thiáº¿t láº­p mÃ u cho cÃ¡c pháº§n tá»­ trong biá»ƒu Ä‘á»“ trÃ²n
                        ArrayList<Integer> colors = new ArrayList<>();

                        int incomeColor = Color.parseColor("#F91115"); //Green but lighter
                        int expenseColor = Color.parseColor("#00CC66");//Red but lighter
                        colors.add(expenseColor);
                        colors.add(incomeColor);
                        dataSet.setColors(colors);

                        // Táº¡o PieData tá»« PieDataSet
                        PieData pieData = new PieData(dataSet);

                        // Thiáº¿t láº­p cÃ¡c thuá»™c tÃ­nh cho PieChart
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

                        //Set false
                        isDayClick = false;
                        isMonthClick = false;
                        isYearClick = false;
                        isCustomClick = false;
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
