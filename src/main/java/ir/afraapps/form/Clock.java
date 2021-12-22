package ir.afraapps.form;


public class Clock {

  private final int hour;
  private final int minute;


  public Clock(int hour, int minute) {
    this.hour = hour;
    this.minute = minute;
  }


  static Clock fromDouble(double arg) {
    arg = fixHour(arg + 0.5 / 60); // add 0.5 minutes to round
    int hour = (int) arg;
    int minute = (int) ((arg - hour) * 60d);

    return new Clock(hour, minute);
  }

  static Clock fromString(String arg) {
    String[] parts = arg.split(":");
    int hour = Integer.valueOf(parts[0]);
    int minute = Integer.valueOf(parts[1]);

    return new Clock(hour, minute);
  }


  public int getHour() {
    return hour;
  }


  public int getMinute() {
    return minute;
  }


  private static double fixHour(double a) {
    return fix(a, 24);
  }

  private static double fix(double a, double b) {
    double result = a % b;
    if (result < 0)
      result = b + result;
    return result;
  }
}