package com.example.budget_management;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;



import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.TextView;

import com.example.budget_management.Model.Data;
import com.github.mikephil.charting.animation.Easing;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.formatter.PercentFormatter;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
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
    private Map<String, Float> typeIncomeAmountMap;
    private Map<String, Float> typeExpenseAmountMap;
    private int totalIncomeSum = 0;
    private int totalExpenseSum = 0;

    Boolean isIncome = false, isExpense = false;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View myview=inflater.inflate(R.layout.fragment_home,container,false);

        mAuth=FirebaseAuth.getInstance();
        //New fix

        FirebaseUser mUser=mAuth.getCurrentUser();
        String uid=mUser.getUid();

        mIncomeDatabase= FirebaseDatabase.getInstance().getReference().child("IncomeData").child(uid);
        mExpenseDatabase= FirebaseDatabase.getInstance().getReference().child("ExpenseData").child(uid);

        mIncomeDatabase.keepSynced(true);
        mExpenseDatabase.keepSynced(true);

        //Connect button to layout
        fab_main_btn=myview.findViewById(R.id.fb_main_plus_btn);
        fab_income_btn=myview.findViewById(R.id.income_Ft_btn);
        fab_expense_btn=myview.findViewById(R.id.expense_Ft_btn);
        //Connect floating text;
        fab_income_txt=myview.findViewById(R.id.income_ft_text);
        fab_expense_txt=myview.findViewById(R.id.expense_ft_text);
        //Total value set
        totalIncomeResult=myview.findViewById(R.id.income_set_result);
        totalExpenseResult=myview.findViewById(R.id.expense_set_result);
        //Recycler
        //mRecyclerIncome=myview.findViewById(R.id.recycler_income);
        //mRecyclerExpense=myview.findViewById(R.id.recycler_expense);
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
        //Load data from begin
        totalExpenseResult.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isExpense = true;
                isIncome = false;
                loadExpensePieChart();

            }
        });
        totalIncomeResult.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isIncome = true;
                isExpense = false;
                loadIncomePieChart();
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
    public static class IncomeViewHolder extends RecyclerView.ViewHolder{
        View mView;
        public IncomeViewHolder(@NonNull View itemView) {
            super(itemView);
            mView = itemView;
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
    }
    private static class ExpenseViewHolder extends RecyclerView.ViewHolder{
        View mView;
        public ExpenseViewHolder(@NonNull View itemView) {
            super(itemView);
            mView = itemView;
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
    }
    private int getColorForType(String type) {
        // Tạo số nguyên từ tên loại bằng hàm băm
        int hashCode = type.hashCode();

        // Tạo một màu sắc ngẫu nhiên từ số nguyên
        Random random = new Random(hashCode);
        int color = Color.argb(255, random.nextInt(256), random.nextInt(256), random.nextInt(256));

        return color;
    }
    private void loadIncomePieChart(){
        mIncomeDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                //if(isIncome){
                    Random random = new Random();
                    totalIncomeSum = 0;
                    dataList = new ArrayList<>();
                    typeIncomeAmountMap = new HashMap<>();
                    for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                        Data data = dataSnapshot.getValue(Data.class);
                        totalIncomeSum+=data.getAmount();
                        totalIncomeResult.setText(formatCurrency(totalIncomeSum));
                        dataList.add(data);
                    }
                    for (Data data : dataList) {
                        String type = data.getType();
                        float amount = data.getAmount();
                        if (typeIncomeAmountMap.containsKey(type)) {
                            amount += typeIncomeAmountMap.get(type);
                        }
                        typeIncomeAmountMap.put(type, amount);
                    }

                    List<PieEntry> entries = new ArrayList<>();
                    PieDataSet dataSet = new PieDataSet(null, "Biểu đồ tròn");
                    for (Map.Entry<String, Float> entry : typeIncomeAmountMap.entrySet()) {
                        String type = entry.getKey();
                        float amount = entry.getValue();
                        int color = getColorForType(type);
                        entries.add(new PieEntry(amount, type));
                        dataSet.addColor(color);
                    }
                    dataSet.setValues(entries);

                    PieData incomeData = new PieData(dataSet);
                    incomeData.setValueTextSize(12f);
                    incomeData.setValueTextColor(Color.BLACK);

                    dataSet.setValueFormatter(new PercentFormatter(mainChart));
                    dataSet.setSliceSpace(3f);
                    dataSet.setValues(entries);

                    mainChart.setCenterText("\tIncome Money\n"+totalIncomeSum + "đ");
                    mainChart.setCenterTextColor(Color.GREEN);
                    mainChart.setCenterTextSize(20f);
                    mainChart.setCenterTextTypeface(Typeface.create(Typeface.DEFAULT_BOLD, Typeface.BOLD));


                    mainChart.setDrawHoleEnabled(true);
                    mainChart.setHoleColor(Color.TRANSPARENT);
                    mainChart.setTransparentCircleRadius(40f);

                    mainChart.setEntryLabelColor(Color.BLACK);
                    mainChart.setEntryLabelTextSize(14f);
                    mainChart.setHoleRadius(70f);

                    mainChart.setUsePercentValues(true);
                    mainChart.animateY(1000, Easing.EaseInOutQuad);
                    mainChart.getDescription().setEnabled(false);
                    mainChart.getLegend().setEnabled(false);
                    mainChart.setData(incomeData);
                    mainChart.invalidate();

                    incomeAdapter = new IncomeAdapter();
                    mainRecycleView.setAdapter(incomeAdapter);

                }
            //}

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });

    }
    private void loadExpensePieChart(){
        mExpenseDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                //if(isExpense){
                    Random random = new Random();
                    dataList = new ArrayList<>();
                    typeExpenseAmountMap = new HashMap<>();
                    totalExpenseSum = 0;
                    for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                        Data data = dataSnapshot.getValue(Data.class);
                        totalExpenseSum+=data.getAmount();
                        totalExpenseResult.setText(formatCurrency(totalExpenseSum));
                        dataList.add(data);
                    }

                    for (Data data : dataList) {
                        String type = data.getType();
                        float amount = data.getAmount();
                        if (typeExpenseAmountMap.containsKey(type)) {
                            amount += typeExpenseAmountMap.get(type);
                        }
                        typeExpenseAmountMap.put(type, amount);
                    }

                    List<PieEntry> entries = new ArrayList<>();
                    PieDataSet dataSet = new PieDataSet(null, "");
                    for (Map.Entry<String, Float> entry : typeExpenseAmountMap.entrySet()) {
                        String type = entry.getKey();
                        float amount = entry.getValue();
                        int color = getColorForType(type);
                        entries.add(new PieEntry(amount, type));
                        dataSet.addColor(color);
                    }
                    dataSet.setValueFormatter(new PercentFormatter(mainChart));
                    dataSet.setSliceSpace(3f);
                    dataSet.setValues(entries);

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
                    mainChart.setHoleRadius(70f);

                    mainChart.setUsePercentValues(true);
                    mainChart.animateY(1000, Easing.EaseInOutQuad);
                    mainChart.getDescription().setEnabled(false);
                    mainChart.getLegend().setEnabled(false);
                    mainChart.setData(expenseData);
                    mainChart.invalidate();

                    expenseAdapter = new ExpenseAdapter();
                    mainRecycleView.setAdapter(expenseAdapter);
                }
            //}
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
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
            DecimalFormat df = new DecimalFormat("#.#");
            Float percent = Float.valueOf(df.format((amount*100)/totalIncomeSum));

            holder.setIncomeType(type);
            holder.setIncomeAmmount(amount);
            holder.setIncomePercent(percent.toString() + "%");
        }
        @Override
        public int getItemCount() {
           return typeIncomeAmountMap.size();
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
            DecimalFormat df = new DecimalFormat("#.#");
            Float percent = Float.valueOf(df.format((amount*100)/totalExpenseSum));

            holder.setExpenseType(type);
            holder.setExpenseAmmount(amount);
            holder.setExpensePercent(percent.toString() + "%");
        }
        @Override
        public int getItemCount() {
            return typeExpenseAmountMap.size();
        }
    }


}