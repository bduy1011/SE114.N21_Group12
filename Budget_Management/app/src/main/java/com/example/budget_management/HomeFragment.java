package com.example.budget_management;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;

import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.TextView;

import com.example.budget_management.databinding.ActivityMainBinding;
import com.example.budget_management.databinding.FragmentHomeBinding;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 * Use the {@link HomeFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class HomeFragment extends Fragment implements ClickEvent{

    FragmentHomeBinding binding;
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
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
    ExpenseAdapter expenseAdapter;
    ExpenseDatabase expenseDatabase;
    ExpenseDao expenseDao;
    int income = 0, expense = 0;
    public HomeFragment() {
        // Required empty public constructor
    }

    public static HomeFragment newInstance(String param1, String param2) {
        HomeFragment fragment = new HomeFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }

    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View myview=inflater.inflate(R.layout.fragment_home,container,false);

        //Connect button to layout
        fab_main_btn=myview.findViewById(R.id.fb_main_plus_btn);
        fab_income_btn=myview.findViewById(R.id.income_Ft_btn);
        fab_expense_btn=myview.findViewById(R.id.expense_Ft_btn);
        //Connect floating text;
        fab_main_btn=myview.findViewById(R.id.income_ft_text);
        fab_expense_btn=myview.findViewById(R.id.expense_ft_text);
        //Animation connect
        FadOpen= AnimationUtils.loadAnimation(getActivity(),R.anim.fade_open);
        FadeClose=AnimationUtils.loadAnimation(getActivity(),R.anim.fade_close);
        fab_main_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
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
        });
        return myview;
        //Phat
        /*binding = FragmentHomeBinding.inflate(inflater, container, false);
        binding.newBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), AddActivity.class);
                startActivity(intent);
            }
        });
        return binding.getRoot();*/
    }
    @Override
    public void onResume() {
        super.onResume();
        LoadItem();
    }
    public void LoadItem(){
        expenseDatabase= ExpenseDatabase.getInstance(getContext());
        expenseDao = expenseDatabase.getDao();
        expenseAdapter = new ExpenseAdapter(getContext(), this);
        binding.itemsRecycler.setAdapter(expenseAdapter);
        binding.itemsRecycler.setLayoutManager(new LinearLayoutManager(getContext()));

        List<ExpenseTable> expenseTables = expenseDao.getAll();

        for(int i = 0; i < expenseTables.size(); i++){
            if(expenseTables.get(i).isIncome()){
                income = income + expenseTables.get(i).getAmount();
            }
            else {
                expense = expense + expenseTables.get(i).getAmount();
            }
            expenseAdapter.add(expenseTables.get(i));
        }
        binding.totalIncome.setText(income+"");
        binding.totalExpense.setText(expense+"");
        int balance = income - expense;
        binding.totalAmount.setText(balance+"");
        income = 0;
        expense = 0;
    }

    @Override
    public void OnClick(int pos) {
        Intent intent = new Intent(getActivity(),AddActivity.class);
        intent.putExtra("update",true);
        intent.putExtra("id", expenseAdapter.getId(pos));
        intent.putExtra("amount", expenseAdapter.Amount(pos));
        intent.putExtra("type", expenseAdapter.paymentType(pos));
        intent.putExtra("description", expenseAdapter.desc(pos));
        intent.putExtra("isIncome", expenseAdapter.isIncome(pos));
        startActivity(intent);
    }

    @Override
    public void OnLongPress(int pos) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("Delete")
                .setMessage("Do you want to delete it?")
                .setPositiveButton("yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        int id = expenseAdapter.getId(pos);
                        expenseDao.delete(id);
                        expenseAdapter.delete(pos);
                        LoadItem();
                    }
                })
                .setNegativeButton("no", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.cancel();
                    }
                });
        builder.show();
    }
}