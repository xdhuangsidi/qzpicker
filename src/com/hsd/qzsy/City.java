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

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.DecimalFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Hashtable;
import java.util.LinkedList;
import java.util.ListIterator;
import java.util.StringTokenizer;
import java.util.TimeZone;

public class City {
	static public final double INVALID = Double.NEGATIVE_INFINITY;

	static public final String UNKNOWN_ZONE = "Unknown";

	static private final double MIN_ERROR_RATIO = 0.99;

	static private final double MATCH_ERROR = 0.125;

	static private final double TIGHT_MATCH_ERROR = 0.01;

	static private final double ANY_MATCH_ERROR = 180.0;

	static public final double MATCH_ERROR_SQ = MATCH_ERROR * MATCH_ERROR;

	static public final double TIGHT_MATCH_ERROR_SQ = TIGHT_MATCH_ERROR
			* TIGHT_MATCH_ERROR;

	static public final double ANY_MATCH_ERROR_SQ = ANY_MATCH_ERROR
			* ANY_MATCH_ERROR;

	static private City[] cities, map_cities;

	static private Hashtable dst_override;

	static private DstEntry dst_last;

	private String country, city, zone;

	private double longitude, latitude;

	public City(String which_country, String which_city, double long_val,
			double lat_val, String which_zone) {
		country = which_country;
		city = which_city;
		longitude = long_val;
		latitude = lat_val;
		zone = which_zone;
	}

	public double getLongitude() {
		return longitude;
	}

	public double getLatitude() {
		return latitude;
	}

	static public boolean inDaylightTime(TimeZone zone, String zone_name,
			int year, Date date) {
		return zone.inDaylightTime(date);
	}

	static public int getDstOffset(Calendar cal) {
		return (dst_last == null) ? (cal.get(Calendar.DST_OFFSET) / BaseCalendar.MILLISECOND_PER_MINUTE)
				: dst_last.offset;
	}


	static public String toMinuteSeconds(double degree, boolean has_second) {
		int second = (int) Math.round(3600.0 * degree);
		int minute = second / 60;
		second -= 60 * minute;
		DecimalFormat format = new DecimalFormat("00");
		if (has_second) {
			return format.format(minute) + "'" + format.format(second);
		} else {
			return format.format(minute + ((second >= 30) ? 1 : 0));
		}
	}

	static public String mapZoneName(String name) {
		if (name.startsWith("Etc/GMT+")) {
			return name.replace('+', '-');
		} else if (name.startsWith("Etc/GMT-")) {
			return name.replace('-', '+');
		}
		return name;
	}

	static public double normalizeDegree(double degree) {
		degree = degree % 360;
		if (degree < 0.0)
			degree += 360.0;
		return degree;
	}


	static private class DstEntry {
		public Date start, end;

		int offset;
	}
}