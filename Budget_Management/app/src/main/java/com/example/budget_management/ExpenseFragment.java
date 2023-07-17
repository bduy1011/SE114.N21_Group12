package com.example.budget_management;

import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.budget_management.Model.Data;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class ExpenseFragment extends Fragment {
    private final int REQUEST_CODE_UPDATE_RECORD = 8;
    //Firebase database
    private FirebaseAuth mAuth;
    private DatabaseReference mExpenseDatabase;
    //Recyclerview
    private RecyclerView recyclerView;
    private TextView expenseTotalSum;
    //Update edit text
    private  EditText edtAmmount;
    private  EditText edtType;
    private  EditText edtNote;
    //Update button and delete button
    private Button btnUpdate;
    private Button btnDelete;
    //Data item value
    private  String type;
    private String note;
    private  int amount;
    private String theDay;
    //DatePicker
    DatePicker datePicker;
    private String post_key;
    //Recycle adapter
    FirebaseRecyclerAdapter<Data, ExpenseFragment.MyViewHolder> adapter;
    private Bundle arg;
    private String myType;
    private String myIcon;
    private int myColor;
    public ExpenseFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        arg = getArguments();
        myType = arg.getString("type");
        myIcon = arg.getString("icon");
        myColor = arg.getInt("color");
        View myview =  inflater.inflate(R.layout.fragment_expense, container, false);
        mAuth = FirebaseAuth.getInstance();
        FirebaseUser mUser = mAuth.getCurrentUser();
        String uid = mUser.getUid();
        mExpenseDatabase = FirebaseDatabase.getInstance().getReference().child("ExpenseData").child(uid);
        Query myQuery = mExpenseDatabase.orderByChild("type").equalTo(myType);
        recyclerView = myview.findViewById(R.id.recycle_id_expense);
        expenseTotalSum = myview.findViewById(R.id.expense_txt_result);

        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        layoutManager.setReverseLayout(true);
        layoutManager.setStackFromEnd(true);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(layoutManager);

        mExpenseDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                int totalValue = 0;
                for(DataSnapshot myDataSnapshot:dataSnapshot.getChildren()){
                    Data data = myDataSnapshot.getValue(Data.class);
                    totalValue += data.getAmount();
                    String sTotalValue = String.valueOf(totalValue);
                    expenseTotalSum.setText(sTotalValue);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        adapter = new FirebaseRecyclerAdapter<Data, ExpenseFragment.MyViewHolder>
                (
                        new FirebaseRecyclerOptions.Builder<Data>()
                                .setQuery(myQuery, Data.class)
                                .build()
                ){
            @NonNull
            @Override
            public ExpenseFragment.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.expense_recycler_data, parent, false);
                return new ExpenseFragment.MyViewHolder(view);
            }
            @Override
            protected void onBindViewHolder(@NonNull ExpenseFragment.MyViewHolder holder, int position, @NonNull Data model) {
                holder.setType(model.getType());
                holder.setNote(model.getNote());
                holder.setDate(model.getDate());
                holder.setAmmount(model.getAmount());
                holder.setIcon(getFileFromDrawable(myIcon),myColor);
                holder.mView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        int adapterPosition=holder.getAdapterPosition();
                        if(adapterPosition!=RecyclerView.NO_POSITION)
                        {
                            post_key = getRef(adapterPosition).getKey();

                            type = model.getType();
                            note = model.getNote();
                            amount = model.getAmount();
                            theDay = model.getDate();

                            updateDataItem();
                        }
                    }
                });
            }
        };
        recyclerView.setAdapter(adapter);
        adapter.startListening();
        return myview;
    }

    @Override
    public void onStart() {
        super.onStart();
        adapter.startListening();
    }

    @Override
    public void onStop() {
        super.onStop();
        adapter.stopListening();
    }

    private class MyViewHolder extends RecyclerView.ViewHolder{
        View mView;
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            mView = itemView;
        }
        private void setType(String type){
            TextView mType=mView.findViewById(R.id.type_txt_expense);
            mType.setText(type);
        }
        private  void setNote(String note){
            TextView mNote=mView.findViewById(R.id.note_txt_expense);
            mNote.setText(note);
        }
        private  void setDate(String date){
            TextView mDate=mView.findViewById(R.id.date_txt_expense);
            mDate.setText(date);
        }
        private  void setAmmount(long ammount){
            TextView mAmmount=mView.findViewById(R.id.ammount_txt_expense);
            String stammount=String.valueOf(ammount);
            mAmmount.setText(stammount);
        }
        private void setIcon(int image, int color) {
            ImageView imageView = mView.findViewById(R.id.expense_icon);
            GradientDrawable background = new GradientDrawable();
            background.setShape(GradientDrawable.OVAL);
            imageView.setScaleType(ImageButton.ScaleType.CENTER_INSIDE);
            if (image != 0) {
                imageView.setImageDrawable(imageChangedSize(image));
            }
            if (color != 0) {
                background.setColor(color);
            }
            else background.setColor(Color.parseColor("#a4b7b1"));

            imageView.setBackground(background);

        }
        private Drawable imageChangedSize(int image) {
            Drawable drawable = getResources().getDrawable(image);

            // Kích thước gốc của ảnh
            int originalWidth = drawable.getIntrinsicWidth();
            int originalHeight = drawable.getIntrinsicHeight();

            // Kích thước mới sau khi thay đổi
            int newWidth, newHeight;

            // Tính toán kích thước mới
            if (originalWidth >= originalHeight) {
                // Khi chiều rộng lớn hơn hoặc bằng chiều cao
                newWidth = 70;
                newHeight = (int) (originalHeight * (70.0f / originalWidth));
            } else {
                // Khi chiều cao lớn hơn chiều rộng
                newWidth = (int) (originalWidth * (70.0f / originalHeight));
                newHeight = 70;
            }

            // Thay đổi kích thước ảnh
            Bitmap resizedBitmap = Bitmap.createScaledBitmap(
                    drawableToBitmap(drawable),
                    newWidth,
                    newHeight,
                    true
            );

            return new BitmapDrawable(getResources(), resizedBitmap);
        }
        private Bitmap drawableToBitmap(Drawable drawable) {
            if (drawable instanceof BitmapDrawable) {
                return ((BitmapDrawable) drawable).getBitmap();
            }

            Bitmap bitmap = Bitmap.createBitmap(
                    drawable.getIntrinsicWidth(),
                    drawable.getIntrinsicHeight(),
                    Bitmap.Config.ARGB_8888
            );
            Canvas canvas = new Canvas(bitmap);
            drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
            drawable.draw(canvas);
            return bitmap;
        }
    }

    private void updateDataItem(){
        Intent intent = new Intent(getContext(), UpdateRecordActivity.class);
        intent.putExtra("amount", String.valueOf(amount));
        intent.putExtra("catalogName", myType);
        intent.putExtra("catalogColor", myColor);
        intent.putExtra("catalogIcon", myIcon);
        intent.putExtra("date", theDay);
        intent.putExtra("description", note);
        intent.putExtra("type", "Chi phí");

        startActivityForResult(intent, REQUEST_CODE_UPDATE_RECORD);

//        AlertDialog.Builder myDialog = new AlertDialog.Builder(getActivity());
//        LayoutInflater inflater = LayoutInflater.from(getActivity());
//
//        View myview = inflater.inflate(R.layout.update_data_item, null);
//        myDialog.setView(myview);
//
//        edtAmmount = myview.findViewById(R.id.ammount_edt);
//        edtType = myview.findViewById(R.id.type_edt);
//        edtNote = myview.findViewById(R.id.note_edt);
//
//        //Set data to editText
//
//        edtType.setText(type);
//        edtType.setSelection(type.length());
//
//        edtNote.setText(note);
//        edtNote.setSelection(note.length());
//
//        edtAmmount.setText(String.valueOf(amount));
//        edtAmmount.setSelection(String.valueOf(amount).length());
//
//        btnUpdate = myview.findViewById(R.id.btn_upd_Update);
//        btnDelete = myview.findViewById(R.id.btn_upd_Delete);
//
//        datePicker = myview.findViewById(R.id.datePicker_update);
//        setDatePicker(theDay);
//
//        AlertDialog dialog = myDialog.create();
//        btnUpdate.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                type = edtType.getText().toString().trim();
//                note = edtNote.getText().toString().trim();
//                String mdAmount = String.valueOf(amount);
//
//                mdAmount = edtAmmount.getText().toString().trim();
//
//                int myAmount = Integer.parseInt(mdAmount);
//
//                Calendar calendar = Calendar.getInstance();
//                int year = datePicker.getYear();
//                int month = datePicker.getMonth();
//                int day = datePicker.getDayOfMonth();
//
//                calendar.set(year,month,day);
//                Date date = calendar.getTime();
//
//                SimpleDateFormat sdf = new SimpleDateFormat("MMM d, yyyy", Locale.ENGLISH);
//                String mDate = sdf.format(date);
//
//                Data data = new Data(myAmount,type,note,post_key,mDate,"#"+Integer.toHexString(myColor),myIcon);
//                mExpenseDatabase.child(post_key).setValue(data);
//                dialog.dismiss();
//
//            }
//        });
//        btnDelete.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                mExpenseDatabase.child(post_key).removeValue();
//                dialog.dismiss();
//            }
//        });
//        dialog.show();
    }
    public void setDatePicker(String thisDay){
        SimpleDateFormat dateFormat = new SimpleDateFormat("MMM d, yyyy", Locale.ENGLISH);
        try {
            Date date = dateFormat.parse(thisDay);
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(date);
            datePicker.updateDate(calendar.get(Calendar.YEAR)
                    , calendar.get(Calendar.MONTH)
                    , calendar.get(Calendar.DAY_OF_MONTH));
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
    }
    private int getFileFromDrawable(String fileName) {
        int drawableId = getResources().getIdentifier(fileName, "drawable", getContext().getPackageName());
        return drawableId;
    }
}