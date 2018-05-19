/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package opencv.recognition;

/**
 * Almacenamos los datos de la distancia mas pequeña de la imagen y las
 * imagenes de entrenamiento
 * 
 * @author Paulo Andrade
 * 
 * Sajan Joseph, sajanjoseph@gmail.com
 * http://code.google.com/p/javafaces/
 * Modified by Paulo Andrade, March 2018
 */
public class ImageDistanceInfo
{
    private int index; // Indice del array con la distancia más pequeña
    private double value; // Distancia más pequeña

    /**
     * Constructor
     * 
     * @param val 
     * @param idx 
     */
    public ImageDistanceInfo(double val, int idx)
    {
        value = val;
        index = idx;
    }
    
    /*
    * Metodos getter
    */

    public int getIndex()
    {
        return index;
    }

    public double getValue()
    {
        return value;
    }
}
