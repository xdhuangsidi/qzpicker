//
// Moira - A Chinese Astrology Charting Program
// Copyright (C) 2004-2015 At Home Projects
//
// This program is free software; you can redistribute it and/or modify
// it under the terms of the GNU General Public License as published by
// the Free Software Foundation; either version 2 of the License, or
// (at your option) any later version.
//
// This program is distributed in the hope that it will be useful,
// but WITHOUT ANY WARRANTY; without even the implied warranty of
// MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
// GNU General Public License for more details.
//
// You should have received a copy of the GNU General Public License
// along with this program; if not, write to the Free Software
// Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
//
// From: http://aa.usno.navy.mil/data/docs/JulianDate.html
// The changeover from the Julian calendar to the Gregorian calendar
// occurred in October of 1582, according to the scheme instituted by Pope
// Gregory XIII. Specifically, for dates on or before 4 October 1582, the
// Julian calendar is used; for dates on or after 15 October 1582, the
// Gregorian calendar is used. Thus, there is a ten-day gap in calendar
// dates, but no discontinuity in Julian dates or days of the week: 4
// October 1582 (Julian) is a Thursday, which begins at JD 2299159.5; and 15
// October 1582 (Gregorian) is a Friday, which begins at JD 2299160.5. The
// omission of ten days of calendar dates was necessitated by the
// astronomical error built up by the Julian calendar over its many
// centuries of use, due to its too-frequent leap years.
//
package com.hsd.qzsy;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.net.URL;
import java.net.URLConnection;
import java.text.DecimalFormat;
import java.util.Arrays;
import java.util.Collections;
import java.util.Hashtable;
import java.util.LinkedList;
import java.util.ListIterator;
import java.util.NoSuchElementException;
import java.util.StringTokenizer;


import org.athomeprojects.base.Message;
import org.athomeprojects.base.SearchRecord;
import org.athomeprojects.swisseph.DblObj;
import org.athomeprojects.swisseph.SweConst;
import org.athomeprojects.swisseph.SweDate;
import org.athomeprojects.swisseph.SwissEph;
import org.athomeprojects.swisseph.SwissephException;

public class Calculate {
	
	static public final int SUN = 0;

    static public final int MOON = 1;

    static public final int VENUS = 2;

    static public final int JUPITER = 3;

    static public final int MERCURY = 4;

    static public final int MARS = 5;

    static public final int SATURN = 6;

    static public final int URANUS = 7;

    static public final int NEPTUNE = 8;

    static public final int PLUTO = 9;

    static public final int TRUE_NODE = 10;

    static public final int INV_TRUE_NODE = 11; // opposite of TRUE_NODE

    static public final int PURPLE = 12;

    static public final int MEAN_APOG = 13;

    static public final int FORTUNE = 14;

    static public final int ASC = 15;

    static public final int MC = 16;

    static public final int CHIRON = 17;

    static public final int CERES = 18;

    static public final int PALLAS = 19;

    static public final int JUNO = 20;

    static public final int VESTA = 21;
	
	
    static public final int SE_START = 1000;

    static public final int SE_FORTUNE = 1000;

    static public final int SE_ASC = 1001;

    static public final int SE_MC = 1002;

    static public final int SE_END = 1002;

    static public final int SPEED_NORMAL = 0;

    static public final int SPEED_REVERSE = 1;

    static public final int SPEED_ECLIPSE = 2;

    static public final int SPEED_STATIONARY = 3;

    static public final int SPEED_INVISIBLE = 4;

    static public final int SPEED_SLOW = 5;

    static public final int SPEED_FAST = 6;

    static public final double TRANSIT_INC = 1.0;

    static public final double DEGREE_PRECISION = 1.0;

    static public final double QUARTER_DEGREE_PRECISION = 0.25;

    static public final double MINUTE = 1.0 / (24.0 * 60.0);

    static public final double HALF_MINUTE = 0.5 * MINUTE;

    static public final double INVALID = Double.MIN_VALUE;

    static private final int SWITCH_YEAR = 1582;

    static private final int SWITCH_MONTH = 10;

    static private final int SWITCH_DAY = 15;

    static private final double SWITCH_UT = 2299160.5;

    static private final double BEIJING_TIME_OFFSET = 8.0 / 24.0;

    private final double MIN_UT = -247640.0;

    private final double MAX_UT = 3690082.0;

    private final double ONE_HOUR = 1.0 / 24.0;

    private final double REJECT_DEGREE_PRECISION = 1.0;

    private final double TO_SIDEREAL_OFFSET = 1.0 / 360.98564736629;

    private final double JUMP_SPEED = 1.2;

    private final double JUMP_PERIOD = 30.0;

    private final double TIME_PERIOD = 1.05;

    private final double TIME_PRECISION = 1.0 / (24.0 * 60.0 * TIME_PERIOD);


    private final double TRUE_NODE_AVERAGE_SPEED = -0.05299;

    private final double NEWTON_DEGREE_PRECISION = 0.01;

    private final int NEWTON_MAX_ITER = 100;

    private int[] planets = { SweConst.SE_SUN, SweConst.SE_MOON,
            SweConst.SE_VENUS, SweConst.SE_JUPITER, SweConst.SE_MERCURY,
            SweConst.SE_MARS, SweConst.SE_SATURN, SweConst.SE_URANUS,
            SweConst.SE_NEPTUNE, SweConst.SE_PLUTO, SweConst.SE_TRUE_NODE, -1,
            -1, SweConst.SE_MEAN_APOG, Calculate.SE_FORTUNE, Calculate.SE_ASC,
            Calculate.SE_MC, SweConst.SE_CHIRON, SweConst.SE_CERES,
            SweConst.SE_PALLAS, SweConst.SE_JUNO, SweConst.SE_VESTA };

    private int ephe_flag, house_system_index;

    private double julian_day_ut, julian_day, mountain_offset;

    static private SweDate jdate = new SweDate();

    private SwissEph eph;

    private boolean computed, leap_month, day_fortune_mode, sidereal_mode;

    private double sun_pos, moon_pos;
                                                                                                               //金木水火土
    final private double[] stationary_gap = new double[] {0.15	,0.07	,0.1	, 0.2 , 0.05};
   
    final private double[]  invisible_gap =  new double[] {3.0 , 3.0	 ,3.0	, 3.0	, 3.0};
    
    final private double[]  slow_speed = new double[] {0.71	, 0.05	,0.88	, 0.4,	0.02};
    
    final private double[]  fast_speed = new double[] {1.245 	,0.23		,1.5	,0.7	, 0.13};

    private double[] location = new double[3];

    private double[] computation = new double[6];

    private double[] ascmc = new double[10];

    private double[] azimuth = new double[3];

    private double[] orbit_data = new double[4];

    private double[] temp_cusp = new double[13];
   private double[]   sign_pos  = new double[] {15.9  ,26.3  ,41.1   ,53.2  ,69.0	   ,70.0
		   ,81.8 ,112.3	   ,115.2   ,130.5
		   ,136.4	   ,151.4   ,170.1	   ,187.2
		   ,200.0   ,208.9   ,225.2   ,230.6	   ,237.0
		   ,255.6	   ,266.3	   ,290.1   ,298.0
		   ,308.9  ,318.3	   ,333.6	   ,349.4   ,358.3};
   private final  int[] degreel_zodiac  = new int[] {          315,  285   ,  255 ,  225 , 195  ,165 , 135  ,  105,  75,    45,   15,    345  };
    private static final String[] zodiac  = new String[] {"戌" ,  "酉" ,  "申", "未" ,  "午" , "巳" , "辰" , "卯" , "寅"  , "丑" , "子"  , "亥"};
    
    
    private final String[] full_zodiac  = new String[] {"戌火" ,  "酉金" ,  "申水", "未月" ,  "午日" , "巳水" , "辰金" , "卯火" , "寅木"
    		, "丑土" , "子土"  , "亥木"};
    
    private  static final String[] zodiac_name  =new String[] {"戌火" ,  "酉金" ,  "申水", "未月" ,  "午日" , "巳水" , "辰金" , "卯火" , "寅木"
    		, "丑土" , "子土"  , "亥木"};
    
    public  final String[] speed_state = new String[] { ""  ,"逆"  ,"蚀"   ,"留"  ,"伏"   ,"迟"   ,"速"};
    
    
    private final String[]   mountain_name = new String[] {	"辛山"	,"酉山"	,"庚山"	,"申山"	,"坤山","未山","丁山"	,"午山"	
    		,"丙山"	,"巳山"	,"巽山","辰山","乙山","卯山"	,"甲山"	,"寅山"	,"艮山"
    		,"丑山","癸山","子山"	,"壬山"	,"亥山","乾山","戌山" };
    
    private final String[]    house_system_char = new String[] {	"P"	,"K"	,"O"	,"R"	,"C"	,"E"	,"V"	,"X"	,"H"	,"T"	,"B"   };

    private final String correction_key = "调整" ;

    private boolean equatorial_orbit;

    private int orbit_body;

    private Hashtable load_table = new Hashtable();

    public Calculate()
    {
        eph = new SwissEph();
        julian_day = julian_day_ut = INVALID;
        computed = false;
        ephe_flag = SweConst.SEFLG_SWIEPH;
        location[0] = location[1] = location[2] = 0.0;
        setLocation(location);
        setTopocentricMode(false, false);
    }

    public void setEphMode(boolean use_moseph)
    {
        ephe_flag &= ~(SweConst.SEFLG_MOSEPH | SweConst.SEFLG_SWIEPH);
        ephe_flag |= use_moseph ? SweConst.SEFLG_MOSEPH : SweConst.SEFLG_SWIEPH;
        if (use_moseph)
            eph.initSwephMosh();
    }

    public boolean getEphMode()
    {
        return (ephe_flag & SweConst.SEFLG_MOSEPH) == SweConst.SEFLG_MOSEPH;
    }

    public void setTopocentricMode(boolean override, boolean val)
    {
        boolean set = override ? val
                : false;
        if (set) {
            ephe_flag |= SweConst.SEFLG_TOPOCTR;
            location[2] = (double)4088;
        } else {
            ephe_flag &= ~SweConst.SEFLG_TOPOCTR;
            location[2] = 0.0;
        }
    }

    public boolean setJulianDay(int[] date)
    {
    
        boolean success = setJulianDay(getJulianDayUT(date));
        if (julian_day_ut < MIN_UT || julian_day_ut > MAX_UT)
            success = false;
        return success;
    }

    public boolean setJulianDay(double jd_ut)
    {
        computed = false;
        julian_day_ut = jd_ut;
        if (julian_day_ut != INVALID) {
            julian_day = julian_day_ut + SweDate.getDeltaT(julian_day_ut);
            return true;
        } else {
            julian_day = INVALID;
            return false;
        }
    }

    static public double getJulianDayUT(int[] date)
    {
        double d_hour = ((double) date[3]) + ((double) date[4]) / 60.0;
        boolean cal_type = (date[0] < SWITCH_YEAR || date[0] == SWITCH_YEAR
                && (date[1] < SWITCH_MONTH || date[1] == SWITCH_MONTH
                        && date[2] < SWITCH_DAY)) ? SweDate.SE_JUL_CAL
                : SweDate.SE_GREG_CAL;
        if (jdate.checkDate(date[0], date[1], date[2], d_hour, cal_type)) {
            return SweDate.getJulDay(date[0], date[1], date[2], d_hour,
                    cal_type);
        } else {
            return INVALID;
        }
    }

    static public void getDateFromJulianDayUT(double jd_ut, int[] date)
    {
        jdate.setCalendarType((jd_ut < SWITCH_UT) ? SweDate.SE_JUL_CAL
                : SweDate.SE_GREG_CAL, SweDate.SE_KEEP_JD);
        jdate.setJulDay(jd_ut + HALF_MINUTE);
        date[0] = jdate.getYear();
        date[1] = jdate.getMonth();
        date[2] = jdate.getDay();
        double hour = jdate.getHour();
        date[3] = (int) Math.floor(hour);
        date[4] = (int) Math.floor(60.0 * (hour - date[3]));
    }

    
    public int getEclipseState(boolean sun)
    {
        if (sun) {
            LinkedList head = computeSolarEclipse(julian_day_ut - ONE_HOUR,
                    julian_day_ut + ONE_HOUR, true, false, false);
            if (!head.isEmpty())
                return SPEED_ECLIPSE;
        } else {
            LinkedList head = computeLunarEclipse(julian_day_ut - 0.5,
                    julian_day_ut + 0.5, true, false);
            if (!head.isEmpty())
                return SPEED_ECLIPSE;
        }
        return SPEED_NORMAL;
    }
    private static final String[] signs = new String[] {
    		"娄金"
    		,"胃土"
    		,"昴日"
    		,"毕月"
    		,"觜火"
    		,"参水"
    		,"井木"
    		,"鬼金"
    		,"柳土"
    		,"星日"
    		,"张月"
    		,"翼火"
    		,"轸水"
    		,"角木"
    		,"亢金"
    		,"氐土"
    		,"房日"
    		,"心月"
    		,"尾火"
    		,"箕水"
    		,"斗木"
    		,"牛金"
    		,"女土"
    		,"虚日"
    		,"危月"
    		,"室火"
    		,"壁水"
    		,"奎木"
};
    public String formatStarDegree(double degree, 
             boolean show_second)
    {
        if (!isValid(degree))
            return "?invalid?";
        DecimalFormat format = new DecimalFormat("00");
        int len = signs.length;
        double last_pos = sign_pos[len - 1];
        for (int i = 0; i < len; i++) {
            double val = degree;
            double pos = sign_pos[i];
            if (pos < last_pos) {
                pos += 360.0;
                if (val < last_pos)
                    val += 360.0;
            }
            if (val >= last_pos && val < pos) {
                val -= last_pos;
                String str = format.format((int) val)
                        + signs[(i > 0) ? (i - 1) : (len - 1)];
                val -= (double) ((int) val);
                return str
                        + City.toMinuteSeconds((val < 0.0) ? -val : val,
                                show_second);
            }
            last_pos = sign_pos[i];
        }
        return "?invalid?";
    }

    public double getJulianDayUT()
    {
        return julian_day_ut;
    }

    public void setLocation(double[] loc)
    {
        location[0] = loc[0];
        location[1] = loc[1];
        eph.swe_set_topo(location[0], location[1], location[2]);
    }

    public void setLocation(double longitude, double latitude,double  altitude)
    {
        location[0] = longitude;
        location[1] = latitude;
        location[2]=altitude;
        eph.swe_set_topo(location[0], location[1], location[2]);
    }


    public double getLongitude()
    {
        return location[0];
    }

    public double getLatitude()
    {
        return location[1];
    }

    public double compute(double jd_ut, int body)
    {
        double ut_sav = julian_day_ut;
        julian_day_ut = jd_ut;
        double val = compute(body);
        julian_day_ut = ut_sav;
        return val;
    }

    public double compute(int body)
    {
    	
        if (body >= SE_START && body <= SE_END)
            return computeSpecial(body);
        else if (body < 0)
            return computeOrbit();
        
        StringBuffer error = new StringBuffer();
        int i_flag = ephe_flag | SweConst.SEFLG_SPEED;
        int o_flag = SweConst.ERR;
        if (sidereal_mode)
            i_flag |= SweConst.SEFLG_SIDEREAL;
        for (int iter = 0; iter < 2; iter++) {
            try {
                o_flag = eph.swe_calc_ut(julian_day_ut, body, i_flag,
                        computation, error);
            } catch (SwissephException e) {
                String index = getEphIndex(e.getMessage());
                if (index != null && loadEphIndex(index))
                    return compute(body);
                computation[0] = INVALID;
                return INVALID;
            }
            if (o_flag != SweConst.NOT_AVAILABLE)
                break;
            if (iter == 0) {
                String index = getEphIndex(error.toString());
                if (index == null || !loadEphIndex(index)) {
                    computation[0] = INVALID;
                    return INVALID;
                }
            }
        }
        computed = o_flag != SweConst.ERR;
        if (!computed)
            computation[0] = INVALID;
     
        return computation[0];
    }

    public void setOrbitData(double speed, double base_date, double base_degree)
    {
        orbit_data[0] = speed;
        orbit_data[1] = base_date;
        orbit_data[2] = base_degree;
        orbit_data[3] = 0.0;
        equatorial_orbit = false;
    }


    private double computeOrbit()
    {
        double day = getJulianDayUT();
        if (day != INVALID) {
            computed = true;
            double offset = orbit_data[0] * (day - orbit_data[1]);
            double degree = orbit_data[2] + offset;
            if (equatorial_orbit) {
                // account for right ascension passed thru in this time
                // span planet due to it's own orbital speed
                double ut = orbit_data[1] - TO_SIDEREAL_OFFSET * offset;
                compute(ut, orbit_body);
                eclToEqu(ut);
                degree = computation[0] + offset;
            }
            if (sidereal_mode)
                degree -= eph.swe_get_ayanamsa_ut(day);
            computation[0] = City.normalizeDegree(degree);
        } else {
            computed = false;
            computation[0] = INVALID;
        }
        computation[1] = orbit_data[3]; // latitude
        computation[2] = 1.0; // distance in AU
        if (equatorial_orbit)
            equToEcl(day);
        computation[3] = orbit_data[0]; // speed in longitude (degree / day)
        return computation[0]; // longitude
    }

    private double getSpeed()
    {
        return computed ? computation[3] : 0.0;
    }


    public LinkedList computeSolarEclipse(double start_ut, double end_ut,
            boolean win, boolean add_last, boolean anywhere)
    {
        LinkedList head = new LinkedList();
        StringBuffer error = new StringBuffer();
        double[] tret = new double[10], attr = new double[20];
        if (!win)
            loadEphIfNeeded(start_ut, end_ut);
        for (double ut = start_ut; ut < end_ut;) {
            int type;
            if (anywhere) {
                type = eph.swe_sol_eclipse_when_glob(ut, ephe_flag, 0, tret, 0,
                        error);
            } else {
                type = eph.swe_sol_eclipse_when_loc(ut, ephe_flag, location,
                        tret, attr, 0, error);
            }
            if (type < 0 || tret[0] > end_ut)
                return head;
            SearchRecord record = new SearchRecord(
                    tret[0],
                    ((type & SweConst.SE_ECL_TOTAL) == SweConst.SE_ECL_TOTAL) ? SearchRecord.TOTAL_ECLIPSE
                            : (((type & SweConst.SE_ECL_ANNULAR) == SweConst.SE_ECL_ANNULAR) ? SearchRecord.ANNULAR_ECLIPSE
                                    : SearchRecord.PARTIAL_ECLIPSE));
            if (add_last)
                head.addLast(record);
            else
                head.addFirst(record);
            if (win)
                return head;
            ut = tret[0] + 1.0;
        }
        return head;
    }

    public LinkedList computeLunarEclipse(double start_ut, double end_ut,
            boolean win, boolean add_last)
    {
        LinkedList head = new LinkedList();
        StringBuffer error = new StringBuffer();
        double[] tret = new double[10], attr = new double[20];
        if (!win)
            loadEphIfNeeded(start_ut, end_ut);
        for (double ut = start_ut; ut < end_ut;) {
            int type = eph.swe_lun_eclipse_when(ut, ephe_flag, 0, tret, 0,
                    error);
            if (type < 0 || tret[0] > end_ut)
                return head;
            type = eph.swe_lun_eclipse_how(tret[0], ephe_flag, location, attr,
                    error);
            if (type <= 0)
                return head;
            SearchRecord record = new SearchRecord(
                    tret[0],
                    ((type & SweConst.SE_ECL_TOTAL) == SweConst.SE_ECL_TOTAL) ? SearchRecord.TOTAL_ECLIPSE
                            : (((type & SweConst.SE_ECL_PENUMBRAL) == SweConst.SE_ECL_PENUMBRAL) ? SearchRecord.PENUMBRAL_ECLIPSE
                                    : SearchRecord.PARTIAL_ECLIPSE));
            if (add_last)
                head.addLast(record);
            else
                head.addFirst(record);
            if (win)
                return head;
            ut = tret[0] + 1.0;
        }
        return head;
    }
    // need pre-computed sun position (sun_pos)
    public int getSpeedState(int body, int index)
    {
    	//index   2水  3金   4火  5木  6土
    	//             2水   0金   3火   1木   4土
        if (index == 0)return 0;
       index = body;
        switch(index) {
        case 2 :  index =2;break;
        case 3 :  index =0;break;
        case 4 :  index =3;break;
        case 5 :  index =1;break;
        case 6 :  index =4;break;
        }
        
        double pos =     computation[0];
        double speed = computation[3];
        double ut_sav = julian_day_ut;
        julian_day_ut = computeSpeedTransit(body, ut_sav, 0.0, true);
        if (julian_day_ut == INVALID) {
            julian_day_ut = ut_sav;
            return getSpeedState(speed);
        }
        double p_pos = compute(body);
        if (getDegreeGap(pos, p_pos) <= stationary_gap[index]) {
            double p_ut = julian_day_ut;
            julian_day_ut = computeTransit(body, ut_sav,
                    City.normalizeDegree(pos + 180.0), true, true);
            if (julian_day_ut > ut_sav || julian_day_ut < p_ut) {
                julian_day_ut = ut_sav;
                return SPEED_STATIONARY;
            }
        }
        julian_day_ut = computeSpeedTransit(body, ut_sav, 0.0, false);
        if (julian_day_ut == INVALID) {
            julian_day_ut = ut_sav;
            return getSpeedState(speed);
        }
        double n_pos = compute(body);
        if (getDegreeGap(pos, n_pos) <= stationary_gap[index]) {
            double n_ut = julian_day_ut;
            julian_day_ut = computeTransit(body, ut_sav,
                    City.normalizeDegree(pos + 180.0), true, false);
            if (julian_day_ut < ut_sav || julian_day_ut > n_ut) {
                julian_day_ut = ut_sav;
                return SPEED_STATIONARY;
            }
        }
        julian_day_ut = ut_sav;
        if(  getDegreeGap(pos, sun_pos) <= invisible_gap[index]) {
     	   return SPEED_INVISIBLE;
         }
        else if (speed < 0.0) {
            return SPEED_REVERSE;
        }
       else if (speed >= fast_speed[index]) {
            return SPEED_FAST;
        } else if (speed <= slow_speed[index]) {
            return SPEED_SLOW;
        } else {
            return SPEED_NORMAL;
        }
    }

    static public double getDegreeGap(double pos1, double pos2)
    {
        double gap = Math.abs(pos1 - pos2);
        if (gap > 180.0)
            gap = 360.0 - gap;
        return gap;
    }

    public int getSpeedState()
    {
        return getSpeedState(getSpeed());
    }

    public  int getSpeedState(double speed)
    {
        return (speed < 0.0) ? SPEED_REVERSE : SPEED_NORMAL;
    }


    public void initSpecial(double sun_long, double moon_long, boolean day)
    {
        sun_pos = sun_long;
        moon_pos = moon_long;
        day_fortune_mode = day;
    }

    private double computeSpecial(int body)
    {
        computation[0] = INVALID;
        computation[1] = computation[2] = computation[3] = 0.0;
        switch (body) {
            case SE_ASC:
                computation[0] = ascmc[0];
                break;
            case SE_MC:
                computation[0] = ascmc[1];
                break;
            case SE_FORTUNE:
                if (ascmc[0] != INVALID) {
                    double gap = moon_pos - sun_pos;
                    if (!day_fortune_mode)
                        gap = -gap;
                    computation[0] = City.normalizeDegree(ascmc[0] + gap);
                }
                break;
            default:
                break;
        }
        return computation[0];
    }

    public double getAltitude()
    {
        if (computation[0] == INVALID)
            return INVALID;
        return azimuth[1];
    }


    public double getLocalJulianDayUT(String zone, boolean noon)
    {
        int[] date = new int[5];
        getDateFromJulianDayUT(julian_day_ut, date);
        if (zone == null || zone.equals(City.UNKNOWN_ZONE)) {
            // longitude adjusted
            BaseCalendar.formatDate(location[0], date, date, 0.0, false, true);
        } else {
            BaseCalendar.addZoneOffset(zone, date, 0, true);
        }
        // noon local time
        double jd_ut = julian_day_ut - (date[3] + date[4] / 60.0) / 24.0;
        return noon ? (jd_ut + 0.5) : jd_ut;
    }

    public boolean computeRiseSet(String zone, int body, double[] rise_set)
    {
        // mid-night local time
        double when = getLocalJulianDayUT(zone, false);
        boolean equator = false;
        int i_flag = ephe_flag;
        rise_set[0] = rise_set[1] = INVALID;
        StringBuffer error = new StringBuffer();
        DblObj rise = new DblObj(), set = new DblObj();
        if (eph.swe_rise_trans(when, body, null, i_flag, SweConst.SE_CALC_RISE,
                location, 0.0, 20.0, rise, error) != SweConst.OK) {
            // change to equator
            double lat_val = location[1];
            location[1] = 0.0;
            eph.swe_rise_trans(when, body, null, i_flag, SweConst.SE_CALC_RISE,
                    location, 0.0, 20.0, rise, error);
            location[1] = lat_val;
            equator = true;
        }
        if (eph.swe_rise_trans(when, body, null, i_flag, SweConst.SE_CALC_SET,
                location, 0.0, 20.0, set, error) != SweConst.OK) {
            // change to equator
            double lat_val = location[1];
            location[1] = 0.0;
            eph.swe_rise_trans(when, body, null, i_flag, SweConst.SE_CALC_SET,
                    location, 0.0, 20.0, set, error);
            location[1] = lat_val;
            equator = true;
        }
        rise_set[0] = rise.val;
        rise_set[1] = set.val;
        return equator;
    }

    public boolean isDayBirth(double[] rise_set)
    {
        // in the sky, valid for sun only
        return julian_day_ut >= rise_set[0] && julian_day_ut < rise_set[1];
    }


    private void eclToEqu(double ut)
    {
        eph.swe_azalt(ut, SweConst.SE_ECL2HOR, location, 0.0, 20.0,
                computation, azimuth);
        eph.swe_azalt_rev(ut, SweConst.SE_HOR2EQU, location, azimuth,
                computation);
    }

    private void equToEcl(double ut)
    {
        eph.swe_azalt(ut, SweConst.SE_EQU2HOR, location, 0.0, 20.0,
                computation, azimuth);
        eph.swe_azalt_rev(ut, SweConst.SE_HOR2ECL, location, azimuth,
                computation);
    }


    private double computeTransit(int body, double start_ut, double degree,
            boolean sidereal_adjust, boolean backward)
    {
        try {
            int i_flag = ephe_flag | SweConst.SEFLG_TRANSIT_LONGITUDE;
            if (sidereal_adjust && sidereal_mode)
                i_flag |= SweConst.SEFLG_SIDEREAL;
            return eph.getNextTransitUT(body, degree, i_flag, start_ut,
                    backward);
        } catch (SwissephException e) {
            String index = getEphIndex(e.getMessage());
            if (index != null && loadEphIndex(index))
                return computeTransit(body, start_ut, degree, sidereal_adjust,
                        backward);
            return INVALID;
        } catch (IllegalArgumentException e) { // unsupported planets
            return INVALID;
        }
    }

    public double computeSpeedTransit(int body, double start_ut, double speed,
            boolean backward)
    {
        ephe_flag |= SweConst.SEFLG_TRANSIT_SPEED;
        double ut = computeTransit(body, start_ut, speed, true, backward);
        ephe_flag &= ~SweConst.SEFLG_TRANSIT_SPEED;
        return ut;
    }

    // Modified Newton-Raphson with bisection method
  
    private void loadEphIfNeeded(double s_ut, double e_ut)
    {
        if (getEphMode())
            return;
        compute(s_ut, SweConst.SE_SUN);
        compute(e_ut, SweConst.SE_SUN);
    }

    private boolean loadEphIndex(String index)
    {
        if ((ephe_flag & SweConst.SEFLG_MOSEPH) != 0) {
            // already in Moseph mode and still failing, must be asteroids
            return false;
        }
        if (index.startsWith("ast")) {
            int n = index.indexOf('\\');
            if (n < 0)
                n = index.indexOf('/');
            String ast_dir_name = index.substring(0, n);
            index = index.substring(n + 1);
            if (load_table.get(index) != null)
                return false; // failed before
            int num = FileIO.parseInt(index.substring(2), 0, true);
         
            String ast_name = index + ".se1";
            String url_base ="ftp://ftp.astro.com/pub/swisseph/ephe/longfiles";
            File ast_dir = new File("ephe" + File.separator + ast_dir_name);
            File ast_file = new File("ephe" + File.separator + ast_dir_name
                    + File.separator + ast_name);
            File ast_tmp = new File("ephe" + File.separator + ast_dir_name
                    + File.separator + index + ".tmp");
            if ((ast_dir.isDirectory() || ast_dir.mkdir())
                    && ast_file.exists()
                    || copyFileFromURL(url_base + "/" + ast_dir_name + "/"
                            + ast_name, ast_tmp.getPath(), true)) {
                if (ast_tmp.exists())
                    ast_tmp.renameTo(ast_file);
                return true;
            } else {
                ast_tmp.deleteOnExit();
               
                load_table.put(index, "t");
                return false;
            }
        } else {
            boolean bc = index.startsWith("m");
            int year = FileIO.parseInt(index.substring(1), 0, true) * 100;
            String s_date, e_date;
            if (bc) {
                s_date = (year + 1) + " B.C.";
                e_date = (year - 600 + 2) + " B.C.";
            } else {
                s_date = (year == 0) ? "1 B.C." : (year + " A.D.");
                e_date = (year + 600) + " A.D.";
            }
        
            String pl_name = "sepl" + index + ".se1";
            String mo_name = "semo" + index + ".se1";
            String as_name = "seas" + index + ".se1";
            String url_base = "";
            File pl_file = new File("ephe" + File.separator + pl_name);
            File mo_file = new File("ephe" + File.separator + mo_name);
            File as_file = new File("ephe" + File.separator + as_name);
            File pl_tmp = new File("ephe" + File.separator + "sepl" + index
                    + ".tmp");
            File mo_tmp = new File("ephe" + File.separator + "semo" + index
                    + ".tmp");
            File as_tmp = new File("ephe" + File.separator + "seas" + index
                    + ".tmp");
            if ((pl_file.exists() || copyFileFromURL(url_base + "/" + pl_name,
                    pl_tmp.getPath(), true))
                    && (mo_file.exists() || copyFileFromURL(url_base + "/"
                            + mo_name, mo_tmp.getPath(), true))
                    && (as_file.exists() || copyFileFromURL(url_base + "/"
                            + as_name, as_tmp.getPath(), true))) {
                if (pl_tmp.exists())
                    pl_tmp.renameTo(pl_file);
                if (mo_tmp.exists())
                    mo_tmp.renameTo(mo_file);
                if (as_tmp.exists())
                    as_tmp.renameTo(as_file);
                return true;
            } else {
                pl_tmp.deleteOnExit();
                mo_tmp.deleteOnExit();
                as_tmp.deleteOnExit();
                Message.warn("error");
                setEphMode(true);
                return true;
            }
        }
    }

    private boolean copyFileFromURL(String url_name, String out_name,
            boolean again)
    {
        try {
            URL url = new URL(url_name);
            URLConnection conn = url.openConnection();
            BufferedInputStream in = new BufferedInputStream(
                    conn.getInputStream());
            BufferedOutputStream out = new BufferedOutputStream(
                    new FileOutputStream(out_name));
            int size;
            byte[] buffer = new byte[4096];
            while ((size = in.read(buffer)) >= 0) {
                out.write(buffer, 0, size);
            }
            in.close();
            out.close();
        } catch (IOException e) {
            if (again)
                return copyFileFromURL(url_name, out_name, false);
            return false;
        }
        return true;
    }

    private String getEphIndex(String message)
    {
        final String missing_ast_file = "SwissEph file 'ast";
        final String missing_pl_file = "SwissEph file 'sepl";
        final String missing_mo_file = "SwissEph file 'semo";
        final String missing_as_file = "SwissEph file 'seas";
        int index = message.indexOf(missing_ast_file);
        if (index >= 0) {
            message = message.substring(message.indexOf("'") + 1);
        } else {
            index = message.indexOf(missing_pl_file);
            if (index < 0) {
                index = message.indexOf(missing_mo_file);
                if (index < 0) {
                    index = message.indexOf(missing_as_file);
                    if (index < 0)
                        return null;
                }
            }
            message = message.substring(index + missing_pl_file.length());
        }
        index = message.indexOf(".");
        return message.substring(0, index);
    }

    static public boolean isValid(double val)
    {
        return val != INVALID;
    }


    public  String  getSpeedState(int planet_no)
    {
        int state ;
    	
        if ( planet_no != 12) {
                if (planet_no == SUN || planet_no == MOON) {
                    state = getEclipseState(planet_no == SUN);
                } else if (planet_no >= VENUS && planet_no <= SATURN) {
         
                    state = getSpeedState(planet_no,planet_no);}
                else if (planet_no >= URANUS && planet_no <= PLUTO)
                    state =getSpeedState();
                else
                    state = Calculate.SPEED_NORMAL;
        } else {
            state = Calculate.SPEED_NORMAL;
        }
   
        return speed_state[state];
        }
    
    public double computePlanet(int planet_no)
    {
    	
    	double pos;
        pos = compute(planets[planet_no]);
        pos = City.normalizeDegree(pos );
		return pos;
    	
    }
    private static String[] name = new String[]{"日","月","水","金","火","木","土"
    		,"天","海","冥","unknown","计","孛","罗","气"};
    public String  formatPos(double pos) {
    	double d = pos;
    	String s1 = ""+d; 
    	String s2 = s1.substring(0,s1.indexOf("."));
    	int i = Integer.parseInt(s2);
    	double  f = d - i ;
    	int num = i / 30 ;
    	String s3 =( i - num * 30)+full_zodiac[num]+f*60; 
    	
		return s3;
	}
    public static  double opposite(double pos) {
    	double d = pos;
    	String s1 = ""+d; 
    	String s2 = s1.substring(0,s1.indexOf("."));
    	String s3 = s1.substring(s1.indexOf("."),s1.length());
    	 int i = Integer.parseInt(s2);
    	 i +=180;
    	 i %=360;
    	 s1 = ""+ i +s3;
    	return Double.parseDouble(s1);
    }

  public double calculateLifeHourse(int hour,double pos_sun) {
	 double pos = pos_sun;

	  if( hour >= 5 && hour < 7 ) {
		  
	  }else if( hour >=7 && hour < 9 ) {
		   pos+=30;
	  }
    else if( hour >= 9 && hour < 11 ) {
    	   pos+=60;
	  }
     else if( hour >= 11 && hour < 13 ) {
    	  pos+=90;
     }
    else if( hour >= 13 && hour < 15 ) {
    	  pos+=120;
     }
    else if( hour >= 15 && hour < 17 ) {
    	  pos+=150;
    }
    else if( hour >= 17 && hour < 19 ) {
    	  pos+=180;
    }
    else if( hour >= 19 && hour < 21 ) {
  	  pos+=210;
  }
    else if( hour >= 21 && hour < 23 ) {
  	  pos+=240;
  }    
    else if( hour >= 23 ||  hour < 1 ) {
  	  pos+=270;
  }  
    else if( hour >= 1 && hour < 3 ) {
  	  pos+=300;
  }
    else if( hour >= 3 && hour < 5 ) {
    	  pos+=330;
    }
	  if( pos < 0 ) pos -= 360 ;

	  return pos;
  }
    
    public String  formatPos(double pos  , int  a ) {
    	double d = pos;
    	String s1 = ""+d; 
    	
    	String s2 = s1.substring(0,s1.indexOf("."));
    	int i = Integer.parseInt(s2);//i为整数部分

    	double  f = d - i ;//小数部分
    	int num = i / 30 ;
    	int  degree = degreel_zodiac[num] - ( i - num * 30);
    	String s3 ="方位角-"+String.format("%03d",degree)+"度-"+name[a] +" : "+String.format("%02d",( i - num * 30))+full_zodiac[num%12]+ String.format("%.0f", f*60); 
    	
		return s3;
	}
    public String  formatPosToZodiac(double pos   ) {
    	double d = pos;
    	String s1 = ""+d; 
    	String s2 = s1.substring(0,s1.indexOf("."));
    	int i = Integer.parseInt(s2);
    	double  f = d - i ;
    	int num = i / 30 ;
    	String s3 =" : "+String.format("%02d",( i - num * 30))+full_zodiac[num%12]+String.format("%.0f", f*60); 
    	
		return s3;
	}
}
