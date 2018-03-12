/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package opencv;

import java.awt.Image;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import org.opencv.core.Mat;

/**
 *
 * @author Paulo Andrade
 */
public final class ImageProcessor
{
    /*
     * Convertimos de matriz a imagen
     *
     * @param matrix Matriz que convertiremos en imagen
     */
    public static Image toBufferedImage(Mat matrix){
        // Por defeco el tipo de imagen es en escala de grises
        int type = BufferedImage.TYPE_BYTE_GRAY;
        // verificamos si la imagen es a color
        if(matrix.channels() > 1){
            // cambiamos el tipo de imagen
            type = BufferedImage.TYPE_3BYTE_BGR;
        }
        // Obtenemos el tamaño de la imagen
        int size = matrix.channels()*matrix.cols()*matrix.rows();
        // Creamos un vector para almacenar los elementos de la matriz
        byte[] buffer = new byte[size];
        // Creamos un respaldo de la matriz original
        matrix.get(0 ,0 ,buffer);
        // creamos una imagen con un búfer accesible de datos de imagen
        BufferedImage image = new BufferedImage(matrix.cols(),matrix.
        rows(), type);
        // Almacenamos los valores dados como array de bytes
        final byte[] targetPixels = ((DataBufferByte) image.getRaster().
        getDataBuffer()).getData();
        // Copiamos los valores de la matriz a la imagen
        System.arraycopy(buffer, 0, targetPixels, 0, buffer.length);
        // retornamos la imagen
        return image;
    }
}
