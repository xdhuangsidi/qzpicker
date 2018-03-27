package com.hsd.qzsy;

import java.text.DecimalFormat;

public class FileIO {
	
	static public int parseInt(String str, int def_val, boolean positive) {
		int val;
		try {
			val = Integer.parseInt(str.trim());
			if (positive && val < 0)
				val = def_val;
		} catch (NumberFormatException e) {
			val = def_val;
		}
		return val;
	}
	
	static public double parseDouble(String str, double def_val,
			boolean positive) {
		double val;
		try {
			val = Double.parseDouble(str.trim());
			if (positive && val < 0.0)
				val = def_val;
		} catch (NumberFormatException e) {
			val = def_val;
		}
		return val;
	}
	
	static public String formatDouble(double val, int width,
			int fraction_width, boolean align, boolean sign) {
		boolean negative = val < 0.0;
		if (negative && sign)
			val = -val;
		String seq = "";
		if (align) {
			for (int i = 1; i < width; i++)
				seq += "0";
		}
		seq += "0.";
		for (int i = 0; i < fraction_width; i++)
			seq += align ? "0" : "#";
		DecimalFormat format = new DecimalFormat(seq);
		return (sign ? (negative ? "-" : "+") : "") + format.format(val);
	}
}
