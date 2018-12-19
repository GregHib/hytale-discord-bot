package world.gregs.hytale.bots.image

/**
 * ImageDirectory
 * @param name Pretty name for the directory
 * @param url The prefix url to test
 * @param currentUpdate The current highest image number
 * @param lastUpdate The last updates highest image number
 */
data class ImageDirectory(val name: String, val url: String, var currentUpdate: Int = 0, var lastUpdate: Int = -1, var time: Long = -1L)