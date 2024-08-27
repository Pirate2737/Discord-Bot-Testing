import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.entities.channel.concrete.VoiceChannel;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.events.interaction.component.StringSelectInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.components.buttons.Button;
import net.dv8tion.jda.api.interactions.components.selections.StringSelectMenu;
import net.dv8tion.jda.api.managers.AudioManager;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.temporal.ChronoUnit;
import java.util.Objects;

public class SlashCommands extends ListenerAdapter {
    public void onSlashCommandInteraction (SlashCommandInteractionEvent event) {
        switch (event.getName()) {
            case "say":
                say(event, Objects.requireNonNull(event.getOption("content")).getAsString());
                break;

            case "setactivity":
                String activityType = Objects.requireNonNull(event.getOption("activity")).getAsString();
                String content = Objects.requireNonNull(event.getOption("content")).getAsString();

                System.out.println(event.getUser().getName() + " used the setstatus command: " + activityType + " " + content);
                setActivityHandler(event, activityType, content);

                break;

            case "joinvc":
                String channelID =  Objects.requireNonNull(event.getOption("vc")).getAsString();
                VoiceChannel channel = Objects.requireNonNull(event.getGuild()).getVoiceChannelById(channelID);
                AudioManager manager = event.getGuild().getAudioManager();

                manager.openAudioConnection(channel);
                event.reply("👍").queue();
                break;

            case "leavevc":
                Objects.requireNonNull(event.getGuild()).getAudioManager().closeAudioConnection();
                event.reply("👍").queue();

                break;

            case "timestampconvert":
                int month = Objects.requireNonNull(event.getOption("month")).getAsInt();
                int day = Objects.requireNonNull(event.getOption("day")).getAsInt();
                int year = Objects.requireNonNull(event.getOption("year")).getAsInt();
                int hour = 0; int minute = 0; int seconds = 0;
                double UTCOffset = -5;

                if (event.getOption("hour") != null) {
                    hour = Objects.requireNonNull(event.getOption("hour")).getAsInt();
                }
                if (event.getOption("minute") != null) {
                    minute = Objects.requireNonNull(event.getOption("minute")).getAsInt();
                }
                if (event.getOption("seconds") != null) {
                    seconds = Objects.requireNonNull(event.getOption("seconds")).getAsInt();
                }
                if (event.getOption("utc") != null) {
                    UTCOffset = Objects.requireNonNull(event.getOption("utc")).getAsDouble();
                }

                // System.out.println("Year: " + year + "\nMonth: " + month + "\nDay: " + day + "\nHour: " + hour + "\nMinute: " + minute + "\nSeconds: " + seconds + "\nUTC Offset: " + UTCOffset);
                LocalDateTime dateTime = LocalDateTime.of(year, month, day, hour, minute, seconds);
                long unixTimeStamp = dateTime.toEpochSecond(ZoneOffset.ofHoursMinutes(((int) UTCOffset), (int) ((UTCOffset%1)*60))); // time zone offset for est // not great

                event.reply("<t:" + unixTimeStamp + ":d> " + "`<t:" + unixTimeStamp + ":d>`").setEphemeral(true).addActionRow(
                        StringSelectMenu.create("timestamp-options")
                                .addOption("Month/Day/Year", "d")
                                .addOption("Month Day, Year Time", "f")
                                .addOption("Time", "t")
                                .addOption("Month Day, Year", "D")
                                .addOption("Weekday, Month Day, Year Time", "F")
                                .addOption("Time since", "R")
                                .addOption("Hours:Minutes:Seconds", "T")
                                .build())
                        .queue();
                break;

            case "ping":
                event.deferReply(true).queue();
                event.getHook().sendMessage("Pong!").queue(message -> {
                    // Measure the time between the command and the reply
                    long ping = event.getInteraction().getTimeCreated().until(message.getTimeCreated(), ChronoUnit.MILLIS);
                    message.editMessage("Pong! `" + ping + "ms`").queue();
                });
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

        if (event.getComponentId().equals("bypassYapLimit")) {
            if (event.getUser().equals(userInteractor)) {
                event.getInteraction().editMessage("yikers, not available rn u rulebreaker").queue(); // send a message in the channel
                event.editButton(event.getButton().withDisabled(true)).queue();
            } else {
                event.reply("that is not your message to do that on es-em-aych u weirdo").setEphemeral(true).queue();
            }
        } else {
            event.reply("I can't handle that interaction right now :(").setEphemeral(true).queue();
        }
    }

    // Dropdown Menu Handler
    public void onStringSelectInteraction(StringSelectInteractionEvent event) {
        if (event.getComponentId().equals("timestamp-options")) {
            String old = event.getMessage().getContentRaw();
            StringBuilder out = new StringBuilder();

            for (int i = 0; i < old.length(); i++) {
                if (Character.isAlphabetic(old.charAt(i)) && old.charAt(i-1) == ':') {
                    out.append(event.getValues().getFirst());
                }
                else {
                    out.append(old.charAt(i));
                }
            }

            event.editMessage(out.toString()).queue();
        }
    }
}