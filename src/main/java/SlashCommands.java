import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.components.buttons.Button;

import java.util.Objects;

public class SlashCommands extends ListenerAdapter {
    public void onSlashCommandInteraction (SlashCommandInteractionEvent event) {
        switch (event.getName()) {
            case "say":
                say(event, event.getOption("content").getAsString());
                break;

            case "setactivity":
                String activityType = event.getOption("activity").getAsString();
                String content = event.getOption("content").getAsString();

                System.out.println(event.getUser().getName() + " used the setstatus command: " + activityType + " " + content);
                setActivityHandler(event, activityType, content);

                break;

            case "joinvc":
                Music.joinVoice(event, event.getOption("vc").getAsString());
                event.reply("👍").queue();
                break;

            case "leavevc":
                if (!Music.leaveVoice(event)) {
                    event.reply("I am not in any voice channel in this sever").queue();
                }
                else {
                    event.reply("👍").queue();
                }

                break;

            case "source":
                event.reply("My source code can be viewed here: https://github.com/Pirate2737/Discord-Bot-Testing/tree/main/src/main/java").setEphemeral(true).queue();
                break;

            default:
                event.reply("I can't handle that command right now :(").setEphemeral(true).queue();
                System.out.println("ERROR!! Event name: " + event.getName());
        }
    }

    public void say (SlashCommandInteractionEvent event, String content) {
        if (content.length() > 100) {
            event.reply("stop yappin man")
                    .addActionRow(
                            Button.danger("bypassYapLimit","Bypass Yap Limit 👎")
                    )
                    .queue();
            return;
        }

        event.reply(content).queue();
    }

    public void setActivityHandler (SlashCommandInteractionEvent event, String activityType, String content) {
        if (!event.getUser().getId().equals(System.getenv("ownerID"))) {
            event.reply("fine").queue();
        }
        else {
            event.reply("👍").setEphemeral(true).queue();
        }

        DiscordBot.writeActivityToJSON(activityType, content);
        DiscordBot.setActivity(activityType, content);
    }

    // Button Handler
    public void onButtonInteraction(ButtonInteractionEvent event) {
        User userInteractor = Objects.requireNonNull(event.getMessage().getInteraction()).getUser();

        switch (event.getComponentId()) {
            case "bypassYapLimit":
                if (event.getUser().equals(userInteractor)) {
                    event.getInteraction().editMessage("yikers, not available rn u rulebreaker").queue(); // send a message in the channel
                    event.editButton(event.getButton().withDisabled(true)).queue();
                } else {
                    event.reply("that is not your message to do that on es-em-aych u weirdo").setEphemeral(true).queue();
                }
                break;

            default:
                event.reply("I can't handle that interaction right now :(").setEphemeral(true).queue();
        }
    }

}