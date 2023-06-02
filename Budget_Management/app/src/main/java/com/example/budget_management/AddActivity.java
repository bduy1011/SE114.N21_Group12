package com.example.budget_management;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

import com.example.budget_management.databinding.ActivityAddBinding;

public class AddActivity extends AppCompatActivity {
    ActivityAddBinding activityAddBinding;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activityAddBinding = ActivityAddBinding.inflate(getLayoutInflater());
        setContentView(activityAddBinding.getRoot());

        boolean update = getIntent().getBooleanExtra("update", false);
        int id = getIntent().getIntExtra("id", -1);
        int amount = getIntent().getIntExtra("amount", -1);
        String paymentType = getIntent().getStringExtra("type");
        String description = getIntent().getStringExtra("description");
        boolean isIncome  = getIntent().getBooleanExtra("isIncome", false);
        if(update){
            activityAddBinding.addText.setText("Update");
            activityAddBinding.amount.setText(amount+"");
            activityAddBinding.paymentType.setText(paymentType);
            activityAddBinding.description.setText(description);
            if(isIncome){
                activityAddBinding.incomeRadio.setChecked(true);
            }else{
                activityAddBinding.expenseRadio.setChecked(true);
            }

        }
        activityAddBinding.addBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String amount = activityAddBinding.amount.getText().toString();
                String type = activityAddBinding.paymentType.getText().toString();
                String description = activityAddBinding.description.getText().toString();
                boolean isIncome = activityAddBinding.incomeRadio.isChecked();

                ExpenseTable expenseTable = new ExpenseTable();

                expenseTable.setAmount(Integer.parseInt(amount));
                expenseTable.setPaymentType(type);
                expenseTable.setDescription(description);
                expenseTable.setIncome(isIncome);

                ExpenseDatabase expenseDatabase = ExpenseDatabase.getInstance(view.getContext());
                ExpenseDao expenseDao = expenseDatabase.getDao();
                if(!update){
                    expenseDao.insertExpense(expenseTable);
                }
                else{
                    expenseTable.setId(id);
                    expenseDao.updateExpense(expenseTable);
                }
                finish();

            }
        });
    }
}