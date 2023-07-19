package com.example.budget_management;

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

import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
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

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class IncomeFragment extends Fragment {
    private final int REQUEST_CODE_UPDATE_RECORD = 8;
    private FirebaseAuth mAuth;
    private DatabaseReference mIncomeDatabase;
    private RecyclerView recyclerView;
    private TextView incomeTotalSum;
    private  String type;
    private String note;
    private  int amount;
    private String theDay;
    private String post_key;
    FirebaseRecyclerAdapter<Data, IncomeFragment.MyViewHolder> adapter;
    private Bundle arg;
    private String myType = null;
    private String myIcon = null;
    private int myColor = 0;
    Query myQuery;
    public IncomeFragment() {
        // Required empty public constructor
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_CODE_UPDATE_RECORD) {
            loadData();
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        arg = getArguments();
        View myview =  inflater.inflate(R.layout.fragment_income, container, false);
        mAuth = FirebaseAuth.getInstance();
        FirebaseUser mUser = mAuth.getCurrentUser();
        String uid = mUser.getUid();
        mIncomeDatabase = FirebaseDatabase.getInstance().getReference().child("IncomeData").child(uid);
        recyclerView = myview.findViewById(R.id.recycle_id_income);
        incomeTotalSum = myview.findViewById(R.id.income_txt_result);

        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        layoutManager.setReverseLayout(true);
        layoutManager.setStackFromEnd(true);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(layoutManager);

        loadData();

        registerForContextMenu(recyclerView);

        return myview;
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        getActivity().getMenuInflater().inflate(R.menu.context_menu, menu);
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.menu_delete) {
            mIncomeDatabase.child(post_key).removeValue();
            return true;
        }
        return super.onContextItemSelected(item);
    }

    private void loadData() {
        if(arg != null) {
            myType = arg.getString("type");
            myIcon = arg.getString("icon");
            myColor = arg.getInt("color");
            myQuery = mIncomeDatabase.orderByChild("type").equalTo(myType);
            myQuery.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    int totalValue = 0;
                    for (DataSnapshot myDataSnapshot : dataSnapshot.getChildren()) {
                        Data data = myDataSnapshot.getValue(Data.class);
                        totalValue += data.getAmount();
                        String sTotalValue = String.valueOf(totalValue);
                        incomeTotalSum.setText(sTotalValue);
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
            adapter = new FirebaseRecyclerAdapter<Data, IncomeFragment.MyViewHolder>
                    (
                            new FirebaseRecyclerOptions.Builder<Data>()
                                    .setQuery(myQuery, Data.class)
                                    .build()
                    ) {
                @NonNull
                @Override
                public IncomeFragment.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                    View view = LayoutInflater.from(parent.getContext())
                            .inflate(R.layout.income_recycler_data, parent, false);
                    return new IncomeFragment.MyViewHolder(view);
                }

                @Override
                protected void onBindViewHolder(@NonNull IncomeFragment.MyViewHolder holder, int position, @NonNull Data model) {
                    holder.setType(model.getType());
                    holder.setNote(model.getNote());
                    holder.setDate(model.getDate());
                    holder.setAmmount(model.getAmount());
                    holder.setIcon(getFileFromDrawable(myIcon), myColor);
                    holder.mView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            int adapterPosition = holder.getAdapterPosition();
                            if (adapterPosition != RecyclerView.NO_POSITION) {
                                post_key = getRef(adapterPosition).getKey();

                                type = model.getType();
                                note = model.getNote();
                                amount = model.getAmount();
                                theDay = model.getDate();

                                updateDataItem(-1);
                                adapter.notifyDataSetChanged();
                            }
                        }
                    });

                    holder.itemView.setOnCreateContextMenuListener(new View.OnCreateContextMenuListener() {
                        @Override
                        public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
                            int adapterPosition = holder.getAdapterPosition();
                            if(adapterPosition != RecyclerView.NO_POSITION)
                            {
                                post_key = getRef(adapterPosition).getKey();
                            }
                        }
                    });
                }
            };
        }
        else{
            mIncomeDatabase.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    int totalValue = 0;
                    for (DataSnapshot myDataSnapshot : dataSnapshot.getChildren()) {
                        Data data = myDataSnapshot.getValue(Data.class);
                        totalValue += data.getAmount();
                        String sTotalValue = String.valueOf(totalValue);
                        incomeTotalSum.setText(sTotalValue);
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
            adapter = new FirebaseRecyclerAdapter<Data, IncomeFragment.MyViewHolder>
                    (
                            new FirebaseRecyclerOptions.Builder<Data>()
                                    .setQuery(mIncomeDatabase, Data.class)
                                    .build()
                    ) {
                @NonNull
                @Override
                public IncomeFragment.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                    View view = LayoutInflater.from(parent.getContext())
                            .inflate(R.layout.income_recycler_data, parent, false);
                    return new IncomeFragment.MyViewHolder(view);
                }

                @Override
                protected void onBindViewHolder(@NonNull IncomeFragment.MyViewHolder holder, int position, @NonNull Data model) {
                    holder.setType(model.getType());
                    holder.setNote(model.getNote());
                    holder.setDate(model.getDate());
                    holder.setAmmount(model.getAmount());
                    holder.setIcon(getFileFromDrawable(model.getIcon()),Color.parseColor(model.getColor()));
                    holder.mView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            int adapterPosition = holder.getAdapterPosition();
                            if (adapterPosition != RecyclerView.NO_POSITION) {
                                post_key = getRef(adapterPosition).getKey();

                                type = model.getType();
                                note = model.getNote();
                                amount = model.getAmount();
                                theDay = model.getDate();

                                updateDataItem(adapterPosition);
                                adapter.notifyDataSetChanged();
                            }
                        }
                    });

                    holder.itemView.setOnCreateContextMenuListener(new View.OnCreateContextMenuListener() {
                        @Override
                        public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
                            int adapterPosition = holder.getAdapterPosition();
                            if(adapterPosition != RecyclerView.NO_POSITION)
                            {
                                post_key = getRef(adapterPosition).getKey();
                            }
                        }
                    });
                }
            };

        }
        recyclerView.setAdapter(adapter);
        adapter.startListening();
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
            TextView mType=mView.findViewById(R.id.type_txt_income);
            mType.setText(type);
        }
        private  void setNote(String note){
            TextView mNote=mView.findViewById(R.id.note_txt_income);
            mNote.setText(note);
        }
        private  void setDate(String date){
            TextView mDate=mView.findViewById(R.id.date_txt_income);
            SimpleDateFormat sdf = new SimpleDateFormat("MMM d, yyyy", Locale.ENGLISH);

            Date tmpDate = null;
            try {
                tmpDate = sdf.parse(date);
            } catch (ParseException e) {
                e.printStackTrace();
            }

            SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
            date = dateFormat.format(tmpDate);
            mDate.setText(date);
        }
        private  void setAmmount(long ammount){
            TextView mAmmount=mView.findViewById(R.id.ammount_txt_income);
            String stammount=String.valueOf(ammount);
            mAmmount.setText(stammount);
        }
        private void setIcon(int image, int color) {
            ImageView imageView = mView.findViewById(R.id.income_icon);
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

    private void updateDataItem(int pos){
        if (getContext() != null && post_key != null)
        {
            Intent intent = new Intent(getContext(), UpdateRecordActivity.class);
            intent.putExtra("amount", String.valueOf(amount));
            if(arg != null) {
                intent.putExtra("catalogName", myType);
                intent.putExtra("catalogColor", myColor);
                intent.putExtra("catalogIcon", myIcon);
            }
            else {
                intent.putExtra("catalogName", adapter.getItem(pos).getType());
                intent.putExtra("catalogColor", Color.parseColor(adapter.getItem(pos).getColor()));
                intent.putExtra("catalogIcon", adapter.getItem(pos).getIcon());
            }
            intent.putExtra("date", theDay);
            intent.putExtra("description", note);
            intent.putExtra("type", "Thu nhập");
            intent.putExtra("postKey", post_key);

            startActivityForResult(intent, REQUEST_CODE_UPDATE_RECORD);
        }
    }

    private int getFileFromDrawable(String fileName) {
        if (getActivity() != null) {
            int drawableId = getActivity().getResources().getIdentifier(fileName, "drawable", getActivity().getPackageName());
            return drawableId;
        }
        return 0;
    }
}