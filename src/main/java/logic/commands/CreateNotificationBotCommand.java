package logic.commands;

import com.j256.ormlite.dao.Dao;
import models.Notification;
import org.telegram.telegrambots.meta.api.objects.Chat;
import org.telegram.telegrambots.meta.api.objects.User;
import org.telegram.telegrambots.meta.bots.AbsSender;

import java.sql.SQLException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import static logic.controller.Bot.deliverNotificationOnTime;
import static models.Notification.checkNameCorrectness;

public class CreateNotificationBotCommand extends BaseBotCommand {

    private static final String formatError="You should format your command as /createnotification <name> <dd.mm.yyyy hh:mm (future time)> <text>";
    private static final String nameDuplicationError="Notification with this name for this chat already exists";

    static
    {
        logger = Logger.getLogger(CreateNotificationBotCommand.class.getName());
    }

    public CreateNotificationBotCommand() {
        super("/createnotification", "Use this command to create notification");
    }

    @Override
    public void execute(AbsSender absSender,
                        User user,
                        Chat chat,
                        String[] arguments) {

        if (arguments.length<4) {
            processError(absSender, chat.getId(), formatError);
        }else {

            String name=arguments[0];

            if (!checkNameCorrectness(name)) {
                processError(absSender, chat.getId(), formatError);
                return;
            }

            Dao<Notification, Long> notificationDao = DatabaseConnection.getNotificationDao();

            try {
                List<Notification> notifications=notificationDao.queryBuilder().where().eq("name",name).and().
                        eq("Chat_Id", chat.getId()).query();
                if (notifications.size()==1){
                    processError(absSender, chat.getId(), nameDuplicationError);
                    return;
                }
            } catch (SQLException e) {
                processError(absSender, chat.getId(), internalError);
                logger.log(Level.SEVERE, e.getMessage());
                return;
            }

            Notification newNotification= new Notification();
            newNotification.setName(name);
            newNotification.setChatId(chat.getId());

            if (!newNotification.setNotificationInfo(arguments)){
                processError(absSender,chat.getId(),formatError);
                return;
            }

            try {
                notificationDao.create(newNotification);
                deliverNotificationOnTime(newNotification, newNotification.getMillisecondsFromEpoch());
            } catch (SQLException e) {
                processError(absSender, chat.getId(), internalError);
                logger.log(Level.SEVERE, e.getMessage());
            }

            sendSuccess(absSender, chat.getId());

        }

    }



}

