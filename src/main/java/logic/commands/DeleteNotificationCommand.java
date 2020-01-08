package logic.commands;

import com.j256.ormlite.dao.Dao;
import models.Notification;
import org.telegram.telegrambots.meta.bots.AbsSender;

import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

import static logic.controller.Bot.timers;

public class DeleteNotificationCommand extends SingleNotificationBotCommand {

    static
    {
        formatError="You should format your command as /deletenotification <name>";
        absentError="Notifications with the given name weren't found";

        logger = Logger.getLogger(ChangeNotificationBotCommand.class.getName());
    }

    public DeleteNotificationCommand() {
        super("deletenotification",
                "Use this command to get notification by name as /deletenotification <name>");
    }

    @Override
    protected void processNotification(AbsSender absSender, Notification notification, String[] arguments) {
        Dao<Notification, Long> notificationDao = DatabaseConnection.getNotificationDao();
        try {
            notificationDao.delete(notification);

            if (timers.containsKey(notification.getId())) {
                timers.get(notification.getId()).cancel();
                timers.remove(notification.getId());
            }
        } catch (SQLException e) {
            processError(absSender, notification.getChatId(), internalError);
            logger.log(Level.SEVERE, e.getMessage());
            return;
        }
        sendSuccess(absSender, notification.getChatId());

    }

    @Override
    protected boolean checkLengthCorrectness(String[] arguments) {
        return arguments.length==1;
    }

}
