/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package opencv;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Collections;
import javax.imageio.ImageIO;

/**
 *
 * @author Paulo Andrade
 * 
 * Utilidades para manipulacion de archivos y directorios
 * 
 * FileUtils.java
 * Sajan Joseph, sajanjoseph@gmail.com
 * http://code.google.com/p/javafaces/
 * Modified by Paulo Andrade, March 2018
 */
public class FileUtils
{
    private static final String FILE_EXT = ".png"; // Extension de imagenes
    private static final String TRAINING_DIR = "resources/trainingImages"; // Directorio de las imagenes de entrenamiento
    private static final String EF_CACHE = "resources/eigen.cache"; // Path para la cache de eigen faces
    private static final String EIGENFACES_DIR = "resources/eigenfaces"; // Directorio de imagenes creadas por eigenfaces
    private static final String EIGENFACES_PREFIX = "eigen_"; // Prefijo para las imagenes creadas por eigenfaces
    private static final String RECON_DIR = "resources/reconstructed";
    private static final String RECON_PREFIX = "recon_";
    
    /**
     * Obtenemos los nombres de las imagenes de entrenamiento
     * y sus rutas
     * 
     * @return Devuelve un array con las rutas y nombres de las imagenes de entrenamiento
     */
    public static ArrayList<String> getTrainingFnms()
    {
        // Creamos una instancia con la ruta abstracta a las imagenes de entrenamiento
        File dir = new File(TRAINING_DIR);
        
        // Obtenemos los nombres de las imagenes de entrenamiento con la extension PNG
        String[] names = dir.list((File f, String name) -> name.endsWith(FILE_EXT));

        // Verificamos si existe el directorio
        if(names == null){
            // Si no se encontraron devolvemos mensaje de error
            System.out.println(TRAINING_DIR + " not found");
            return null;
        } else if (names.length == 0){
            // Verificamos si no encontro imagenes
            System.out.println(TRAINING_DIR + " contains no " + " " + FILE_EXT + " files");
            return null;
        } else {
            // Si encontro imagenes, añadimos sus rutas
            return getPathNms(names);
        }
    }
    
    /**
     * Añadimos las rutas a los nombres de las imagenes de entrenamiento
     * 
     * @param names Array con los nombres de imagenes de entrenamiento
     * 
     * @return Devolvemos un array con las rutas + nombres de las imagenes
     */
    private static ArrayList<String> getPathNms(String[] names)
    {
        // Creamos un array para almacenar las rutas + nombre imagen
        ArrayList<String> paths = new ArrayList<>();
        
        // Recorremos el array de nombres
        for(String name : names){
            // Añadimos al array ruta + nombre
            paths.add(TRAINING_DIR + File.separator + name);
        }

        // Ordenamos las rutas
        Collections.sort(paths);
        
        return paths;
    }
    
    /**
     * Cargamos todas las imagenes en escala de grises
     * 
     * @param namesImg Array con las rutas de las imagenes
     * 
     * @return Devuelve un array con los datos de las imagenes
     */
    public static BufferedImage[] loadTrainingIms(ArrayList<String> namesImg)
    {
        // Creamos un vector para almacenar las imagenes
        BufferedImage[] imgs = new BufferedImage[namesImg.size()];
        
        // Creamos una instancia para almacenar una imagen temporalmente
        BufferedImage img = null;
        
        int i = 0; // Contador de imagenes cargadas
        System.out.println("Loading grayscale images from " + TRAINING_DIR + "...");
        
        // Recorremos el array de rutas de imagen
        for(String name : namesImg){
            try{
                // Cargamos la imagen
                img = ImageIO.read(new File(name));
                System.out.println("  " + name);  // imagenes leidas
                // Añadimos la imagen al array de imagenes en escala de grises
                imgs[i++] = ImageUtils.toScaledGray(img, 1.0);
            } catch (Exception e) {
                System.out.println("Could not read image from " + name);
            }
        }
        System.out.println("Loading done\n");

        //
        ImageUtils.checkImSizes(namesImg, imgs);
    
        return imgs;
    }
   
    /**
     * Guardamos cada fila de los eigen faces como una imagen en el directorio
     * EIGENFACES_DIR, donde el ancho de pixeles es el ancho de la imagen
     * 
     * @param egfaces Matriz de eigen faces
     * @param imgWidth 
     */
    public static void saveEFIms(Matrix2D egfaces, int imgWidth)
    {
        // Convertimos la matriz en un array bidimensional
        double[][] egFacesArr = egfaces.toArray();
        
        // Creamos el directorio
        makeDirectory(EIGENFACES_DIR);

        // Recorresmo las filas de los eigenfaces
        for(int row = 0; row < egFacesArr.length; row++){
            // Creamos la ruta para almacenar la imagen
            String path = EIGENFACES_DIR + File.separator + EIGENFACES_PREFIX + row + FILE_EXT;
            // Almacenamos la imagen
            saveArrAsImage(path, egFacesArr[row], imgWidth);
        }
    }
   
    /**
     * Crea un nuevo directorio, si este ya existe, elimina su contenido
     * 
     * @param dir Ruta del directorio a crear o limpiar
     */
    private static void makeDirectory(String dir)
    {
        // Creamos una instancia con la ruta abstracta al directorio
        File dirF = new File(dir);
        
        // Verificamos si existe el directorio
        if(dirF.isDirectory()){
            System.out.println("Directory: " + dir + " already exists; deleting its contents");
            // Recorremos los directorios (en caso de existir mas de uno)
            for(File f : dirF.listFiles()){
                // Eliminamos los archivos del directorio
                deleteFile(f);
            }
        } else {
            // Creamos el directorio
            dirF.mkdir();
            System.out.println("Created new directory: " + dir);
        }
    }
    
    /**
     * Elimina los archivos de un directorio
     * 
     * @param f Archivo a eliminar
     */
    private static void deleteFile(File f)
    {
        // Verificamos si existe el archivo
        if(f.isFile()){
            // Eliminamos el archivo
            if(f.delete()){
                System.out.println("  deleted: "+ f.getName() );
            }
        }
    }
   
    /**
     * Almacena un array como una imagen
     * 
     * @param path Ruta de la imagen a almacenar
     * @param imgData array de datos de la imagen
     * @param width Ancho de la imagen
     */
    private static void saveArrAsImage(String path, double[] imgData, int width)
    {
        // Creamos la imagen
        BufferedImage img = ImageUtils.createImFromArr(imgData, width);
        
        // Verificamos que la imagen no sea null
        if(img != null){
            try{
                // Guardamos la imagen
                ImageIO.write(img, "png", new File(path));
                System.out.println("  " + path);
            } catch (IOException e) {
                System.out.println("Could not save image to " + path);
            }
        }
    }
   
    /**
     * Almacena en cache el objeto faceBundle
     * 
     * @param bundle Objeto FaceBundle a almacenar
     */
    public static void writeCache(FaceBundle bundle)
    {
        System.out.println("Saving eigenfaces to: " + EF_CACHE + " ...");
        try{
            // Creamos un stream para la escritura del objeto
            ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(EF_CACHE));
            
            // Almacenamos el objeto
            oos.writeObject(bundle);
            System.out.println("Cache save succeeded");
            
            //
            oos.close();
        } catch (IOException e) {
            System.out.println("Cache save failed");
            System.out.println(e);
        }
    }
   
    /**
     * Reconstruye imagenes a partir de una matriz de datos
     * 
     * @param imgs Array de las datos a reconstruir
     * @param imgWidth Ancho de la imagen a crear
     * 
     */
    public static void saveReconIms2(double[][] imgs, int imgWidth)
    {
        // Creamos el directorio
        makeDirectory(RECON_DIR);
        
        // Recorremos la matriz de datos
        for(int i = 0; i < imgs.length; i++){
            // Creamos las rutas de las imagenes
            String fnm = RECON_DIR + File.separator + RECON_PREFIX + i + FILE_EXT;
            
            // Creamos las imagenes
            saveArrAsImage(fnm, imgs[i], imgWidth);
        }
    }
}
