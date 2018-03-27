package com.example.qzpicker;

import com.hsd.qzsy.BaseCalendar;
import com.hsd.qzsy.Calculate;

import junit.framework.Assert;
import android.test.AndroidTestCase;  
import android.util.Log;  
public class XmlTest extends AndroidTestCase {  
     public void testSomething() throws Throwable {  
    	 
    		double pos , luo = 0;
    		Calculate  cal = new Calculate();
    		int[] date_buf = new int[] { 2000 , 9 , 17 , 22 , 10};
    		 cal.setLocation( 116.4 , 39.91 , 408 );
            BaseCalendar.addZoneOffset("Asia/Shanghai" ,  date_buf ,  0 ,  false);
            cal.setJulianDay( date_buf );
            for(int i : date_buf)System.out.println(i);
            int i = 0 ;
    		for(; i < 13; i++){
    			if(i >= 7 && i <=  10 ) continue ;
    			pos = cal.compute(i);
    			if(i == 11) luo = pos;
    			System.out.println(cal. formatPos(pos, i ));
    		}
    		System.out.println(cal. formatPos(cal.opposite(luo) , i ));
    		cal.setOrbitData(0.035200321903032655, Calculate.getJulianDayUT(new int[] {1975,3,13,16,0}), 230.5);
    		pos = cal.compute(-1);
    		i++;
    		System.out.println( cal.formatPos(pos , i ));
     }  
}  