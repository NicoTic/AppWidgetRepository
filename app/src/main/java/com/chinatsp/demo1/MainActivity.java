package com.chinatsp.demo1;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

import android.app.Dialog;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.drawable.RippleDrawable;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;

import com.chinatsp.demo1.citypicker.CityPickerDialog;
import com.chinatsp.demo1.datePicker.DatePickerDialog;

public class MainActivity extends AppCompatActivity {
    private AppCompatButton button;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        findViewById(R.id.chip).setEnabled(false);
//        findViewById(R.id.show_dialog_btn).setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                new CityPickerDialog().show(getSupportFragmentManager(),"CityPickerDialog");
//            }
//        });
//        findViewById(R.id.show_date_dialog_btn).setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                new DatePickerDialog().show(getSupportFragmentManager(),"DatePickerDialog");
//            }
//        });
//        button = findViewById(R.id.ripple_btn);
//        RippleDrawable rippleDrawable = new RippleDrawable(ColorStateList.valueOf(Color.BLUE),null,null);
//        rippleDrawable.setState(button.getDrawableState());
//        button.setBackground(rippleDrawable);
    }
}