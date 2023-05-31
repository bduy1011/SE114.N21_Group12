package com.example.budget_management;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class AddActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add);

        CardView addBtn = findViewById(R.id.add_btn);

        addBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                EditText amount = findViewById(R.id.amount);
                EditText type = findViewById(R.id.paymentType);
                EditText description = findViewById(R.id.description);

                ExpenseTable expenseTable = new ExpenseTable();

                expenseTable.setAmount(Integer.parseInt(amount.getText().toString()));
                expenseTable.setPaymentType(type.getText().toString());
                expenseTable.setDescription(description.getText().toString());

                ExpenseDatabase expenseDatabase = ExpenseDatabase.getInstance(view.getContext());
                ExpenseDao expenseDao = expenseDatabase.getDao();

                expenseDao.insertExpense(expenseTable);
                finish();

            }
        });
    }
}