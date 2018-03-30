/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package opencv;

import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.awt.image.Raster;
import java.awt.image.WritableRaster;
import java.util.ArrayList;

/**
 *
 * @author Paulo Andrade
 * 
 * Utilidades para as imagenes
 * 
 * FileUtils.java
 * Sajan Joseph, sajanjoseph@gmail.com
 * http://code.google.com/p/javafaces/
 * Modified by Paulo Andrade, March 2018
 */
public class ImageUtils
{
    /**
     * Escala y convierte una imagen a escala de grises
     * 
     * @param img Imagen a convertir
     * @param scale Escala para la imagen en escala de grises
     * 
     * @return Devuelve la imagen a escala de grises
     */
    public static BufferedImage toScaledGray(BufferedImage img, double scale)
    {
        // Obtenemos ancho y alto de la imagen
        int imgWidth = img.getWidth();
        int imgHeight = img.getHeight();

        // Obtenemos ancho y alto de la imagen ya escalada
        int nWidth = (int)Math.round(imgWidth * scale);
        int nHeight = (int)Math.round(imgHeight * scale);

        // Convertimos a escala de grises mientras la renderizamos
        BufferedImage grayImg = new BufferedImage(nWidth, nHeight, 
                BufferedImage.TYPE_BYTE_GRAY);
        
        // Creamos una grafico 2D para dibujar en la imagen
        Graphics2D g2 = grayImg.createGraphics();
        
        // Establecemos el valor de una única preferencia para los algoritmos de representación
        g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, 
                RenderingHints.VALUE_INTERPOLATION_BILINEAR);
        
        // Dibujamos y escalamos la imagen
        g2.drawImage(img, 0, 0, nWidth, nHeight,  0, 0, imgWidth, imgHeight, null);  
        
        // Liberamos recursos del sistema
        g2.dispose();

        return grayImg;
    }
    
    /**
     * Verifica que todas las imagenes sean del mismo tamaño
     * 
     * @param namesImg Array con las rutas de las imagenes de entrenamiento
     * @param imgs Array de Imagenes cargadas
     */
    public static void checkImSizes(ArrayList<String> namesImg, BufferedImage[] imgs)
    {
        // Obtenemos el ancho y alto de la imagen
        int imgWidth = imgs[0].getWidth();
        int imgHeight = imgs[0].getHeight();
        
        System.out.println("Image (w,h): (" + imgWidth + ", " + imgHeight + ")");

        //
        for(int i = 1; i < imgs.length; i++){
            if((imgs[i].getHeight() != imgHeight) || (imgs[i].getWidth() != imgWidth) ){
                System.out.println("All images should have be the same size; "  +
                        namesImg.get(i) + " is a different size");
                System.exit(1);
            }
        }
    }
    
    /**
     * Crea una imagen apartir de un array de datos
     * 
     * @param imgData Array de datos
     * @param width Ancho de la imagen a crear
     * 
     * @return Devuelve una imagen
     */
    public static BufferedImage createImFromArr(double[] imgData, int width)
    {
        // Instanciamos la imagen a crear
        BufferedImage img = null;
        
        try{
            // Creamos la imagen en escala de grises
            img = new BufferedImage(width, imgData.length/width, BufferedImage.TYPE_BYTE_GRAY);
            
            // Encapsulamos el buffer data
            Raster rast = img.getData();
            
            // Extendemos la clase Raster para darle capacidades de escritura
            WritableRaster wr = rast.createCompatibleWritableRaster();
            
            // Declaramos los valores minimos y maximos del tipo double
            double maxVal = Double.MIN_VALUE;
            double minVal = Double.MAX_VALUE;

            // Reccorremos el array de datos
            for(int i = 0; i < imgData.length; i++){
                //  Buscamos los valores minimos y maximos
                maxVal = Math.max(maxVal, imgData[i]);
                minVal = Math.min(minVal, imgData[i]);
            }

            // Recorremos el array de datos
            for(int j = 0; j < imgData.length; j++){
                // Corregimos los datos
                imgData[j] = ((imgData[j] - minVal) * 255)/(maxVal - minVal);
            }
            
            // Obtenemos los pixeles del array de datos
            wr.setPixels(0, 0, width, imgData.length/width, imgData);
            
            // Agregamos los pixeles a la imagen
            img.setData(wr);
        } catch (Exception e) {
            System.out.println(e);
        }
        
        return img;
  }
   
    /**
     * Creamos un array a partir de una Imagen
     * 
     * @param img
     * 
     * @return 
     */
    public static double[] createArrFromIm(BufferedImage img)
    {
        // Obtenemos el ancho y alto de la imagen
        int imgWidth = img.getWidth();
        int imgHeight = img.getHeight();

        // Creamos el array con las dimensiones especificas
        double[] imgArr = new double[imgWidth * imgHeight];
        
        // Pasamos los datos de la imagen al array
        img.getData().getPixels(0, 0, imgWidth, imgHeight, imgArr);
        
        return imgArr;
    }
}
