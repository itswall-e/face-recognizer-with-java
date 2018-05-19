/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package opencv.recognition;

/**
 *
 * @author Paulo Andrade
 */
public class MatchResult
{
    private String matchName; // Path de la imagen de entrenamiento
    private double matchDist; // distancia de la imagen de netrenamiento

    /**
     * Constructor
     * 
     * @param name Path de la imagen
     * @param dist Distancia de la imagen
     */
    public MatchResult(String name, double dist)
    {
        matchName = name;
        matchDist = dist;
    }
    
    /*
    Metodos getter y setter
    */
    
    public String getMatchFileName()
    {
        return matchName; 
    }
    public void setMatchFileName(String name)
    {
        matchName = name;
    }
    public double getMatchDistance()
    {
        return matchDist;
    }
    public void setMatchDistance(double dist)
    {
        matchDist = dist;
    }

    /**
     * Obtiene el nombre de la imagen quitando el guin bao y numeros
     * ejemplo.-
     * "trainingImages\paulo_0123.png"; return "paulo"
     * 
     * @return Devuelve el nombre asignado a la imagen de entrenamiento
     */
    public String getName()
    {
        // Buscamos el indice de la ultima concurrencia de \
        int slashPos = matchName.lastIndexOf('\\');
        // Buscamos el indice de la ultima concurrencia de .png
        int extPos = matchName.lastIndexOf(".png");
        // Obtenemos el nombre con numeros y caracteres (paulo_0123)
        String name = (slashPos == -1) ? matchName.substring(0, extPos) : 
                                         matchName.substring(slashPos+1, extPos);

        // Removemos numeros y caracteres
        name = name.replaceAll("[-_0-9]*$", "");
    
        // Retornamos el nombre
        return name;
    }
}
