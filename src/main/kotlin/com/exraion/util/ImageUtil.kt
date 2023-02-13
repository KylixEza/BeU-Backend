package com.exraion.util

import io.ktor.http.content.*
import java.awt.image.BufferedImage
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.util.*
import javax.imageio.ImageIO

fun PartData.FileItem.convert(compressQuality: Float = 1.0f) = run {
    val fileBytes = streamProvider().readBytes()
    val compressedImage = fileBytes compressTo compressQuality
    val fileExtension = originalFileName?.takeLastWhile { it != '.' }
    val fileName = UUID.randomUUID().toString() + "." + fileExtension
    Pair(fileName, compressedImage)
}

infix fun ByteArray.compressTo(quality: Float): ByteArray {
    var image = ImageIO.read(ByteArrayInputStream(this))
    val outputStream = ByteArrayOutputStream()
    val writer = ImageIO.getImageWritersByFormatName("jpg").next()
    val params = writer.defaultWriteParam
    params.compressionMode = javax.imageio.ImageWriteParam.MODE_EXPLICIT
    params.compressionQuality = quality
    val imageOutputStream = ImageIO.createImageOutputStream(outputStream)
    writer.output = imageOutputStream

    image = if (image.colorModel.hasAlpha()) {
        image.removeAlphaChannel()
    } else {
        image
    }

    writer.write(null, javax.imageio.IIOImage(image, null, null), params)
    imageOutputStream.close()
    return outputStream.toByteArray()
}
private fun BufferedImage.removeAlphaChannel(): BufferedImage {
    if (!colorModel.hasAlpha()) {
        return createImage(width, height, true)
    }
    val target: BufferedImage = createImage(width, height, false)
    val g = target.createGraphics()
    // g.setColor(new Color(color, false));
    g.fillRect(0, 0, width, height)
    g.drawImage(this, 0, 0, null)
    g.dispose()
    return target
}

private fun createImage(width: Int, height: Int, hasAlpha: Boolean): BufferedImage {
    return BufferedImage(width, height, if (hasAlpha) BufferedImage.TYPE_INT_ARGB else BufferedImage.TYPE_INT_RGB)
}