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

                expenseDao.insertExpense(expenseTable);
                finish();

            }
        });
    }
}