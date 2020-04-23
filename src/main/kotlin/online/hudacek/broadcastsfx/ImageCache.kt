package online.hudacek.broadcastsfx

import javafx.scene.image.Image
import mu.KotlinLogging
import online.hudacek.broadcastsfx.model.Station
import org.apache.commons.io.IOUtils
import java.io.File
import java.io.FileInputStream
import java.io.InputStream
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths
import java.nio.file.StandardCopyOption

object ImageCache {
    private val logger = KotlinLogging.logger {}

    fun isImageInCache(station: Station): Boolean {
        val imagePath = Paths.get(About.imageCacheLocation + "/" + station.stationuuid)
        val isValidFile = Files.exists(imagePath)

        if (!isValidFile) {
            logger.debug { "file $imagePath not in cache, should create it" }
        }
        return isValidFile
    }

    fun getImageFromCache(station: Station): Image {
        return if (!isImageInCache(station)) Image("Industry-Radio-Tower-icon.png")
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