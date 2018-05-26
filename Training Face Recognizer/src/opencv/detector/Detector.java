/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package opencv.detector;

import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.MatOfRect;
import org.opencv.core.Point;
import org.opencv.core.Rect;
import org.opencv.core.Scalar;
import org.opencv.imgproc.Imgproc;
import org.opencv.objdetect.Objdetect;

/**
 *
 * @author Paulo Andrade
 */
public class Detector
{
    protected double scaleFactor; // Cuanto se reduce la imagen en cada escala de imagen
    protected int minNeighbors; // Cuantos vecinos debe tener cada rectangulo candidato para concervarlo
    protected int minSize; // Tamaño minimo para la busqueda del objeto
    protected int flags; // Banderas
    protected String path;
    
    /**
     * Constructor
     */
    public Detector()
    {
        // Valores por defecto
        scaleFactor = 1.05;
        minNeighbors = 1;
        minSize = 0;
        flags = 0 | Objdetect.CASCADE_SCALE_IMAGE;
        path = "resources/haarcascades/";
    }
    
    /**
     * Dibujamos rectangulos al detectar la sonrisa
     * 
     * @param roi Matriz original
     * @param objects Matriz con los rectangulos detectados
     * @param color Color del rectangulo a dibujar
     */
    protected void objectDraw(Mat roi, MatOfRect objects, Scalar color)
    {
        // Convertimos la matriz en un array (vector)
        Rect[] objectsArray = objects.toArray();

        // Recorremos cada uno de los objetos
        for(Rect object : objectsArray){
            // m <- matriz original
            // facesArray[i].tl() <- punto del objeto en x
            // facesArray[i].br() <- punto del objeto en y
            // new Scalar(0, 255, 0) <- color del rectangulo a dibujar
            // 3 <- Espesor de la linea
            Imgproc.rectangle(roi, object.tl(), object.br(), color, 2);
        }
    }
    
    /**
     * Dibujamos rectangulos al detectar la sonrisa
     * 
     * @param roi Matriz original
     * @param objects Matriz con los rectangulos detectados
     * @param color Color del rectangulo a dibujar
     * @param type Opcion para devolver la matriz
     * 
     * @return Devuelve la matriz ya con los objetos dibujados
     */
    protected Mat objectDraw(Mat roi, MatOfRect objects, Scalar color, boolean type)
    {
        // Convertimos la matriz en un array (vector)
        Rect[] objectsArray = objects.toArray();

        // Recorremos cada uno de los objetos
        for(Rect object : objectsArray){
            // m <- matriz original
            // facesArray[i].tl() <- punto del objeto en x
            // facesArray[i].br() <- punto del objeto en y
            // new Scalar(0, 255, 0) <- color del rectangulo a dibujar
            // 3 <- Espesor de la linea
            Imgproc.rectangle(roi, object.tl(), object.br(), color, 2);
        }
        
        if(type) return roi;
        else return new Mat();
    }
    
    /**
     * Calculamos el tamaño minimo para el objeto a rastrear
     * 
     * @param grayFrame Matriz en escala de grises del cual obtendremos el tamaño
     * @param minSize Tamaño minimo para buscar el objeto (0.2F)
     * 
     * @return Devuelve el tamaño minimo del objeto a localizar
     */
    protected int calcSize(Mat grayFrame, float minSize)
    {
        int objectSize = 0;
        
        // Obtenemos el alto de la imagen (frame)
        int height = grayFrame.rows();

        // verificamos que el minimo a detectar sea mayor a 0
        if (Math.round(height * minSize) > 0){
            // Asignamos el nuevo tamaño a detectar
            objectSize = Math.round(height * minSize);
        }
        
        return objectSize;
    }
    
    /**
     * Imprime un texto en pantalla
     * 
     * @param m Matriz donde imprimiremos el texto
     * @param txt Texto a imprimir
     * @param p Punto X,Y para la colocacion del texto
     */
    protected void printText(Mat m, String txt, Point p)
    {
        Imgproc.putText(m, txt, new Point(p.x, p.y),
            Core.FONT_HERSHEY_COMPLEX, 1.0 ,new  Scalar(0, 255, 0));
    }
}
