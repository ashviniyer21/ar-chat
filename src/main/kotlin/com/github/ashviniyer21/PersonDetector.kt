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

    //Run the model and store the resulting data in an output tensor
    val outputTensor = Session(graph)
        .runner()
        .feed(INPUT_TENSOR_NAME, inputTensor)
        .fetch(OUTPUT_TENSOR_NAME)
        .run()

    val outputWidth = outputTensor.first().shape().asArray()[2].toInt()
    val outputHeight = outputTensor.first().shape().asArray()[1].toInt()

    val longArr = outputTensor.first().rawData().asLongs()

    //Create output 2d array of bytes (0 if not detected, 1 if detected)
    val outputByteArray = Array(outputWidth){Array(outputHeight){0.toByte()} }
    for(y in outputByteArray[0].indices){
        for(x in outputByteArray.indices){
            val i = x.toLong() + y*outputWidth.toLong()
            val outputLong = longArr.getLong(i).toInt()
            if(outputLong == PERSON_ID){
               outputByteArray[x][y] = 1
            }
        }
    }

    return outputByteArray
}

fun resizeImageToModel(img:BufferedImage):BufferedImage{
    //Resize image to fit the model's input size
    val resizeRatio = 1.0 * INPUT_SIZE / Integer.max(img.width, img.height)
    val targetWidth = (resizeRatio * img.width).toInt()
    val targetHeight = (resizeRatio * img.height).toInt()
    return Scalr.resize(img, Scalr.Method.SPEED, Scalr.Mode.FIT_TO_WIDTH, targetWidth, targetHeight, Scalr.OP_ANTIALIAS)
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

fun main(){
    init()
    val imgFile = File("src/main/resources/test-images/person1.jpg")
    val img = resizeImageToModel(ImageIO.read(imgFile))
    val byteArray = detectPerson(img)
    val overlayImg = generateOverlayImage(img, byteArray)
    val outputfile = File("src/main/resources/output/result.jpg")
    ImageIO.write(overlayImg, "jpg", outputfile)
}