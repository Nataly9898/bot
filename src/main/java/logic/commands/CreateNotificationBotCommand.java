package logic.commands;

import models.Notification;
import org.telegram.telegrambots.meta.api.objects.Chat;
import org.telegram.telegrambots.meta.api.objects.User;
import org.telegram.telegrambots.meta.bots.AbsSender;

import java.util.Set;
import java.util.logging.Logger;

import static models.Notification.checkNameCorrectness;

public class CreateNotificationBotCommand extends BaseBotCommand {

    private static final String formatError = "You should format your command as /createnotification <name> <dd.mm.yyyy hh:mm (future time)> <text>";
    private static final String nameDuplicationError = "Notification with this name for this chat already exists";

    static {
        logger = Logger.getLogger(CreateNotificationBotCommand.class.getName());
    }

    public CreateNotificationBotCommand(Set<Notification> notifications) {
        super("/createnotification", "Use this command to create notification", notifications);
    }

    @Override
    public void execute(AbsSender absSender,
                        User user,
                        Chat chat,
                        String[] arguments) {

        if (arguments.length < 4) {
            processError(absSender, chat.getId(), formatError);
        } else {

            String name = arguments[0];

            if (!checkNameCorrectness(name)) {
                processError(absSender, chat.getId(), formatError);
                return;
            }

            Notification newNotification = new Notification();
            newNotification.setName(name);
            newNotification.setChatId(chat.getId());


            if (notifications.contains(newNotification)) {
                processError(absSender, chat.getId(), nameDuplicationError);
                return;
            }


            if (!newNotification.setNotificationInfo(arguments)) {
                processError(absSender, chat.getId(), formatError);
                return;
            }

            notifications.add(newNotification);

            sendSuccess(absSender, chat.getId());

        }

    }


}

