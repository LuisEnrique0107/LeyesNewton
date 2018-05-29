 package Clases;

import InterfazGrafica.PrimeraLey;
import com.jogamp.opengl.GL2;
import com.jogamp.opengl.GLAutoDrawable;
import com.jogamp.opengl.GLEventListener;
import static com.jogamp.opengl.fixedfunc.GLLightingFunc.GL_LIGHTING;
import com.jogamp.opengl.glu.GLU;
import com.jogamp.opengl.util.gl2.GLUT;
import com.jogamp.opengl.util.texture.Texture;
import com.jogamp.opengl.util.texture.TextureIO;
import java.awt.Dimension;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.io.File;
import java.io.IOException;

public class NaveSimulator extends MouseAdapter implements GLEventListener, MouseWheelListener, MouseMotionListener, KeyListener {
        PrimeraLey panel;
 
    GL2 gl;
    GLU glu;
    GLUT glut = new GLUT();
    
    private float UbicacionX = 0f;
    private float UbicacionY = 0f;
    private float UbicacionZ = 0f;
    
    private float PocisionCamX = 0;
    private float PocisionCamY =20;
    private float PocisionCamZ = 15.0f;
    private float TraduccionCamZ = 5.0f;
    //Enfoque
    private float EnfoqueX = 0;
    private float EnfoqueY = 7;
    private float EnfoqueZ = 0;
    
    private float GiroX = 0;
    private float GiroY = 0;
    private float GiroZ = 0;
    private float GiroX2 = 0;
    private float GiroY2 = 0;
    
    private int mouseX;
    private int mouseY;
 
    private float x, y;
    
    private Texture lado1, lado2, lado3, lado4, lado5, lado6,Espacio,Espacio2;
    
    //CONDUCCION
    boolean Adelante = false;
    boolean Atras = false;
    boolean NOF=true;
     //Controles
        float velocidad=0; //metros/segundo
        float VelocidadMinima=0.01f;//MinimaEspacio=1m/s
        float VelocidadLimite=10f;//*1000=m/s
        float VelocidadLimiteR=5f;//500m/s 
        float FuerzaCohete=5000f;//5000N
        float FuerzaR=1000f;//1000N
        float MasaNave=500;//500KG
        float  Aceleracion = (FuerzaCohete/MasaNave)/100; // metros / segundo ^ 2--------5000/500=10m/s^2---------Escala de 100(0.1)
        float AceleracionR=(FuerzaR/MasaNave)/100;//1m/s^2
        boolean Reversa;
        //F=MA  M=F/A   A=F/M
    //CONSTRUCTOR
    NaveSimulator(PrimeraLey panel) {
        this.panel = panel;
    }

    @Override
    public void init(GLAutoDrawable drawable) {
        
        try {
        lado1 = TextureIO.newTexture(new File("src/images/nave-atras.png"), true);            
        lado2 = TextureIO.newTexture(new File("src/images/nave-izq.png"), true);            
        lado3 = TextureIO.newTexture(new File("src/images/nave-frente.png"), true);       
        lado4 = TextureIO.newTexture(new File("src/images/nave-der.png"), true);       
        lado5 = TextureIO.newTexture(new File("src/images/debajodelante.png"), true);       
        lado6 = TextureIO.newTexture(new File("src/images/nave-techo.png"), true); 
        Espacio=TextureIO.newTexture(new File("src/images/Espacio1.jpg"), true); 
        Espacio2=TextureIO.newTexture(new File("src/images/Espacio2.jpg"), true); 
        } catch (IOException e) {
            System.err.print("No se puede cargar textura" + e);
            System.exit(1);
        }

        GL2 gl = drawable.getGL().getGL2();
        
        //Suavizado
        gl.glShadeModel(GL2.GL_SMOOTH);
           
//Profundidad
        gl.glEnable(GL2.GL_DEPTH_TEST);
        gl.glDepthFunc(GL2.GL_LESS);
         gl.glClearColor(0f, 0f, 0f, 0f);
    }

    @Override
    public void dispose(GLAutoDrawable drawable) {
    }

    @Override
    public void display(GLAutoDrawable drawable) {
        
        
        gl = drawable.getGL().getGL2();
        gl.glClear(GL2.GL_COLOR_BUFFER_BIT | GL2.GL_DEPTH_BUFFER_BIT);
        gl.glClear(GL2.GL_ACCUM_BUFFER_BIT);
        gl.glTexEnvi(gl.GL_TEXTURE_ENV, gl.GL_TEXTURE_ENV_MODE, gl.GL_DECAL);
        gl.glLoadIdentity();  
        gl.glTranslatef(0, 0, TraduccionCamZ);
        //Camara
        glu.gluLookAt(PocisionCamX, PocisionCamY , PocisionCamZ, //Posicion de la camara
                                  EnfoqueX, EnfoqueY , EnfoqueZ, //Enfoque 
                                  0, 1, 0); //Altura
        //Rotacion Escenario
        gl.glRotatef(GiroX2, 1, 0, 0);
        gl.glRotatef(GiroY2, 0, 1, 0);
        gl.glPointSize(10f);
        Espacio(gl, GiroX, GiroY, GiroZ);
        CrearNave(gl, UbicacionX, UbicacionY, UbicacionZ , GiroX, GiroY, GiroZ);
       velocidad();
        gl.glFlush(); 
    }

    @Override
    public void reshape(GLAutoDrawable drawable, int x, int y, int width, int height) {
        
        GL2 gl = drawable.getGL().getGL2();
        glu = new GLU();

        if (height <= 0)
            height = 1;
        
        final float h = (float) width / (float) height;
       gl.glViewport(0, 0, width, height);
        gl.glMatrixMode(GL2.GL_PROJECTION);
        gl.glLoadIdentity();
        glu.gluPerspective(45.0f, h, 1.0, 100000000.0);
        gl.glMatrixMode(GL2.GL_MODELVIEW);
        gl.glLoadIdentity();
    }
    
    //Rueda del raton
    @Override
    public void mouseWheelMoved(MouseWheelEvent e){
        int raton = e.getWheelRotation();
        switch(raton){
            case -1:
                TraduccionCamZ += 0.5f;
                break;
            case 1:
                TraduccionCamZ -= 0.5f;
                break;
        }
    }
    //Arrastrar con el raton
    @Override
    public void mouseDragged(MouseEvent e){
        
        x = e.getX();
        y = e.getY();
        
        Dimension size = e.getComponent().getSize();
        
        float thetaY = 45.0f * ( (float)( x -mouseX) / (float)size.width);
        float thetaX = 45.0f * ( (float)( mouseY - y) / (float)size.height);

        mouseX = (int)x;
        mouseY = (int)y;

        GiroX2 += thetaX;
        GiroY2 += thetaY;
        panel.getPanelCanvas().display();
    }
    
    @Override
    public void keyPressed(KeyEvent e){
        int keyCode = e.getKeyCode();
        switch(keyCode){
            case KeyEvent.VK_W:
               Adelante=true;
               NOF=false;
                if (velocidad<=VelocidadLimite) {
                    velocidad+=Aceleracion;
                }
                if(velocidad>=VelocidadLimite){
                    velocidad=VelocidadLimite;
                }
                System.out.println("ADELANTE: "+velocidad*36+" Km/H");
                break;
            case KeyEvent.VK_SPACE:
                 Atras=true;
                 NOF=false;
                 if (velocidad<=VelocidadLimite) {
                 velocidad-=AceleracionR;
                }
                 if (velocidad<=-VelocidadLimiteR) {
                 velocidad=-VelocidadLimiteR;
                }
                 System.out.println("ATRAS: "+velocidad*36+" Km/H");
                break;
        }
        
    }
//CREAR ESPACIO
private void Espacio(GL2 gl, float GiroX, float GiroY, float GiroZ) {
float distancia=400f;
float LimiteEspacio=60;
float UltPoint=distancia, PenulPoint=distancia;       
        gl.glPolygonMode(GL2.GL_BACK, GL2.GL_FILL);            
        //Rota 
        gl.glRotatef(GiroX, 1, 0, 0);
        gl.glRotatef(GiroY, 0, 1, 0);
        gl.glRotatef(GiroZ, 0, 0, 1);
        //Caras
///////////////////////////////////////////////////////////////////////////////ATRAS
        Espacio.enable(gl);
        Espacio.bind(gl);
        gl.glBegin(gl.GL_QUADS);                                            

            gl.glTexCoord2f(0, 0);                
            gl.glVertex3f(-distancia*LimiteEspacio, -distancia*LimiteEspacio, distancia*LimiteEspacio);     

            gl.glTexCoord2f(1, 0);
            gl.glVertex3f(distancia*LimiteEspacio, -distancia*LimiteEspacio, distancia*LimiteEspacio);

            gl.glTexCoord2f(1, 1);
            gl.glVertex3f(distancia*LimiteEspacio, distancia*LimiteEspacio, distancia*LimiteEspacio);

            gl.glTexCoord2f(0, 1);
            gl.glVertex3f(-distancia*LimiteEspacio, distancia*LimiteEspacio, distancia*LimiteEspacio);                                                                                                                                                                                                                                    
        gl.glEnd();
        Espacio.disable(gl);
/////////////////////////////////////////////////////////////////////////////////IZQUIERDA
Espacio2.enable(gl);
        Espacio2.bind(gl);
        gl.glBegin(gl.GL_QUADS);
            gl.glTexCoord2f(0, 0);
            gl.glVertex3f(-distancia, -distancia, distancia);

            gl.glTexCoord2f(1, 0);
            gl.glVertex3f(-distancia, -distancia, -distancia);

            gl.glTexCoord2f(1, 1);
            gl.glVertex3f(-distancia, distancia, -distancia);

            gl.glTexCoord2f(0, 1);
            gl.glVertex3f(-distancia, distancia, distancia);                                
        gl.glEnd();
        Espacio2.disable(gl);
for (float i = 1; i <=LimiteEspacio; i++) {
        UltPoint=UltPoint+distancia;
        Espacio.enable(gl);
        Espacio.bind(gl);
        gl.glBegin(gl.GL_QUADS);
            gl.glTexCoord2f(0, 0);
            gl.glVertex3f(-distancia, -distancia, -PenulPoint);
            gl.glTexCoord2f(1, 0);
           gl.glVertex3f(-distancia, -distancia, -UltPoint);
            gl.glTexCoord2f(1, 1);
            gl.glVertex3f(-distancia, distancia, -UltPoint);
            gl.glTexCoord2f(0, 1);
            gl.glVertex3f(-distancia, distancia, -PenulPoint);                                
        gl.glEnd();
        Espacio.disable(gl);
        PenulPoint=PenulPoint+distancia;
        }
        UltPoint=distancia;
       PenulPoint=distancia;
//////////////////////////////////////////////////////////////////////////////////////DERECHA
        Espacio2.enable(gl);
        Espacio2.bind(gl);
        gl.glBegin(gl.GL_QUADS);                
            gl.glTexCoord2f(0, 0);
            gl.glVertex3f(distancia, -distancia, distancia);

            gl.glTexCoord2f(1, 0);
            gl.glVertex3f(distancia, -distancia, -distancia);

            gl.glTexCoord2f(1, 1);
            gl.glVertex3f(distancia, distancia, -distancia);

            gl.glTexCoord2f(0, 1);
            gl.glVertex3f(distancia, distancia, distancia); 
        gl.glEnd();
        Espacio2.disable(gl);
        for (float i = 1; i <=LimiteEspacio; i++) {
        UltPoint=UltPoint+distancia;
        Espacio.enable(gl);
        Espacio.bind(gl);
        gl.glBegin(gl.GL_QUADS);
            gl.glTexCoord2f(0, 0);
            gl.glVertex3f(distancia, -distancia, -PenulPoint);
            gl.glTexCoord2f(1, 0);
           gl.glVertex3f(distancia, -distancia, -UltPoint);
            gl.glTexCoord2f(1, 1);
            gl.glVertex3f(distancia, distancia, -UltPoint);
            gl.glTexCoord2f(0, 1);
            gl.glVertex3f(distancia, distancia, -PenulPoint);
        gl.glEnd();
        Espacio.disable(gl);
        PenulPoint=PenulPoint+distancia;
        }
       UltPoint=distancia;
       PenulPoint=distancia;
///////////////////////////////////////////////////////////////FRENTE
Espacio2.enable(gl);
        Espacio2.bind(gl);
        gl.glBegin(gl.GL_QUADS);                                            

            gl.glTexCoord2f(0, 0);                
            gl.glVertex3f(-distancia, -distancia, -distancia*LimiteEspacio);     

            gl.glTexCoord2f(1, 0);
            gl.glVertex3f(distancia, -distancia,- distancia*LimiteEspacio);

            gl.glTexCoord2f(1, 1);
            gl.glVertex3f(distancia, distancia,- distancia*LimiteEspacio);

            gl.glTexCoord2f(0, 1);
            gl.glVertex3f(-distancia, distancia, -distancia*LimiteEspacio);                                                                                                                                                                                                                                    
        gl.glEnd();
        Espacio2.disable(gl);

//////////////////////////////////////////////////////////////////ARRIBA
        Espacio.enable(gl);
        Espacio.bind(gl);
        gl.glBegin(gl.GL_QUADS);
            gl.glTexCoord2f(0, 0);
            gl.glVertex3f(-distancia, distancia, distancia);

            gl.glTexCoord2f(1, 0);
            gl.glVertex3f(-distancia, distancia, -distancia);

            gl.glTexCoord2f(1, 1);
            gl.glVertex3f(distancia, distancia, -distancia);

            gl.glTexCoord2f(0, 1);
            gl.glVertex3f(distancia, distancia, distancia); 
        gl.glEnd();
        Espacio.disable(gl);
               for (float i = 1; i <=LimiteEspacio; i++) {
        UltPoint=UltPoint+distancia;
        Espacio.enable(gl);
        Espacio.bind(gl);
        gl.glBegin(gl.GL_QUADS);
            gl.glTexCoord2f(0, 0);
            gl.glVertex3f(-distancia, distancia,- PenulPoint);
            gl.glTexCoord2f(1, 0);
           gl.glVertex3f(-distancia, distancia, -UltPoint);
            gl.glTexCoord2f(1, 1);
            gl.glVertex3f(distancia, distancia, -UltPoint);
            gl.glTexCoord2f(0, 1);
            gl.glVertex3f(distancia, distancia, -PenulPoint);
        gl.glEnd();
        Espacio.disable(gl);
        PenulPoint=PenulPoint+distancia;
        }
UltPoint=distancia;
PenulPoint=distancia;
  /////////////////////////////////////////////////ABAJO
        Espacio.enable(gl);
        Espacio.bind(gl);
        gl.glBegin(gl.GL_QUADS);
            gl.glTexCoord2f(0, 0);
            gl.glVertex3f(-distancia, -distancia, distancia);

            gl.glTexCoord2f(1, 0);
            gl.glVertex3f(distancia, -distancia, distancia);

            gl.glTexCoord2f(1, 1);
            gl.glVertex3f(distancia, -distancia, -distancia);

            gl.glTexCoord2f(0, 1);
            gl.glVertex3f(-distancia, -distancia, -distancia); 
        gl.glEnd();
        Espacio.disable(gl);
        
         for (float i = 1; i <=LimiteEspacio; i++) {
        UltPoint=UltPoint+distancia;
        Espacio.enable(gl);
        Espacio.bind(gl);
        gl.glBegin(gl.GL_QUADS);
            gl.glTexCoord2f(0, 0);
            gl.glVertex3f(-distancia, -distancia,- PenulPoint);
            gl.glTexCoord2f(1, 0);
           gl.glVertex3f(distancia,- distancia, -PenulPoint);
            gl.glTexCoord2f(1, 1);
            gl.glVertex3f(distancia, -distancia, -UltPoint);
            gl.glTexCoord2f(0, 1);
            gl.glVertex3f(-distancia, -distancia, -UltPoint);
        gl.glEnd();
        Espacio.disable(gl);
        PenulPoint=PenulPoint+distancia;
        }
    }
    
    private void CrearNave(GL2 gl, float UbicacionX, float UbicacionY, float UbicacionZ, float GiroX, float GiroY, float GiroZ)         {

        gl.glPolygonMode(GL2.GL_FRONT, GL2.GL_FILL);
        //Traslada             
        gl.glTranslatef(UbicacionX, UbicacionY, UbicacionZ);
        //Rota 
        gl.glRotatef(GiroX, 1, 0, 0);
        gl.glRotatef(GiroY, 0, 1, 0);
        gl.glRotatef(GiroZ, 0, 0, 1);
        //Cara 1 A
        lado1.enable(gl);
        gl.glEnable(GL_LIGHTING);
        lado1.bind(gl);
        gl.glBegin(gl.GL_QUADS);                                            

            gl.glTexCoord2f(0, 0);                
            gl.glVertex3f(-2f, 0, 2f);     

            gl.glTexCoord2f(1, 0);
            gl.glVertex3f(2f, 0, 2f);

            gl.glTexCoord2f(1, 1);
            gl.glVertex3f(2f, 4f, 2f);

            gl.glTexCoord2f(0, 1);
            gl.glVertex3f(-2f, 4f, 2f);                                                                                                                                                                                                                                    
        gl.glEnd();
        lado1.disable(gl);

        lado2.enable(gl);
        lado2.bind(gl);
        //Cara 2    D
        gl.glBegin(gl.GL_QUADS);
            gl.glTexCoord2f(0, 0);
            gl.glVertex3f(2f, 0, 2f);

            gl.glTexCoord2f(1, 0);
            gl.glVertex3f(2f, 0, -6f);

            gl.glTexCoord2f(1, 1);
            gl.glVertex3f(2f, 4f, -6f);

            gl.glTexCoord2f(0, 1);
            gl.glVertex3f(2f, 4f, 2f);                                
        gl.glEnd();
        lado2.disable(gl);

        lado3.enable(gl);
        lado3.bind(gl);
        //Cara 3    F
        gl.glBegin(gl.GL_QUADS);                
            gl.glTexCoord2f(0, 0);
            gl.glVertex3f(2f, 0, -6f);

            gl.glTexCoord2f(0, 1);
            gl.glVertex3f(2f, 4f, -6f);

            gl.glTexCoord2f(1, 1);
            gl.glVertex3f(-2f, 4f,-6f);                

            gl.glTexCoord2f(1, 0);
            gl.glVertex3f(-2f, 0, -6f);
        gl.glEnd();
        lado3.disable(gl);

        //Cara 4  I
        lado4.enable(gl);
        lado4.bind(gl);
        gl.glBegin(gl.GL_QUADS);                
            gl.glTexCoord2f(0, 0);
            gl.glVertex3f(-2f, 0, -6f);                               

            gl.glTexCoord2f(0, 1);
            gl.glVertex3f(-2f, 4f, -6f);                

            gl.glTexCoord2f(1, 1);
            gl.glVertex3f(-2f, 4f, 2f);                                                

            gl.glTexCoord2f(1, 0);
            gl.glVertex3f(-2f, 0, 2f);
        gl.glEnd();
        lado4.disable(gl);

        //Cara 5  AB
        lado5.enable(gl);
        lado5.bind(gl);
        gl.glBegin(gl.GL_QUADS);
            gl.glTexCoord2f(0, 0);
            gl.glVertex3f(2f, 0.5f, 2f);

            gl.glTexCoord2f(0, 1);
            gl.glVertex3f(2f, 0.5f, -6f);

            gl.glTexCoord2f(1, 1);
            gl.glVertex3f(-2f, 0.5f,-6f);

            gl.glTexCoord2f(1, 0);
            gl.glVertex3f(-2f, 0.5f, 2f);                                                
        gl.glEnd();
        lado5.disable(gl);

        //Cara 6  T
        lado6.enable(gl);
        lado6.bind(gl);
        gl.glBegin(gl.GL_QUADS);
            gl.glTexCoord2f(0, 0);
            gl.glVertex3f(-2f, 4f, 2f);

            gl.glTexCoord2f(0, 1);
            gl.glVertex3f(-2f, 4f, -6f);

            gl.glTexCoord2f(1, 1);
            gl.glVertex3f(2f, 4f, -6f);

            gl.glTexCoord2f(1, 0);
            gl.glVertex3f(2f, 4f, 2f);                                

        gl.glEnd();
        lado6.disable(gl);
                
//-----------------------------------------------------------------------------        
        //TRIANGULO DE ARRIBA
        gl.glBegin(gl.GL_TRIANGLES); 
            gl.glColor3f(0, 0, 0);
            gl.glVertex3f(-5, 2, 1);
            gl.glColor3f(0, 0, 0);
            gl.glVertex3f(5, 2, 1);
            gl.glColor3f(0, 0, 0);
            gl.glVertex3f(0, 2, -4);
        gl.glEnd();

        //TRIANGULO DE ABAJO
        gl.glBegin(gl.GL_TRIANGLES); 
            gl.glColor3f(0, 0, 0);
            gl.glVertex3f(-5, 2f, 1);
            gl.glColor3f(0, 0, 0);
            gl.glVertex3f(5, 2f, 1);
            gl.glColor3f(0, 0, 0);
            gl.glVertex3f(0, 1.5f, -4);
        gl.glEnd();
 
        gl.glBegin(gl.GL_QUADS); 
            gl.glColor3f(0, 0, 0);  
            gl.glTexCoord2f(0, 0);
            gl.glVertex3f(5, 2f, 1);

            gl.glTexCoord2f(1, 0);
            gl.glVertex3f(5, 2, 1);

            gl.glTexCoord2f(1, 1);
            gl.glVertex3f(0, 2, -4);

            gl.glTexCoord2f(0, 1);
            gl.glVertex3f(0, 1.5f, -4);
        gl.glEnd();

        gl.glBegin(gl.GL_QUADS); 
            gl.glColor3f(0, 0, 0);  
            gl.glTexCoord2f(0, 0);
            gl.glVertex3f(-5, 2, 1);

            gl.glTexCoord2f(1, 0);
            gl.glVertex3f(-5, 2, 1);

            gl.glTexCoord2f(1, 1);
            gl.glVertex3f(0, 2, -4);

            gl.glTexCoord2f(0, 1);
            gl.glVertex3f(0, 1.5f, -4);
        gl.glEnd();

        gl.glBegin(gl.GL_QUADS); 
            gl.glColor3f(0, 0, 0); 
            gl.glTexCoord2f(0, 0);
            gl.glVertex3f(-5, 2f, 1);

            gl.glTexCoord2f(1, 0);
            gl.glVertex3f(-5, 2, 1);

            gl.glTexCoord2f(1, 1);
            gl.glVertex3f(0, 2, 1);

            gl.glTexCoord2f(0, 1);
            gl.glVertex3f(0, 1.5f, 1);
        gl.glEnd();

        gl.glBegin(gl.GL_QUADS); 
            gl.glColor3f(0, 0, 0);  
            gl.glTexCoord2f(0, 0);
            gl.glVertex3f(0, 1.5f, 1);

            gl.glTexCoord2f(1, 0);
            gl.glVertex3f(0, 2, 1);

            gl.glTexCoord2f(1, 1);
            gl.glVertex3f(5, 2, 1);

            gl.glTexCoord2f(0, 1);
            gl.glVertex3f(5, 2, 1);
        gl.glEnd();

//-----------------------------------------------------------------------------

    }
    
    //Posicion Mouse
    @Override
    public void mousePressed(MouseEvent e){
        mouseX = e.getX();
        mouseY = e.getY();
    }

    @Override
    public void keyTyped(KeyEvent e) {
    }

    @Override
    public void keyReleased(KeyEvent e) {
         int keyCode = e.getKeyCode();
        switch(keyCode){
            case KeyEvent.VK_W:
               Adelante=false;
                System.out.println("Suelto Acelerador");
               break;
            case KeyEvent.VK_SPACE:
                 Atras=false;
                 System.out.println("Elimino Fuerza negativa");
                break;
        }
    }
    
public float velocidad(){
    //SIN EXISTENCIA DE FUERZAS
        if (velocidad<=VelocidadMinima && NOF==true ) {
            UbicacionZ -=VelocidadMinima;
//             PocisionCamZ = (UbicacionZ + 30);
//             EnfoqueZ-=VelocidadMinima;
          //   System.out.println("Sin acelerar la velocidad es: "+velocidad);
        }
    //ACELERANDO SIN FUERZAS HACIA ATRAS
            if (velocidad<VelocidadLimite && velocidad!=0 && NOF==false && Adelante==true && Atras==false) {
            UbicacionZ -=velocidad; 
            PocisionCamZ = (UbicacionZ + 30);
             EnfoqueZ-=velocidad;
           // System.out.println("Acelerando la velocidad es: "+velocidad);
         }
      //CONSERVANDO MOMENTUM
      if (velocidad<VelocidadLimite && velocidad!=0 && NOF==false && Adelante==false && Atras==false) {
            UbicacionZ -=velocidad; 
            PocisionCamZ = (UbicacionZ + 30);
             EnfoqueZ-=velocidad;
             EnfoqueY=6;
           // System.out.println("Acelerando la velocidad es: "+velocidad);
         }
      //LLEGANDO AL LIMITE DE VELOCIDAD ACELERANDO
            if (velocidad>=VelocidadLimite && NOF==false && Adelante==true  && Atras==false) {
             UbicacionZ -=VelocidadLimite; 
             PocisionCamZ = (UbicacionZ + 30);
             EnfoqueZ-=VelocidadLimite;
             EnfoqueY=6;
           //  System.out.println("La velocidad Máxima es: "+velocidad);
         }
        //CONSERVANDO MOMENTUM A MAXIMA VELOCIDAD
             if (velocidad>=VelocidadLimite && NOF==false && Adelante==false  && Atras==false) {
             UbicacionZ -=VelocidadLimite; 
             PocisionCamZ = (UbicacionZ + 30);
             EnfoqueZ-=VelocidadLimite;
             EnfoqueY=6;
           //  System.out.println("La velocidad Máxima es: "+velocidad);
         }
         //GENERANDO FUERZA CONTRARIA SIN LLEGAR A SU VELOCIDAD LIMITE
            if (velocidad<VelocidadLimite && velocidad!=0 && NOF==false && Adelante==false  && Atras==true) {
            UbicacionZ -=velocidad; 
            PocisionCamZ = (UbicacionZ + 30);
             EnfoqueZ-=velocidad;
             EnfoqueY=6;
           // System.out.println("Acelerando la velocidad es: "+velocidad);
         }
           //GENERANDO FUERZA CONTRARIA LLEGANDO A SU VELOCIDAD LIMITE
            if (velocidad>=VelocidadLimite && velocidad!=0 && NOF==false && Adelante==false  && Atras==true) {
            UbicacionZ -=velocidad; 
            PocisionCamZ = (UbicacionZ + 30);
             EnfoqueZ-=velocidad;
             EnfoqueY=6;
           // System.out.println("Acelerando la velocidad es: "+velocidad);
         }
            //MULTITECLAS
            //ACELERANDO Y GENERANDO FUERZA CONTRARIA
            if (velocidad<VelocidadLimite && velocidad!=0 && NOF==false && Adelante==true && Atras==true) {
            UbicacionZ -=velocidad; 
            PocisionCamZ = (UbicacionZ + 30);
             EnfoqueZ-=velocidad;
           // System.out.println("Acelerando la velocidad es: "+velocidad);
         }
          //LLEGANDO AL LIMITE DE VELOCIDAD ACELERANDO Y GENERANDO FUERZA CONTRARIA
            if (velocidad>=VelocidadLimite && NOF==false && Adelante==true  && Atras==true) {
             UbicacionZ -=VelocidadLimite; 
             PocisionCamZ = (UbicacionZ + 30);
             EnfoqueZ-=VelocidadLimite;
             EnfoqueY=6;
           //  System.out.println("La velocidad Máxima es: "+velocidad);
         }
        return velocidad;
    }

}
