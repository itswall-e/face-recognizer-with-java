/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package opencv.trainer;

import java.io.Serializable;
import java.util.ArrayList;

/**
 *
 * @author Paulo Andrade
 * 
 * Modelo para almacenar los datos de las imagenes de entrenamiento
 * 
 * FileUtils.java
 * Sajan Joseph, sajanjoseph@gmail.com
 * http://code.google.com/p/javafaces/
 * Modified by Paulo Andrade, March 2018
 */
public class FaceBundle implements Serializable
{
    private double[][] imagesMat; // each row contains a training image 
    private ArrayList<String> namesImage;	
    private double[] avgImage; // average training image
    private double[][] eigenFaces; // the eigenvectors for the face images
    private double[] eigenValues;
    private int imageWidth, imageHeight;

    /**
     * Constructor
     * 
     * @param nms Lista de nombres de las imagenes de entrenamiento
     * @param ims Imagenes normalizadas
     * @param avgImg Promedio de las imagenes
     * @param facesMat Matriz de eigen faces
     * @param evals Array Eigen values
     * @param w Ancho de la imagen estandar
     * @param h Alto de la imagen estandar
     */
    public FaceBundle(ArrayList<String> nms, double[][] ims, double[] avgImg, 
                      double[][] facesMat, double[] evals, int w, int h)
    {
        namesImage = nms;
        imagesMat = ims;		
        avgImage = avgImg;
        eigenFaces = facesMat;
        eigenValues = evals;
        imageWidth = w;
        imageHeight = h;
    }

    /*
    Metodos getter
    */

    public double[][] getImages()
    {
        return imagesMat;
    }
    public double[][] getEigenFaces()
    {
        return eigenFaces;
    }
    public int getNumEigenFaces()
    {
        return eigenFaces.length;
    }
    public double[] getAvgImage()
    {
        return avgImage;
    }
    public double[] getEigenValues()
    {
        return eigenValues;
    }
    public ArrayList<String> getNamesImage()
    {
        return namesImage;
    }
    public int getImageWidth()
    {
        return imageWidth;
    }
    public int getImageHeight()
    {
        return imageHeight;
    }

    /**
     * Calcula los pesos para el subconjunto elegido de caras propias.
     * Los pesos pueden considerarse como las coordenadas de imagen giradas
     * entonces las caras propias (vectores propios) se convierten en ejes.
     * 
     * @param numEFs Numero de eigen faces
     * 
     * @return 
     */
    public double[][] calcWeights(int numEFs)
    {
        // Creamos una matriz para almacenar los datos
        Matrix2D imgsMat = new Matrix2D(imagesMat);

        // Creamos una copia de los eigen faces
        Matrix2D facesMat = new Matrix2D(eigenFaces);
        
        //Obtenemos una submatriz con los eigen faces correspondientes
        Matrix2D facesSubMatTr = facesMat.getSubMatrix(numEFs).transpose();

        // Obtenemos los pesos
        Matrix2D weights = imgsMat.multiply(facesSubMatTr);
        
        return weights.toArray();
    }
}
