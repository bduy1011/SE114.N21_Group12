package com.example.budget_management;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class ExpenseAdapter extends RecyclerView.Adapter<ExpenseAdapter.MyViewHolder>{
    private Context context;
    private List<ExpenseTable> expenseTableList;
    ExpenseAdapter(Context context){
        this.context = context;
        expenseTableList = new ArrayList<>();
    }
    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_item, parent,false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        ExpenseTable expenseTable = expenseTableList.get(position);
        holder.amount.setText(expenseTable.getAmount() + " Vnd");
        holder.description.setText("" + expenseTable.getDescription());
        holder.paymentType.setText("" + expenseTable.getPaymentType());
        if(expenseTable.isIncome()){
            holder.status.setText("Income");
        }
        else{
            holder.status.setText("Expense");
        }
    }
    @Override
    public int getItemCount() {
        return expenseTableList.size();
    }

    public void add(ExpenseTable expenseTable) {
        expenseTableList.add(expenseTable);
        notifyDataSetChanged();
    }


    public class MyViewHolder extends RecyclerView.ViewHolder{
        TextView status, amount, paymentType, description;
        public MyViewHolder(@NotNull View itemView){
            super(itemView);
            status = itemView.findViewById(R.id.isIncome);
            amount = itemView.findViewById(R.id.amount_text);
            paymentType = itemView.findViewById(R.id.payment_type_text);
            description = itemView.findViewById(R.id.description_text);
        }
    }
}
