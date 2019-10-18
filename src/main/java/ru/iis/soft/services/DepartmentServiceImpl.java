package ru.iis.soft.services;

import lombok.SneakyThrows;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.w3c.dom.*;
import org.w3c.dom.ls.DOMImplementationLS;
import org.w3c.dom.ls.LSOutput;
import org.w3c.dom.ls.LSSerializer;
import ru.iis.soft.models.Department;
import ru.iis.soft.resporitories.DepartmentRepositoryJdbc;
import ru.iis.soft.resporitories.DepartmentRepositoryJdbcImpl;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.File;
import java.io.FileInputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.DriverManager;
import java.util.*;

/**
 * @author Samat Zaydullin
 *
 * */

public class DepartmentServiceImpl implements DepartmentService{

  /**Класс, который работает с данными БД*/
  private DepartmentRepositoryJdbc departmentRepository;

  /**Список отделов в XML*/
  private List<Department> departmentsInXml;
  /**Уникальные значения, для проверки на наличие дубликатов*/
  private Set<Department> uniqueDepartments;
  /**Список отделов в БД*/
  private List<Department> departmentsInDB;
  /**Соединение с БД*/
  private Connection connection;

  private static final Logger logger = Logger.getLogger(DepartmentServiceImpl.class);

  /**Конструктор класса, в котором происходит инициализация*/
  @SneakyThrows
  public DepartmentServiceImpl() {
    /**Файл настроек приложения*/
    File file = new File("src/main/resources/db.properties");
    Properties properties = new Properties();
    properties.load(new FileInputStream(file));
    String driver = properties.getProperty("jdbc.driver");
    String url = properties.getProperty("jdbc.url");
    String username = properties.getProperty("jdbc.username");
    String password = properties.getProperty("jdbc.password");
    Class.forName(driver);
    connection = DriverManager.getConnection(url, username, password);
    connection.setAutoCommit(false); //отключаем автокоммит, для корректной работы с транзакциями
    departmentRepository = new DepartmentRepositoryJdbcImpl(connection);
    logger.info("Database connection");
  }


  @Override
  public List<Department> getData() {
    logger.info("Getting a List");
    return departmentRepository.uploadData();
  }

  @Override
  @SneakyThrows
  public void writeDataInXml(String fileName) {
    DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
    DocumentBuilder builder = factory.newDocumentBuilder();
    Document document = builder.newDocument();
    Element departments = document.createElement("departments");
    document.appendChild(departments);
    Element department;
    Element depCode;
    Element depJob;
    Element description;
    Text depCodeText, depJobText, descriptionText;
    for (Department dep: getData()) {
      department = document.createElement("department");

      depCode = document.createElement("depCode");
      depJob = document.createElement("depJob");
      description = document.createElement("description");

      depCodeText = document.createTextNode(dep.getDepCode());
      depJobText = document.createTextNode(dep.getDepJob());
      descriptionText = document.createTextNode(dep.getDescription());

      departments.appendChild(department);

      department.appendChild(depCode);
      department.appendChild(depJob);
      department.appendChild(description);

      depCode.appendChild(depCodeText);
      depJob.appendChild(depJobText);
      description.appendChild(descriptionText);
    }

    DOMImplementation impl = document.getImplementation();
    DOMImplementationLS implLS = (DOMImplementationLS) impl.getFeature("LS", "3.0");
    LSSerializer serializer = implLS.createLSSerializer();
    serializer.getDomConfig().setParameter("format-pretty-print", true);
    LSOutput output = implLS.createLSOutput();
    output.setEncoding("UTF-8");
    output.setByteStream(Files.newOutputStream(Paths.get(fileName + ".xml")));
    serializer.write(document, output);
    logger.info("Writing data in XML file");
    connection.close();
  }

  @Override
  @SneakyThrows
  public List<Department> readDataFromXml(String fileName) {
    Map<Integer, Department> departmentMap = new HashMap<>();
    File xmlFile = new File(fileName + ".xml");
    //Проверяем, существует ли файл
    if (!xmlFile.exists()){
      logger.error("File not found");

      System.err.println("File not found");
      System.exit(1);
    }
    DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
    DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
    Document doc = dBuilder.parse(xmlFile);
    doc.getDocumentElement().normalize();
    NodeList nList = doc.getElementsByTagName("department");

    for (int i = 0; i < nList.getLength(); i++) {
      Node nNode = nList.item(i);
      if (nNode.getNodeType() == Node.ELEMENT_NODE) {
        Element eElement = (Element) nNode;
        Department department = Department.builder()
                .depCode(eElement.getElementsByTagName("depCode")
                        .item(0).getTextContent())
                .depJob(eElement.getElementsByTagName("depJob")
                        .item(0).getTextContent())
                .description(eElement.getElementsByTagName("description")
                        .item(0).getTextContent())
                .build();
        departmentMap.put(i, department);
      }
    }
    uniqueDepartments = new HashSet<>(departmentMap.values());
    if (departmentMap.size() != uniqueDepartments.size()){
      System.err.println("XML file has two entries with one natural key");
      System.exit(1);
    }
    logger.info("Reading data from XML file");
    return new ArrayList<>(departmentMap.values());
  }

  @Override
  public void deleteMissingValues(List<Department> depsDB, List<Department> depsXml) {
    logger.info("Delete missing values");
    for (Department dep : depsDB) {
      //XML файл не содержит данные из БД -> удаляется
      if (!depsXml.contains(dep)){
        departmentRepository.delete(dep);
      }
    }
  }

  @Override
  public void updateValues(List<Department> depsDB, List<Department> depsXml) {
    logger.info("Updating values");
    //Если есть данные в БД и в файле, но описания разные, то обновляем
    for (Department dep1 : depsDB) {
      for (Department dep2 : depsXml) {
        if (dep1.equals(dep2) && !dep1.getDescription().equals(dep2.getDescription())){
          departmentRepository.update(dep2);
        }
      }
    }
  }

  @Override
  public void addNewValues(List<Department> depsDB, List<Department> depsXml) {
    logger.info("Adding new values");
    for (Department dep : depsXml) {
      //Добавляется новое значение, так как в БД нет такого из XML
      if (!depsDB.contains(dep)){
        departmentRepository.save(dep);
      }
    }
  }

  @Override
  @SneakyThrows
  public void syncData(String fileName) {
    departmentsInDB = departmentRepository.uploadData();
    departmentsInXml = readDataFromXml(fileName);
    deleteMissingValues(departmentsInDB, departmentsInXml);
    updateValues(departmentsInDB, departmentsInXml);
    addNewValues(departmentsInDB, departmentsInXml);
    //Конец транзакции
    connection.commit();
    connection.close();
  }
}
