package world.gregs.hytale.bots.image

import com.nhaarman.mockito_kotlin.mock
import com.nhaarman.mockito_kotlin.times
import com.nhaarman.mockito_kotlin.verify
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.Mockito
import org.mockito.junit.jupiter.MockitoExtension

@ExtendWith(MockitoExtension::class)
class CrawlerTest {

    private val directory = ImageDirectory("Mock", "mock.com")
    private lateinit var crawler: Crawler

    private val callback: (ImageDirectory) -> Unit = mock()

    @BeforeEach
    fun setup() {
        crawler = Crawler(directory, callback)
        crawler.start()
    }

    @Test
    fun `Crawl first update doesn't callback`() {
        //When
        findImages(2)
        assertUpdated(2, 0)

        //Then
        Mockito.verifyZeroInteractions(callback)
    }

    @Test
    fun `Crawl twice runs callback`() {
        //Given
        setupInitialCheck(1)
        assertUpdated(1, 0)

        //When
        findImages(2)
        assertUpdated(3, 1)

        //Then
        verify(callback, times(1)).invoke(directory)
    }

    @Test
    fun `Crawl found 4 new images check`() {
        setupInitialCheck(1)
        assertUpdated(1, 0)

        findImages(5)
        assertUpdated(6, 1)
    }

    @Test
    fun `Crawl initial image update count check`() {
        findImages(5)

        assertUpdated(5, 0)
    }

    @Test
    fun `Crawl found no new images`() {
        //Given initial setup
        setupInitialCheck(1)
        assertUpdated(1, 0)

        //No new images found
        findImages(0)
        assertUpdated(1, 1)
    }

    private fun assertUpdated(current: Int, last: Int) {
        assertThat(directory.currentUpdate).isEqualTo(current)
        assertThat(directory.lastUpdate).isEqualTo(last)
        assertThat(directory.currentUpdate - directory.lastUpdate).isEqualTo(current - last)
    }

    private fun setupInitialCheck(initialImageCount: Int) {
        findImages(initialImageCount)
    }

    private fun findImages(imageCount: Int) {
        for(i in 0 until imageCount) {
            crawler.logic(true)
        }
        crawler.logic(false)
    }

}