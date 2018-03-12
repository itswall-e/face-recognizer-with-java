/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package opencv;

import java.awt.Color;
import javax.swing.ButtonGroup;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import org.opencv.core.Mat;

/**
 *
 * @author Paulo Andrade
 */
public class ImageViewer
{
    JFrame v;
    JPanel mainPanel, subPanel;
    JLabel lblImg, lblHistogram, lblText, lblButton;
    ButtonGroup btnGroup;
    JRadioButton rColor, rGray;
    String title;
    int width, height;
    ImageUtils util;
    
    /**
     * Constructor
     * 
     * @param title Titulo para la ventana
     */
    public ImageViewer(String title)
    {
        this.title = title; // Titulo del JFrame
        width = 880; // Ancho del JFrame
        height = 500; // Alto del JFrame
        util = new ImageUtils();
        
        // Inicializamos los componentes
        initComponents();
    }
    
    /**
     * Inicializa los componentes del JFrame
     */
    private void initComponents()
    {
        v = new JFrame(title);
        v.setSize(width, height + 20);
        v.setResizable(false);
        v.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        v.setLayout(null);
        
        // Componentes
        mainPanel = new JPanel();
        mainPanel.setBounds(0, 0, 660, 500);
        mainPanel.setBackground(Color.WHITE);
        mainPanel.setLayout(null);
        
        subPanel = new JPanel();
        subPanel.setBounds(660, 0, 220, 500);
        subPanel.setBackground(Color.WHITE);
        subPanel.setLayout(null);
        
        lblImg = new JLabel();
        lblImg.setBounds(10, 10, 640, 480);
        
        lblHistogram = new JLabel();
        lblHistogram.setBounds(10, 40, 200, 180);
        
        lblText = new JLabel("Histograms");
        lblText.setBounds(10, 10, 200, 20);
        
        lblButton = new JLabel("Channels");
        lblButton.setBounds(10, 230, 200, 20);
        
        rColor = new JRadioButton("Color");
        rColor.setBounds(10, 260, 200, 20);
        rColor.setBackground(Color.WHITE);
        rColor.setSelected(true);
        
        rGray = new JRadioButton("Gray");
        rGray.setBounds(10, 290, 200, 20);
        rGray.setBackground(Color.WHITE);
        
        btnGroup = new ButtonGroup();
        btnGroup.add(rColor);
        btnGroup.add(rGray);
        
        // Añadimos componentes
        v.add(mainPanel);
        mainPanel.add(lblImg);
        v.add(subPanel);
        subPanel.add(lblText);
        subPanel.add(lblHistogram);
        subPanel.add(lblButton);
        subPanel.add(rColor);
        subPanel.add(rGray);
        
        // Eventos
        
        // Mostramos JFrame
        v.setVisible(true);
    }
    
    /**
     * Redimenciona el ancho y alto del JFrame
     * 
     * @param width Ancho del JFrame
     * @param height Alto del JFrame
     */
    public void set(int width, int height)
    {
        this.width = width;
        this.height = height;
    }
    
    /**
     * Muestra un frame en pantalla reconociendo las caras
     * 
     * @param m matriz a mostrar en pantalla
     */
    public void show(Mat m)
    {
        // Verificamos si el histograma es en escala de grises
        boolean type = true;
        if(rGray.isSelected()) type = false;
        
        // Verificamos si se muestra en escala de grises
        if(type){
            // Convertimos la matriz en imagen y la mostramos en pantalla
            lblImg.setIcon(new ImageIcon(ImageProcessor.toBufferedImage(m)));
        } else {
            // Convertimos la matriz en imagen y la mostramos en pantalla
            lblImg.setIcon(new ImageIcon(ImageProcessor.toBufferedImage(util.toGrayImage(m))));
        }
        
        // Mostramos el histograma de la imagen
        lblHistogram.setIcon(new ImageIcon(ImageProcessor.toBufferedImage(util.calcHistogram(m, type))));
    }
}
