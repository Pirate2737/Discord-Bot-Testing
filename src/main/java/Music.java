import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.entities.channel.concrete.VoiceChannel;
import net.dv8tion.jda.api.managers.AudioManager;

public class Music extends ListenerAdapter {
    public static void joinVoice (SlashCommandInteractionEvent event, String channelID) {
        VoiceChannel channel = event.getGuild().getVoiceChannelById(channelID);
        AudioManager manager = event.getGuild().getAudioManager();

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