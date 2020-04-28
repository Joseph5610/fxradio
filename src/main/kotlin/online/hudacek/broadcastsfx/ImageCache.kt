package online.hudacek.broadcastsfx

import javafx.scene.image.Image
import online.hudacek.broadcastsfx.model.rest.Station
import org.apache.commons.io.FileUtils
import java.io.FileInputStream
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

    init {
        //prepare cache directory
        if (!Files.isDirectory(cacheBasePath)) {
            Files.createDirectories(cacheBasePath)
        }
    }

    fun clearCache() = FileUtils.cleanDirectory(cacheBasePath.toFile())

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