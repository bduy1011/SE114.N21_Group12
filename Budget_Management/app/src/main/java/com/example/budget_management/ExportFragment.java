package com.example.budget_management;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Environment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;

import com.example.budget_management.Model.Data;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;

import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.List;

public class ExportFragment extends Fragment {
    private File filePath;
    private Button btnExportExcel;
    private static final int PERMISSION_REQUEST_CODE = 1;
    private FirebaseAuth mAuth;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_export, container, false);

        btnExportExcel = rootView.findViewById(R.id.btnExportExcel);

        // File lưu ở SDK trong mục document của package dự án
        filePath = new File(requireActivity().getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS), "Data.xls");

        // File lưu ở SDK của package dự án (không nằm trong mục nào cả)
        // filePath = new File(requireActivity().getExternalFilesDir(null), "Data.xls");

        btnExportExcel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Kiểm tra quyền truy cập WRITE_EXTERNAL_STORAGE
                if (ActivityCompat.checkSelfPermission(requireActivity(), Manifest.permission.WRITE_EXTERNAL_STORAGE)
                        == PackageManager.PERMISSION_GRANTED) {
                    // Quyền đã được cấp, tiến hành xử lý tạo file Excel
                    getExpenseAndIncomeDataFromFirebase();
                } else {
                    // Quyền chưa được cấp, yêu cầu người dùng cấp quyền
                    ActivityCompat.requestPermissions(requireActivity(),
                            new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                            PERMISSION_REQUEST_CODE);
                }
            }
        });

        return rootView;
    }

    // Xử lý kết quả của việc yêu cầu quyền
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Quyền đã được cấp, tiến hành xử lý tạo file Excel
                getExpenseAndIncomeDataFromFirebase();
            } else {
                // Quyền bị từ chối, xử lý theo yêu cầu của ứng dụng (ví dụ: thông báo cho người dùng)
                Toast.makeText(requireActivity(), "Permission denied", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void getExpenseAndIncomeDataFromFirebase() {
        mAuth = FirebaseAuth.getInstance();
        FirebaseUser mUser = mAuth.getCurrentUser();
        String uid = mUser.getUid();

        DatabaseReference expenseRef = FirebaseDatabase.getInstance().getReference().child("ExpenseData").child(uid);
        expenseRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                List<Data> expenseList = new ArrayList<>();
                for (DataSnapshot expenseSnapshot : dataSnapshot.getChildren()) {
                    Data expense = expenseSnapshot.getValue(Data.class);
                    expenseList.add(expense);
                }

                getIncomeDataFromFirebase(expenseList);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(requireActivity(), "Failed to retrieve expense data from Firebase", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void getIncomeDataFromFirebase(final List<Data> expenseList) {
        mAuth = FirebaseAuth.getInstance();
        FirebaseUser mUser = mAuth.getCurrentUser();
        String uid = mUser.getUid();

        DatabaseReference incomeRef = FirebaseDatabase.getInstance().getReference().child("IncomeData").child(uid);
        incomeRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                List<Data> incomeList = new ArrayList<>();
                for (DataSnapshot incomeSnapshot : dataSnapshot.getChildren()) {
                    Data income = incomeSnapshot.getValue(Data.class);
                    incomeList.add(income);
                }

                exportToExcel(expenseList, incomeList);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(requireActivity(), "Failed to retrieve income data from Firebase", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void exportToExcel(List<Data> expenseList, List<Data> incomeList) {
        HSSFWorkbook workbook = new HSSFWorkbook();
        HSSFSheet expense_sheet = workbook.createSheet("Expense Sheet");
        // Tạo header
        HSSFRow expenseHeaderRow = expense_sheet.createRow(0);
        expenseHeaderRow.createCell(0).setCellValue("Type");
        expenseHeaderRow.createCell(1).setCellValue("Note");
        expenseHeaderRow.createCell(2).setCellValue("Amount");
        expenseHeaderRow.createCell(3).setCellValue("Date");

        // Đổ dữ liệu vào sheet
        int expenseRowNum = 1;
        for (Data expense : expenseList) {
            HSSFRow row = expense_sheet.createRow(expenseRowNum);
            row.createCell(0).setCellValue(expense.getType());
            row.createCell(1).setCellValue(expense.getNote());
            row.createCell(2).setCellValue(expense.getAmount());
            row.createCell(3).setCellValue(expense.getDate());
            expenseRowNum++;
        }

        HSSFSheet income_sheet = workbook.createSheet("Income Sheet");
        // Tạo header for income sheet
        HSSFRow incomeHeaderRow = income_sheet.createRow(0);
        incomeHeaderRow.createCell(0).setCellValue("Type");
        incomeHeaderRow.createCell(1).setCellValue("Note");
        incomeHeaderRow.createCell(2).setCellValue("Amount");
        incomeHeaderRow.createCell(3).setCellValue("Date");

        // Đổ dữ liệu vào income sheet
        int incomeRowNum = 1;
        for (Data income : incomeList) {
            HSSFRow row = income_sheet.createRow(incomeRowNum);
            row.createCell(0).setCellValue(income.getType());
            row.createCell(1).setCellValue(income.getNote());
            row.createCell(2).setCellValue(income.getAmount());
            row.createCell(3).setCellValue(income.getDate());
            incomeRowNum++;
        }

        // Lưu workbook vào file
        try {
            FileOutputStream fileOutputStream = new FileOutputStream(filePath);
            workbook.write(fileOutputStream);
            fileOutputStream.flush();
            fileOutputStream.close();

            Toast.makeText(requireActivity(), "File saved at: " + filePath.getAbsolutePath(), Toast.LENGTH_LONG).show();
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(requireActivity(), "Failed to export data to Excel", Toast.LENGTH_LONG).show();
        }
    }
}