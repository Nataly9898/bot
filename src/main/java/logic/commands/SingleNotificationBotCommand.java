package logic.commands;

import models.Notification;
import org.telegram.telegrambots.meta.api.objects.Chat;
import org.telegram.telegrambots.meta.api.objects.User;
import org.telegram.telegrambots.meta.bots.AbsSender;

import java.util.Optional;

import static models.Notification.checkNameCorrectness;

public abstract class SingleNotificationBotCommand extends BaseBotCommand {

    protected static String formatError;
    protected static String absentError;

    public SingleNotificationBotCommand(String commandIdentifier, String description) {
        super(commandIdentifier, description);
    }


    protected abstract void processNotification(AbsSender absSender, Notification notification, String[] arguments);

    protected abstract boolean checkLengthCorrectness(String[] arguments);

    @Override
    public void execute(AbsSender absSender,
                        User user,
                        Chat chat,
                        String[] arguments) {

        if (!checkLengthCorrectness(arguments)) {
            processError(absSender, chat.getId(), formatError);
        } else {

            if (!checkNameCorrectness(arguments[0])) {
                processError(absSender, chat.getId(), formatError);
                return;
            }

            String name = arguments[0];

            Optional<Notification> notification = notifications.stream().filter(it -> it.getName().equals(name)
                    && it.getChatId().equals(chat.getId())).findFirst();


            if (!notification.isPresent()) {
                processError(absSender, chat.getId(), absentError);
                return;
            }

            processNotification(absSender, notification.get(), arguments);

        }
    }


}

