/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package opencv;

import org.opencv.core.Mat;
import org.opencv.core.MatOfRect;
import org.opencv.core.Rect;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;
import org.opencv.objdetect.CascadeClassifier;
import org.opencv.objdetect.Objdetect;

/**
 * Detecta ojos en un rostro, el rostro debe ser enviado al método en una
 * matriz de tipo ROI (region of interes).
 * 
 * @author Paulo Andrade
 * @version 1.0.0
 */
public class EyesDetector
{
    private final CascadeClassifier eyesHaar; // clasificadores
    double scaleFactor; // Cuanto se reduce la imagen en cada escala de imagen
    int minNeighbors; // Cuantos vecinos debe tener cada rectangulo candidato para concervarlo
    int minSize; // Tamaño minimo para la busqueda del objeto
    int flags; // Banderas
    
    /**
     * Constructor
     */
    public EyesDetector()
    {
        // Inicializamos los clasificadores
        eyesHaar = new CascadeClassifier();
        
        // Inicializamos las propiedades
        scaleFactor = 1.1;
        minNeighbors = 8;
        minSize = 0;
        flags = 0 | Objdetect.CASCADE_SCALE_IMAGE;
        
        // Cargamos los clasificadores
        loadClassifiers();
    }
    
    /**
     * Cargamos los clasificadores con los que vamos a trabajar
     */
    private void loadClassifiers()
    {
        // Ruta principal
        String path = "resources/haarcascades/";
        
        // cargamos los clasificadores cascada
        eyesHaar.load(path+"haarcascade_eye.xml");
    }
    
    /**
     * Detector de ojos en un rostro
     * 
     * @param m Matriz original
     * @param grayFrame Matriz original en escala de grises
     * @param faces Matriz de rostros detectados
     */
    public void eyesDetector(Mat m, Mat grayFrame, MatOfRect faces)
    {
        // Matriz para los objetos detectados (ojos)
        MatOfRect eyes = new MatOfRect();
        
        // Convertimos la matriz en un array (vector)
        Rect[] facesArray = faces.toArray();

        // trabajamos en cada uno de los rostros
        for(Rect rect: facesArray){
            // Obtenemos el rectangulo donde vamos a trabajar
            Rect rectCrop = new Rect(rect.x, rect.y , rect.width, rect.height);
            // Obtenemos la matriz roi
            Mat roiGray = grayFrame.submat(rectCrop);
            Mat roiColor = m.submat(rectCrop);
            
            // Obtenemos el tamaño minimo
            minSize = calcSize(roiGray, 0.15F);
            
            eyesHaar.detectMultiScale(roiGray, eyes, scaleFactor, minNeighbors,
                    flags, new Size(minSize, minSize), new Size());

            // Dibujamos los rectangulos para los ojos
            eyesDraw(roiColor, eyes);
        }
    }
    
    /**
     * Dibujamos rectangulos al detectar los ojos
     * 
     * @param m Matriz original
     * @param faces Matriz con los rectangulos detectados
     */
    private void eyesDraw(Mat roi, MatOfRect eyes)
    {
        // Convertimos la matriz en un array (vector)
        Rect[] eyesArray = eyes.toArray();

        // Recorremos cada uno de los objetos
        for (int i = 0; i < eyesArray.length; i++){
            // m <- matriz original
            // facesArray[i].tl() <- punto del objeto en x
            // facesArray[i].br() <- punto del objeto en y
            // new Scalar(0, 255, 0) <- color del rectangulo a dibujar
            // 3 <- Espesor de la linea
            Imgproc.rectangle(roi, eyesArray[i].tl(), eyesArray[i].br(), new Scalar(0, 0, 255), 2);
        }
    }
    
    /**
     * Calculamos el tamaño minimo para el objeto a rastrear
     * 
     * @param grayFrame Matriz en escala de grises del cual obtendremos el tamaño
     * @param minSize Tamaño minimo para buscar el objeto (0.2F)
     */
    private int calcSize(Mat grayFrame, float minSize)
    {
        int eyeSize = 0;
        
        // Obtenemos el alto de la imagen (frame)
        int height = grayFrame.rows();

        // verificamos que el minimo a detectar sea mayor a 0
        if (Math.round(height * minSize) > 0){
            // Asignamos el nuevo tamaño a detectar
            eyeSize = Math.round(height * minSize);
        }
        
        return eyeSize;
    }
}
