/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package opencv;

import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.Point;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;
import org.opencv.imgproc.Moments;

/**
 *
 * @author Paulo Andrade
 */
public class MotionDetector
{
    private final int MIN_PIXELS = 100; // Pixeles minimos para COG
    private final int MIN_MOVE_REPORT = 3; // Minimo de movimiento para reporte de COG
    private final int MAX_POINTS = 3; // Maximo de COGs a almacenar
    private Mat firstFrame; // Primer frame
    private final Size kernelSize; // Tamaño del kernel
    private final int sigmaX; // Desviacion estandar
    private final int threshold_value; // Valor con respecto al cual se realiza la operación de umbralización
    private final int max_BINARY_value; // Valor utilizado con las operaciones de umbralización binarias (para establecer los píxeles elegidos)
    private final int threshold_type; // Operación de umbralizacion
    private Point cogPoint; // Centro de gravedad
    private final int circleSize; // Tamaño del circulo para el COG
    private final Scalar circleColor; // Color del circulo para el COG
    private Point[] cogPoints;
    private int ptIdx; // Indice del COG a almacenar
    private int totalPoints; // total de puntos almacenados
    
    /**
     * Constructor
     */
    public MotionDetector()
    {
        kernelSize = new Size(21, 21);
        sigmaX = 0;
        threshold_value = 25;
        max_BINARY_value = 255;
        threshold_type = Imgproc.THRESH_BINARY;
        cogPoint = null;
        circleSize = 10;
        circleColor = new Scalar(0, 0, 255);
        firstFrame = null;
        cogPoints = new Point[MAX_POINTS];
        ptIdx = 0;
        totalPoints = 0;
    }
    
    /**
     * Inicializamos el primer frame
     * 
     * @param firstFrame Primer frame al inicializar la camara
     */
    public void prevFrame(Mat firstFrame)
    {   
        // Convertimos a escala de grises
        this.firstFrame = toGray(firstFrame);
    }
    
    /**
     * Detectamos el movimiento
     * 
     * @param frame Frame que comparamos con firstFrame para buscar movimiento
     */
    public void calcMove(Mat frame)
    {
        // Verificamos que exista un frame previo
        if(firstFrame != null){
            // Matriz para computar las diferencias
            Mat frameDelta = new Mat();
            // Matriz para segmentar la imagen respecto a un umbral dado
            Mat thresh = new Mat();

            // Convertimos a escala de grises
            Mat grayFrame = toGray(frame);

            // Calculamos la diferencia entre los dos frames
            Core.absdiff(firstFrame, grayFrame, frameDelta);

            /* Convertimos de escala de grises a binario de dos niveles:
            small diffs (0 -- LOW_THRESHOLD) --> 0
            large diffs (LOW_THRESHOLD+1 -- 255) --> 255 */
            Imgproc.threshold(frameDelta, thresh, threshold_value,
                    max_BINARY_value, threshold_type);

            Imgproc.dilate(thresh, thresh, new Mat(), new Point(-1, -1), 2);

            // Obtenemos el centro de gravedad
            Point pt = findCOG(thresh);
            // verificamos si existe un nuevo centro de gravedad
            if(pt != null){
                // Almacenamos el punto
                cogPoints[ptIdx] = pt;
                // Actualizamos el Idx
                ptIdx = (ptIdx+1) % MAX_POINTS;
                // Actualizamos el total de cogs almacenados
                if(totalPoints < MAX_POINTS) totalPoints++;
            }
        }
    }
    
    /**
     * Procesamos el frame para convertirlo a escala de grises
     * 
     * @param frame Frame que convertiremos en escala de grises
     * 
     * @return Matriz con escala de grises
     */
    private Mat toGray(Mat frame)
    {
        // Matriz para escala de grises
        Mat grayFrame = new Mat();
        
        // Convertimos a escala de grises
        Imgproc.cvtColor(frame, grayFrame, Imgproc.COLOR_BGR2GRAY);
        
        // Aplicamos filtro para desenfocar la imagen (reducimos el ruido)
        Imgproc.GaussianBlur(grayFrame, grayFrame, kernelSize, sigmaX);
        
        // Ecualizamos el histograma del frame para mejorar el resultado
        Imgproc.equalizeHist(grayFrame, grayFrame);
        
        return grayFrame;
    }
    
    /**
     * Buscamos el centro de gravedad (center of gravity)
     * 
     * Para obtener el centro de gravedad, se necesita conocer los momentos
     * para obtener el centro de una forma (regular e irregular), su formula:
     * 
     * M (momento) = F (fuerza) * d (distancia)
     * 
     * Esta formula funciona para una sola dimension, para dos dimenciones
     * (como nuestro caso) se utiliza la formula.-
     * 
     * My = m1*x1 + m2*x2 + ... + mn*xn (eje Y)
     * Mx = m1*y1 + m2*y2 + ... + mn*yn (eje X)
     * 
     * @param thresh Matriz con las diferencias
     * 
     * @return Devuelve el punto del centro de gravedad
     */
    private Point findCOG(Mat thresh)
    {
        // Declaramos el punto para las coordenadas
        Point p = null;
        
        // Obtenemos el total de pixeles
        int pixels = Core.countNonZero(thresh);
        
        // Verificamos que se cumpla el minimo de pixeles
        if(pixels > MIN_PIXELS){
            // Declaramos un momento (true = binary image)
            Moments m = Imgproc.moments(thresh, true);
            
            // Inicializamos el punto
            p = new Point();
            
            // Obtenemos las coordenadas del COG
            p.x = (int) (m.get_m10() / m.get_m00());
            p.y = (int) (m.get_m01() / m.get_m00());
        }
        
        return p;
    }
    
    /**
     * Reportamos cambios en el punto de gravedad
     * 
     * @param cogPoint Punto de COG actual
     * @param prevCogPoint Punto de COG previo (anterior)
     */
    public void reportCOGChanges(Point cogPoint, Point prevCogPoint)
    {    
        // Verificamos que exista un punto de gravedad
        if(prevCogPoint != null){
            // Calculamos la distancia y angulo de movimiento
            double xStep = cogPoint.x - prevCogPoint.x;
            double yStep = -1 *(cogPoint.y - prevCogPoint.y);

            int distMoved = (int) Math.round(Math.sqrt((xStep*xStep) + (yStep*yStep)));
            int angle = (int) Math.round( Math.toDegrees( Math.atan2(yStep, xStep)));
            
            // Verificamos si la distancia cumple el minimo a considerar
            if(distMoved > MIN_MOVE_REPORT){
                // Imprimimos el reporte
                System.out.println("COG: (" + cogPoint.x + ", " + cogPoint.y + ")");
                System.out.println(" Dist moved: " + distMoved + "; angle: " + angle);
            }
        }
    }
    
    /**
     * Dibujamos el centro de gravedad
     * 
     * @param frame Matriz con la imagen original
     */
    public void drawCOG(Mat frame)
    {
        // Obtenemos el COG
        Point cog = getCog();
        
        // Verificamos que el frame no sea nulo
        if(frame != null){
            // Verificamos que el COG no sea nulo
            if(cog != null){
                // Dibujamos el punto
                Imgproc.circle(frame, cog, circleSize, circleColor, 10);
            }
        }
    }
    
    /**
     * Obtenemos el centro de gravedad COG con suavizado
     * 
     * Si desea mejorar el suavizado para movimientos rapidos,
     * disminuya el total de puntos COG a almacenar
     * 
     * @return COG
     */
    private Point getCog()
    {
        // verificamos que existan cogs almacenados
        if(totalPoints == 0) return null;
        
        // Sumatorias para X y Y
        int xTotal = 0;
        int yTotal = 0;
        
        // Obtenemos las suatorias
        for(int i = 0; i < totalPoints; i++){
            xTotal += cogPoints[i].x;
            yTotal += cogPoints[i].y;
        }
        
        // Obtenemos el COG promedio
        return new Point((int) (xTotal / totalPoints), (int) (yTotal/totalPoints));
    }
}
