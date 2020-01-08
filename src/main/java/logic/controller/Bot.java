package logic.controller;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.support.ConnectionSource;
import logic.commands.*;
import models.Notification;
import org.telegram.telegrambots.ApiContextInitializer;
import org.telegram.telegrambots.bots.DefaultBotOptions;
import org.telegram.telegrambots.extensions.bots.commandbot.TelegramLongPollingCommandBot;
import org.telegram.telegrambots.extensions.bots.commandbot.commands.BotCommand;
import org.telegram.telegrambots.meta.ApiContext;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.meta.generics.BotSession;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.sql.SQLException;
import java.sql.Time;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

import static logic.commands.BaseBotCommand.processError;
import static logic.commands.BaseBotCommand.sendMessageSequentially;
import static logic.commands.GetNotificationBotCommand.getSendMessageFromNotification;
import static logic.commands.SingleNotificationBotCommand.internalError;

public class Bot extends TelegramLongPollingCommandBot {

    private static final String TOKEN = "912187838:AAE4zu7BAEKA1vrtUyb8dwEoKLA16hdI830";//System.getenv("ReminderNatalyBot");
    final static Logger logger = Logger.getLogger(Bot.class.getName());
    private List<BotCommand> commands=new ArrayList<>();

    public Bot(DefaultBotOptions botOptions) throws SQLException {
        super(botOptions, "ReminderNatalyBot");

        CreateNotificationBotCommand createNotificationBotCommand=new CreateNotificationBotCommand();
        GetNotificationBotCommand getNotificationBotCommand=new GetNotificationBotCommand();
        ListNotificationsBotCommand listNotificationsBotCommand=new ListNotificationsBotCommand();
        DeleteNotificationCommand deleteNotificationCommand=new DeleteNotificationCommand();
        ChangeNotificationBotCommand changeNotificationBotCommand=new ChangeNotificationBotCommand();

        commands.add(createNotificationBotCommand);
        commands.add(getNotificationBotCommand);
        commands.add(listNotificationsBotCommand);
        commands.add(deleteNotificationCommand);
        commands.add(changeNotificationBotCommand);

        for (BotCommand command: commands){
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

    private void processPlannedNotifications() throws SQLException {

        Dao<Notification, Long> notificationDao = DatabaseConnection.getNotificationDao();

        List<Notification> notifications =
                notificationDao.queryBuilder().where().eq("status", "active")
                        .query();

        for (Notification notification : notifications) {
            if (!(notification.getMillisecondsFromEpoch() > new Date().getTime()))
                deliverNotificationOnTime(notification, System.currentTimeMillis());
        }
    }

    public static Map<Long, Timer> timers = new HashMap<Long, Timer>();

    public static void deliverNotificationOnTime(Notification notification, Long milliseconds) {
        Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                SendMessage sendMessage = getSendMessageFromNotification(notification);
                try {
                    boolean res = sendMessageSequentially(bot, sendMessage);

                    if (!res)
                        return;

                    notification.setStatus("delivered");
                    Dao<Notification, Long> notificationDao = DatabaseConnection.getNotificationDao();
                    notificationDao.update(notification);
                } catch (SQLException e) {
                    logger.log(Level.SEVERE, e.getMessage());
                    processError(bot,notification.getChatId(),internalError);
                } finally {
                    timers.remove(notification.getId());
                }
            }
        }, new Date(milliseconds));

        timers.put(notification.getId(),timer);
    }

    private static Bot bot;

    public static void main(String[] args) {

        ApiContextInitializer.init();
        TelegramBotsApi telegramBotsApi = new TelegramBotsApi();

        BotSession botSession = null;
        try {
            DefaultBotOptions botOptions = ApiContext.getInstance(DefaultBotOptions.class);

            botOptions.setProxyHost("129.146.181.251");
            botOptions.setProxyPort(3128);
            botOptions.setProxyType(DefaultBotOptions.ProxyType.HTTP);


            bot = new Bot(botOptions);
            botSession=telegramBotsApi.registerBot(bot);
            bot.processPlannedNotifications();

        } catch (TelegramApiException | SQLException e) {
            logger.log(Level.SEVERE, e.getMessage());
            return;
        }

        BufferedReader obj = new BufferedReader(new InputStreamReader(System.in));
        String str;

        System.out.println("Enter 'stop' to stop bot.");

        do {

            try {
                str = obj.readLine();
            } catch (IOException e) {
                logger.log(Level.SEVERE, e.getMessage());
                return;
            }
        }
        while(!str.equals("stop"));


        for (Timer timer: timers.values()){
            timer.cancel();
        }

        try {
            DatabaseConnection.getConnectionSource().close();
        } catch (IOException e) {
            logger.log(Level.SEVERE, e.getMessage());
        }

        for (BotCommand command: bot.commands){
            bot.deregister(command);
        }

        botSession.stop();
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

