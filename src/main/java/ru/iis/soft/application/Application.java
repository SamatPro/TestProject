package ru.iis.soft.application;

import ru.iis.soft.services.DepartmentService;
import ru.iis.soft.services.DepartmentServiceImpl;

import java.util.Scanner;

/**
 * @author Samat Zaydullin
 *
 * */

public class Application {
  public static void main(String[] args) {
    DepartmentService departmentService = new DepartmentServiceImpl();

    if (args.length >= 2){
      switch (args[0].toLowerCase()){
        case "sync":
          departmentService.syncData(args[1]);
          break;
        case "load":
          departmentService.writeDataInXml(args[1]);
          break;
        default:
          System.err.println("Wrong command!");
          System.exit(1);;
      }
    }
  }
}
