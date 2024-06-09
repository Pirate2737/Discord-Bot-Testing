import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.OnlineStatus;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.entities.channel.ChannelType;
import net.dv8tion.jda.api.interactions.commands.Command;
import net.dv8tion.jda.api.interactions.commands.DefaultMemberPermissions;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import net.dv8tion.jda.api.requests.GatewayIntent;
import net.dv8tion.jda.api.requests.restaction.CommandListUpdateAction;
import javax.security.auth.login.LoginException;
import static net.dv8tion.jda.api.interactions.commands.OptionType.*;

public class DiscordBot {
    public static void main(String[] args) throws LoginException {
        JDABuilder api = JDABuilder.createDefault(System.getenv("token"));

        api.addEventListeners(new Messenger(), new SlashCommands(), new Music());
        api.enableIntents(GatewayIntent.MESSAGE_CONTENT);
        api.setActivity(Activity.competing("amogus"));
        api.setStatus(OnlineStatus.DO_NOT_DISTURB);

        slashCommands(api.build());
    }

    // set status method
    public static void setStatus(String activity, String status) {
        JDABuilder api = JDABuilder.createDefault(System.getenv("token"));
        api.setStatus(OnlineStatus.DO_NOT_DISTURB);

        switch (activity) {
            case "playing":
                api.setActivity(Activity.playing(status));
                break;
            case "competing":
                api.setActivity(Activity.competing(status));
                break;
            case "listening":
                api.setActivity(Activity.listening(status));
                break;
            case "custom":
                api.setActivity(Activity.customStatus(status));
                break;
        }
        api.build();
    }

    // all the slash commands !!!!
    public static void slashCommands(JDA api) {
        CommandListUpdateAction commands = api.updateCommands(); // can take upwards of an hour

        // say command
        commands.addCommands (
            Commands.slash("say", "Make the bot say a message")
                    .addOption(STRING, "content", "Message for the bot to repeat", true)
                    .setDefaultPermissions(DefaultMemberPermissions.enabledFor(Permission.MESSAGE_SEND))
        );

        // set status command
        commands.addCommands(
            Commands.slash("setstatus", "Change the bot's status")
                    .addOptions(new OptionData(STRING,"type", "The Type of Status",true)
                            .setRequired(true)
                            .addChoices(new Command.Choice("playing","playing"))
                            .addChoices(new Command.Choice("competing in","competing"))
                            .addChoices(new Command.Choice("listening to", "listening"))
                            .addChoices(new Command.Choice("custom status", "custom"))
                            .addChoices(new Command.Choice("clear status", "clear")))
                    .addOptions(new OptionData(STRING, "content", "Status Info", true)
                            .setRequired(true))
        );

        // join voice channel
        commands.addCommands(
                Commands.slash("joinvc", "Have Arcaneous join a voice channel")
                        .addOptions(new OptionData(CHANNEL, "vc", "Name of the voice channel", true)
                                .setChannelTypes(ChannelType.VOICE))
                        .setGuildOnly(true)
                        .setDefaultPermissions(DefaultMemberPermissions.enabledFor(Permission.ALL_VOICE_PERMISSIONS))
        );

        commands.queue();
    }
}
