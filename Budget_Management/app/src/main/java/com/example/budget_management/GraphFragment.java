package com.example.budget_management;

import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

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

import java.util.ArrayList;

public class GraphFragment extends Fragment {
    private FirebaseAuth mAuth;
    private DatabaseReference mIncomeDatabase;
    private DatabaseReference mExpenseDatabase;
    private PieChart pieChart;
    GraphFragment(){

    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View myview = inflater.inflate(R.layout.fragment_graph,container,false);
        pieChart = myview.findViewById(R.id.pieChart);
        mAuth=FirebaseAuth.getInstance();
        FirebaseUser mUser=mAuth.getCurrentUser();
        String uid=mUser.getUid();

        mIncomeDatabase= FirebaseDatabase.getInstance().getReference().child("IncomeData").child(uid);
        mExpenseDatabase= FirebaseDatabase.getInstance().getReference().child("ExpenseData").child(uid);

        mIncomeDatabase.addValueEventListener(new ValueEventListener() {
            int expense = 0;
            int income = 0;
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot mysnap: snapshot.getChildren()){
                    Data data= mysnap.getValue(Data.class);
                    income += data.getAmount();
                }
                mExpenseDatabase.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        for(DataSnapshot mysnap: snapshot.getChildren()){
                            Data data= mysnap.getValue(Data.class);
                            expense += data.getAmount();
                        }
                        ArrayList<PieEntry> entries = new ArrayList<>();
                        entries.add(new PieEntry((float) expense, "Doanh thu"));
                        entries.add(new PieEntry((float) income, "Chi phí"));

                        PieDataSet dataSet = new PieDataSet(entries, "Tổng doanh thu và chi phí");

                        dataSet.setValueFormatter(new PercentFormatter(pieChart));
                        dataSet.setValueTextSize(16f);

                        // Thiết lập màu cho các phần tử trong biểu đồ tròn
                        ArrayList<Integer> colors = new ArrayList<>();
                        colors.add(Color.GREEN);
                        colors.add(Color.RED);
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
                        pieChart.setEntryLabelTextSize(16f);
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
        return myview;
    }
}
