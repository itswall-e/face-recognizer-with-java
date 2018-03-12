/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package opencv;

import java.awt.List;
import java.util.ArrayList;
import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.MatOfFloat;
import org.opencv.core.MatOfInt;
import org.opencv.core.Point;
import org.opencv.core.Scalar;
import org.opencv.imgproc.Imgproc;

/**
 *
 * @author Paulo Andrade
 */
public class ImageUtils
{
    /**
     * Calcula los histogramas de un frame
     * 
     * @param m Matriz de la cual se calculan los histogramas
     * @param type Seleccion entre color y gris
     */
    public Mat calcHistogram(Mat m, boolean type)
    {
        // Lista para los canales de la matriz
        ArrayList<Mat> images = new ArrayList<>();
        // Obtenemos los canales
        Core.split(m, images);
        
        // Asignamos el numero de bins (parametros a calcular) del histograma
        MatOfInt histogramSize = new MatOfInt(256);
        // Declaramos un solo canal
        MatOfInt channels = new MatOfInt(0);
        // Asignamos el rango de los parametros a calcular
        MatOfFloat histogramRange = new MatOfFloat(0, 256);
        
        // Matrices para los canales de colores
        Mat histogram_r = new Mat();
        Mat histogram_g = new Mat();
        Mat histogram_b = new Mat();
        
        // Calculamos los histogramas (el primero es para el canal azul [escala de grises])
        // params.-
        // image - Imagen de la que se calculara el histograma
        // Channel - Lista de canales de atenuación para calcular el histograma
        // Mask - Puede ser una matriz vacia
        // histogram - Histograma de salida
        // Size - Array de tamaños del histograma en cada dirección
        // Range - Matriz de las matrices de atenuación de los límites del intervalo del histograma en cada dimensión
        // flag
        Imgproc.calcHist(images.subList(0, 1), channels, new Mat(),
                histogram_b, histogramSize, histogramRange, false);
        // Calculamos solo para color
        if(type){
            Imgproc.calcHist(images.subList(1, 2), channels, new Mat(),
                    histogram_g, histogramSize, histogramRange, false);
            Imgproc.calcHist(images.subList(2, 3), channels, new Mat(),
                    histogram_r, histogramSize, histogramRange, false);
        }
        
        // Dimensiones del histograma a dibujar
	int histogram_w = 200; // ancho
	int histogram_h = 180; // alto
	int bin_w = (int) Math.round(histogram_w / histogramSize.get(0, 0)[0]);
		
        // Creamos la matriz para el histograma
	Mat histogramImage = new Mat(histogram_h, histogram_w, CvType.CV_8UC3, new Scalar(0, 0, 0));
        
	// Normalizamos el resultado para [0, histImage.rows()]
	Core.normalize(histogram_b, histogram_b, 0, histogramImage.rows(), Core.NORM_MINMAX, -1, new Mat());
        // Calculamos solo para color
        if(type){
            Core.normalize(histogram_g, histogram_g, 0, histogramImage.rows(), Core.NORM_MINMAX, -1, new Mat());
            Core.normalize(histogram_r, histogram_r, 0, histogramImage.rows(), Core.NORM_MINMAX, -1, new Mat());
        }
		
	// Dibujamos los histogramas
	for (int i = 1; i < histogramSize.get(0, 0)[0]; i++){
            Imgproc.line(histogramImage,
                    new Point(bin_w * (i - 1), histogram_h - Math.round(histogram_b.get(i - 1, 0)[0])),
                    new Point(bin_w * (i), histogram_h - Math.round(histogram_b.get(i, 0)[0])),
                    new Scalar(255, 0, 0), 2, 8, 0);
            // Calculamos solo para color
            if(type){
                Imgproc.line(histogramImage,
                        new Point(bin_w * (i - 1), histogram_h - Math.round(histogram_g.get(i - 1, 0)[0])),
                        new Point(bin_w * (i), histogram_h - Math.round(histogram_g.get(i, 0)[0])),
                        new Scalar(0, 255, 0), 2, 8, 0);
                Imgproc.line(histogramImage,
                        new Point(bin_w * (i - 1),histogram_h - Math.round(histogram_r.get(i - 1, 0)[0])),
                        new Point(bin_w * (i), histogram_h - Math.round(histogram_r.get(i, 0)[0])),
                        new Scalar(0, 0, 255), 2, 8, 0);
            }
	}
        
        return histogramImage;
    }
    
    /**
     * Convertimos la imagen a escala de grises
     * 
     * @param m Matriz a convertir en escala de grises
     */
    public Mat toGrayImage(Mat m){
        // Matriz en escala de grises
        Mat grayFrame = new Mat();
        
        // Convertimos el frame original en escala de grises
        Imgproc.cvtColor(m, grayFrame, Imgproc.COLOR_BGR2GRAY);
        
        return grayFrame;
    }
}
