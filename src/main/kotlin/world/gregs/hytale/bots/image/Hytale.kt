package world.gregs.hytale.bots.image

import net.dv8tion.jda.core.JDABuilder
import net.dv8tion.jda.core.entities.TextChannel
import org.slf4j.LoggerFactory
import java.util.concurrent.Executors
import java.util.concurrent.ScheduledFuture
import java.util.concurrent.TimeUnit

class Hytale(private val channelName: String, token: String) : Bot {
    private val discord = JDABuilder(token)
        .addEventListener(Startup(this))
        .addEventListener(Messenger(this))
        .build()

    private val directories = listOf(
        ImageDirectory("screenshots", "https://hytale.com/static/images/media/screenshots/"),
        ImageDirectory("wallpapers", "https://hytale.com/static/images/media/wallpapers/"),
        ImageDirectory("concept-art", "https://hytale.com/static/images/media/conceptArt/")
    )

    private val crawlers = ArrayList<Crawler>()
    private val futures = ArrayList<ScheduledFuture<*>>()
    override var channel: TextChannel? = null

    private val callback: (ImageDirectory) -> Unit = { directory ->
        val channel = channel
        val change = directory.currentUpdate - directory.lastUpdate
        channel?.sendMessage("$change new ${"image".plural(change)} have been added to ${directory.name}!")
    }

    override fun start() {
        setChannel(channelName)
        directories.forEach {
            addCrawler(it)
        }
    }

    private fun addCrawler(directory: ImageDirectory) {
        val crawler = Crawler(directory, callback)
        val future = executor.scheduleWithFixedDelay(crawler, 0, 10, TimeUnit.MINUTES)
        crawlers.add(crawler)
        futures.add(future)
    }

    override fun setChannel(name: String): Boolean {
        discord.textChannels.forEach { channel ->
            if (channel.name.equals(name, true)) {
                this.channel = channel
                log.debug("Default channel set $channel")
                return true
            }
        }
        return false
    }

    override fun addDirectory(name: String, url: String) {
        val directory = ImageDirectory(name, url)
        addCrawler(directory)
    }

    override fun clearDirectories() {
        //Clear the current crawlers
        crawlers.clear()
        //And their timers
        futures.forEach {
            it.cancel(true)
        }
    }

    override fun getChannels(): List<TextChannel> {
        return discord.textChannels
    }

    override fun getDirectories(): List<ImageDirectory> {
        return directories
    }

    override fun restart() {
        clearDirectories()
        //Before starting again
        start()
    }

    companion object {
        private val executor = Executors.newSingleThreadScheduledExecutor()!!
        private val log = LoggerFactory.getLogger(Hytale::class.java)

        @JvmStatic
        fun main(args: Array<String>) {
            Hytale(args[0], args[1])
        }
    }
}