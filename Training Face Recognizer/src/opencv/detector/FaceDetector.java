/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package opencv.detector;

import java.io.File;
import opencv.trainer.BuildEigenFaces;
import opencv.trainer.FileUtils;
import org.opencv.core.Mat;
import org.opencv.core.MatOfRect;
import org.opencv.core.Point;
import org.opencv.core.Rect;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.imgcodecs.Imgcodecs;
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
    LeftEyeDetector led;
    RightEyeDetector red;
    SmileDetector sd;
    private final CascadeClassifier faceHaar; // Clasificadores 
    private final CascadeClassifier perfilFaceHaar;
    private int count;
    private int temporizer;
    private static final String TRAINING_DIR = "resources/temp"; // Directorio de las imagenes de entrenamiento
    
    /**
     * Constructor
     */
    public FaceDetector()
    {
        // Inicializamos las clases externas
        ed = new EyesDetector();
        sd = new SmileDetector();
        led = new LeftEyeDetector();
        red = new RightEyeDetector();
        
        // Inicializamos los clasificadores
        faceHaar = new CascadeClassifier();
        perfilFaceHaar = new CascadeClassifier();
        
        // Inicializamos las propiedades
        scaleFactor = 1.05;
        minNeighbors = 1;
        
        // Inicializamos el control para el entrenamiento
        count = 0;
        temporizer = 20;
        
        // Cargamos los clasificadores
        loadClassifiers();
    }
    
    /**
     * Metodos getter y setter
     */
    public int getCount(){
        return count;
    }
    public void setCount(int n)
    {
        count = n;
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
     * @param ctr Control del sistema de entranamiento [true - false]
     */
    public Mat FaceDetect(Mat m, boolean ctr)
    {
        // Matriz para los objetos detectados (rostros)
        MatOfRect faces = new MatOfRect();
        MatOfRect leftEyes = new MatOfRect();
        MatOfRect rightEyes = new MatOfRect();
        
        // Matriz en escala de grises
        Mat grayFrame = new Mat();
        
        // Convertimos el frame original en escala de grises
        Imgproc.cvtColor(m, grayFrame, Imgproc.COLOR_BGR2GRAY);
        
        // Ecualizamos el histograma del frame para mejorar el resultado
        Imgproc.equalizeHist(grayFrame, grayFrame);
        
        // Calculamos el tamaño minimo del rostro a detectar (20%)
        minSize = calcSize(grayFrame, 0.2F);
        
        /**
         * Aqui comenzamos el proceso de entrenamiento
         * 1.- Frente
         * 2.- Perfil
         * 3.- Ojos abiertos
         * 4.- Sonrisa
         */
        
        // detectamos los rostros de frente (imagen donde detectaremos los objetos,
        // vector de cuadrados para los objetos detectados,
        // cuanto se reduce la imagen en cada escala de imagen,
        // cuantos vecinos debe tener cada rectangulo candidato para concervarlo,
        // flags,
        // tamaño minimo posible del objeto
        // tamaño maximo posible del objeto
        faceHaar.detectMultiScale(grayFrame, faces, scaleFactor, minNeighbors,
                flags, new Size(minSize, minSize), new Size());
        
        // verificamos si estamos en modo entrenamiento
        if(ctr && count == 0){
            // Mostramos texto
            printText(m, "Captura 1: Rostro de frente", new Point(10 , m.rows() - 20));
            // Decrementamos el temporizador
            temporizer--;
            
            // verificamos que solo se detecte un rostro
            if(faces.rows() == 1 && temporizer <= 0){
                // Convertimos la matriz en un array (vector)
                Rect[] facesArray = faces.toArray();
            
                // trabajamos en cada uno de los rostros
                for(Rect rect: facesArray){
                    // Obtenemos el rectangulo donde vamos a trabajar
                    Rect rectCrop = new Rect(rect.x, rect.y , rect.width, rect.height);
                    
                    // Obtenemos la matriz roi
                    Mat roiGray = grayFrame.submat(rectCrop);
                    
                    // Creamos el directorio
                    FileUtils.makeDirectory(TRAINING_DIR); // Crea el directorio, si ya existe elimina su contenido
                    
                    // Almacenamos la imagen
                    FileUtils.saveImg(roiGray, count);
                }
                
                // Actualizamos el contador
                count++;
                // Actualizamos el temporizador
                temporizer = 20;
            }
        }
        
        // verificamos si detecto rostros de frente
        if(faces.empty() || count == 1){
            // Buscamos rostros de perfil
            perfilFaceHaar.detectMultiScale(grayFrame, faces, scaleFactor,
                    minNeighbors, flags, new Size(minSize, minSize), new Size());
            
            if(ctr && count == 1){
                // Mostramos texto
                printText(m, "Captura 2: Rostro de perfil", new Point(10 , m.rows() - 20));
                // Decrementamos el temporizador
                temporizer--;

                // verificamos que solo se detecte un rostro
                if(faces.rows() == 1 && temporizer <= 0){
                    // Convertimos la matriz en un array (vector)
                    Rect[] facesArray = faces.toArray();

                    // trabajamos en cada uno de los rostros
                    for(Rect rect: facesArray){
                        // Obtenemos el rectangulo donde vamos a trabajar
                        Rect rectCrop = new Rect(rect.x, rect.y , rect.width, rect.height);

                        // Obtenemos la matriz roi
                        Mat roiGray = grayFrame.submat(rectCrop);

                        // Almacenamos la imagen
                        FileUtils.saveImg(roiGray, count);
                    }

                    // Actualizamos el contador
                    count++;
                    // Actualizamos temporizador
                    temporizer = 20;
                }
            }
        }
        
        // verificamos si hay rostros para buscar ojos
        if(!faces.empty()){
            // Detectamos los dos ojos
            // ed.eyesDetector(m, grayFrame, faces);
            led.leftEyeDetector(m, grayFrame, faces);
            
            if(ctr && count == 2){
                // Mostramos texto
                printText(m, "Captura 3: Deteccion ojo izquierdo", new Point(10 , m.rows() - 20));
                // Decrementamos el temporizador
                temporizer--;

                // verificamos que solo se detecte un rostro
                System.out.println(faces.rows());
                if(faces.rows() == 1 && temporizer <= 0){
                    // Convertimos la matriz en un array (vector)
                    Rect[] facesArray = faces.toArray();

                    // trabajamos en cada uno de los rostros
                    for(Rect rect: facesArray){
                        // Obtenemos el rectangulo donde vamos a trabajar
                        Rect rectCrop = new Rect(rect.x, rect.y , rect.width, rect.height);

                        // Obtenemos la matriz roi
                        Mat roiGray = grayFrame.submat(rectCrop);

                        // Almacenamos la imagen
                        FileUtils.saveImg(roiGray, count);
                    }

                    // Actualizamos el contador
                    count++;
                    // Actualizamos el temporizador
                    temporizer = 20;
                }
            }
            
            red.rightEyeDetector(m, grayFrame, faces);
            
            if(ctr && count == 3){
                // Mostramos texto
                printText(m, "Captura 3: Deteccion ojo derecho", new Point(10 , m.rows() - 20));
                // Decrementamos el temporizador
                temporizer--;

                // verificamos que solo se detecte un rostro
                if(faces.rows() == 1 && temporizer <= 0){
                    // Convertimos la matriz en un array (vector)
                    Rect[] facesArray = faces.toArray();

                    // trabajamos en cada uno de los rostros
                    for(Rect rect: facesArray){
                        // Obtenemos el rectangulo donde vamos a trabajar
                        Rect rectCrop = new Rect(rect.x, rect.y , rect.width, rect.height);

                        // Obtenemos la matriz roi
                        Mat roiGray = grayFrame.submat(rectCrop);

                        // Almacenamos la imagen
                        FileUtils.saveImg(roiGray, count);
                    }

                    // Actualizamos el contador
                    count++;
                    // Actualizamos el temporizador
                    temporizer = 20;
                }
            }
        }
        
        // verificamos si hay rostros para buscar sonrisa
        if(!faces.empty()){
            // Detectamos las sonrisas
            sd.smileDetector(m, grayFrame, faces);
            
            if(ctr && count == 4){
                // Mostramos texto
                printText(m, "Captura 4: Detección de sonrisa", new Point(10 , m.rows() - 20));
                // Decrementamos el temporizador
                temporizer--;

                // verificamos que solo se detecte un rostro
                if(faces.rows() == 1 && temporizer <= 0){
                    // Convertimos la matriz en un array (vector)
                    Rect[] facesArray = faces.toArray();

                    // trabajamos en cada uno de los rostros
                    for(Rect rect: facesArray){
                        // Obtenemos el rectangulo donde vamos a trabajar
                        Rect rectCrop = new Rect(rect.x, rect.y , rect.width, rect.height);

                        // Obtenemos la matriz roi
                        Mat roiGray = grayFrame.submat(rectCrop);

                        // Almacenamos la imagen
                        FileUtils.saveImg(roiGray, count);
                    }

                    // Actualizamos el contador
                    count++;
                    // Actualizamos el temporizador
                    temporizer = 20;
                }
            }
        }
        
        // Imprimimos texto en pantalla
        if(ctr){
            printText(m, "Training mode", new Point(10, 20));
            
            // verificamos si estamos en modo entrenamiento
            if(count == 5){
                // Mostramos texto
                printText(m, "Ingrese un nombre", new Point(10 , m.rows() - 20));
            }
            // verificamos si estamos en modo entrenamiento
            if(count == 6){
                // Mostramos texto
                printText(m, "Entrenando...", new Point(10 , m.rows() - 20));
            }
        }
        
        // Dibujamos los bordes de los rostros detectados
        return objectDraw(m, faces, new Scalar(0, 255, 0), true);
    }
    
    /**
     * Procesamos las nuevas imagenes de entrenamiento
     * 
     * @param name Nombre para lo rostros detectados
     * 
     * @return 
     */
    public boolean process(String name)
    {
        // Paths para las imagenes
        String pathTemp = "resources/temp";
        String pathTraining = "resources/trainingImages";
        
        // Matriz para almacenar las imagenes
        Mat temp;
        
        // cargamos las imagenes
        for(int i = 0; i < 5; i++){
            // Leemos la imagen
            temp = Imgcodecs.imread(pathTemp+File.separator+"eigen_"+i+".png");
            
            // Las guardamos en el directorio de entrenamiento
            Imgcodecs.imwrite(pathTraining+File.separator+name+i+".png", temp);
        }
        
        // Reeentrenamos
        BuildEigenFaces.build(0);
        
        return true;
    }
}
