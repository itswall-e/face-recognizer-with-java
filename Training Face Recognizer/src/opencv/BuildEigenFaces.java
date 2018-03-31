/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package opencv;

import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Set;

/**
 *
 * @author Paulo Andrade
 * 
 * Construimos los eigenFaces
 * 
 * FileUtils.java
 * Sajan Joseph, sajanjoseph@gmail.com
 * http://code.google.com/p/javafaces/
 * Modified by Paulo Andrade, March 2018
 */
public class BuildEigenFaces
{
    /**
     * Crea un FaceBundle para el numero especifico of eigenfaces y lo almacena
     * 
     * @param numEFs Numero de EigenFaces
     */
    public static void build(int numEFs)
    {
        // Obtenemos las rutas a las imagenes de entrenamiento
        ArrayList<String> namesImg = FileUtils.getTrainingFnms();
        
        // Obtenemos el total de imagenes
        int totalImg = namesImg.size();
        
        // Verificamos que el numero de eigen faces sea menor al total de imagenes
        if((numEFs < 1) || (numEFs >= totalImg)){
            // Mostramos aviso
            System.out.println("Number of eigenfaces must be in range (1-" +
                    (totalImg-1) + ")" + "; using " + (totalImg-1));
            // Ajustamos el numero de eigen faces
            numEFs = totalImg - 1;
        } else {
            // Mostramos aviso
            System.out.println("Number of eigenfaces: " + numEFs);
        }

        // Creamos un FaceBundle
        FaceBundle bundle = makeBundle(namesImg);
        
        // Guardamos los datos en la cache
        FileUtils.writeCache(bundle);
        
        // (Opcional) Se reconstruyen las imagenes originales desde el FaceBundle 
        reconstructIms(numEFs, bundle);
    }
    
    /**
     * Crea eigenvectors/eigenvalue para las imagenes de entrenamiento
     * tambien almacena cada eigenface (eigenvector) como una imagen
     * 
     * @param fnms Nombres de las imagenes de entrenamiento
     * 
     * @return Devuelve un FaceBundle con los datos de entrenamiento
     */
    private static FaceBundle makeBundle(ArrayList<String> namesImg)
    {
        // Cargamos las imagenes de entrenamiento
        BufferedImage[] imgs = FileUtils.loadTrainingIms(namesImg);

        // Normalizamos las imagenes
        Matrix2D imgsMat = convertToNormMat(imgs); // cada fila es una imagen
        
        // Obtenemos el promedio de cada columna
        double[] avgImage = imgsMat.getAverageOfEachColumn();
        
        // Restamos el promedio a cada fila
        imgsMat.subtractMean();

        // Calculamos la covarianza de la matriz
        Matrix2D imgsDataTr = imgsMat.transpose(); // Transpone la matriz
        Matrix2D covarMat = imgsMat.multiply(imgsDataTr); // Obtenemos la covarianza

        // Calculamos los Eigenvalues y Eigenvectors para la matriz de covarianza
        EigenvalueDecomp egValDecomp = covarMat.getEigenvalueDecomp();
        double[] egVals = egValDecomp.getEigenValues();
        double[][] egVecs = egValDecomp.getEigenVectors();

        // Ordenamos los eigen values y eigenvector
        sortEigenInfo(egVals, egVecs);

        // Obtenemos los eigenfaces normalizados
        Matrix2D egFaces = getNormEgFaces(imgsMat, new Matrix2D(egVecs));

        // Creamos las imagenes a partir de las eigenfaces
        System.out.println("\nSaving Eigenfaces as images...");
        FileUtils.saveEFIms(egFaces, imgs[0].getWidth());
        System.out.println("Saving done\n");

        // Creamos el face bundle
        return new FaceBundle(namesImg, imgsMat.toArray(), avgImage,
                       egFaces.toArray(), egVals, imgs[0].getWidth(), imgs[0].getHeight());
    }
    
    /**
     * Convierte un array de imagenes en una matriz de imagenes normalizadas.
     * Cada fila es una imagen y el numero de columnas son los pixeles de la
     * imagen
     * 
     * @param imgs Array de imagenes a normalizar
     * 
     * @return Devuelve una matriz con las imagenes normalizada
     */
    private static Matrix2D convertToNormMat(BufferedImage[] imgs)
    {
        // Obtenemos el ancho y alto de las imagenes
        int imgWidth = imgs[0].getWidth();
        int imgHeight = imgs[0].getHeight();

        // Obtenemos las filas y columnas
        int numRows = imgs.length;
        int numCols = imgWidth * imgHeight;
        
        // Creamos un array bidimencional para almacenar las imagenes
        double[][] data = new double[numRows][numCols];
        
        // Recorremos el array de imagenes
        for(int i = 0; i < numRows; i++){
            // Almacenamos una imagen por fila
            imgs[i].getData().getPixels(0, 0, imgWidth, imgHeight, data[i]);
        }

        // Convertimos el array bidimensional a una matriz 2D
        Matrix2D imgsMat = new Matrix2D(data);
        
        // Normalizamos la matriz
        imgsMat.normalise();
        
        return imgsMat;
    }
    
    /**
     * Ordenamos los eigen values y eigen vectors de forma descendente tomando
     * como referencia a los eigen values.
     * Agréguegandolos a una tabla para que la clasificación de los valores
     * ajuste a los vectores correspondientes
     * 
     * @param egVals vector con los eigen values
     * @param egVecs matriz con los eigen vectors
     */
    private static void sortEigenInfo(double[] egVals, double[][] egVecs)
    {
        // Convertimos los eigenvalues a objetos Doubles
        Double[] egDvals = getEgValsAsDoubles(egVals);

        // Creamos una tabla donde key == eigenvalue; value == eigenvector
        HashMap<Double, double[]> table = new HashMap<>();
        
        // Recorremos el array de los eigen values
        for(int i = 0; i < egDvals.length; i++){
            // Añadimos un campo a la tabla
            table.put(egDvals[i], getColumn(egVecs, i));
        }

        // Ordenamos las llaves en forma desendente
        ArrayList<Double> sortedKeyList = sortKeysDescending(table);
        
        // 
        updateEgVecs(egVecs, table, egDvals, sortedKeyList);
       // use the sorted key list to update the Eigenvectors array

    // convert the sorted key list into an array
    Double[] sortedKeys = new Double[sortedKeyList.size()];
    sortedKeyList.toArray(sortedKeys); 

    // use the sorted keys array to update the Eigenvalues array
    for (int i = 0; i < sortedKeys.length; i++)
      //egVals[i] = sortedKeys[i].doubleValue();
        egVals[i] = sortedKeys[i];

    }
   
    /**
     * Convertimos los eigen values a objetos Doubles
     * 
     * @param egVals Array de datos a convertir
     * 
     * @return Devuelve un array de objetos Doubles con los datos
     */
    private static Double[] getEgValsAsDoubles(double[] egVals)
    {  
        // Creamos un array de objetos Doubles.
        Double[] egDvals = new Double[egVals.length];
        
        // Recorremos el array de datos
        for(int i = 0; i < egVals.length; i++){
            // Convertimos los valores a objetos doubles
            egDvals[i] = new Double(egVals[i]);
        }
        
        return egDvals;
    }
   
    /**
     * El array eigen vector esta en columnas de forma ordenada.
     * 
     * @param vecs Matriz de eigenvectores
     * @param col Columna a devolver
     * 
     * @return Devuelve el vector en la columna col
     */
    private static double[] getColumn(double[][] vecs, int col)
    { 
        // Creamos un array para la columna a devolver
        double[] res = new double[vecs.length];
        
        // Recorremos la matriz de datos
        for(int i = 0; i < vecs.length; i++){
            // Obtenemos los valores de la columna dada
            res[i] = vecs[i][col];
        }
        
        return res;
    }
    
    /**
     * Ordena las llaves de la tabla en forma descendente
     * 
     * @param table Datos a ser ordenados
     * 
     * @return Devuelve una coleccion con las llaves ordenadas
     */
    private static ArrayList<Double> sortKeysDescending(HashMap<Double, double[]> table)
    {    
        // Obtenemos las llaves de la tabla
        Set<Double> keys = table.keySet();
        
        // almacenamos las llaves en un array list
        ArrayList<Double> keyList = new ArrayList<>();
        keys.forEach(keyList::add);
        
        // Ordenamos con el mas grande al principio
        Collections.sort(keyList, Collections.reverseOrder());
    
        return keyList;
    }
   
    /**
     * Obtenemos los vectores de la tabla en orden descendente desde la clave
     * ordenada, y actualiza la matriz de vectores originales
     * 
     * @param egVecs Matriz de eigenVectores
     * @param table Tabla de datos
     * @param egDvals Array de eigen values (objetos Doubles)
     * @param sortedKeyList Lista de llaves ordenadas
     */
    private static void updateEgVecs(double[][] egVecs,
            HashMap<Double, double[]> table, Double[] egDvals,
            ArrayList<Double> sortedKeyList)
    { 
        // Recorremos el array de los eigen values
        for(int col = 0; col < egDvals.length; col++){
            // Obtenemos los eigen vectores por llave
            double[] egVec = table.get(sortedKeyList.get(col));
            
            // Recorremos la matriz de los eigen vectores
            for(int row = 0; row < egVec.length; row++){
                // Actualizamos los valores
                egVecs[row][col] = egVec[row];
            }
        }
    }
   
    /**
     * Calcula ponderaciones (weights) x eigenfaces, que genera imágenes de
     * entrenamiento medias-normalizadas
     * hay una imagen por fila en la matriz devuelta
     * 
     * @param weights
     * @param egFacesSubMat
     * @param egValsSubMat
     * 
     * @return Devuelve una matriz con imagenes de entrenamiento normalizadas
     */
    private static double[][] getNormImages(double[][] weights, 
                                  Matrix2D egFacesSubMat, Matrix2D egValsSubMat)
  /* calculate weights x eigenfaces, which generates mean-normalized traimning images;
     there is one image per row in the returned array
  */
  {
    double[] egDValsSub = egValsSubMat.flatten();
    Matrix2D tempEvalsMat = new Matrix2D(weights.length, egDValsSub.length);
    tempEvalsMat.replaceRowsWithArray(egDValsSub);

    Matrix2D tempMat = new Matrix2D(weights);
    tempMat.multiplyElementWise(tempEvalsMat);

    Matrix2D normImgsMat = tempMat.multiply(egFacesSubMat);
    return normImgsMat.toArray();
    }
   
    /**
     * Calcula eigenfaces normalizados para las imagenes de entrenamiento
     * multiplicando los eigenvectores de la matriz de imagenes de entrenamiento
     * 
     * @param imgsMat Matriz de imagenes
     * @param egVecs Matriz de eigen vectors
     * 
     * @return Matriz
     */
    private static Matrix2D getNormEgFaces(Matrix2D imgsMat, Matrix2D egVecs)
    {
        // Creamos una matriz con los vectores transpuestos
        Matrix2D egVecsTr = egVecs.transpose();
        
        // Creamos matriz con los eigen faces
        Matrix2D egFaces = egVecsTr.multiply(imgsMat);
        
        // Convertimos la matriz a un aray bidimensional
        double[][] egFacesData = egFaces.toArray();

        // Recorremos el array bidimensional
        for(int row = 0; row < egFacesData.length; row++){
            // Obtenemos norm de los datos
            double norm = Matrix2D.norm(egFacesData[row]);
            
            for(int col = 0; col < egFacesData[row].length; col++){
                // Normalizamos los datos
                egFacesData[row][col] = egFacesData[row][col]/norm;
            }
        }
        
        return new Matrix2D(egFacesData);
    }
    
    /**
     * Recostruye las imagenes desde el FaceBundle
     * 
     * @param numEFs Numero de eigen faces
     * @param bundle Objeto de faceBundle
     */
    private static void reconstructIms(int numEFs, FaceBundle bundle)
    {
        System.out.println("\nReconstructing training images...");

        // Obtenemos la matriz de eigen faces
        Matrix2D egFacesMat = new Matrix2D(bundle.getEigenFaces());
        
        // Obtenemos la submatriz a partir del numero de eigen faces
        Matrix2D egFacesSubMat = egFacesMat.getSubMatrix(numEFs);

        // Obtenemos la matriz de eigen values
        Matrix2D egValsMat = new Matrix2D(bundle.getEigenValues(), 1);
        
        // Obtenemos la submatriz a partir del numero de eigen faces
        Matrix2D egValsSubMat = egValsMat.transpose().getSubMatrix(numEFs);

        // Obtenemos los pesos de las imagenes a partir del numero de eigen faces
        double[][] weights = bundle.calcWeights(numEFs);
        
        // Normalizamos los datos para las imagenes de entrenamiento 
        double[][] normImgs = getNormImages(weights, egFacesSubMat, egValsSubMat);
        
        // Imagenes originales de entrenamiento  = normalized images + average image
        double[][] origImages = addAvgImage(normImgs, bundle.getAvgImage() );

        // Reconstruimos las imagenes
        FileUtils.saveReconIms2(origImages, bundle.getImageWidth()); 
        System.out.println("Reconstruction done\n");
    }
    
    /**
     * Sumamos los promedios de las imagenes a cada imagen normalizada y la
     * almacenamos en un nuevo array, el resultado son las imagenes originales
     * de entrenamiento, una por fila.
     * 
     * @param normImgs Matriz de imagenes normalizadas
     * @param avgImage Media de las imagenes
     * 
     * @return Devuelve la matriz de imagenes de entrenamiento reales
     */
    private static double[][] addAvgImage(double[][] normImgs, double[] avgImage)
    {
        // Creamos un array bidimensional para las imagenes originales
        double[][] origImages = new double[normImgs.length][normImgs[0].length];
        
        // Recorremos las imagenes normalizadas
        for (int i = 0; i < normImgs.length; i++) {
            for (int j = 0; j < normImgs[i].length; j++){
                // Hacemos la suma y almacenamos
                origImages[i][j] = normImgs[i][j] + avgImage[j];
            }
        }
        
        return origImages;
    }
}
