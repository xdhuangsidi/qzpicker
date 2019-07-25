//
//Moira - A Chinese Astrology Charting Program
//Copyright (C) 2004-2015 At Home Projects
//
//This program is free software; you can redistribute it and/or modify
//it under the terms of the GNU General Public License as published by
//the Free Software Foundation; either version 2 of the License, or
//(at your option) any later version.
//
//This program is distributed in the hope that it will be useful,
//but WITHOUT ANY WARRANTY; without even the implied warranty of
//MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
//GNU General Public License for more details.
//
//You should have received a copy of the GNU General Public License
//along with this program; if not, write to the Free Software
//Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
//
package com.hsd.qzsy;

import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.StringTokenizer;
import java.util.TimeZone;

public class BaseCalendar {
	static public final int DATE_WITHIN_RANGE = 7;

	static public final int MILLISECOND_PER_MINUTE = 60 * 1000;

	static private boolean dst_adjust = true, dst_adjust_save;

	static private TimeZone gmt_zone = TimeZone.getTimeZone("GMT");

	static private int[] zone_buf = new int[5];

	static private Calendar work_cal = null;

	static public String chineseNumber(int num, boolean month, boolean day) {
		String[] array =new String[] {
				"零"
				,"一"
				,"二"
				,"三"
				,"四"
				,"五"
				,"六"
				,"七"
				,"八"
				,"九"
				,"十"
				,"初"
				,"正"};
		if (num <= 10) {
			if (month) {
				return (num == 1) ? array[12] : array[num];
			} else if (day) {
				return array[11] + array[num];
			} else {
				return array[num];
			}
		} else if (num < 20) {
			return array[10] + array[num % 10];
		} else {
			String str = array[num / 10] + array[10];
			if ((num % 10) != 0)
				str += array[num % 10];
			return str;
		}
	}


	static public String formatDate(double longitude, int[] date, int[] result,
			double solar_adj, boolean subtract, boolean time_only) {
		int minute = (int) Math.round(1440.0 * longitude / 360.0 + solar_adj);
		if (subtract)
			minute = -minute;
		initTime(date);
		work_cal.add(Calendar.MINUTE, minute);
		String sign = (minute < 0) ? "-" : "+";
		if (minute < 0)
			minute = -minute;
		int hour = minute / 60;
		minute -= hour * 60;
		DecimalFormat format = new DecimalFormat("00");
		String zone_str = " (GMT" + sign + hour + ":" + format.format(minute)
				+ ")";
		if (result != null)
			getTime(work_cal, result);
		String str = getDateString();
		int h = work_cal.get(Calendar.HOUR);
		if (h == 0)
			h = 12;
		String time = format.format(h) + ":"
				+ format.format(work_cal.get(Calendar.MINUTE))
				+ ((work_cal.get(Calendar.AM_PM) == 0) ? "AM" : "PM");
		if (time_only)
			return time;
		else
			return str + " " + time + " " + zone_str;
	}



	// if mode = 1 is true, convert between dst and standard time,
	// if mode = 0 is false, convert between GMT and local time
	// if mode = -1 is false, convert between GMT and standard time
	static public void addZoneOffset(String zone_name, int[] date, int mode,
			boolean gmt_to_local) {
		if (zone_name == null || zone_name.equals(City.UNKNOWN_ZONE))
			return;
		TimeZone zone = TimeZone.getTimeZone(City.mapZoneName(zone_name));
		initTime(date);
		if (mode <= 0 && gmt_to_local) {
			int m = getZoneOffset(zone);
			work_cal.add(Calendar.MINUTE, m);
		}
		if (mode >= 0 && dst_adjust
				&& City.inDaylightTime(zone, zone_name, date[0], getDate(zone))) {
			int delta = getDstOffset(zone);
			work_cal.add(Calendar.MINUTE, gmt_to_local ? delta : (-delta));
		}
		if (mode <= 0 && !gmt_to_local) {
			int m = getZoneOffset(zone);
			work_cal.add(Calendar.MINUTE, -m);
		}
		getTime(work_cal, date);
	}



	static public void initTime(int[] date) {
		if (work_cal == null) {
			work_cal = Calendar.getInstance();
			work_cal.setTimeZone(gmt_zone);
		}
		setTime(work_cal, date);
	}

	static public void setTime(Calendar cal, int[] date) {
		boolean bc = date[0] <= 0; // 1 B.C. is 0, 2 B.C. is -1, and so on
		cal.set(Calendar.ERA, bc ? GregorianCalendar.BC : GregorianCalendar.AD);
		cal.set(Calendar.YEAR, bc ? (-date[0] + 1) : date[0]);
		cal.set(Calendar.MONTH, date[1] - 1);
		cal.set(Calendar.DAY_OF_MONTH, date[2]);
		cal.set(Calendar.HOUR_OF_DAY, date[3]);
		cal.set(Calendar.MINUTE, date[4]);
	}

	static public void getTime(Calendar cal, int[] date) {
		date[0] = cal.get(Calendar.YEAR);
		if (cal.get(Calendar.ERA) == GregorianCalendar.BC)
			date[0] = -date[0] + 1; // 1 B.C. is 0, 2 B.C. is -1, and so on
		date[1] = cal.get(Calendar.MONTH) + 1;
		date[2] = cal.get(Calendar.DAY_OF_MONTH);
		date[3] = cal.get(Calendar.HOUR_OF_DAY);
		date[4] = cal.get(Calendar.MINUTE);
	}

	static public String getDateString() {
		TimeZone old_zone = TimeZone.getDefault();
		TimeZone.setDefault(gmt_zone);
		DateFormat date_format = new SimpleDateFormat("MMM dd, yyyy");
		String str = date_format.format(work_cal.getTime());
		TimeZone.setDefault(old_zone);
		if (work_cal.get(Calendar.ERA) == GregorianCalendar.BC)
			str += " B.C.";
		return str;
	}

	static public int getZoneOffset(TimeZone zone) {
		getTime(work_cal, zone_buf);
		work_cal.setTimeZone(zone);
		int minute = work_cal.get(Calendar.ZONE_OFFSET)
				/ MILLISECOND_PER_MINUTE;
		work_cal.setTimeZone(gmt_zone);
		initTime(zone_buf);
		return minute;
	}

	static public int getDstOffset(TimeZone zone) {
		getTime(work_cal, zone_buf);
		work_cal.setTimeZone(zone);
		initTime(zone_buf);
		int minute = City.getDstOffset(work_cal);
		work_cal.setTimeZone(gmt_zone);
		initTime(zone_buf);
		return minute;
	}

	static public Date getDate(TimeZone zone) {
		getTime(work_cal, zone_buf);
		work_cal.setTimeZone(zone);
		initTime(zone_buf);
		Date date = work_cal.getTime();
		work_cal.setTimeZone(gmt_zone);
		initTime(zone_buf);
		return date;
	}

}