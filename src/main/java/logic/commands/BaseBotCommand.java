package logic.commands;

import models.Notification;
import org.jetbrains.annotations.NotNull;
import org.telegram.telegrambots.extensions.bots.commandbot.commands.BotCommand;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.bots.AbsSender;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;

import static java.lang.Math.ceil;
import static java.lang.Math.min;

public abstract class BaseBotCommand extends BotCommand {

    public static final String internalError="Internal error";

    public BaseBotCommand(String commandIdentifier, String description) {
        super(commandIdentifier, description);
    }
    static Logger logger = Logger.getLogger(BaseBotCommand.class.getName());

    public static void processError(AbsSender absSender, Long chatId, String message) {
        try {
            SendMessage answer = new SendMessage();
            answer.setChatId(chatId.toString());
            answer.setText(message);
            absSender.execute(answer);
        } catch (TelegramApiException e) {
            logger.log(Level.SEVERE, e.getMessage());
        }
    }


    protected void sendSuccess(AbsSender absSender, Long chatId) {
        SendMessage answer = new SendMessage();
        answer.setChatId(chatId);
        answer.setText("Notification command was successfully processed");
        try {
            absSender.execute(answer);
        } catch (TelegramApiException e) {
            logger.log(Level.SEVERE, e.getMessage());
        }
    }


    @NotNull
    public static SendMessage getSendMessageFromNotification(Notification notification) {
        SendMessage answer = new SendMessage();
        answer.setChatId(notification.getChatId());
        StringBuilder message= new StringBuilder(notification.getName());
        message.append(System.lineSeparator())
                .append(new SimpleDateFormat("dd.MM.yyyy HH:mm").
                        format(new Date(notification.getMillisecondsFromEpoch())))
                .append(System.lineSeparator());
        message.append(notification.getNotification());
        message.append(System.lineSeparator());
        answer.setText(message.toString());
        return answer;
    }

    public static StringBuilder appendDeliveryStatusInfo(Notification notification) {

        StringBuilder appendInfo = new StringBuilder();

        appendInfo.append(notification.getStatus())
                .append(System.lineSeparator());

        if (notification.getStatus().equals("active")){
            appendInfo.append("Will be delivered in ").append( (notification.getMillisecondsFromEpoch()-System.currentTimeMillis())/1000)
                    .append(" seconds");
            appendInfo.append(System.lineSeparator());
        }

        return appendInfo;
    }

    public static int MAX_TELEGRAM_MESSAGE_SIZE=4096;

    public static boolean sendMessageSequentially(AbsSender absSender, SendMessage sendMessage){

        int metaDataSize=sendMessage.toString().length() - sendMessage.getText().length();

        if (sendMessage.toString().length()>MAX_TELEGRAM_MESSAGE_SIZE){
            int actualSize= sendMessage.getText().length();
            int availableTextSpace=MAX_TELEGRAM_MESSAGE_SIZE - metaDataSize;

            for (int i=0; i< ceil((float)actualSize/availableTextSpace); i++){
                SendMessage partialSendMessage = new SendMessage();
                partialSendMessage.setChatId(sendMessage.getChatId());
                partialSendMessage.setText(sendMessage.getText().substring(i*availableTextSpace,
                        min(i * availableTextSpace + availableTextSpace,
                                sendMessage.getText().length())));
                try {
                    absSender.execute(partialSendMessage);
                } catch (TelegramApiException e) {
                    logger.log(Level.SEVERE, e.getMessage());
                    return false;
                }
            }
        }else{
            try {
                absSender.execute(sendMessage);
            } catch (TelegramApiException e) {
                logger.log(Level.SEVERE, e.getMessage());
                return false;
            }
        }


        return true;
    }
}

