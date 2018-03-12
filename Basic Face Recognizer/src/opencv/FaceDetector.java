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
 * Detector de rostros, detecta tanto frontal como de perfil.
 * 
 * @author Paulo Andrade
 * @version 1.0.0
 */
public class FaceDetector
{
    EyesDetector ed;
    SmileDetector sd;
    private final CascadeClassifier faceHaar; // Clasificadores 
    private final CascadeClassifier perfilFaceHaar;
    double scaleFactor; // Cuanto se reduce la imagen en cada escala de imagen
    int minNeighbors; // Cuantos vecinos debe tener cada rectangulo candidato para concervarlo
    int minSize; // Tamaño minimo para la busqueda del objeto
    int flags; // Banderas
    
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
        scaleFactor = 1.1;
        minNeighbors = 4;
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
        String path = "resources/haarcascades/";
        // cargamos los clasificadores cascada
        faceHaar.load(path+"haarcascade_frontalface_alt.xml");
        perfilFaceHaar.load(path+"haarcascade_profileface.xml");
    }
    
    /**
     * Detecta los rostros
     * 
     * @param m Matriz donde reconoceremos los rostros
     */
    public Mat FaceDetect(Mat m)
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
            ed.eyesDetector(m, grayFrame, faces);
        }
        
        // verificamos si hay rostros para buscar sonrisa
        if(!faces.empty()){
            sd.smileDetector(m, grayFrame, faces);
        }
        
        return faceDraw(m, faces);
    }
    
    /**
     * Dibujamos rectangulos al detectar los rostros
     * 
     * @param m Matriz original
     * @param faces Matriz con los rectangulos detectados
     */
    private Mat faceDraw(Mat m, MatOfRect faces)
    {
        // Convertimos la matriz en un array (vector)
        Rect[] facesArray = faces.toArray();
        
        // Recorremos cada uno de los objetos
	for (int i = 0; i < facesArray.length; i++){
            // m <- matriz original
            // facesArray[i].tl() <- punto del objeto en x
            // facesArray[i].br() <- punto del objeto en y
            // new Scalar(0, 255, 0) <- color del rectangulo a dibujar
            // 3 <- Espesor de la linea
            Imgproc.rectangle(m, facesArray[i].tl(), facesArray[i].br(), new Scalar(0, 255, 0), 2);
        }
                
        return m;
    }
    
    /**
     * Calculamos el tamaño minimo para el objeto a rastrear
     * 
     * @param grayFrame Matriz en escala de grises del cual obtendremos el tamaño
     * @param minSize Tamaño minimo para buscar el objeto (0.2F)
     */
    private int calcSize(Mat grayFrame, float minSize)
    {
        int faceSize = 0;
        
        // Obtenemos el alto de la imagen (frame)
        int height = grayFrame.rows();

        // verificamos que el minimo a detectar sea mayor a 0
        if (Math.round(height * minSize) > 0){
            // Asignamos el nuevo tamaño a detectar
            faceSize = Math.round(height * minSize);
        }
        
        return faceSize;
    }
}
