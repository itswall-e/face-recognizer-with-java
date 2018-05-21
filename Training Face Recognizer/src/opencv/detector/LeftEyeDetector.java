/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package opencv.detector;

import org.opencv.core.Mat;
import org.opencv.core.MatOfRect;
import org.opencv.core.Rect;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.objdetect.CascadeClassifier;

/**
 * Detecta el ojo izquierdo en un rostro, el rostro debe ser enviado al método en una
 * matriz de tipo ROI (region of interes).
 * 
 * @author Paulo Andrade
 * @version 1.0.0
 */
public class LeftEyeDetector extends Detector
{
    private final CascadeClassifier leftEyeHaar; // clasificadores
    
    /**
     * Constructor
     */
    public LeftEyeDetector()
    {
        // Inicializamos los clasificadores
        leftEyeHaar = new CascadeClassifier();
        
        // Inicializamos las propiedades
        scaleFactor = 1.1;
        minNeighbors = 8;
        
        // Cargamos los clasificadores
        loadClassifiers();
    }
    
    /**
     * Cargamos los clasificadores con los que vamos a trabajar
     */
    private void loadClassifiers()
    {   
        // cargamos los clasificadores cascada
        leftEyeHaar.load(path+"haarcascade_lefteye_2splits.xml");
    }
    
    /**
     * Detector de ojo iquierdo en un rostro
     * 
     * @param m Matriz original
     * @param grayFrame Matriz original en escala de grises
     * @param faces Matriz de rostros detectados
     */
    public void leftEyeDetector(Mat m, Mat grayFrame, MatOfRect faces)
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
            
            leftEyeHaar.detectMultiScale(roiGray, eyes, scaleFactor, minNeighbors,
                    flags, new Size(minSize, minSize), new Size());

            // Dibujamos los rectangulos para los ojos
            objectDraw(roiColor, eyes, new Scalar(0, 0, 255));
        }
    }
}
