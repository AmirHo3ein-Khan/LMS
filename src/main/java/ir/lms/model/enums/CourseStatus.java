package ir.lms.model.enums;

import lombok.Getter;

@Getter
public enum CourseStatus {
 FILLED(1, "پرشده", "12343") ,
 UNFILLED(2,"تعریف نشده", "12345") ,;

 private final int value;
 private final String name;
 private final String code;

 CourseStatus(int value, String name, String code) {
  this.value = value;
  this.name = name;
  this.code = code;
 }

}
