package world.gregs.hytale.bots.image

import org.slf4j.LoggerFactory
import java.net.HttpURLConnection
import java.net.URL
import java.util.concurrent.ThreadLocalRandom

class Crawler(var directory: ImageDirectory, val callback: (ImageDirectory) -> Unit): Runnable {
    private var count = 0
    var finished = false

    /**
     * Restarts the crawler
     */
    fun start() {
        finished = false
    }

    override fun run() {
        //Just in case
        var overflow = 0

        //Check one before the last known (don't need to recheck all) and minimum is 0
        count = Math.max(directory.lastUpdate - 1, 0)

        while(!finished) {
            //Begin
            start()
            //Check if image exists at url
            val valid = isValidUrl("${directory.url}${count + 1}.jpg")

            //Process
            logic(valid)

            //Give the web server a chance before pinging again
            Thread.sleep(ThreadLocalRandom.current().nextInt(150, 300).toLong())

            //Don't want any inf loops
            if(overflow++ > 1000) {
                break
            }
        }
    }

    /**
     * If url is valid try the next image
     * @param valid Whether the url pinged valid
     */
    fun logic(valid: Boolean) {
        if(valid) {
            //Valid so increment counter and search for that image
            count++
        } else {
            //Not valid so previous image was the last one

            //Only send message update if not first run
            val update = directory.lastUpdate != -1
            //Set the last update
            directory.lastUpdate = directory.currentUpdate

            //Only update if count is higher than last time
            if(count > directory.currentUpdate) {
                complete(update)
            }

            reset()
        }
    }

    private fun complete(update: Boolean) {
        //Update the current highest number
        directory.currentUpdate = count

        if(update) {
            callback(directory)
            directory.time = System.currentTimeMillis()
        } else {
            log.info("Initial update complete ${directory.currentUpdate - directory.lastUpdate} ${directory.name} image(s) found.")
        }
    }

    private fun reset() {
        count = 1
        finished = true
    }

    companion object {
        private val log = LoggerFactory.getLogger(Crawler::class.java)

        private fun isValidUrl(url: String): Boolean {
            //Open connection to url
            val connection = URL(url).openConnection() as HttpURLConnection
            //with header info
            connection.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.11 (KHTML, like Gecko) Chrome/23.0.1271.95 Safari/537.11");
            connection.requestMethod = "HEAD"
            //returns a response
            val responseCode = connection.responseCode
            //Log if not something we are expecting (no internet?)
            if(responseCode != 200 && responseCode != 404) {
                log.debug("Unexpected response code $url $responseCode")
            }
            //Return if valid (200) response code, otherwise 404 not found.
            return responseCode == 200
        }
    }
}