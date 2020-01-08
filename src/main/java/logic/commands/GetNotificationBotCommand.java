package logic.commands;


import models.Notification;
import org.jetbrains.annotations.NotNull;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.bots.AbsSender;

import java.util.logging.Logger;

public class GetNotificationBotCommand extends SingleNotificationBotCommand {

    static
    {
        formatError="You should format your command as /getnotification <name>";
        absentError="Notifications with the given name weren't found";

        logger = Logger.getLogger(GetNotificationBotCommand.class.getName());
    }


    public GetNotificationBotCommand() {
        super("getnotification",
                "Use this command to get notification by name as /getnotification <name>");
    }

    public void processNotification(@NotNull AbsSender absSender, Notification notification, String[] arguments) {
        SendMessage answer = getSendMessageFromNotification(notification);
        sendMessageSequentially(absSender,answer);
    }

    @Override
    protected boolean checkLengthCorrectness(String[] arguments) {
        return arguments.length==1;
    }

}
