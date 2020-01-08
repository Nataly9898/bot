package logic.commands;


import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.DaoManager;
import com.j256.ormlite.jdbc.JdbcConnectionSource;
import com.j256.ormlite.support.ConnectionSource;
import models.Notification;

import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class DatabaseConnection {

    final static Logger logger = Logger.getLogger(DatabaseConnection.class.getName());
    private static ConnectionSource connectionSource;
    private static Dao<Notification, Long> notificationDao;

    static {
        try {
            String databaseUrl = "jdbc:sqlite:notifications.db";
            connectionSource =
                    new JdbcConnectionSource(databaseUrl);

            notificationDao=DaoManager.createDao(connectionSource, Notification.class);
        } catch (SQLException e) {
            logger.log(Level.SEVERE, e.getMessage()); //TODO
        }
    }

    public static Dao<Notification, Long> getNotificationDao() {
        return notificationDao;
    }

    public static ConnectionSource getConnectionSource() {
        return connectionSource;
    }
}
