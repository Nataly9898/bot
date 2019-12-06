
package mainbot;

import org.telegram.telegrambots.ApiContextInitializer;
import org.telegram.telegrambots.bots.DefaultBotOptions;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.ApiContext;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.ArrayList;
import java.util.List;

public class Bot extends TelegramLongPollingBot{
    public static final String TOKEN=System.getenv("ReminderNatalyBot");

    public Bot(DefaultBotOptions botOptions) {
        super(botOptions);
    }

  //  public int creatorId() {
   //     return 98;
  //  }

    public static void main(String[] args) {
        ApiContextInitializer.init();
        TelegramBotsApi telegramBotsApi = new TelegramBotsApi();
        try {
            DefaultBotOptions botOptions = ApiContext.getInstance(DefaultBotOptions.class);
            botOptions.setProxyHost("129.146.181.251");
            botOptions.setProxyPort(3128);
            botOptions.setProxyType(DefaultBotOptions.ProxyType.HTTP);
            telegramBotsApi.registerBot(new Bot(botOptions));
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }





    @Override
    public void onUpdateReceived(Update update) {
        try {
            //проверяем есть ли сообщение и текстовое ли оно
            if (update.hasMessage() && update.getMessage().hasText()) {
                //Извлекаем объект входящего сообщения
                Message inMessage = update.getMessage();
                //Создаем исходящее сообщение
                SendMessage outMessage = new SendMessage();
                //Указываем в какой чат будем отправлять сообщение
                //(в тот же чат, откуда пришло входящее сообщение)
                outMessage.setChatId(inMessage.getChatId());
                //Указываем текст сообщения
                outMessage.setText(inMessage.getText());
                //Отправляем сообщение
                execute(outMessage);
            }
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }
    //private void sendMessage(Message message, String args) {
       // SendMessage sendMessage = new SendMessage();
      //  sendMessage.enableMarkdown(true);
      //  sendMessage.setChatId(message.getChatId().toString());
      //  sendMessage.setReplyToMessageId(message.getMessageId());
      //  sendMessage.setText("Hello");
      //  try {
      //      setButtons(sendMessage);
       //     execute(sendMessage);
      //  } catch (TelegramApiException e) {
       //     e.printStackTrace();
       // }
   // }

   // public void setButtons(SendMessage sendMessage) {
      //  ReplyKeyboardMarkup replyKeyboardMarkup = new ReplyKeyboardMarkup();
     //   sendMessage.setReplyMarkup(replyKeyboardMarkup);
     //   replyKeyboardMarkup.setSelective(true);
     //   replyKeyboardMarkup.setResizeKeyboard(true);
      //  replyKeyboardMarkup.setOneTimeKeyboard(false);
      //  List<KeyboardRow> keyboardRowList = new ArrayList<>();
      //  KeyboardRow keyboardFirstRow = new KeyboardRow();
      //  keyboardFirstRow.add(new KeyboardButton("/help"));
       // keyboardFirstRow.add(new KeyboardButton("/setting"));
      //  keyboardRowList.add(keyboardFirstRow);
      //  replyKeyboardMarkup.setKeyboard(keyboardRowList);
   // }

    @Override
    public String getBotUsername() {
        return "ReminderNatalyBot";
    }

    @Override
    public String getBotToken() {
        return TOKEN;
    }

}

