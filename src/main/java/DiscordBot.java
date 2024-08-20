import net.dv8tion.jda.api.utils.ChunkingFilter;
import net.dv8tion.jda.api.utils.MemberCachePolicy;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.OnlineStatus;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.entities.channel.ChannelType;
import net.dv8tion.jda.api.interactions.commands.Command;
import net.dv8tion.jda.api.interactions.commands.DefaultMemberPermissions;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import net.dv8tion.jda.api.requests.GatewayIntent;
import net.dv8tion.jda.api.requests.restaction.CommandListUpdateAction;
import net.dv8tion.jda.api.managers.Presence;
import javax.security.auth.login.LoginException;
import static net.dv8tion.jda.api.interactions.commands.OptionType.*;

import org.json.JSONException;
import org.json.JSONObject;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

public class DiscordBot {

    private static JDA jda;

    public static void main(String[] args) throws LoginException {

        JDABuilder api = JDABuilder.createDefault(System.getenv("token"));

        api.addEventListeners(new Messenger(), new SlashCommands(), new Music(), new ContextMenu());
        api.enableIntents(GatewayIntent.MESSAGE_CONTENT, GatewayIntent.GUILD_MEMBERS, GatewayIntent.DIRECT_MESSAGES);
        api.setMemberCachePolicy(MemberCachePolicy.ALL);
        api.setChunkingFilter(ChunkingFilter.ALL);
        api.setStatus(OnlineStatus.DO_NOT_DISTURB);

        jda = api.build();

        // Shutdown hook to close bot
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            jda.shutdownNow();
            System.out.println("Shutdown hook ran!");
        }));

        readActivityFromJSON("status.json");
        slashCommands();
        contextMenuCommands();
    }

    public static JDA getJDA() {
        return jda;
    }

    public static void readActivityFromJSON(String fileName) {
        try {
            String contentFromFile = new String(Files.readAllBytes(Paths.get(fileName)));
            JSONObject activity = new JSONObject(contentFromFile);

            if (activity.has("Activity Type") && activity.has("Status")) {
                String type = activity.getString("Activity Type");
                String status = activity.getString("Status");

                setActivity(type, status);
            }

        } catch (IOException | JSONException e) {
            System.err.println(e);
        }
    }

    public static void writeActivityToJSON(String activity, String status) {
        JSONObject newStatus = new JSONObject();
        newStatus.put("Activity Type", activity);
        newStatus.put("Status", status);

        String fileName = "status.json";
        try (FileWriter file = new FileWriter(fileName)) {
            file.write(newStatus.toString());
            file.flush();

        }
        catch (IOException e) {
            System.out.println(e.fillInStackTrace().toString());
        }
    }

    // set status method
    public static void setActivity(String activity, String status) {
        Presence presence = jda.getPresence();

        switch (activity) {
            case "playing"   -> presence.setActivity(Activity.playing(status));
            case "competing" -> presence.setActivity(Activity.competing(status));
            case "listening" -> presence.setActivity(Activity.listening(status));
            case "streaming" -> presence.setActivity(Activity.streaming(status, "https://twitch.tv/innerslothdevs"));
            case "custom"    -> presence.setActivity(Activity.customStatus(status));
        }
    }

    // all the slash commands !!!!
    public static void slashCommands() {
        CommandListUpdateAction commands = jda.updateCommands(); // can take upwards of an hour
        String botName = jda.getSelfUser().getName();

        // say command
        commands.addCommands (
            Commands.slash("say", "Make " + botName + " say a message")
                    .addOption(STRING, "content", "Message for the bot to repeat", true)
                    .setDefaultPermissions(DefaultMemberPermissions.enabledFor(Permission.MESSAGE_SEND))
        );

        // set status command
        commands.addCommands(
            Commands.slash("setactivity", "Change " + botName + "'s activity status")
                    .addOptions(new OptionData(STRING,"activity", "the type of status",true)
                            .addChoices(new Command.Choice("playing","playing"))
                            .addChoices(new Command.Choice("competing in","competing"))
                            .addChoices(new Command.Choice("listening to", "listening"))
                            .addChoices(new Command.Choice("streaming", "streaming"))
                            .addChoices(new Command.Choice("custom status", "custom")))
                    .addOptions(new OptionData(STRING, "content", "Message to be displayed alongside the activity type", true)
                            .setMaxLength(128))
        );

        // join voice channel
        commands.addCommands(
                Commands.slash("joinvc", "Have " + botName + " join a voice channel")
                        .addOptions(new OptionData(CHANNEL, "vc", "Name of the voice channel", true)
                                .setChannelTypes(ChannelType.VOICE))
                        .setGuildOnly(true)
                        .setDefaultPermissions(DefaultMemberPermissions.enabledFor(Permission.VOICE_CONNECT, Permission.VOICE_SPEAK, Permission.VIEW_CHANNEL))
        );

        // leave voice channel
        commands.addCommands(
                Commands.slash("leavevc","Have " + botName + " leave the voice channel in this server")
                        .setGuildOnly(true)
                        .setDefaultPermissions(DefaultMemberPermissions.enabledFor(Permission.VOICE_CONNECT, Permission.VOICE_SPEAK, Permission.VIEW_CHANNEL))
        );

        // source code
        commands.addCommands(
                Commands.slash("source", "View the source code for " + botName)
        );

        commands.queue();
    }

    public static void contextMenuCommands() {
        jda.updateCommands().addCommands(
                Commands.context(Command.Type.USER, "Get user avatar"),
                Commands.message("Count words")
        ).queue();
    }

    public static void sendMessageToUser(Long userID, String message) {
        jda.retrieveUserById(userID).queue();
        User user = jda.getUserById(userID);

        assert user != null;
        user.openPrivateChannel().queue(privateChannel -> privateChannel.sendMessage(message).queue(
                success -> System.out.println("Message sent successfully."),
                failure -> System.err.println("Failed to send message: " + failure.getMessage())
        ));
    }
}