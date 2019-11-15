
package mainbot;


import org.telegram.abilitybots.api.bot.AbilityBot;
import org.telegram.abilitybots.api.objects.Ability;


import static org.telegram.abilitybots.api.objects.Locality.USER;
import static org.telegram.abilitybots.api.objects.Privacy.PUBLIC;


public class HelloBot extends AbilityBot{
    private static final String TOKEN = System.getenv("ReminderNatalyBot");
    private static final String BOT_USERNAME="ReminderNatalyBot";



    public HelloBot(){
        super(TOKEN,BOT_USERNAME);
    }

    @Override
  public int creatorId(){
        return 98;
    }
    public Ability saysHelloWorld(){
        return Ability.builder()
                .name("hello")
                .info("Says hello world!")
                .privacy(PUBLIC)
                .locality(USER)
                .input(1)
                .action(ctx->silent.send("Hi, I'm a bot that will help you not to forget about important events",ctx.chatId()))
                .post(ctx -> silent.send("Bay", ctx.chatId()))
                .build();
    }

}
