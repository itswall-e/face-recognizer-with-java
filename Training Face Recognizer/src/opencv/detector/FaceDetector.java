/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package opencv.detector;

import org.opencv.core.Mat;
import org.opencv.core.MatOfRect;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;
import org.opencv.objdetect.CascadeClassifier;

/**
 * Detector de rostros, detecta tanto frontal como de perfil.
 * 
 * @author Paulo Andrade
 * @version 1.0.0
 */
public class FaceDetector extends Detector
{
    EyesDetector ed;
    SmileDetector sd;
    private final CascadeClassifier faceHaar; // Clasificadores 
    private final CascadeClassifier perfilFaceHaar;
    
    /**
     * Constructor
     */
    public FaceDetector()
    {
        // Inicializamos las clases externas
        ed = new EyesDetector();
        sd = new SmileDetector();
        
        // Inicializamos los clasificadores
        faceHaar = new CascadeClassifier();
        perfilFaceHaar = new CascadeClassifier();
        
        // Inicializamos las propiedades
        scaleFactor = 1.05;
        minNeighbors = 1;
        
        // Cargamos los clasificadores
        loadClassifiers();
    }
    
    /**
     * Cargamos los clasificadores con los que vamos a trabajar
     */
    private void loadClassifiers()
    {
        // cargamos los clasificadores cascada
        faceHaar.load(path+"haarcascade_frontalface_alt.xml");
        perfilFaceHaar.load(path+"haarcascade_profileface.xml");
    }
    
    /**
     * Detecta los rostros
     * 
     * @param m Matriz donde reconoceremos los rostros
     * @param option Opciones de deteccion [0-face,1-eye,2-smile]
     */
    public Mat FaceDetect(Mat m, boolean[] option)
    {
        // Matriz para los objetos detectados (rostros)
        MatOfRect faces = new MatOfRect();
        // Matriz en escala de grises
        Mat grayFrame = new Mat();
        
        // Convertimos el frame original en escala de grises
        Imgproc.cvtColor(m, grayFrame, Imgproc.COLOR_BGR2GRAY);
        
        // Ecualizamos el histograma del frame para mejorar el resultado
        Imgproc.equalizeHist(grayFrame, grayFrame);
        
        // Calculamos el tamaño minimo del rostro a detectar (20%)
        minSize = calcSize(grayFrame, 0.2F);
        
        // detectamos los rostros de frente (imagen donde detectaremos los objetos,
        // vector de cuadrados para los objetos detectados,
        // cuanto se reduce la imagen en cada escala de imagen,
        // cuantos vecinos debe tener cada rectangulo candidato para concervarlo,
        // flags,
        // tamaño minimo posible del objeto
        // tamaño maximo posible del objeto
        faceHaar.detectMultiScale(grayFrame, faces, scaleFactor, minNeighbors,
                flags, new Size(minSize, minSize), new Size());
        
        // verificamos si detecto rostros de frente
        if(faces.empty()){
            // Buscamos rostros de perfil
            perfilFaceHaar.detectMultiScale(grayFrame, faces, scaleFactor,
                    minNeighbors, flags, new Size(minSize, minSize), new Size());
        }
        
        // verificamos si hay rostros para buscar ojos
        if(!faces.empty()){
            // verificamos si esta activa la deteccion de ojos
            if(option[1])
                // Detectamos los ojos
                ed.eyesDetector(m, grayFrame, faces);
        }
        
        // verificamos si hay rostros para buscar sonrisa
        if(!faces.empty()){
            // verificamos si esta activa la deteccion de sonrisas
            if(option[2])
                // Detectamos las sonrisas
                sd.smileDetector(m, grayFrame, faces);
        }
        
        // verificamos si esta activa la deteccion de rostro
        if(option[0])
            // Dibujamos los bordes de los rostros detectados
            return objectDraw(m, faces, new Scalar(0, 255, 0), true);
        else
            return m;
    }
}
