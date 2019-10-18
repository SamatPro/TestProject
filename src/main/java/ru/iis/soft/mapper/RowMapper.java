package ru.iis.soft.mapper;

import java.sql.ResultSet;
/**
 * @author Samat Zaydullin
 *
 * */
public interface RowMapper<T> {
  /**Каждый репозиторий сам определяет реализацию.
   * Используется, чтобы унифицировать отображение
   * строки ResultSet в конткретный Java-объект*/
  T rowMap(ResultSet resultSet);
}
