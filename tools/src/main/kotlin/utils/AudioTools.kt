package utils

import runCommand
import java.io.File
import java.lang.IllegalArgumentException
import java.nio.file.Files
import java.nio.file.StandardCopyOption
import kotlin.io.path.Path

data class Silence(val start: Double, val end: Double, val duration: Double)

fun getSilences(file: File, threshold: Int = -30, duration: String = "0.1"): List<Silence> {
    val cmd = "ffmpeg -i ${file.name} -af silencedetect=noise=${threshold}dB:d=${duration} -f null -"
    val result = file.parentFile.runCommand(cmd) ?: ""
    return result.split("\n").flatMap { it.split("\r") }.filter { it.startsWith("[silencedetect @") }.chunked(2).map { (rawStart, rawEnd) ->
        val start = rawStart.split(":").last().toDoubleOrNull() ?: 0.0
        val endParts = rawEnd.split("|")
        val end = endParts.first().split(":").last().toDoubleOrNull() ?: 0.0
        val silenceDuration = endParts.last().split(":").last().toDoubleOrNull() ?: 0.0

        Silence(start, end, silenceDuration)
    }
}

fun File.trimAudio(outPut: File, start: Double, end: Double, sampleRate: Int? = null, volumeChange: Double? = null) {
    val rate = sampleRate?.let { "-r $sampleRate " } ?: ""
    val volume = volumeChange?.let { "-v $volumeChange " } ?: ""
    val result = parentFile.runCommand("sox $volume$name $rate${outPut.name} trim $start $end")
    if (!outPut.exists()) throw IllegalArgumentException("Trim Audio failed:\n$result")
}

fun File.combineAudio(outPut: File, prefixName: String, suffixName: String) {
    val prefix = if (File("$parent/$prefixName.wav").exists()) "$prefixName.wav " else ""
    val suffix = if (File("$parent/$suffixName.wav").exists()) " $suffixName.wav" else ""

    val result = parentFile.runCommand("sox ${prefix}${name}${suffix} ${outPut.name}")
    if (!outPut.exists()) throw IllegalArgumentException("Combine Audio failed:\n$result")
}