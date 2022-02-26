package ir.afraapps.form;


public class Clock {

  private final int hour;
  private final int minute;

  public Clock(int hour, int minute) {
    this.hour = hour;
    this.minute = minute;
  }

  public int getHour() {
    return hour;
  }

  public int getMinute() {
    return minute;
  }

}