package ru.iis.soft.resporitories;

import lombok.SneakyThrows;
import org.apache.log4j.Logger;
import ru.iis.soft.mapper.RowMapper;
import ru.iis.soft.models.Department;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * @author Samat Zaydullin
 *
 * */

public class DepartmentRepositoryJdbcImpl implements DepartmentRepositoryJdbc {

  /**Логгер*/
  private static final Logger logger = Logger.getLogger(DepartmentRepositoryJdbcImpl.class);

  /**Соединение с базой данных*/
  private Connection connection;

  /**Список отделов*/
  private List<Department> departments;

  /**SQL код для выгрузки данных из БД*/
  private static final String SQL_UPLOAD_DATA = "SELECT * FROM department;";
  /**SQL код для добавления новых значений в БД*/
  private static final String SQL_INSERT = "INSERT INTO department(depcode, depjob, description) VALUES (?, ?, ?);";
  /**SQL код для обновления данных в БД*/
  private static final String SQL_UPDATE = "UPDATE department SET description=? WHERE depcode=? AND depjob=?;";
  /**SQL код для удаления данных из БД*/
  private static final String SQL_DELETE = "DELETE FROM ONLY department WHERE depcode=? AND depjob=?;";

  /**Через конструктор инициализируем соедиение с БД*/
  public DepartmentRepositoryJdbcImpl(Connection connection) {
    this.connection = connection;
  }

  @Override
  public Optional<Department> findOne(Long id) {
    return Optional.empty();
  }

  @Override
  @SneakyThrows
  public void save(Department model) {
    PreparedStatement statement = connection.prepareStatement(SQL_INSERT);
    statement.setString(1, model.getDepCode());
    statement.setString(2, model.getDepJob());
    statement.setString(3, model.getDescription());
    statement.execute();
    logger.info("Adding ".concat(model.toString()));
  }

  @Override
  @SneakyThrows
  public void delete(Department model) {
    PreparedStatement statement = connection.prepareStatement(SQL_DELETE);
    statement.setString(1, model.getDepCode());
    statement.setString(2, model.getDepJob());
    statement.execute();
    logger.info("Deleting ".concat(model.toString()));
  }

  @Override
  @SneakyThrows
  public void update(Department model) {
    PreparedStatement statement = connection.prepareStatement(SQL_UPDATE);
    statement.setString(1, model.getDescription());
    statement.setString(2, model.getDepJob());
    statement.setString(3, model.getDescription());
    statement.executeUpdate();
    logger.info("Updating ".concat(model.toString()));
  }


  @Override
  @SneakyThrows
  public List<Department> uploadData() {
    departments = new ArrayList<>();
    PreparedStatement statement = connection.prepareStatement(SQL_UPLOAD_DATA);
    ResultSet resultSet = statement.executeQuery();
    while (resultSet.next()){
      departments.add(departmentRowMapper.rowMap(resultSet));
    }
    logger.info("data uploading from DB");
    return departments;
  }

  /**Реализация интерфейса.
   * Унифицирование строки ResultSet в конкретный Java-объект*/
  private RowMapper<Department> departmentRowMapper =new RowMapper<Department>() {
    @Override
    @SneakyThrows
    public Department rowMap(ResultSet resultSet) {
      return Department.builder()
              .depCode(resultSet.getString("depcode"))
              .depJob(resultSet.getString("depjob"))
              .description(resultSet.getString("description"))
              .build();
    }
  };

}
