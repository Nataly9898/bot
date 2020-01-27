package logic.commands;

import models.Notification;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.bots.AbsSender;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.logging.Level;
import java.util.logging.Logger;


public class ChangeNotificationBotCommand extends SingleNotificationBotCommand {

    static {
        formatError = "You should format your command as /changenotification <name> <dd.mm.yyyy hh:mm> <text>";
        absentError = "Notifications with the given name weren't found";

        logger = Logger.getLogger(ChangeNotificationBotCommand.class.getName());
    }

    public ChangeNotificationBotCommand() {
        super("changenotification",
                "Use this command to get notification by name as /changenotification <name> <dd.mm.yyyy hh:mm (future time)>  <text>");
    }

    @Override
    protected void processNotification(AbsSender absSender, Notification notification, String[] arguments) {

        if (!notification.setNotificationInfo(arguments)) {
            processError(absSender, notification.getChatId(), formatError);
            return;
        }

        SendMessage answer = new SendMessage();
        answer.setChatId(notification.getChatId());
        answer.setText("Notification was successfully updated");
        try {
            absSender.execute(answer);
        } catch (TelegramApiException e) {
            logger.log(Level.SEVERE, e.getMessage());
        }
    }

    @Override
    protected boolean checkLengthCorrectness(String[] arguments) {
        return arguments.length >= 4;
    }
}

