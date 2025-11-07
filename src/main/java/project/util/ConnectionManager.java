package project.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public final class ConnectionManager {
    private static final String URL_KEY = "db.url";
    private static final String DRIVER_KEY = "db.driver";
    static {
        load();
    }

    private ConnectionManager() {}

    public static Connection get() {
        try {
//            return DriverManager.getConnection(PropertiesUtil.get(URL_KEY)); НЕ РАБОТАЕТ НА НОУТЕ
            return DriverManager.getConnection("jdbc:sqlite:C:/Users/111/IdeaProjects/exchange_rates/identifier.sqlite");
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private static void load() {
        try {
            Class.forName(PropertiesUtil.get(DRIVER_KEY));
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }
}
