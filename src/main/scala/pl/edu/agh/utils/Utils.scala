package pl.edu.agh.utils

import java.util.zip.CRC32

object Utils {
  def crc32(str: String) = {
    val bytes = str.getBytes()
    val checksum = new CRC32()
    checksum.update(bytes, 0, bytes.length)
    Math.abs(checksum.getValue.toInt)
  }
}
