package online.hudacek.broadcastsfx

import javafx.scene.image.Image
import mu.KotlinLogging
import online.hudacek.broadcastsfx.model.Station
import java.io.FileInputStream
import java.io.InputStream
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths
import java.nio.file.StandardCopyOption

object ImageCache {
    private val logger = KotlinLogging.logger {}

    private val cacheBasePath: Path = Paths.get(About.imageCacheLocation)

    init {
        //prepare cache directory
        if (!Files.isDirectory(cacheBasePath)) {
            Files.createDirectories(cacheBasePath)
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