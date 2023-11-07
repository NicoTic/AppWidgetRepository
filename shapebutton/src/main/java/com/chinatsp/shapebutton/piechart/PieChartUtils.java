package com.chinatsp.shapebutton.piechart;

import android.content.Context;

import java.util.ArrayList;

public class PieChartUtils {
    public static int dipToPx(Context context, int dip) {
        float px = context.getResources().getDisplayMetrics().density;
        return (int) (dip * px + 0.5f);
    }

    public static float asAngle(float data,float total){
        return data * 360f / total;
    }

    public static String assemableTimeStr(int hour,int minute){
        String hourStr;
        if(hour<10){
            hourStr = "0"+hour;
        }else{
            hourStr = String.valueOf(hour);
        }
        String minuteStr;
        if(minute<10){
            minuteStr = "0"+minute;
        }else{
            minuteStr = String.valueOf(minute);
        }
        return hourStr+":"+minuteStr;
    }
}
