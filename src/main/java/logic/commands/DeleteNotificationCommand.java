package logic.commands;

import models.Notification;
import org.telegram.telegrambots.meta.bots.AbsSender;

import java.util.logging.Logger;

public class DeleteNotificationCommand extends SingleNotificationBotCommand {

    static {
        formatError = "You should format your command as /deletenotification <name>";
        absentError = "Notifications with the given name weren't found";

        logger = Logger.getLogger(ChangeNotificationBotCommand.class.getName());
    }

    public DeleteNotificationCommand() {
        super("deletenotification",
                "Use this command to get notification by name as /deletenotification <name>");
    }

    @Override
    protected void processNotification(AbsSender absSender, Notification notification, String[] arguments) {

        notifications.remove(notification);

        //TODO: timers deletion

        sendSuccess(absSender, notification.getChatId());

    }

    @Override
    protected boolean checkLengthCorrectness(String[] arguments) {
        return arguments.length == 1;
    }

}
