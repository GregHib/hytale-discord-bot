package world.gregs.hytale.bots.image

import net.dv8tion.jda.core.entities.ChannelType
import net.dv8tion.jda.core.entities.MessageChannel
import net.dv8tion.jda.core.events.message.MessageReceivedEvent
import net.dv8tion.jda.core.hooks.ListenerAdapter

class Messenger(private val bot: Bot) : ListenerAdapter() {

    override fun onMessageReceived(event: MessageReceivedEvent) {
        if (event.author.isBot) {
            return
        }

        if (event.isFromType(ChannelType.PRIVATE)) {
            privateMessage(event.message.contentRaw, event.channel)
        } else {
            if(event.textChannel == bot.channel) {
                channelMessage(event.message.contentRaw, event.textChannel)
            }
        }
    }

    private fun channelMessage(message: String, channel: MessageChannel) {
        when(message) {
            "!commands" -> {
                channel.sendMessage("Commands:").queue()
                channel.sendMessage("!images").queue()
                channel.sendMessage("----").queue()
            }
            else -> {
                globalCommands(message, channel)
            }
        }
    }

    private fun privateMessage(message: String, channel: MessageChannel) {
        val parts = message.split(" ")
        when(parts[0]) {
            "!commands" -> {
                channel.sendMessage("Commands:").queue()
                channel.sendMessage("!channel `text-channel-name`     - Sets the text channel for notifications").queue()
                channel.sendMessage("!restart     - Restarts me").queue()
                channel.sendMessage("!images     - Displays latest image info").queue()
                channel.sendMessage("!add `name` `url`     - Adds a new directory to crawl (example url `https://google.com/images/`)").queue()
                channel.sendMessage("!clear     - Clears all crawled directories").queue()
                channel.sendMessage("----").queue()
            }
            "!channel" -> {
                if(parts.size > 1) {
                    val success = bot.setChannel(parts[1])
                    if(success) {
                        channel.sendMessage("Channel set to `${parts[1]}`").queue()
                    } else {
                        channel.sendMessage("Unable to find channel `${parts[1]}`").queue()
                        channel.sendMessage("Channels found:").queue()
                        bot.getChannels().forEach {
                            channel.sendMessage(it.name).queue()
                        }
                        channel.sendMessage("----").queue()
                    }
                } else {
                    channel.sendMessage("Please enter a text-channel name to set.").queue()
                }
            }
            "!restart" -> {
                channel.sendMessage("Restarting...").queue()
                bot.restart()
                channel.sendMessage("Restart completed.").queue()
            }
            else -> {
                globalCommands(message, channel)
            }
        }
    }

    private fun globalCommands(message: String, channel: MessageChannel) {
        when(message) {
            "!images" -> {
                bot.getDirectories().forEach {
                    if(it.time == -1L) {
                        if(it.lastUpdate == -1) {
                            channel.sendMessage("Hold on I haven't finished my search yet...").queue()
                            return
                        } else {
                            channel.sendMessage("${it.name} hasn't been updated from ${it.currentUpdate} ${"image".plural(it.currentUpdate)} since I woke up.").queue()
                        }
                    } else {
                        val seconds = (System.currentTimeMillis() - it.time) / 1000//In seconds
                        val minutes = (seconds % 60).toInt()
                        val hours = (seconds / 60 % 24).toInt()
                        val days = (seconds / 24 / 60).toInt()
                        val time = "${if(days > 0) "$days ${"day".plural(days)}, " else ""}${if(hours > 0) "$hours ${"hour".plural(hours)}, " else ""}$minutes ${"minute".plural(minutes)}"
                        channel.sendMessage("${it.name} was updated $time ago to ${it.currentUpdate} ${"image".plural(it.currentUpdate)}.").queue()
                    }
                }
            }
        }
    }

}

fun String.plural(count: Int, plural: String = this + 's'): String {
    return if(count == 1) this else plural
}