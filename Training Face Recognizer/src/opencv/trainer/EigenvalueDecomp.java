/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package opencv.trainer;

import cern.colt.matrix.linalg.EigenvalueDecomposition;

/**
 *
 * @author Paulo Andrade
 * 
 * Descomposicion de datos para obtener los eigenVector y eigenValues
 * 
 * FileUtils.java
 * Sajan Joseph, sajanjoseph@gmail.com
 * http://code.google.com/p/javafaces/
 * Modified by Paulo Andrade, March 2018
 */
public class EigenvalueDecomp extends EigenvalueDecomposition
{
    /**
     * Constructor.
     * Comprueba la simetría y luego construye la estructura de descomposición
     * de valores propios para acceder a D (diagonal de la matriz) y
     * V (matriz de vectores propios).
     * 
     * @param dmat 
     */
    public EigenvalueDecomp(Matrix2D dmat)
    { 
        super(dmat);
    }

    /**
     * Obtenemos los eigen values
     * 
     * @return Devuelve un vector con los eigen values
     */
    public double[] getEigenValues()
    {
        return diag(getD().toArray());
    }

    /**
     * Obtenemos los eigen vectores
     * 
     * @return Devuelve una matriz con los eigen vectores
     */
    public double[][] getEigenVectors()
    {
        return getV().toArray();
    }

    /**
     * Obtiene la diagonal de una matriz
     * 
     * @param m Matriz de datos
     * 
     * @return Devuelve un vector con los valores de la diagonal
     */
    private double[] diag(double[][] m)
    {
        // Vector para los valores de la diagonal
        double[] diag = new double[m.length];
        
        // Recorremos la matriz de datos
        for(int i = 0; i < m.length; i++){
            // Obtenemos los valores de la diagonal
            diag[i] = m[i][i];
        }
        
        return diag;
    }
}