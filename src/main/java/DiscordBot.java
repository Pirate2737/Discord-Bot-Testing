import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.OnlineStatus;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.interactions.commands.DefaultMemberPermissions;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.requests.GatewayIntent;
import net.dv8tion.jda.api.requests.restaction.CommandListUpdateAction;
import javax.security.auth.login.LoginException;
import static net.dv8tion.jda.api.interactions.commands.OptionType.*;

public class DiscordBot {
    public static void main(String[] args) throws LoginException {
        JDABuilder api = JDABuilder.createDefault(System.getenv("token"));

        api.addEventListeners(new Messenger(), new SlashCommands());
        api.enableIntents(GatewayIntent.MESSAGE_CONTENT);
        api.setActivity(Activity.competing("amogus"));
        api.setStatus(OnlineStatus.DO_NOT_DISTURB);

        slashCommands(api.build());
    }

    public static void setStatus(String status) {
        JDABuilder api = JDABuilder.createDefault(System.getenv("token"));
        api.setActivity(Activity.competing(status));
    }

    public static void slashCommands(JDA api) {
        CommandListUpdateAction commands = api.updateCommands();

        // say command
        commands.addCommands (
            Commands.slash("Say", "Make the bot say a message")
                    .addOption(STRING, "content", "Message for the bot to repeat", true)
                    .setGuildOnly(true)
                    .setDefaultPermissions(DefaultMemberPermissions.enabledFor(Permission.MESSAGE_SEND))
        );
        // set status
        commands.addCommands(
            Commands.slash("Set Status", "Change the bot's status")
                    .addOption(STRING,"content", "Status to be displayed",true)
        );

        commands.queue();
    }
}
