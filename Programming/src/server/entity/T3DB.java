package server.entity;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

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
            System.out.println("trying other path to db");
            try {
                Class.forName("org.sqlite.JDBC");
                String url = "jdbc:sqlite:server/assets/db/t3db.db";
                connect = DriverManager.getConnection(url);
                System.out.println("Connect database successfully");
            } catch (Exception e1) {
                System.out.println(e1.getMessage());
            }

        }
        return connect;
    }


    public static void main(String[] args) {
        T3DB.getConnection();
    }
}
