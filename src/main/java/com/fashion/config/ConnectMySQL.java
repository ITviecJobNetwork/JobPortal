package com.fashion.config;

import java.sql.Connection;
import java.sql.DriverManager;

public class ConnectMySQL {
  private static final String URL ="";
  private static Connection conn;
  private static final String USER = "";
  private static final String PASSWORD = "";
  public static Connection getConnection() {


    try {
      System.out.println("Connecting to database...");
      System.out.println("URL..."+ URL);
      System.out.println("USER..."+ USER);
      System.out.println("PASSWORD..."+ PASSWORD);
      Class.forName("com.mysql.jdbc.Driver");
      conn = DriverManager.getConnection(URL, USER, PASSWORD);
      System.out.println("Connected database successfully...");
    } catch (Exception e) {
      e.printStackTrace();
    }
    return conn;
  }
}
