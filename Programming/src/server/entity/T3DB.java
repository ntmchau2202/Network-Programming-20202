package server.entity;

import java.sql.Connection;
import java.sql.DriverManager;

public class T3DB {
    private static Connection connect;

    public static Connection getConnection() {
        if (connect != null) return connect;
        try {
            Class.forName("org.sqlite.JDBC");
            String url = "jdbc:sqlite:src/server/assets/db/t3db.db";
            connect = DriverManager.getConnection(url);
            System.out.println("Connect database successfully");
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        return connect;
    }


    public static void main(String[] args) {
        T3DB.getConnection();
    }
}
