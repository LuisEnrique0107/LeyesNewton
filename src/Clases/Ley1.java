/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Clases;

import InterfazGrafica.PrimeraLey;
import com.jogamp.opengl.GLCapabilities;
import com.jogamp.opengl.GLProfile;
import com.jogamp.opengl.util.FPSAnimator;
import javax.swing.JFrame;

/**
 *
 * @author LuisEnrique
 */
public class Ley1 {
    public void run(){
        GLProfile perfil = GLProfile.getDefault();
        GLCapabilities capacidades = new GLCapabilities(perfil);
        PanelCanvas  panelCanvas = new PanelCanvas(capacidades);
        //Pruebas
        PrimeraLey PrimeraLey = new PrimeraLey();
        PrimeraLey.add(panelCanvas);
        PrimeraLey.setPanelCanvas(panelCanvas);
        //Listener
        NaveSimulator listener = new NaveSimulator(PrimeraLey);
        panelCanvas.addGLEventListener(listener);
        PrimeraLey.addEventos(listener);
        //ventana
        JFrame frame = new JFrame("Leyes de Newton");
         FPSAnimator animador = new FPSAnimator(panelCanvas, 60, true);
        frame.add(panelCanvas);
        frame.setSize(800, 600);
        frame.setLocationRelativeTo(null);
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.setVisible(true);
        animador.start();
    }
}
