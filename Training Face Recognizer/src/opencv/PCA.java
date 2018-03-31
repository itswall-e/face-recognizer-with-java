/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package opencv;

import cern.colt.list.DoubleArrayList;
import cern.colt.matrix.DoubleFactory1D;
import cern.colt.matrix.DoubleFactory2D;
import cern.colt.matrix.DoubleMatrix1D;
import cern.colt.matrix.DoubleMatrix2D;
import cern.colt.matrix.doublealgo.Statistic;
import cern.colt.matrix.impl.DenseDoubleMatrix2D;
import cern.colt.matrix.linalg.Algebra;
import cern.colt.matrix.linalg.EigenvalueDecomposition;
import cern.jet.math.Functions;
import cern.jet.stat.Descriptive;

/**
 *
 * @author Paulo Andrade
 * 
 * PCA (Principal Components Analysis)
 * 
 * Utiliza la libreria COLT para analisis numerico para Java, aun que es una version
 * obsoleta de 2004, se utiliza ya que Google Java Face la utiliza, otras
 * opciones de librerias pueden ser encontradas en:
 * https://math.nist.gov/javanumerics/
 * 
 * EJML (http://code.google.com/p/efficient-java-matrix-library/) que parece ser
 * el más rápido para el cálculo de Eigenvector para matrices más pequeñas, y
 * JBlas (http://jblas.org/) mejor para los más grandes. Hay una entrada de blog
 * interesante sobre estos puntos de referencia en
 * http://measuringmeasures.com/blog/2010/3/28/matrix-benchmarks-fast-linear-algebra-on-the-jvm.html
 */
public class PCA
{
    private double[][] trainingData; // Datos de entrenamiento
    
    /**
     * Constructor
     */
    public PCA()
    {
        trainingData = new double[][]{ {2.5, 0.5, 2.2, 1.9, 3.1, 2.3, 2.0, 1.0, 1.5, 1.1}, // x data 
            {2.4, 0.7, 2.9, 2.2, 3.0, 2.7, 1.6, 1.1, 1.6, 0.9}, // y
        };
    }
    
    /**
     * Ejemplo de uso
     */
    public void example()
    {
        /* Calculamos la covarianza (relacion que existe entre los conjuntos de
        datos) de la matriz*/
        DoubleMatrix2D matCov = calcCovarMat(trainingData);
        
        /* Obtenemos los eigenVector y eigen Values de la matriz
        Un vector propio es un vector ordinario que cuando se multiplica por una
        matriz dada solamente cambia su magnitud*/
        EigenvalueDecomposition matEig = new EigenvalueDecomposition(matCov);
        
        /* Obtenemos los eigenVector
        (eigenValue es el nombre elegante para esa magnitud)*/
        DoubleMatrix2D eigenVecs = matEig.getV();
        
        // Mostramos los eigenVector
        System.out.println("\nEigenvectors: \n" + eigenVecs);
        
        // Obtenemos la parte real de los eigenValues
        DoubleMatrix1D realEigVals = matEig.getRealEigenvalues();
        
        // Mostramos los valores reales
        System.out.println("\nEigenvalues: \n" + realEigVals);
        
        // Reportamos la posicion y el valor mas grande del eigenValues
        reportBiggestEigen(realEigVals);

        // calculate array of means for each row of training data
        double[] means = calcMeans(trainingData);

        // recognition task for new data (x, y)
        double[] newData = new double[]{ 2.511, 2.411 };

        // transform all data
        DoubleMatrix2D matTransAllData = transformAllData(newData, trainingData, means, eigenVecs);
        
        /* report minimal euclidean distances between new data and training data */
        minEuclid(matTransAllData);
    }
    
    /**
     * Obtenemos la matriz de covarianza
     * 
     * @return Devuelve una matriz con los datos de la covarianza
     */
    public DoubleMatrix2D calcCovarMat(double[][] data)
    {
        // Obtenemos el numero de filas
        int rows = data.length;
        
        // Creamos la matriz para almacenar los datos de la covarianza
        DoubleMatrix2D matCov = new DenseDoubleMatrix2D(rows, rows);
        
        // Recorremos las filas de la matriz
        for(int i = 0; i < rows; i++){
            // Creamos un array list con las filas de la matriz
            DoubleArrayList iRow = new DoubleArrayList(data[i]);
            
            // Obtenemos la varianza de dos secuencias de datos (para la diagonal)
            double variance = Descriptive.covariance(iRow, iRow);
            
            // Almacenamos la informacion
            matCov.setQuick(i, i, variance);
            
            // Buscamos los valores simetricos alrededor de la diagonal
            for(int j = 0; j < rows; j++){
                // Creamos un array list con las filas de la matriz
                DoubleArrayList jRow = new DoubleArrayList(data[j]);
            
                /* Obtenemos la covarianza para X, Y
                cov(x,y) = (1/(size()-1)) * Sum((x[i]-mean(x)) * (y[i]-mean(y)))*/
                double cov = Descriptive.covariance(iRow, jRow);
                
                // Almacenamos la informacion
                matCov.setQuick(i, j, variance);
                matCov.setQuick(j, i, variance);
            }
        }
        
        return matCov;
    }
    
    /**
     * Itera a través de la matriz de eigenValues buscando el valor más grande,
     * luego informa su posición de columna
     * 
     * @param eigenValues Vector de valores de la matriz
     */
    public void reportBiggestEigen(DoubleMatrix1D eigenValues)
    {
        int pos = 0; // Posicion del valor
        double maxValue = 0.0; // valor maximo
        
        // Recorremos el vector
        for(int i = 0; i < eigenValues.size(); i++){
            // verificamos el valor
            if(eigenValues.getQuick(i) > maxValue){
                // Actualizamos la informacion
                pos = i;
                maxValue = eigenValues.getQuick(i);
            }
        }
        
        // Mostramos la informacion
        System.out.println("Max value: "+maxValue+" Pos: "+pos);
    }
    
    /**
     * Calcula una matriz de medias para cada fila de datos de entrenamiento
     * 
     * @param data Datos de entrenamiento
     * 
     * @return Vector con las medias para cada filas
     */
    public double[] calcMeans(double[][] data)
    {
        int rows = data.length;
        int cols = data[0].length;
        
        // declaramos el vector
        double[] means = new double[data.length];
        
        // Recorremos la matriz para obtener cada fila
        for(int i = 0; i < rows; i++){
            // suma para los valores de la fila
            double sum = 0.0;
            
            // Obtenemos cada columna
            for(int j = 0; j < cols; j++){
                // Obtenemos la sumatora
                sum += data[i][j];
            }
            
            // Obtenemos la media
            means[i] = sum / (double) cols;
        }
        
        return means;
    }
    
    /**
     * La nueva coordenada se agrega a los datos de entrenamiento existentes,
     * que transforma todos los datos, primero, los datos de la matriz son normalizados,
     * luego rotados y reflejados de modo que el componente principal está alineado
     * con el eje Y
     * 
     * Esta alineación se logra mediante el uso de vectores propios normalizados
     * para transformar los puntos de datos a través de la multiplicación de la matriz
     * 
     * @param newData Datos nuevos de entrenamiento
     * @param trainingData Datos de entrenamiento
     * @param means Medias de los datos de entrenamiento
     * @param eigenVecs Matriz de vectores
     * 
     * @return 
     */
    public DoubleMatrix2D transformAllData(double[] newData, double[][] trainingData,
            double[] means, DoubleMatrix2D eigenVecs)
    {
        // Obtenemos filas y columnas
        int rows = trainingData.length;
        int cols = trainingData[0].length;
        
        // Creamos una nueva matriz para añadir los datos nuevos
        DoubleMatrix2D matAllData = new DenseDoubleMatrix2D(rows, cols+1);
        
        // Convertimos el vector de newData en una matriz de dos celdas
        DoubleMatrix1D matNewData = DoubleFactory1D.dense.make(newData);
        
        // Añadimos la nueva informacion en la primer columna
        matAllData.viewColumn(0).assign(matNewData);
        
        // Convertimos de double[][] a DoubleMatrix2D
        DoubleMatrix2D matData = DoubleFactory2D.dense.make(trainingData);
        
        // Añadimos la informacion de entrenamiento
        matAllData.viewPart(0, 1, rows, matData.columns()). assign(matData);
        
        // subtract mean from all data matrix
        for(int i=0; i < rows; i++){
            matAllData.viewRow(i).assign(Functions.minus(means[i]));
        }

        // transpose the eigenvectors
        DoubleMatrix2D eigenVecsTr = Algebra.DEFAULT.transpose(eigenVecs);

        // return the transformed data return
        Algebra.DEFAULT.mult(eigenVecsTr, matAllData);
        
        return matAllData;
    }
    
    /**
     * 
     */
    private static void minEuclid(DoubleMatrix2D matTransAllData)
    {
        // Calculamos la distancia eucladiana entre todos los puntos
        DoubleMatrix2D matDist = Statistic.distance(matTransAllData, Statistic.EUCLID);

        // Obtenemos la primer fila de las distancias (pertenecen al primer dato)
        DoubleMatrix1D matNewDist = matDist.viewRow(0);
        
        // Imprimios la distancia del primer dato
        System.out.println("\nEuclid dists for new data: \n" + matNewDist);

        // Ordenamos las distancias y obtenemos la mas pequeña
        DoubleMatrix1D matSortNewDist = matDist.viewRow(0).viewSorted();

        // Obtenemos el segundo valor mas pequeño (ordenado)
        double smallestDist = matSortNewDist.get(1);
        
        // Imprimimos el segundo valor
        System.out.printf("\nSmallest distance to new data: %.4f\n", smallestDist);

        // Buscamos la distancia mas pequeña de los puntos al nuevo dato
        int pos = -1;
        
        for (int i = 1; i < matNewDist.size(); i++){
            // Inicia en la segunda columna
            if (smallestDist == matNewDist.get(i)) {
                pos = i;
                break;
            }
        }
        
        // Mostramos la posicion
        if (pos != -1)
            System.out.println("Closest pt index in training data:"+(pos-1));
        else
            System.out.println("Closest point not found");
    }
}
