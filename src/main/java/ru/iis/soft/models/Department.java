package ru.iis.soft.models;

import lombok.*;

import java.util.Objects;

/**
 * @author Samat Zaydullin
 *
 * */
@Getter
@Setter
@Builder
public class Department {
  /**Код отдела*/
  private String depCode;
  /**Название должности в отделе*/
  private String depJob;
  /**Комментарий*/
  private String description;

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    Department that = (Department) o;
    return Objects.equals(depCode, that.depCode) &&
            Objects.equals(depJob, that.depJob);
  }

  @Override
  public int hashCode() {
    return Objects.hash(depCode, depJob);
  }

  @Override
  public String toString() {
    return " {" + depCode + ", " + depJob + ", " + description + "}";
  }
}
