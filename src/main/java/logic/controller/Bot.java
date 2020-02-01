package logic.controller;

import logic.commands.ChangeNotificationBotCommand;
import logic.commands.CreateNotificationBotCommand;
import logic.commands.DeleteNotificationCommand;
import logic.commands.ListNotificationsBotCommand;
import models.Notification;
import org.telegram.telegrambots.bots.DefaultBotOptions;
import org.telegram.telegrambots.extensions.bots.commandbot.TelegramLongPollingCommandBot;
import org.telegram.telegrambots.extensions.bots.commandbot.commands.BotCommand;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;


import static logic.commands.BaseBotCommand.sendMessageSequentially;

public class Bot extends TelegramLongPollingCommandBot {
    public static final String TOKEN=System.getenv("ReminderNatalyBot");

    final static Logger logger = Logger.getLogger(Bot.class.getName());

    public List<BotCommand> getCommands() {
        return commands;
    }

    private List<BotCommand> commands = new ArrayList<>();

    public Bot(DefaultBotOptions botOptions) {
        super(botOptions, "ReminderNatalyBot");

        Set<Notification> notifications= new HashSet<>();

        CreateNotificationBotCommand createNotificationBotCommand = new CreateNotificationBotCommand(notifications);
        ListNotificationsBotCommand listNotificationsBotCommand = new ListNotificationsBotCommand(notifications);
        DeleteNotificationCommand deleteNotificationCommand = new DeleteNotificationCommand(notifications);
        ChangeNotificationBotCommand changeNotificationBotCommand = new ChangeNotificationBotCommand(notifications);

        commands.add(createNotificationBotCommand);
        commands.add(listNotificationsBotCommand);
        commands.add(deleteNotificationCommand);
        commands.add(changeNotificationBotCommand);

        for (BotCommand command : commands) {
            this.register(command);
        }

        registerDefaultAction((absSender, message) -> {
            SendMessage commandUnknownMessage = new SendMessage();
            commandUnknownMessage.setChatId(message.getChatId());
            commandUnknownMessage.setText("The command '" + message.getText() +
                    "' is not known by this bot.");

            sendMessageSequentially(absSender, commandUnknownMessage);

        });
    }


    @Override
    public void processNonCommandUpdate(Update update) {
        logger.log(Level.INFO, update.toString());
    }

    @Override
    public String getBotToken() {
        return TOKEN;
    }

}

