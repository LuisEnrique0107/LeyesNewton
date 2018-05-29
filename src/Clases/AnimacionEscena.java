package Clases;

import com.jogamp.opengl.awt.GLCanvas;
import java.util.TimerTask;

public class AnimacionEscena extends TimerTask {

    GLCanvas Object;
    
    public AnimacionEscena(GLCanvas p ) {
        Object = p;
    }

    @Override
    public void run() {
        Object.display();
    }

}
