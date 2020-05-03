package online.hudacek.broadcastsfx

import javafx.scene.image.Image
import mu.KotlinLogging
import online.hudacek.broadcastsfx.model.rest.Station
import org.apache.commons.io.FileUtils
import java.io.FileInputStream
import java.io.IOException
import java.io.InputStream
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths
import java.nio.file.StandardCopyOption

/**
 * Simple image cache
 * Images are saved according to their station id
 * and loaded using fileInputStream
 */
object ImageCache {
    private val cacheBasePath: Path = Paths.get(About.imageCacheLocation)
    private val logger = KotlinLogging.logger {}

    init {
        //prepare cache directory
        if (!Files.isDirectory(cacheBasePath)) {
            logger.debug { "init cache directory" }
            Files.createDirectories(cacheBasePath)
        }
    }

    fun clearCache(): Boolean {
        return try {
            FileUtils.cleanDirectory(cacheBasePath.toFile())
            true
        } catch (e: IOException) {
            logger.debug { "IOException when clearing cache: " + e.localizedMessage }
            e.printStackTrace()
            false
        }
    }

    fun isImageInCache(station: Station): Boolean {
        val imagePath = Paths.get(About.imageCacheLocation + "/" + station.stationuuid)
        return Files.exists(imagePath)
    }

    fun getImageFromCache(station: Station): Image {
        return if (!isImageInCache(station) || station.isInvalidImage()) Image("Industry-Radio-Tower-icon.png")
        else {
            val imagePath = Paths.get(About.imageCacheLocation + "/" + station.stationuuid)
            Image(FileInputStream(imagePath.toFile()))
        }
    }

    fun saveImage(station: Station, inputStream: InputStream) {
        if (isImageInCache(station)) return
        val imagePath = Paths.get(About.imageCacheLocation + "/" + station.stationuuid)
        Files.copy(
                inputStream,
                imagePath,
                StandardCopyOption.REPLACE_EXISTING)
    }
}