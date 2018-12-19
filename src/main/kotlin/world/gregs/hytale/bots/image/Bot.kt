package world.gregs.hytale.bots.image

import net.dv8tion.jda.core.entities.TextChannel

interface Bot {

    var channel: TextChannel?

    /**
     * Starts the bot
     */
    fun start()


    /**
     * Sets the channel to display messages in
     * @param name channel name to attempt to set
     * @return whether set was successful
     */
    fun setChannel(name: String): Boolean

    /**
     * @return list of text channels
     */
    fun getChannels(): List<TextChannel>

    /**
     * Adds a directory to be crawled
     */
    fun addDirectory(name: String, url: String)

    /**
     * Clears all directories
     */
    fun clearDirectories()

    /**
     * @return list of [ImageDirectory]s under surveillance
     */
    fun getDirectories(): List<ImageDirectory>

    /**
     * Restarts the bot
     */
    fun restart()

}