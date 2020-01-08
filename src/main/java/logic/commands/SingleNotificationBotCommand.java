package logic.commands;

import com.j256.ormlite.dao.Dao;
import models.Notification;
import org.telegram.telegrambots.meta.api.objects.Chat;
import org.telegram.telegrambots.meta.api.objects.User;
import org.telegram.telegrambots.meta.bots.AbsSender;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static models.Notification.checkNameCorrectness;

public abstract class SingleNotificationBotCommand extends BaseBotCommand{

    protected static String formatError;
    protected static String absentError;

    public SingleNotificationBotCommand(String commandIdentifier, String description) {
        super(commandIdentifier, description);
    }


    protected abstract void processNotification(AbsSender absSender, Notification notification, String[] arguments);

    protected abstract boolean checkLengthCorrectness(String [] arguments);

    @Override
    public void execute(AbsSender absSender,
                        User user,
                        Chat chat,
                        String[] arguments) {

        if (!checkLengthCorrectness(arguments)){
            processError(absSender, chat.getId(), formatError);
        }else {

            if (!checkNameCorrectness(arguments[0])){
                processError(absSender, chat.getId(), formatError);
                return;
            }

            String name = arguments[0];

            Dao<Notification, Long> notificationDao = DatabaseConnection.getNotificationDao();

            Map<String, String> selectArgs = new HashMap<>();
            selectArgs.put("name",name);

            try {
                List<Notification> notifications=notificationDao.queryBuilder().where().eq("name",name).and().
                        eq("Chat_Id", chat.getId()).query();

                if (notifications.size()<1){
                    processError(absSender, chat.getId(), absentError);
                    return;
                }

                Notification notification = notifications.get(0);

                processNotification(absSender, notification, arguments);

            } catch (SQLException e) {
                processError(absSender, chat.getId(), formatError);
            }
        }
    }




}

