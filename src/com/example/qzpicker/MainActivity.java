package com.example.qzpicker;

import java.text.SimpleDateFormat;
import java.util.Date;

import com.hsd.qzsy.BaseCalendar;
import com.hsd.qzsy.Calculate;
import com.jzxiang.pickerview.TimePickerDialog;
import com.jzxiang.pickerview.data.Type;
import com.jzxiang.pickerview.listener.OnDateSetListener;


import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;


public class MainActivity extends FragmentActivity   implements  OnDateSetListener{
   private final  long tenYears = 10L * 365 * 1000 * 60 * 60 * 24L;
	TimePickerDialog mDialogAll ;
 	Calculate  cal = new Calculate();
 	TextView tv;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        tv = (TextView) findViewById(R.id.textView1);
        Button bt = (Button)findViewById(R.id.button1);
		  mDialogAll = new TimePickerDialog.Builder()
          .setCallBack(MainActivity.this)
          .setCancelStringId("取消")
          .setSureStringId("确定")
          .setTitleStringId("选择时间")
          .setYearText("年")
          .setMonthText("月")
          .setDayText("日")
          .setHourText("时")
          .setMinuteText("分")
          .setCyclic(false)
          .setMinMillseconds(System.currentTimeMillis()-4*tenYears)
          .setMaxMillseconds(System.currentTimeMillis() + 5*tenYears)
          .setCurrentMillseconds(System.currentTimeMillis())
          .setThemeColor(getResources().getColor(R.color.timepicker_dialog_bg))
          .setType(Type.ALL)
          .setWheelItemTextNormalColor(getResources().getColor(R.color.timetimepicker_default_text_color))
          .setWheelItemTextSelectorColor(getResources().getColor(R.color.timepicker_toolbar_bg))
          .setWheelItemTextSize(12)
          .build();
        bt.setOnClickListener(new View.OnClickListener(){
        	@Override
        	public void onClick(View arg0) {
        		mDialogAll.show(getSupportFragmentManager(), "all");
        	}
        });
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }


    SimpleDateFormat sf = new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss");
    public String getDateToString(long time) {
        Date d = new Date(time);
        return sf.format(d);
    }

	@Override
	public void onDateSet(TimePickerDialog timePickerView, long millseconds) {
		// TODO Auto-generated method stub
		 double  pos , luo =0;
		 String text = getDateToString(millseconds);
		 String[] date =text.split("-");
	     int[] date_buf = new int[5];
	     date_buf[0] = Integer.parseInt(date[0]) ;//year
	     date_buf[1] = Integer.parseInt(date[1]) ;//month
	     date_buf[2] = Integer.parseInt(date[2]) ;//day
	     date_buf[3] = Integer.parseInt(date[3]) ;//hour
	     date_buf[4] = Integer.parseInt(date[4]) ;//minute
	     tv.setText("时间 ： "+text);
	     BaseCalendar.addZoneOffset("Asia/Shanghai" ,  date_buf ,  0 ,  false);
	        cal.setJulianDay( date_buf );
	    	double birth_sign_pos_sun =cal.computePlanet(0);
	        double birth_sign_pos_MOON = cal.computePlanet(1);
	        double[] sun_rise_set = new double[2];
	        cal.computeRiseSet("Asia/Shanghai", 0, sun_rise_set);
	        boolean day_time = cal.isDayBirth(sun_rise_set);
	        cal.initSpecial(birth_sign_pos_sun, birth_sign_pos_MOON, day_time);
	        for(int i : date_buf)System.out.println(i);
	        int i = 0 ;
			for(; i < 13; i++){
				if(i >= 7 && i <=  10 ) continue ;
				pos = cal.compute(i);
		
				if(i == 11) luo = pos;
				tv.append("\n"+cal. formatPos(pos, i )+"    ");
				String  state =cal.formatStarDegree(pos, false)+cal. getSpeedState(i);
				tv.append(state);
			}
			tv.append("\n"+cal. formatPos(cal.opposite(luo) , i )+"    ");
			String  state =cal.formatStarDegree(luo, false);
			tv.append(state);
			cal.setOrbitData(0.035200321903032655, Calculate.getJulianDayUT(new int[] {1975,3,13,16,0}), 230.5);
			pos = cal.compute(-1);
			i++;
			tv.append("\n"+ cal.formatPos(pos , i )+"    ");
			 state =cal.formatStarDegree(pos, false);
			tv.append(state);
			tv.append("\n命宫"+cal.formatPosToZodiac(cal.calculateLifeHourse(date_buf[3]+8, birth_sign_pos_sun)));
	
		
	}
}
