/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package opencv.trainer;

import cern.colt.matrix.DoubleMatrix2D;
import cern.colt.matrix.impl.DenseDoubleMatrix2D;
import cern.jet.math.Functions;

/**
 *
 * @author Paulo Andrade
 * 
 * Extendemos la funcionalida de la clase DenseDoubleMatrix2D de COLT
 * 
 * FileUtils.java
 * Sajan Joseph, sajanjoseph@gmail.com
 * http://code.google.com/p/javafaces/
 * Modified by Paulo Andrade, March 2018
 */
public class Matrix2D extends DenseDoubleMatrix2D
{
    /**
     * Construtor - Crea una matriz con los valores dados
     * 
     * @param data Matriz con los datos dados
     */
    public Matrix2D(double[][] data)
    {
        super(data);
    }

    /**
     * Constructor (implementado) - Crea un array de dos dimenciones con la
     * matriz dada
     * 
     * @param dmat Matriz con los datos seleccionados 
     */
    public Matrix2D(DoubleMatrix2D dmat)
    {
        super(dmat.toArray());
    }

    /**
     * Constructor - Crea una matriz del tamaño filas x columnas dado
     * 
     * @param rows Filas de la matriz
     * @param cols Columnas de la matriz
     */
    public Matrix2D(int rows, int cols)
    {
        super(rows, cols);
    }

    /**
     * Constructor (implementado) - 
     * 
     * @param data Datos dados
     * @param rows Numero de filas para la matriz
     */
    public Matrix2D(double[] data, int rows)
    {
        super(rows, ((rows != 0) ? data.length/rows : 0));
        
        // Obtenemo el total de columnas
        int cols = (rows != 0) ? data.length/rows : 0;
        
        // Verificamos que la longitud del array sea un multiplo del total de filas
        if ((rows * cols) != data.length)
            throw new IllegalArgumentException("Array length must be a multiple of " + rows);
        
        // Creamos el array de dos dimensiones
        double[][] vals = new double[rows][cols];
        
        // Recorremos el array bidimencional
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++){
                // Almacenamos los valores dados
                vals[i][j] = data[i + (j * rows)];
            }
        }
        
        // Establecemos todas las celdas en el estado especificado por valores.
        assign(vals);		
    }

    /**
     * Obtenemos una submatriz de ciertas filas, devuelve una nueva vista de
     * subintervalo que es una matriz secundaria de altura x ancho que comienza
     * en [fila, columna].
     * Las operaciones en la vista devuelta solo se pueden aplicar al rango
     * restringido
     * 
     * @param rows Numero de filas de la submatriz
     * 
     * @return Devuelve una submatriz
     */
    public Matrix2D getSubMatrix(int rows)
    {
        return new Matrix2D(viewPart(0, 0, rows, super.columns()).copy());
    }

    /**
     * Ajuste de la unidad de longitud
     * 
     * @param data 
     */
    public static void fitToUnitLength(double[] data)
    {
        // Obtenemos el valor maximo de los datos
        double max = max(data);
        
        // Recorremos el array de datos
        for(int i = 0; i < data.length; i++){
            // Ajustamos
            data[i] /= max;
        }
    }

    /**
     * Resta la media de la columna de cada fila
     */
    public void subtractMean()
    {
        // Restamos la media
        subtractFromEachRow(getAverageOfEachColumn());
    }

    /**
     * Obtenemos los promedios de cada columna
     * 
     * @return Devuelve un vector con los promedios por columna
     */
    public double[] getAverageOfEachColumn()
    {
        // Convertimos la matriz en un array
        double[][] data = this.toArray();
        
        // Suma de los datos de cada columna
        double total;
        
        // Creamos un array para almacenar los promedios
        double[] avgValues = new double[this.columns];

        // Recorremos el array de datos
        for(int col = 0; col < this.columns; col++){
            // Inicializamos la suma
            total = 0.0;
            
            for (int row = 0; row < this.rows; row++){
                // Sumamos los valores de la columna
                total += data[row][col];
            }
            
            // Obtenemos y almacenamos el promedio
            avgValues[col] = total/this.rows;
        }
        
        return avgValues;
    }

    public void replaceRowsWithArray(double[] data)
    {
        if(this.columns != data.length)
        throw new RuntimeException(
             "matrix columns not matching number of input array elements");
		
        for (int row = 0; row < this.rows; row++) {
            for (int col = 0; col < this.columns; col++)
                set(row, col, data[col]);
        }
    }

    /**
     * Normalizamos la matriz
     */
    public void normalise()
    {
        // Convertimos la matriz en un array bidimensional
        double[][] temp = this.toArray();
        
        // Array para los valores maximos
        double[] mvals = new double[temp.length];

        // Recorremos el array bidimensional
        for(int i = 0; i < temp.length; i++){
            // Obtenemos el valor maximo de cada fila
            mvals[i] = max(temp[i]);
        }

        // Recorremos el array bidimensional
        for(int i = 0; i < temp.length; i++){
            for(int j = 0; j < temp[0].length; j++){
                // Normalizamos
                temp[i][j] /= mvals[i];
            }
        }
        
        // Establecemos todas las celdas en el estado especificado por valores.
        assign(temp);
    }

    /**
     * Obtenemos el valor maximo de un array
     * 
     * @param arr Array con los datos
     * 
     * @return Devuelve el valor maximo
     */
    private static double max(double[] arr)
    {
        // Declaramos el valor minimo para un tipo double
        double max = Double.MIN_VALUE;
        
        // Recorremos el array
        for (int i = 0; i < arr.length; i++){
            // Verificamos si se trata del valor maximo, si es asi, remplazamos
            max = Math.max(max, arr[i]);
        }
        
        return max;
    }

    public void subtract(Matrix2D mat)
    {
        assign(mat, Functions.functions.minus);
    }

    public void add(Matrix2D mat)
    {
        assign(mat, Functions.functions.plus);
    }

    /**
     * Restamos un valor (promedio) a cada fila
     * 
     * @param oneDArray Array con los valores a restar
     */
    public void subtractFromEachRow(double[] oneDArray)
    {
        // Convertimos la matriz en un array bidimensional
        double[][] denseArr = this.toArray();
        
        // Recorremos el array bidimensional
        for(int i = 0; i < denseArr.length; i++){
            for(int j = 0; j < denseArr[0].length; j++){
                // Restamos el valor a cada celda
                denseArr[i][j] -= oneDArray[j];
            }
        }
        
        // Establecemos todas las celdas en el estado especificado por valores.
        assign(denseArr);
    }

    /**
     * Multiplicacion de matrices
     * 
     * @param mat Matriz por la que se multiplica
     * 
     * @return Matriz con el resultado de la multiplicacion
     */
    public Matrix2D multiply(Matrix2D mat)
    {
        // Multiplicamos
        return new Matrix2D(this.zMult(mat, null));
    }

    /**
     * Reemplaza todos los valores de celda del receptor con los valores de otra
     * matriz.
     * 
     * @param mat 
     */
    public void multiplyElementWise(Matrix2D mat)
    {
        // Remplazamos
        assign(mat, Functions.functions.mult);
    }

    /**
     * Transposicion de la matriz
     * La transposición de una matriz es una matriz nueva, cuyas filas son las
     * columnas del original.
     * (Esto hace que las columnas de la nueva matriz sean las filas del original)
     * 
     * @return Retorna la matriz transpuesta
     */
    public Matrix2D transpose()
    {
        // Transposicion de la matriz
        return new Matrix2D(this.viewDice());
    }

    /**
     * Aplanamos la matriz (la convertimos en un array)
     * 
     * @return Devuelve un array con los datos aplanados
     */
    public double[] flatten()
    {
        // Creamos el array para almacenar los datos aplanados
        double[] res = new double[this.rows * this.columns];
        
        // Control del indice del array
        int i = 0;
        
        // Recorremos la matriz
        for (int row = 0; row < this.rows; row++) {
            for (int col = 0; col < this.columns; col++)
                // Almacenamos los datos en el array
                res[i++] = get(row, col);
        }
        
        return res;
    }	

    /**
     * Obtiene la suma de los valores al cuadrado
     * 
     * @param arr array de datos
     * 
     * @return Devuelve la suma de los valores elevados al cuadrado
     */
    public static double norm(double[] arr)
    {
        // Variable para almacenar la suma
        double val = 0.0;
        
        // Recorremos el array de datos
        for(int i = 0; i < arr.length; i++){
            // Elevamos al cuadrado el valor y lo sumamos
            val += (arr[i] * arr[i]);
        }
        
        return val;
    }

    public static void subtract(double[] inputFace, double[] avgFace)
    {
        for (int i = 0; i < inputFace.length; i++)
            inputFace[i] -= avgFace[i];
    }

    /**
     * Obetenemos un modelo con los datos listos para obtener eigen values y
     * eigen vectors
     * 
     * @return Devueve una instancia de EigenvalueDecomp
     */
    public EigenvalueDecomp getEigenvalueDecomp()
    {
        // Instancia de EigenvalueDecomp
        return new EigenvalueDecomp(this);
    }
}