package logic.commands;

import models.Notification;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Chat;
import org.telegram.telegrambots.meta.api.objects.User;
import org.telegram.telegrambots.meta.bots.AbsSender;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.logging.Logger;

public class ListNotificationsBotCommand extends BaseBotCommand {

    private static final String formatError = "You should format your command as /listnotifications -a to get full" +
            "list of notifications or " +
            "/listnotifications -e for expired & unchecked up to this moment";
    private static final String absentError = "You haven't any notifications belonging to the requested type now";

    static {
        logger = Logger.getLogger(ListNotificationsBotCommand.class.getName());
    }

    public ListNotificationsBotCommand() {
        super("listnotifications",
                "Use this command to list notifications as /listnotifications -a or /listnotifications -e");
    }


    @Override
    public void execute(AbsSender absSender,
                        User user,
                        Chat chat,
                        String[] arguments) {

        if (arguments.length != 1|| !(arguments[0].equals("-a")||arguments[0].equals("-e"))) {
            processError(absSender, chat.getId(), formatError);
        } else {

            Iterator<Notification> notificationIterator = notifications.iterator();

            Set<Notification> notificationSet = new HashSet<>();

            Boolean onlyExpiredMode = arguments[0].equals("-e");

            while (notificationIterator.hasNext()) {
                Notification notification = notificationIterator.next();
                if (notification.getChatId().equals(chat.getId()))
                    if (!onlyExpiredMode)
                        notificationSet.add(notification);
                    else{
                        if ((notification.getStatus().equals("active"))&&(notification.getMillisecondsFromEpoch()<=
                                System.currentTimeMillis())) {
                            notification.setStatus("expired");
                            notificationSet.add(notification);
                        }
                    }
            }

            if (notificationSet.size() < 1) {
                processError(absSender, chat.getId(), absentError);
                return;
            }

            SendMessage answer = new SendMessage();
            answer.setChatId(chat.getId().toString());
            StringBuilder message = new StringBuilder();

            for (Notification notification : notificationSet) {
                message.append(getSendMessageFromNotification(notification).getText());
                message.append(appendDeliveryStatusInfo(notification));
                message.append("---------------------------------------")
                        .append(System.lineSeparator());
            }

            answer.setText(message.toString());

            sendMessageSequentially(absSender, answer);

        }
    }


}
