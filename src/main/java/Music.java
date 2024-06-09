import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.entities.channel.concrete.VoiceChannel;
import net.dv8tion.jda.api.managers.AudioManager;

public class Music extends ListenerAdapter {
    public static void joinVoice (SlashCommandInteractionEvent event, String channelID) {
        Guild guild = event.getGuild();

        VoiceChannel channel = guild.getVoiceChannelById(channelID);
        AudioManager manager = guild.getAudioManager();

        // MySendHandler should be your AudioSendHandler implementation
        //  manager.setSendingHandler(new MySendHandler()); <-----
        // Here we finally connect to the target voice channel
        // and it will automatically start pulling the audio from the MySendHandler instance
        manager.openAudioConnection(channel);
    }

    public static boolean leaveVoice (SlashCommandInteractionEvent event) {
        AudioManager manager;
        try {
            manager = event.getGuild().getAudioManager();
        }
        catch (NullPointerException e) {
            return false;
        }

        manager.closeAudioConnection();
        return true;
    }
}