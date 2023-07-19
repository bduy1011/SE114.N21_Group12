package com.example.budget_management;

import android.Manifest;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.ActivityNotFoundException;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.fragment.app.DialogFragment;

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

public class ExportDialogFragment extends DialogFragment {

    private static final int PERMISSION_REQUEST_CODE = 1;
    private FirebaseAuth mAuth;
    private File filePath;

    public static void showExportDialog(androidx.fragment.app.FragmentManager fragmentManager) {
        ExportDialogFragment exportDialog = new ExportDialogFragment();
        exportDialog.show(fragmentManager, "export_dialog");
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        // Use the Builder class for convenient dialog construction
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = requireActivity().getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog_export, null);
        builder.setView(dialogView);
        Button btnExport = dialogView.findViewById(R.id.btnExport);
        Button btnCancel = dialogView.findViewById(R.id.btnCancel);

        filePath = new File(requireActivity().getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS), "Data.xls");

        //Set click listener
        btnExport.setOnClickListener(new View.OnClickListener() {
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

        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
        return builder.create();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Quyền đã được cấp, tiến hành xử lý tạo file Excel
                getExpenseAndIncomeDataFromFirebase();
            } else {
                // Quyền bị từ chối, xử lý theo yêu cầu của ứng dụng (ví dụ: thông báo cho người dùng)
                Toast.makeText(requireActivity(), "Từ chối cấp quyền", Toast.LENGTH_SHORT).show();
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
                Toast.makeText(requireActivity(), "Lấy dữ liệu chi tiêu từ Firebase thất bại", Toast.LENGTH_SHORT).show();
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
                Toast.makeText(requireActivity(), "Lấy dữ liệu thu nhập từ Firebase thất bại", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void exportToExcel(List<Data> expenseList, List<Data> incomeList) {
        HSSFWorkbook workbook = new HSSFWorkbook();
        HSSFSheet expense_sheet = workbook.createSheet("Expense Sheet");
        // Tạo header
        HSSFRow expenseHeaderRow = expense_sheet.createRow(0);
        expenseHeaderRow.createCell(0).setCellValue("Loại");
        expenseHeaderRow.createCell(1).setCellValue("Ghi chú");
        expenseHeaderRow.createCell(2).setCellValue("Số tiền");
        expenseHeaderRow.createCell(3).setCellValue("Ngày");

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
        // Tạo header cho bảng thu nhập
        HSSFRow incomeHeaderRow = income_sheet.createRow(0);
        incomeHeaderRow.createCell(0).setCellValue("Loại");
        incomeHeaderRow.createCell(1).setCellValue("Ghi chú");
        incomeHeaderRow.createCell(2).setCellValue("Số tiền");
        incomeHeaderRow.createCell(3).setCellValue("Ngày");

        // Đổ dữ liệu vào bảng thu nhập
        int incomeRowNum = 1;
        for (Data income : incomeList) {
            HSSFRow row = income_sheet.createRow(incomeRowNum);
            row.createCell(0).setCellValue(income.getType());
            row.createCell(1).setCellValue(income.getNote());
            row.createCell(2).setCellValue(income.getAmount());
            row.createCell(3).setCellValue(income.getDate());
            incomeRowNum++;
        }

        // Lưu workbook vào tập tin
        try {
            FileOutputStream fileOutputStream = new FileOutputStream(filePath);
            workbook.write(fileOutputStream);
            fileOutputStream.flush();
            fileOutputStream.close();

            // Hiển thị thông báo về xuất thành công
            showExportSuccessMessage();

            Toast.makeText(requireActivity(), "Tập tin đã được lưu tại: " + filePath.getAbsolutePath(), Toast.LENGTH_LONG).show();
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(requireActivity(), "Xuất dữ liệu sang Excel thất bại", Toast.LENGTH_LONG).show();
        }
    }

    private void showExportSuccessMessage() {
            // Tạo và hiển thị AlertDialog để hiển thị thông báo xuất thành công
            new AlertDialog.Builder(requireActivity())
                    .setTitle("Xuất thành công")
                    .setMessage("Bạn có muốn mở file Excel vừa tạo không")
                    .setNegativeButton("Hủy", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    })
                    .setPositiveButton("Xác nhận", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            openExcelFile(filePath);
                        }
                    }

                    )
                    .create()
                    .show();
        }
    private void openExcelFile(File file) {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        Uri uri = FileProvider.getUriForFile(requireActivity(), "com.example.budget_management.fileprovider", file);
        intent.setDataAndType(uri, "application/vnd.ms-excel");
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        try {
            startActivity(intent);
        } catch (ActivityNotFoundException e) {
            Toast.makeText(requireActivity(), "Không có ứng dụng để mở tập tin Excel", Toast.LENGTH_SHORT).show();
        }
    }
}
