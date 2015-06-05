package com.spartanlaboratories.engine.util;

public class Calendar {
	public enum Day{
		MONDAY, TUESDAY, WEDNESDAY, THURSDAY, FRIDAY, SATURDAY, SUNDAY,;
	}
	public enum Month{
		JANUARY, FUBRUARY, MARCH, APRIL, MAY, JUNE, JULY, AUGUST, SEPTEMBER, OCTOBER, NOVEMBER, DECEMBER,;
	}
	private int seconds;
	private int minutes;
	private int hours;
	private int dayOfYear;
	private int date;
	private Day day;
	private int week;
	private Month month;
	private int year;
	public Calendar(){
		date = 1;
		day = Day.MONDAY;
		month  = Month.JANUARY;
		year = 0;
	}
	public void advanceYears(){
		dayOfYear = 1;
		week = 1;
		year ++;
	}
	public void advanceMonth(){
		if(month.ordinal() < Month.values().length - 1)
			month = Month.values()[month.ordinal() + 1];
		else {
			month = Month.values()[0];
			advanceYears();
		}
	}
	public void advanceDay(){
		if(day.ordinal() < Day.values().length - 1)
			day = Day.values()[day.ordinal() + 1];
		else {
			day = Day.values()[0];
			week++;
		}
		++dayOfYear; ++date;
		switch(month){
		case JANUARY: case MARCH: case MAY: case JULY: case AUGUST: case OCTOBER: case DECEMBER:
			if(date < 31)break;
			date -= 30;
			advanceMonth();
			break;
		case FUBRUARY:
			int numDays = year % 4 == 0 ? 29 : 28;
			if(date < numDays)break;
			date-= numDays;
			advanceMonth();
			break;
		case APRIL: case JUNE: case SEPTEMBER: case NOVEMBER:
			if(date < 30)break;
			date -= 30;
			advanceMonth();
			break;
		}
	}
	public void advanceHours(int increment) throws IllegalArgumentException{
		if(increment < 0)throw new IllegalArgumentException();
		if((hours += increment) > 24){
			hours -= 24;
			advanceDay();
			advanceHours(0);
		}
	}
	public void advanceMinutes(int increment) throws IllegalArgumentException{
		if(increment < 0)throw new IllegalArgumentException();
		advanceHours((minutes += increment) / 60);
		minutes %= 60;
	}
	public void advanceSeconds(int increment) throws IllegalArgumentException{
		advanceMinutes((seconds += increment) / 60);
		seconds %= 60;
	}
}
