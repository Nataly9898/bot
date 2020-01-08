package logic.commands;

import com.j256.ormlite.dao.Dao;
import models.Notification;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Chat;
import org.telegram.telegrambots.meta.api.objects.User;
import org.telegram.telegrambots.meta.bots.AbsSender;

import java.sql.SQLException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ListNotificationsBotCommand extends BaseBotCommand {

    private static final String formatError="You should format your command as /listnotifications";
    private static final String absentError="You haven't any notifications now";

    static
    {
        logger = Logger.getLogger(ListNotificationsBotCommand.class.getName());
    }

    public ListNotificationsBotCommand() {
        super("listnotifications",
                "Use this command to list notifications as /listnotifications" );
    }


    @Override
    public void execute(AbsSender absSender,
                        User user,
                        Chat chat,
                        String[] arguments) {

        if (arguments.length!=0) {
            processError(absSender, chat.getId(), formatError);
        }else {

            Dao<Notification, Long> notificationDao = DatabaseConnection.getNotificationDao();

            try {
                List<Notification> notifications=notificationDao.queryBuilder().where().
                        eq("Chat_Id", chat.getId()).query();

                if (notifications.size()<1){
                    processError(absSender, chat.getId(), absentError);
                    return;
                }

                SendMessage answer = new SendMessage();
                answer.setChatId(chat.getId().toString());
                StringBuilder message = new StringBuilder();

                for (Notification notification: notifications) {
                    message.append(getSendMessageFromNotification(notification).getText());
                    message.append(appendDeliveryStatusInfo(notification));
                    message.append("---------------------------------------")
                            .append(System.lineSeparator());
                }

                answer.setText(message.toString());

                sendMessageSequentially(absSender, answer);

            } catch (SQLException e) {
                processError(absSender, chat.getId(), formatError);
                logger.log(Level.SEVERE, e.getMessage());
            }
        }
    }


}
