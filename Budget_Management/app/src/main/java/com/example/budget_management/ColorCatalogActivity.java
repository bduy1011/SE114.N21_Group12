package com.example.budget_management;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.GridLayout;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ColorCatalogActivity extends AppCompatActivity {
    private GridLayout gridLayoutColor;
    private ArrayList<ImageButton> mImageButtonColor;
    private ImageButton mSelectedImageButtonColor;
    private ArrayList<String> mColorList;
    private String mSelectedColor;
    private Button btnSelect;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_color_catalog);

        init();

        createGridViewColor(mColorList);

        createButtonSelectClick();

        receiveIntent();
    }

    private void init() {
        gridLayoutColor = findViewById(R.id.mainGridLayout);
        btnSelect = findViewById(R.id.btnSelect);

        mColorList = new ArrayList<>(getColorResources());
        mImageButtonColor = new ArrayList<>();

        if (mSelectedColor == null) {
            btnSelect.setAlpha(0.5f);
            btnSelect.setEnabled(false);
        }
        else {
            btnSelect.setAlpha(1f);
            btnSelect.setEnabled(true);
        }
    }
    private void createGridViewColor(ArrayList<String> mColorList) {
        int countColumn = 6;
        gridLayoutColor.setColumnCount(countColumn);

        int screenWidthPx = getResources().getDisplayMetrics().widthPixels - 50;
        int columnWidthPx = screenWidthPx / countColumn;
        int widthItem = screenWidthPx / 6;

        for (int i = 0; i < mColorList.size(); i++) {
            ImageButton imageButton = new ImageButton(this);
            imageButton.setScaleType(ImageButton.ScaleType.CENTER_INSIDE);

            imageButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(mSelectedImageButtonColor != null) {
                        mSelectedImageButtonColor.setImageResource(0);
                    }
                    mSelectedImageButtonColor = (ImageButton) v;
                    mSelectedImageButtonColor.setImageResource(R.drawable.ic_tick);
                    mSelectedColor = (String) mSelectedImageButtonColor.getTag();

                    btnSelect.setAlpha(1f);
                    btnSelect.setEnabled(true);
                }
            });

            float percent = 0.6f;
            int with = (int) (widthItem * percent);
            int marginH = (int) (widthItem * (percent / 3));
            LinearLayout.LayoutParams paramsImageButton = new LinearLayout.LayoutParams(with, with);
            if (i != mColorList.size() - 1) paramsImageButton.setMargins(marginH, 40, marginH, 40);
            else paramsImageButton.setMargins(marginH, 40, marginH, 270);
            imageButton.setLayoutParams(paramsImageButton);

            String customColor = mColorList.get(i);

            GradientDrawable drawableImageButton = new GradientDrawable();
            drawableImageButton.setShape(GradientDrawable.OVAL);
            drawableImageButton.setColor(Color.parseColor(customColor));
            imageButton.setBackground(drawableImageButton);

            imageButton.setTag(customColor);

            mImageButtonColor.add(imageButton);

            gridLayoutColor.addView(imageButton);
        }
    }
    private ArrayList<String> getColorResources() {
        String[] colors_1 = {"#4e8e2e", "#67ae44", "#80cf5c", "#8dd66b", "#a3dd88", "#b7e3a4"};
        String[] colors_2 = {"#52664a", "#6a8260", "#859d79", "#9bb58e", "#bad6ad", "#d4f0c7"};
        String[] colors_3 = {"#2e78cf", "#4895dd", "#5ea2e1", "#5ea2e1", "#5ea2e1", "#5ea2e1"};
        String[] colors_4 = {"#2234d2", "#3a4de9", "#5162f6", "#7b8aff", "#9ca6ff", "#bec4ff"};
        String[] colors_5 = {"#ec8208", "#fb9904", "#f1b109", "#e9ba0c", "#f1cb06", "#f2e149"};
        String[] colors_6 = {"#eb6f19", "#fa7518", "#fb8837", "#fe9850", "#ffab6f", "#ffc59d"};
        String[] colors_7 = {"#db4d87", "#e16096", "#e274a1", "#fe86b6", "#ffa9cc", "#ffb7d4"};
        String[] colors_8 = {"#e030b9", "#e93fc3", "#eb54c8", "#ea5fd6", "#ef7ddd", "#ffa3f0"};
        String[] colors_9 = {"#19bbc6", "#23cbcb", "#36d9d8", "#46e3d2", "#60eadb", "#7bf8ea"};
        String[] colors_10 = {"#0c9384", "#21b2a1", "#35cebc", "#4bdecc", "#7aebd7", "#8bf0de"};
        String[] colors_11 = {"#ec494a", "#f45453", "#fa5a4e", "#fd6f63", "#ff867d", "#ffb3ad"};
        String[] colors_12 = {"#de2020", "#f63635", "#ff4955", "#fe616a", "#ff7980", "#ff9ea5"};
        String[] colors_13 = {"#9255ce", "#9e75d5", "#a788d6", "#c2a3f3", "#d2baf8", "#dfc8ff"};
        String[] colors_14 = {"#7455cd", "#8f70e7", "#9d7ef3", "#b095fe", "#b8b1ff", "#d3d0ff"};
        String[] colors_15 = {"#242424", "#414141", "#616161", "#888888", "#a6a6a6", "#c9c9c9"};

        ArrayList<String> resourceList = new ArrayList<>();
        resourceList.addAll(Arrays.asList(colors_1));
        resourceList.addAll(Arrays.asList(colors_2));
        resourceList.addAll(Arrays.asList(colors_3));
        resourceList.addAll(Arrays.asList(colors_4));
        resourceList.addAll(Arrays.asList(colors_5));
        resourceList.addAll(Arrays.asList(colors_6));
        resourceList.addAll(Arrays.asList(colors_7));
        resourceList.addAll(Arrays.asList(colors_8));
        resourceList.addAll(Arrays.asList(colors_9));
        resourceList.addAll(Arrays.asList(colors_10));
        resourceList.addAll(Arrays.asList(colors_11));
        resourceList.addAll(Arrays.asList(colors_12));
        resourceList.addAll(Arrays.asList(colors_13));
        resourceList.addAll(Arrays.asList(colors_14));
        resourceList.addAll(Arrays.asList(colors_15));

        return resourceList;
    }
    private void createButtonSelectClick() {
        btnSelect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mSelectedColor != null) {
                    Intent intent = new Intent();
                    intent.putExtra("SelectedColor", mSelectedColor);
                    setResult(RESULT_OK, intent);
                    finish();
                }
            }
        });
    }
    private void receiveIntent() {
        Intent intent = getIntent();
        String currentResColor = intent.getStringExtra("CurrentResColor");
        if (currentResColor != null) {
            int position = mColorList.indexOf(currentResColor);
            if (position != -1) {
                if(mSelectedImageButtonColor != null) {
                    mSelectedImageButtonColor.setImageResource(0);
                }
                mSelectedImageButtonColor = mImageButtonColor.get(position);
                mSelectedImageButtonColor.setImageResource(R.drawable.ic_tick);
                mSelectedColor = (String) mSelectedImageButtonColor.getTag();

                btnSelect.setAlpha(1f);
                btnSelect.setEnabled(true);
            }
        }
    }
}