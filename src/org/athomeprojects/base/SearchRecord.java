package org.athomeprojects.base;


public class SearchRecord {
	static public final int UNKNOWN = 0;

	static public final int PARTIAL_ECLIPSE = 1;

	static public final int ANNULAR_ECLIPSE = 2;

	static public final int PENUMBRAL_ECLIPSE = 3;

	static public final int TOTAL_ECLIPSE = 4;

	private int type;

	private double jd_ut;

	private String data;

	public SearchRecord(double val, int kind) {
		jd_ut = val;
		type = kind;
	}

	public SearchRecord(double val, String mesg) {
		jd_ut = val;
		data = mesg;
	}

	public double getTime() {
		return jd_ut;
	}

	public String getData() {
		return data;
	}

	public boolean isType(int kind) {
		return type == kind;
	}

	public String getType() {
		switch (type) {
		case ANNULAR_ECLIPSE:
			return ("annular_eclipse");
		case PENUMBRAL_ECLIPSE:
			return ("penumbral_eclipse");
		case TOTAL_ECLIPSE:
			return ("total_eclipse");
		case PARTIAL_ECLIPSE:
			return("partial_eclipse");
		default:
			return "";
		}
	}
}
