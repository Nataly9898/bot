package mainbot;

import org.telegram.abilitybots.api.bot.AbilityBot;
import org.telegram.abilitybots.api.objects.Ability;
import org.telegram.abilitybots.api.objects.Locality;
import org.telegram.abilitybots.api.objects.Privacy;



public class Bot extends AbilityBot {
    private static final String BOT_USERNAME = "ReminderNatalyBot";
    private static final String BOT_TOKEN = "912187838:AAE4zu7BAEKA1vrtUyb8dwEoKLA16hdI830";
    public Bot() {
        super(BOT_TOKEN, BOT_USERNAME);
    }
    @Override
    public int creatorId() {
        return 9898;
    }
    public Ability sayHelloWorld() {
        return Ability
                .builder()
                .name("hello")
                .info("says hello world!")
                .input(0)
                .locality(Locality.ALL)
                .privacy(Privacy.PUBLIC)
                .action(ctx-> silent.send("Hello World!", ctx.chatId()))
                .post(ctx-> silent.send("Bye world!", ctx.chatId()))
                .build();
}



   @Override
   public String getBotUsername() {
       return BOT_USERNAME;
   }
    @Override
    public String getBotToken() {
        return BOT_TOKEN;
    }

}

