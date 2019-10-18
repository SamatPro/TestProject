package ru.iis.soft.resporitories;

import ru.iis.soft.models.Department;

import java.util.List;

/**
 * @author Samat Zaydullin
 *
 * */

public interface DepartmentRepositoryJdbc extends CrudRepository<Department>{
  /**Получение данных из БД*/
  List<Department> uploadData();
}
