package world.gregs.hytale.bots.image

import net.dv8tion.jda.core.events.Event
import net.dv8tion.jda.core.events.ReadyEvent
import net.dv8tion.jda.core.hooks.EventListener

class Startup(private val bot: Bot) : EventListener {

    /**
     * Listen for startup complete before starting
     */
    override fun onEvent(event: Event) {
        if (event is ReadyEvent) {
            bot.start()
        }
    }

}