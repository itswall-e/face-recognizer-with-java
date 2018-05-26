/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package opencv.recognition;

import java.awt.image.BufferedImage;
import java.util.ArrayList;
import opencv.trainer.FaceBundle;
import opencv.trainer.FileUtils;
import opencv.trainer.ImageUtils;
import opencv.trainer.Matrix2D;

/**
 *
 * @author Paulo Andrade
 */
public class FaceRecognition
{
    private static final float FACES_FRAC = 0.75f; // fraccion por defecto usada en un match
    private FaceBundle bundle = null; // Almacenamos los eigenfaces de entrenamiento
    private double[][] weights = null; // Pesos de las imagenes de entrenamiento
    private int numEFs = 0; // numero de eigenfaces a utilizar para el reconocimiento

    /**
     * Constructor por defecto
     */
    public FaceRecognition()
    {
        // Llamamos al mismo constructor para 0 eigenfaces
        this(0);
    }

    /**
     * Constructor
     * 
     * @param numEigenFaces Numero de eigenfaces para el reconocimiento
     */
    public FaceRecognition(int numEigenFaces)
    {
        // Obtenemos los eigenfaces de entrenamiento
        bundle = FileUtils.readCache();
        
        // Verificamos que esten cargados los eigenfaces
        if(bundle == null){
            // Si no se cargaron mostramos mensaje y salimos
            System.out.println("You must build an Eigenfaces cache before any matching");
            System.exit(1);
        }

        // Obtenemos el total de eigenfaces de entrenamiento
        int numFaces = bundle.getNumEigenFaces();

        // Verificamos que los eigenfaces asignados sean corectos
        numEFs = numEigenFaces;
        if((numEFs < 1) || (numEFs > numFaces-1)){
            // Si no estan asignados, obtenemos los eigenfaces por defecto
            numEFs = Math.round((numFaces-1) * FACES_FRAC);
            System.out.println("Number of matching eigenfaces must be in the range (1-" + 
                    (numFaces-1) + ")" + "; using " + numEFs);
        } else {
            System.out.println("Number of eigenfaces: " + numEFs);
        }
        
        // Calculamos los pesos de las imagenes de entrenamiento
        weights = bundle.calcWeights(numEFs);
    }
    
    /**
     * Buscamos coincidiencia entre la imagen y las imagenes de entrenamiento
     * 
     * @param name Path de la imagen a buscar coincidencias
     * 
     * @return 
     */
    public MatchResult match(String name)
    {
        // Verificamos que la imagen sea un archivo .png
        if(!name.endsWith(".png")) {
            // Mostramos mensaje de error
            System.out.println("Input image must be a PNG file");
            return null;
        } else {
            // Mostramos mensaje
            System.out.println("Matching " + name);
        }

        // Cargamos la imagen
        BufferedImage image = FileUtils.loadImage(name);
        
        // Verificamos que la imagen se halla cargado
        if(image == null){
            return null;
        }

        // Obtenemos 
        return match(image);    
    }
   
    /**
     * Buscamos coincidiencia entre la imagen cargada y las imagenes de entrenamiento
     * 
     * @param img Imagen a verificar si hay coincidencias
     * 
     * @return 
     */
    public MatchResult match(BufferedImage img)
    {
        // Verificamos que esten cargados los eigenfaces
        if (bundle == null) {
            System.out.println("You must build an Eigenfaces cache before any matching");
            
            // Si no estan cargados los eigenfaces, no buscamos coincidencias
            return null;
        }

        // No verificamos el tamaño de la imagen o la escala de grises
        return findMatch(img);
    }
    
    /**
     * Buscamos coincidencias entre la imagen e imagenes de entrenamiento
     * 
     * @param img Imagen sobre la que buscaremos coincidencias
     * 
     * @return 
     */
    private MatchResult findMatch(BufferedImage img)
    {
        // Convertimos la imagen a un array
        double[] imgArr = ImageUtils.createArrFromIm(img);

        // Convertimos el array en una matriz
        Matrix2D imgMat = new Matrix2D(imgArr, 1);
        
        // Normalizamos la matriz
        imgMat.normalise();

        // Obtenemos el promedio de la matriz
        imgMat.subtract(new Matrix2D(bundle.getAvgImage(), 1));
        
        // mapeamos la imagen en eigenspace, devolviendo sus coordenadas (pesos);
        // Limitamos el mapeo solo a los eigenfaces asignados en el contructor
        Matrix2D imgWeights = getImageWeights(numEFs, imgMat);

        // Obtenemos las distancias entre la imagen y los pesos
        double[] dists = getDists(imgWeights);
        
        // Buscamos la distancia eucladiana mas pequeña entre la imagen y las imagenes de entrenamiento
        ImageDistanceInfo distInfo = getMinDistInfo(dists);
      
        // Obtenemos los nombres de las imagenes de entrenamiento
        ArrayList<String> imageFNms = bundle.getNamesImage();
        
        // Obtenemos el nombre de archivo de la imagen de entrenamiento más cercano
        String matchingFNm = imageFNms.get(distInfo.getIndex());

        // Obtenemos la raiz cuadrada de la distancia minima
        double minDist = Math.sqrt(distInfo.getValue());

        // Regresamos el path de la imagen y value
        return new MatchResult(matchingFNm, minDist);
    }
   
    /**
     * Obtiene los pesos en eigenspaces de las imagenes de entrenamiento
     * mapeando la imagen (de la que se buscan coincidencias)
     * 
     * @param numEFs Numero de eigenfaces a utilizar
     * @param imgMat Matriz de la imagen a buscar coincidencias
     * 
     * @return Matriz con los pesos (coordenadas) de las imagenes de entrenamiento
     */
    private Matrix2D getImageWeights(int numEFs, Matrix2D imgMat)
    {
        // Obtenemos los eigenfaces
        Matrix2D egFacesMat = new Matrix2D(bundle.getEigenFaces());
        
        // Obtenemos una submatriz de los eigenfaces
        Matrix2D egFacesMatPart = egFacesMat.getSubMatrix(numEFs);
        
        // Transponemos la matriz
        Matrix2D egFacesMatPartTr = egFacesMatPart.transpose();

        // Multiplicamos la imagen por la transpuesta de los eigenfaces
        // para obtener los pesos
        return imgMat.multiply(egFacesMatPartTr);
    }
    
    /**
     * Obtiene una matriz de la suma de la distancia euclidiana al cuadrado
     * entre los pesos de la imagen de entrada y todos los pesos de la imagen
     * de entrenamiento
     * 
     * @param imgWeights Pesos de las imagenes de entrenamiento
     * 
     * @return Devuelve una matriz de la suma de las distancia 
     */
    private double[] getDists(Matrix2D imgWeights)
    {
        // Matriz temporal para los pesos de las imagenes
        Matrix2D tempWt = new Matrix2D(weights);
        
        // Convertimos la matriz en un array
        double[] wts = imgWeights.flatten();

        // Restamos el valor promedio a cada fila
        tempWt.subtractFromEachRow(wts);
        
        // Reemplazamos los valores
        tempWt.multiplyElementWise(tempWt);
        
        // Convertimos la matriz en un array de dos dimensiones
        double[][] sqrWDiffs = tempWt.toArray();
        
        // Creamos el array para alamacenar las distancias
        double[] dists = new double[sqrWDiffs.length];

        // Recorremos el array de dos dimensiones
        for (int row = 0; row < sqrWDiffs.length; row++) {
            double sum = 0.0;
            for (int col = 0; col < sqrWDiffs[0].length; col++){
                // Obtenemos la suma de la distancia de cada fila
                sum += sqrWDiffs[row][col];
            }
            
            // Almacenamos la suma
            dists[row] = sum;
        }
        
        return dists;
    }
    
    /**
     * Buscamos la distancia eucladiana mas pequeña entre la imagen y las
     * imagenes de entrenamiento
     * 
     * @param dists Array con las distancias
     * 
     * @return Devuelve la distancia mas pequeña
     */
    private ImageDistanceInfo getMinDistInfo(double[] dists)
    {
        // Asignamos el maximo valor double disponible
        double minDist = Double.MAX_VALUE;
        // Indicamos el indice
        int index = 0;
        
        // Recorremos el array de distancias
        for(int i = 0; i < dists.length; i++){
            // Verificamos si es la distancia minima
            if(dists[i] < minDist){
                // Asignamos la minima distancia
                minDist = dists[i];
                // Asignamos el indice
                index = i;
            }
        }
        
        // Retornamos la distancia minima
        return new ImageDistanceInfo(dists[index], index);
    }
}
