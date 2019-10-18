package ru.iis.soft.services;

import ru.iis.soft.models.Department;

import java.util.List;

/**
 * @author Samat Zaydullin
 *
 * */

public interface DepartmentService {
  /**Получение отделов в виде списка*/
  List<Department> getData();
  /**Запись данных в XML файл*/
  void writeDataInXml(String fileName);
  /**Чтение данных из XML*/
  List<Department> readDataFromXml(String fileName);
  /**Удаление отсутствующих значений*/
  void deleteMissingValues(List<Department> depsDB, List<Department> depsXml);
  /**Обновление значений*/
  void updateValues(List<Department> depsDB, List<Department> depsXml);
  /**Добавление новых значений*/
  void addNewValues(List<Department> depsDB, List<Department> depsXml);
  /**Синхронизация данных*/
  void syncData(String fileName);
}
