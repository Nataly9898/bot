package logic.controller;

import org.telegram.telegrambots.ApiContextInitializer;
import org.telegram.telegrambots.bots.DefaultBotOptions;
import org.telegram.telegrambots.extensions.bots.commandbot.commands.BotCommand;
import org.telegram.telegrambots.meta.ApiContext;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.meta.generics.BotSession;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Main {

    final static Logger logger = Logger.getLogger(Main.class.getName());
    private static Bot bot;

    public static void main(String[] args) throws IOException {

        ApiContextInitializer.init();
        TelegramBotsApi telegramBotsApi = new TelegramBotsApi();

        BotSession botSession = null;
        try {
            DefaultBotOptions botOptions = ApiContext.getInstance(DefaultBotOptions.class);
            botOptions.setProxyHost("127.0.0.1");
            botOptions.setProxyPort(1080);
            botOptions.setProxyType(DefaultBotOptions.ProxyType.SOCKS5);

            bot = new Bot(botOptions);
            botSession = telegramBotsApi.registerBot(bot);

        } catch (TelegramApiException e) {
            logger.log(Level.SEVERE, e.getMessage());
            return;
        }

        String str;

        System.out.println("Enter 'stop' to stop bot.");

        do {

            BufferedReader obj = new BufferedReader(new InputStreamReader(System.in));
            str = obj.readLine();

        }
        while (!str.equals("stop"));

        for (BotCommand command : bot.getCommands()) {
            bot.deregister(command);
        }

        botSession.stop();
    }
}
