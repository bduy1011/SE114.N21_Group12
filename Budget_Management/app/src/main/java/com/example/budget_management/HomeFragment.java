package com.example.budget_management;

import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.budget_management.Model.Data;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
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

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
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
    //FireBase Adapter
    FirebaseRecyclerAdapter<Data,ExpenseViewHolder> expenseAdapter;
    FirebaseRecyclerAdapter<Data,IncomeViewHolder> incomeAdapter;
    //PieChart
    private PieChart mainChart;

    Boolean isIncome = true, isExpense = true;
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
        loadIncomePieChart();
        loadExpensePieChart();
        totalExpenseResult.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isExpense = true;
                loadExpensePieChart();
            }
        });
        totalIncomeResult.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isIncome = true;
                loadIncomePieChart();
            }
        });
        fab_main_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addData();
//                if(isOpen){
//                    fab_income_btn.startAnimation(FadeClose);
//                    fab_expense_btn.startAnimation(FadeClose);
//                    fab_income_btn.setClickable(false);
//                    fab_expense_btn.setClickable(false);
//
//                    fab_income_txt.startAnimation(FadeClose);
//                    fab_expense_txt.startAnimation(FadeClose);
//                    fab_income_txt.setClickable(false);
//                    fab_expense_txt.setClickable(false);
//                    isOpen=false;
//
//                }
//                else
//                {
//                    fab_income_btn.startAnimation(FadOpen);
//                    fab_expense_btn.startAnimation(FadOpen);
//                    fab_income_btn.setClickable(true);
//                    fab_expense_btn.setClickable(true);
//
//                    fab_income_txt.startAnimation(FadOpen);
//                    fab_expense_txt.startAnimation(FadOpen);
//                    fab_income_txt.setClickable(true);
//                    fab_expense_txt.setClickable(true);
//                    isOpen=true;
//                }
            }
        });

        incomeAdapter = new FirebaseRecyclerAdapter<Data, IncomeViewHolder>(
                new FirebaseRecyclerOptions.Builder<Data>()
                        .setQuery(mIncomeDatabase, Data.class)
                        .build()
        ) {
            @Override
            protected void onBindViewHolder(@NonNull IncomeViewHolder holder, int position, @NonNull Data model) {
                holder.setIncomeType(model.getType());
                holder.setIncomeAmmount((int) model.getAmount());
                holder.setIncomeDate(model.getDate());
                holder.setIncomeNote(model.getNote());
            }
            @NonNull
            @Override
            public IncomeViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.income_recycler_data, parent, false);
                return new IncomeViewHolder(view);
            }
        };
        //Expense
        expenseAdapter=new FirebaseRecyclerAdapter<Data, ExpenseViewHolder>(
                new FirebaseRecyclerOptions.Builder<Data>()
                        .setQuery(mExpenseDatabase, Data.class)
                        .build()
        ) {
            @Override
            protected void onBindViewHolder(@NonNull ExpenseViewHolder holder, int position, @NonNull Data model) {
                holder.setExpenseType(model.getType());
                holder.setExpenseAmmount((int) model.getAmount());
                holder.setExpenseDate(model.getDate());
                holder.setExpenseNote(model.getNote());
            }

            @NonNull
            @Override
            public ExpenseViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.expense_recycler_data, parent, false);
                return new ExpenseViewHolder(view);
            }
        };
        return myview;
    }
    //Floating button animation
    private void ftAnimation(){
        if(isOpen){
            fab_income_btn.startAnimation(FadeClose);
            fab_expense_btn.startAnimation(FadeClose);
            fab_income_btn.setClickable(false);
            fab_expense_btn.setClickable(false);

            fab_income_txt.startAnimation(FadeClose);
            fab_expense_txt.startAnimation(FadeClose);
            fab_income_txt.setClickable(false);
            fab_expense_txt.setClickable(false);
            isOpen=false;

        }
        else
        {
            fab_income_btn.startAnimation(FadOpen);
            fab_expense_btn.startAnimation(FadOpen);
            fab_income_btn.setClickable(true);
            fab_expense_btn.setClickable(true);

            fab_income_txt.startAnimation(FadOpen);
            fab_expense_txt.startAnimation(FadOpen);
            fab_income_txt.setClickable(true);
            fab_expense_txt.setClickable(true);
            isOpen=true;
        }
    }
    private void addData() {
//        if (!isIncome) {
            incomeDataInsert();
//        }

        //Fab Button income
//        fab_income_btn.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                incomeDataInsert();
//            }
//        });
//        //Fab button expense
//        fab_expense_btn.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                expenseDataInsert();
//            }
//        });
    }
    public void incomeDataInsert() {
        isIncome = true;
        Intent intentIncome = new Intent(getContext(), AddIncomeActivity.class);
        startActivity(intentIncome);


//        AlertDialog.Builder mydialog=new AlertDialog.Builder(getActivity());
//        LayoutInflater inflater=LayoutInflater.from(getActivity());
//        View myview=inflater.inflate(R.layout.custom_layout_for_insertdata,null);
//        mydialog.setView(myview);
//
//        final AlertDialog dialog=mydialog.create();
//        dialog.setCancelable(false);
//
//
//        final EditText ammount=myview.findViewById(R.id.ammount_edt);
//        final EditText type=myview.findViewById(R.id.type_edt);
//        final EditText note=myview.findViewById(R.id.note_edt);
//        final DatePicker datePicker = myview.findViewById(R.id.datePicker_insert);
//        Button btnSave=myview.findViewById(R.id.btnSave);
//        Button btnCancel=myview.findViewById(R.id.btnCancel);
//
//        btnSave.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                String tmAmmount = ammount.getText().toString().trim();
//                String tmtype = type.getText().toString().trim();
//                String tmnote = note.getText().toString().trim();
//                int inamount=Integer.parseInt(tmAmmount);
//
//
//                Calendar calendar = Calendar.getInstance();
//                int year = datePicker.getYear();
//                int month = datePicker.getMonth();
//                int day = datePicker.getDayOfMonth();
//                calendar.set(year,month,day);
//
//                if(TextUtils.isEmpty(tmAmmount)){
//                    ammount.setError("Required field");
//                    return;
//                }
//                if (TextUtils.isEmpty(tmtype)){
//                    type.setError("Required field");
//                    return;
//                }
//                if (TextUtils.isEmpty(tmnote)){
//                    note.setError("Required field");
//                    return;
//                }
//                String id=mIncomeDatabase.push().getKey();
//
//                Date date = calendar.getTime();
//                SimpleDateFormat sdf = new SimpleDateFormat("MMM d, yyyy", Locale.ENGLISH);
//                String mDate = sdf.format(date);
//
//                Data data=new Data(inamount,tmtype,tmnote,id,mDate);
//                mIncomeDatabase.child(id).setValue(data);
//
//                Toast.makeText(getActivity(),"Data added",Toast.LENGTH_SHORT).show();
//                ftAnimation();
//                loadIncomePieChart();
//                dialog.dismiss();
//            }
//        });
//        btnCancel.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                ftAnimation();
//                dialog.dismiss();
//            }
//        });
//        dialog.show();

    }
    public void expenseDataInsert(){
        isExpense = true;
        AlertDialog.Builder mydialog=new AlertDialog.Builder(getActivity());
        LayoutInflater inflater=LayoutInflater.from(getActivity());
        View myview=inflater.inflate(R.layout.custom_layout_for_insertdata,null);
        mydialog.setView(myview);

        final AlertDialog dialog=mydialog.create();
        dialog.setCancelable(false);


        final EditText ammount=myview.findViewById(R.id.ammount_edt);
        final EditText type=myview.findViewById(R.id.type_edt);
        final EditText note=myview.findViewById(R.id.note_edt);
        final DatePicker datePicker = myview.findViewById(R.id.datePicker_insert);
        Button btnSave=myview.findViewById(R.id.btnSave);
        Button btnCancel=myview.findViewById(R.id.btnCancel);

        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String tmAmmount = ammount.getText().toString().trim();
                String tmtype = type.getText().toString().trim();
                String tmnote = note.getText().toString().trim();
                int inamount=Integer.parseInt(tmAmmount);

                Calendar calendar = Calendar.getInstance();
                int year = datePicker.getYear();
                int month = datePicker.getMonth();
                int day = datePicker.getDayOfMonth();
                calendar.set(year,month,day);
                if(TextUtils.isEmpty(tmAmmount)){
                    ammount.setError("Required field");
                    return;
                }

                if (TextUtils.isEmpty(tmtype)){
                    type.setError("Required field");
                    return;
                }
                if (TextUtils.isEmpty(tmnote)){
                    note.setError("Required field");
                    return;
                }

                String id=mExpenseDatabase.push().getKey();

                Date date = calendar.getTime();

                SimpleDateFormat sdf = new SimpleDateFormat("MMM d, yyyy", Locale.ENGLISH);

                String mDate = sdf.format(date);

                Data data=new Data(inamount,tmtype,tmnote,id,mDate);
                mExpenseDatabase.child(id).setValue(data);

                Toast.makeText(getActivity(),"Data added",Toast.LENGTH_SHORT).show();
                ftAnimation();
                loadExpensePieChart();
                dialog.dismiss();
            }
        });
        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ftAnimation();
                dialog.dismiss();
            }
        });
        dialog.show();
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
        incomeAdapter.startListening();
        expenseAdapter.startListening();
    }

    @Override
    public void onStop() {
        super.onStop();
        incomeAdapter.stopListening();
        expenseAdapter.stopListening();
    }
    public static class IncomeViewHolder extends RecyclerView.ViewHolder{
        View mView;
        public IncomeViewHolder(@NonNull View itemView) {
            super(itemView);
            mView = itemView;
        }

        private void setIncomeType(String type){
            TextView mType=mView.findViewById(R.id.type_txt_income);
            mType.setText(type);
        }
        private  void setIncomeNote(String note){
            TextView mNote=mView.findViewById(R.id.note_txt_income);
            mNote.setText(note);
        }
        private  void setIncomeDate(String date){
            TextView mDate=mView.findViewById(R.id.date_txt_income);
            mDate.setText(date);
        }
        private  void setIncomeAmmount(long ammount){
            TextView mAmmount=mView.findViewById(R.id.ammount_txt_income);
            String stammount=String.valueOf(ammount);
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
            TextView mType=mView.findViewById(R.id.type_txt_expense);
            mType.setText(type);
        }
        private  void setExpenseNote(String note){
            TextView mNote=mView.findViewById(R.id.note_txt_expense);
            mNote.setText(note);
        }
        private  void setExpenseDate(String date){
            TextView mDate=mView.findViewById(R.id.date_txt_expense);
            mDate.setText(date);
        }
        private  void setExpenseAmmount(long ammount){
            TextView mAmmount=mView.findViewById(R.id.ammount_txt_expense);
            String stammount=String.valueOf(ammount);
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
                if(isIncome){
                    Random random = new Random();
                    int totalIncomeSum=0;
                    mainRecycleView.setAdapter(incomeAdapter);
                    List<Data> dataList = new ArrayList<>();
                    for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                        Data data = dataSnapshot.getValue(Data.class);
                        totalIncomeSum+=data.getAmount();
                        totalIncomeResult.setText(formatCurrency(totalIncomeSum));
                        dataList.add(data);
                    }

                    Map<String, Float> typeAmountMap = new HashMap<>();
                    for (Data data : dataList) {
                        String type = data.getType();
                        float amount = data.getAmount();
                        if (typeAmountMap.containsKey(type)) {
                            amount += typeAmountMap.get(type);
                        }
                        typeAmountMap.put(type, amount);
                    }

                    List<PieEntry> entries = new ArrayList<>();
                    PieDataSet dataSet = new PieDataSet(null, "Biểu đồ tròn");
                    for (Map.Entry<String, Float> entry : typeAmountMap.entrySet()) {
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

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                isIncome = false;
            }
        });

    }
    private void loadExpensePieChart(){
        mExpenseDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(isExpense){
                    Random random = new Random();
                    int totalExpenseSum=0;
                    mainRecycleView.setAdapter(expenseAdapter);
                    List<Data> dataList = new ArrayList<>();
                    for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                        Data data = dataSnapshot.getValue(Data.class);
                        totalExpenseSum+=data.getAmount();
                        totalExpenseResult.setText(formatCurrency(totalExpenseSum));
                        dataList.add(data);
                    }

                    Map<String, Float> typeAmountMap = new HashMap<>();
                    for (Data data : dataList) {
                        String type = data.getType();
                        float amount = data.getAmount();
                        if (typeAmountMap.containsKey(type)) {
                            amount += typeAmountMap.get(type);
                        }
                        typeAmountMap.put(type, amount);
                    }

                    List<PieEntry> entries = new ArrayList<>();
                    PieDataSet dataSet = new PieDataSet(null, "");
                    for (Map.Entry<String, Float> entry : typeAmountMap.entrySet()) {
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
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                isExpense = false;
            }
        });
    }
}