package com.github.ashviniyer21

import org.imgscalr.Scalr
import org.tensorflow.Graph
import org.tensorflow.Session
import org.tensorflow.ndarray.StdArrays
import org.tensorflow.proto.framework.GraphDef
import org.tensorflow.types.TUint8
import java.awt.Color
import java.awt.image.BufferedImage
import java.io.File
import java.nio.file.Files
import javax.imageio.ImageIO
import javax.swing.ImageIcon

import javax.swing.JLabel

import java.awt.FlowLayout

import javax.swing.JFrame
import kotlin.concurrent.fixedRateTimer
import java.io.IOException
import javax.imageio.ImageReader

import javax.imageio.stream.ImageInputStream





val MODEL_FILE = File("src/main/resources/model/image-segmentation-model.pb") //Model file
val INPUT_TENSOR_NAME = "ImageTensor:0" //Model input name constant
val OUTPUT_TENSOR_NAME = "SemanticPredictions:0" //Model output name constant
val PERSON_ID = 15 //The identifier for person in the model output
val INPUT_SIZE = 513 //The model input dimension (either width or height, whichever is bigger)
val modelBytes = Files.readAllBytes(MODEL_FILE.toPath()) //Read model
val graph = Graph() //Model graph


fun init(){
    graph.importGraphDef(GraphDef.parseFrom(modelBytes))
}

/**
 * Detects people in the image and returns a byte array with a binary mask (1 where people are, 0 where people arent)
 */
fun detectPerson(inputImg:BufferedImage):Array<Array<Byte>>{

    //Resize image to correct model size
    val resizedImg = resizeImageToModel(inputImg)

    val width = resizedImg.width
    val height = resizedImg.height

    //Populate 4d data array with resized image RGB values to prepare for entering into the model
    val data: Array<Array<Array<Array<Byte>>>> = Array(1) { Array(height) { Array(width) { Array(3) { 0 } } } }
    for (x in 0 until width) {
        for (y in 0 until height) {
            val color = Color(resizedImg.getRGB(x, y))
            data[0][y][x][0] = color.red.toByte()
            data[0][y][x][1] = color.blue.toByte()
            data[0][y][x][2] = color.green.toByte()
        }
    }

    //Create input tensor with the prepared data from the image
    val inputTensor = TUint8.tensorOf(StdArrays.ndCopyOf(data))
    val begin = System.nanoTime()
    //Run the model and store the resulting data in an output tensor
    val outputTensor = Session(graph)
        .runner()
        .feed(INPUT_TENSOR_NAME, inputTensor)
        .fetch(OUTPUT_TENSOR_NAME)
        .run()
    val end = System.nanoTime()
    println("Time: " + (end-begin)/1000000)
    val outputWidth = outputTensor.first().shape().asArray()[2].toInt()
    val outputHeight = outputTensor.first().shape().asArray()[1].toInt()

    val longArr = outputTensor.first().rawData().asLongs()

    //Create output 2d array of bytes (0 if not detected, 1 if detected)
    val outputByteArray = Array(outputWidth){Array(outputHeight){0.toByte()} }
    try {
        for(y in outputByteArray[0].indices){
            for(x in outputByteArray.indices){
                val i = x.toLong() + y*outputWidth.toLong()
                val outputLong = longArr.getLong(i).toInt()
                if(outputLong == PERSON_ID){
                    outputByteArray[x][y] = 1
                }
            }
        }
    }
    catch (e: Exception){

    }


    return outputByteArray
}

fun resizeImageToModel(img:BufferedImage):BufferedImage{

    //Resize image to fit the model's input size
    val resizeRatio = 1.0 * INPUT_SIZE / Integer.max(img.width, img.height)
    val targetWidth = (resizeRatio * img.width).toInt()
    val targetHeight = (resizeRatio * img.height).toInt()
    val resized = Scalr.resize(img, Scalr.Method.SPEED, Scalr.Mode.FIT_TO_WIDTH, targetWidth, targetHeight, Scalr.OP_ANTIALIAS)


    return resized
}

fun generateOverlayImage(original:BufferedImage, mask:Array<Array<Byte>>): BufferedImage {
    val outputImg = BufferedImage(original.width, original.height, original.type)
    for(x in mask.indices){
        for(y in mask[0].indices){
            val originalColor = Color(original.getRGB(x, y))
            if(mask[x][y] == 1.toByte()){
                outputImg.setRGB(x, y, Color(originalColor.red, (0.5*originalColor.green).toInt(), (0.5*originalColor.blue).toInt()).rgb)
            }
            else{
                outputImg.setRGB(x, y, originalColor.rgb)
            }
        }
    }
    return outputImg
}

val frame = JFrame()


fun readGif(gif: File):MutableList<BufferedImage>{
    val images = mutableListOf<BufferedImage>()
    try {
        val reader: ImageReader = ImageIO.getImageReadersByFormatName("gif").next()
        val stream = ImageIO.createImageInputStream(gif)
        reader.input = stream
        val count: Int = reader.getNumImages(true)

        for (index in 0 until count) {
            println(index)
            val frame: BufferedImage = reader.read(index)
            images.add(frame)
        }
    } catch (e: Exception) {
        // An I/O problem has occurred
    }
    return images
}

var counter = 0
var gifImages = mutableListOf<BufferedImage>()

fun runIteration(){
    val img = resizeImageToModel(gifImages[counter])
    val byteArray = detectPerson(img)

    val overlayImg = generateOverlayImage(img, byteArray)
    val outputfile = File("src/main/resources/output/result.jpg")
    frame.contentPane.removeAll()
    frame.contentPane.add(JLabel(ImageIcon(overlayImg)))
    frame.pack()

    counter++
    if(counter > gifImages.size-1){
        counter = 0
    }
}

fun main(){
    gifImages = readGif(File("src/main/resources/test-images/person.gif"))

    frame.contentPane.layout = FlowLayout()
    frame.pack()
    frame.isVisible = true
    frame.defaultCloseOperation = JFrame.EXIT_ON_CLOSE;

    init()

    fixedRateTimer("timer", false, 0L, 1){
        runIteration()
    }
}