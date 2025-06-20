package cg.assignment;

import com.jogamp.opengl.*;
import com.jogamp.opengl.awt.GLCanvas;
import com.jogamp.opengl.glu.GLU;
import com.jogamp.opengl.util.gl2.GLUT;
import com.jogamp.opengl.util.FPSAnimator;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import com.jogamp.opengl.util.Animator;
import com.jogamp.opengl.glu.GLUquadric;
import com.jogamp.opengl.util.texture.*;

import javax.swing.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.io.IOException;
import java.io.InputStream;


import javax.swing.*;
import java.awt.event.*;
import java.util.HashMap;
import java.util.Map;

public class CGAssignment implements GLEventListener, KeyListener, MouseListener, MouseMotionListener {

    private float camX = 0.0f, camY = 0.0f, camZ = 5.0f; // camera position
    private float rotY = 0.0f; // rotation angle
    private float rotX = 0.0f; // rotation on X-axis
    private float lastMouseX, lastMouseY;
    private boolean dragging = false;
    private float bladeRotation = 0f;  // Angle in degrees
    private Texture grassTexture; // For grass
   
  
   
    
    //---------------Pine Cone---------------------------------------
    private final GLU glu = new GLU();
    private float angleX = 20;
    private float angleY = 30;
    private float zoom = -5.0f;
    public int prevMouseX, prevMouseY;

    // Store pinecone positions
    private List<PineCone> pineCones = new ArrayList<>();

    private class PineCone {
        double x, z;

        public PineCone(double x, double z) {
            this.x = x;
            this.z = z;
        }
    }
    //-----------------------HayStick------------------------
    

    private List<HayStick> haySticks = new ArrayList<>();
    
    
    private class HayStick {
        double x, y, z;
        double dx, dy;

        public HayStick(double x, double y, double z, double dx, double dy) {
            this.x = x;
            this.y = y;
            this.z = z;
            this.dx = dx;
            this.dy = dy;
        }
    }
    
    //----------------------------pig------------------------------
        private GLUT glut = new GLUT();
      Texture pig_body_texture,pig_leg_texture;
    
    //----------------------------pond------------------------------ 
      
       Texture water_texture, rock_texture;
       private final float[] LIGHT_BROWN = {0.6f, 0.5f, 0.12f};
       private final float[] DARK_GREEN = {0.23f, 0.38f, 0.03f};
       private final float[] GREY = {0.4f, 0.4f, 0.4f};
       private final float[] BLUE = {0.0f, 0.2f, 0.4f};
       
      //----------------------------scarecrow-----------------------
       
       Texture body_texture, straw_texture;
 
       //---------------------------clouds-----------------------
       
     private float[] cloudOffsets = {
    -10f, -5f, 0f, 5f, 10f, 
     12f, -12f, 7f, -7f, 3f
};
       private float[] cloudSpeeds = {
       0.01f, -0.008f, 0.012f, -0.007f, 0.009f,
       -0.01f, 0.011f, -0.009f, 0.008f, -0.006f
        };
     
    
    public static void main(String[] args) {
        JFrame frame = new JFrame("Red Cube Scene - JOGL");
        GLProfile profile = GLProfile.get(GLProfile.GL2);
        GLCapabilities caps = new GLCapabilities(profile);
        GLCanvas canvas = new GLCanvas(caps);
        
        

        CGAssignment renderer = new CGAssignment();
        canvas.addGLEventListener(renderer);
        canvas.addKeyListener(renderer);
        canvas.addMouseListener(renderer);
        canvas.addMouseMotionListener(renderer);

// frameeeee 
        frame.getContentPane().add(canvas);
        frame.setSize(800, 600);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);
        canvas.requestFocus();

        FPSAnimator animator = new FPSAnimator(canvas, 60);
        animator.start();
        
        
        
    }

    @Override
    public void init(GLAutoDrawable drawable) {
        GL2 gl = drawable.getGL().getGL2();
        gl.glEnable(GL.GL_DEPTH_TEST); // Enable depth test
        gl.glClearColor(0.5f, 0.8f, 1f, 1f); // Sky blue background
        
        try{
            InputStream pig_input1 = getClass().getResourceAsStream("/pig.jpg");
            InputStream pig_input2 = getClass().getResourceAsStream("/pigleg.jpg");
            InputStream grass_input = getClass().getResourceAsStream("/grasstexture.jpg"); // New grass texture
            InputStream waterInput = getClass().getResourceAsStream("/water.jpg"); 
            InputStream rockInput = getClass().getResourceAsStream("/rock.jpg");
            InputStream body_input = getClass().getResourceAsStream("/body.jpg");
            InputStream straw_input = getClass().getResourceAsStream("/straw.jpg");
           
             
            if (pig_input1 != null && pig_input2 != null) {
                pig_body_texture = TextureIO.newTexture(pig_input1, true, "jpg");
                pig_body_texture.setTexParameteri(gl, GL2.GL_TEXTURE_WRAP_S, GL2.GL_REPEAT);
                pig_body_texture.setTexParameteri(gl, GL2.GL_TEXTURE_WRAP_T, GL2.GL_REPEAT);
                
                pig_leg_texture = TextureIO.newTexture(pig_input2, true, "jpg");
                pig_leg_texture.setTexParameteri(gl, GL2.GL_TEXTURE_WRAP_S, GL2.GL_REPEAT);
                pig_leg_texture.setTexParameteri(gl, GL2.GL_TEXTURE_WRAP_T, GL2.GL_REPEAT);
                pig_leg_texture.setTexParameteri(gl, GL2.GL_TEXTURE_MIN_FILTER, GL2.GL_LINEAR);
                pig_leg_texture.setTexParameteri(gl, GL2.GL_TEXTURE_MAG_FILTER, GL2.GL_LINEAR);
            } else System.out.println("Texture not found");
            
            // Grass texture (new)
        if (grass_input != null) {
            grassTexture = TextureIO.newTexture(grass_input, true, "jpg");
            grassTexture.setTexParameteri(gl, GL2.GL_TEXTURE_WRAP_S, GL2.GL_REPEAT);
            grassTexture.setTexParameteri(gl, GL2.GL_TEXTURE_WRAP_T, GL2.GL_REPEAT);
            grassTexture.setTexParameteri(gl, GL2.GL_TEXTURE_MIN_FILTER, GL2.GL_LINEAR_MIPMAP_LINEAR);
            grassTexture.setTexParameteri(gl, GL2.GL_TEXTURE_MAG_FILTER, GL2.GL_LINEAR);
            gl.glGenerateMipmap(GL2.GL_TEXTURE_2D); // For better quality at different distances
        } else {
            System.out.println("Grass texture not found - using procedural grass");
        }
        
         if(waterInput != null && rockInput != null){
                water_texture = TextureIO.newTexture(waterInput, true, "jpg");
                water_texture.setTexParameteri(gl, GL2.GL_TEXTURE_WRAP_S, GL2.GL_REPEAT);
                water_texture.setTexParameterf(gl, GL2.GL_TEXTURE_WRAP_T, GL2.GL_REPEAT);
                
                rock_texture = TextureIO.newTexture(rockInput, true, "jpg");
                rock_texture.setTexParameteri(gl, GL2.GL_TEXTURE_WRAP_S, GL2.GL_REPEAT);
                rock_texture.setTexParameteri(gl, GL2.GL_TEXTURE_WRAP_T, GL2.GL_REPEAT);
                
            } else {
                System.out.println("Texture not found");
            }
         
          if (body_input != null && straw_input != null) {
                
                body_texture = TextureIO.newTexture(body_input, true, "jpg");
                body_texture.setTexParameteri(gl, GL2.GL_TEXTURE_WRAP_S, GL2.GL_REPEAT);
                body_texture.setTexParameteri(gl, GL2.GL_TEXTURE_WRAP_T, GL2.GL_REPEAT);
                
                straw_texture = TextureIO.newTexture(straw_input, true, "jpg");
                straw_texture.setTexParameteri(gl, GL2.GL_TEXTURE_WRAP_S, GL2.GL_REPEAT);
                straw_texture.setTexParameteri(gl, GL2.GL_TEXTURE_WRAP_T, GL2.GL_REPEAT);
                
            }  else System.out.println("Texture not found");
          
         
            
        } catch(IOException e){
            e.printStackTrace();
            pig_body_texture = null;
            pig_leg_texture = null;
            grassTexture = null;
            water_texture = null;
            rock_texture = null;
            body_texture = null;
            straw_texture = null;
            
        }
        
        gl.glEnable(GL2.GL_DEPTH_TEST);
        //Blending ---------------------------------------------------------------
    gl.glEnable(GL.GL_BLEND);
gl.glBlendFunc(GL.GL_SRC_ALPHA, GL.GL_ONE_MINUS_SRC_ALPHA);
  //Blending ---------------------------------------------------------------
//        gl.glEnable(GL2.GL_LIGHTING);
//        gl.glEnable(GL2.GL_LIGHT0);
        gl.glEnable(GL2.GL_NORMALIZE);
         // Optional: For better texture quality
    gl.glHint(GL2.GL_PERSPECTIVE_CORRECTION_HINT, GL2.GL_NICEST);
    
  
    }
    

    @Override
public void display(GLAutoDrawable drawable) {
    GL2 gl = drawable.getGL().getGL2();

    // Clear screen
    gl.glClearColor(0.4f, 0.7f, 1.0f, 1.0f);
    gl.glClear(GL.GL_COLOR_BUFFER_BIT | GL.GL_DEPTH_BUFFER_BIT);

    // Set up view matrix
    gl.glMatrixMode(GL2.GL_MODELVIEW);
    gl.glLoadIdentity();
    gl.glTranslatef(-camX, -camY, -camZ);
    gl.glRotatef(rotY, 0, 1, 0);
    gl.glRotatef(rotX, 1, 0, 0);
   
    // Update cloud positions and loop them
for (int i = 0; i < 10; i++) {
    cloudOffsets[i] += cloudSpeeds[i];

    if (cloudSpeeds[i] > 0 && cloudOffsets[i] > 15f) {
        cloudOffsets[i] = -15f;
    } else if (cloudSpeeds[i] < 0 && cloudOffsets[i] < -15f) {
        cloudOffsets[i] = 15f;
    }
}

// Draw the clouds
drawClouds(gl);



   

    // Draw ground (always at origin)
    drawGroundWithTexture(gl);

    // ===== INDEPENDENT OBJECTS ===== //
    // Each has its own glPushMatrix/glPopMatrix block
    
    // Draw Barn
    gl.glPushMatrix();
    gl.glTranslatef(.5f, 0f, 0f);
    drawBarnObject(gl);
    gl.glPopMatrix();


    // Draw Windmill
    gl.glPushMatrix();
    gl.glTranslatef(0f, 0f, -2.5f);  // Behind the barn
    drawWindmill(gl);
    gl.glPopMatrix();
    
    // Update rotation
    bladeRotation += 2.0f;
    if (bladeRotation >= 360f) bladeRotation -= 360f;
    
  
   // Draw 20 rows of wheat
float zOffset = -1.0f; // Adjust this value to move all wheat backward (more negative = further back)

for (int row = 0; row < 20; row++) {
    float z = (row * 0.15f) + zOffset;  // Apply zOffset to shift all rows backward
    
    // Draw 5 wheat plants per row
    for (int plant = 0; plant < 5; plant++) {
        float x = 2.4f - (plant * 0.1f);  // Plant spacing
        
        gl.glPushMatrix();
        gl.glTranslatef(x, 0f, z);
        drawWheatObject(gl);
        gl.glPopMatrix();
    }
}
    
    // Draw Pine Tree
    gl.glPushMatrix();
    gl.glTranslatef(-1.5f, 0f, 0.3f);
    drawPineTreeObject(gl);
    gl.glPopMatrix();
    
    // Draw Haybale
    gl.glPushMatrix();
    gl.glTranslatef(-2.5f, 0.5f, -0.5f);
    drawHaybaleObject(gl);
    gl.glPopMatrix();
    
    // Draw Silo
    gl.glPushMatrix();
    gl.glTranslatef(4f, 0f, -2f);
    drawSilo(gl);
    gl.glPopMatrix();

    //---------fence belakang------------
    for (int i=0 ;i < 13 ;i++){
    gl.glPushMatrix();
    gl.glTranslatef(-0.7f+(i*0.2f), 0f, -2f);
    drawFenceObject(gl);
    gl.glPopMatrix();
    }
    //-------------right fence-----------
    // Draw Fence
    gl.glPushMatrix();
    gl.glTranslatef(-0.1f, 0f, 2f);
    drawFenceObject(gl);
    gl.glPopMatrix();
    // Draw Fence
    gl.glPushMatrix();
    gl.glTranslatef(-0.3f, 0f, 2f);
    drawFenceObject(gl);
    gl.glPopMatrix();
    // Draw Fence
    gl.glPushMatrix();
    gl.glTranslatef(-0.5f, 0f, 2f);
    drawFenceObject(gl);
    gl.glPopMatrix();
    // Draw Fence
    gl.glPushMatrix();
    gl.glTranslatef(-0.7f, 0f, 2f);
    drawFenceObject(gl);
    gl.glPopMatrix();
    
     for (int i=0 ;i < 20;i++){
    gl.glPushMatrix();
    gl.glTranslatef(-.8f, 0f, 1.9f + (i*-0.2f));
    gl.glRotatef(90f, 0f, 1f, 0f);
    drawFenceObject(gl);
    gl.glPopMatrix();
    }
    
    //------------left fence-----------------
    // Draw Fence
    gl.glPushMatrix();
    gl.glTranslatef(1.2f, 0f, 2f);
    drawFenceObject(gl);
    gl.glPopMatrix();
    // Draw Fence
    gl.glPushMatrix();
    gl.glTranslatef(1f, 0f, 2f);
    drawFenceObject(gl);
    gl.glPopMatrix();
    // Draw Fence
    gl.glPushMatrix();
    gl.glTranslatef(1.4f, 0f, 2f);
    drawFenceObject(gl);
    gl.glPopMatrix();
    // Draw Fence
    gl.glPushMatrix();
    gl.glTranslatef(1.6f, 0f, 2f);
    drawFenceObject(gl);
    gl.glPopMatrix();
    // Draw Fence
    gl.glPushMatrix();
    gl.glTranslatef(1.66f, 0f, 2f);
    drawFenceObject(gl);
    gl.glPopMatrix();
    
    
    for (int i=0 ;i < 20;i++){
    gl.glPushMatrix();
    gl.glTranslatef(1.8f, 0f, 1.9f + (i*-0.2f));
    gl.glRotatef(90f, 0f, 1f, 0f);
    drawFenceObject(gl);
    gl.glPopMatrix();
    }
    
    //------------pig-----------------
    
    // Draw the pig
    Pig(gl, -2f, 0.15f, 2f, .3f);
    
   FullPond(gl,4.3f,0,1f);
   
   
   //------------pig-----------------
   
   // Draw the scarecrow components
    drawScarecrow(gl, -2f, 0.3f, -0.5f, 0.2f);  
   
}

    private void drawFenceObject(GL2 gl){
        float scale = .05f;
        gl.glPushMatrix();
        gl.glScalef(scale, scale, scale);
        drawFenceSegment(gl, 0.5f, 6.0f, 0.2f, 5.0f, 0.5f, 0.1f);
        gl.glPopMatrix();
    }

    private void drawWheatObject(GL2 gl){
        float scale = .1f; // Half the original size
        gl.glPushMatrix();
        gl.glScalef(scale, scale, scale);
        drawWheat(gl, 1f, 0.5f, 2f);
        gl.glPopMatrix(); // End of scale context
    }

    private void drawBarnObject(GL2 gl){
        // Draw barn
        gl.glPushMatrix();
        gl.glTranslatef(0.0f, 0.5f, 0.0f); // Shift origin to top center of box
        drawBarn(gl, 2.0f, 1.0f, 3f); // width, height, depth
        gl.glPushMatrix();
        gl.glTranslatef(0.0f, 0.2f, 0.0f); // Shift origin to top center of box
        drawRoof(gl, 2.25f, 1.0f, 3.25f);
        gl.glTranslatef(0.0f, 0.1f, 0.5f); // Shift origin to top center of box
        drawWindow2D(gl, 0.4f, 0.4f, 1.0f / 2);
        drawDoor2D(gl, 1f, 0.5f, 0.0f);
        gl.glPopMatrix();
        drawLeftWindows(gl, 1.0f, 3.0f, 0.25f, 0.5f);
        drawRightWindows(gl, 1.0f, 3.0f, 0.25f, 0.5f);
        drawBackWindows(gl, 1.0f, 3.0f, 0.5f, 0.5f);
        gl.glPopMatrix();
    }

    private void drawFenceSegment(GL2 gl, float postWidth, float postHeight, float postDepth, float sidebarWidth, float sidebarHeight, float sidebarDepth) {
        float tipHeight = 0.5f;

        gl.glColor3f(1.0f, 1.0f, 1.0f); // white

        // Leftmost post
        gl.glPushMatrix();
        gl.glTranslatef(-sidebarWidth/2.5f, postHeight / 2, 0f);
        drawFenceSidebar(gl, postWidth, postHeight, postDepth);
        gl.glTranslatef(0f, postHeight / 2 + tipHeight / 2, 0f);
        drawFencePyramid(gl, postWidth, postDepth, tipHeight);
        gl.glPopMatrix();

        // Left post
        gl.glPushMatrix();
        gl.glTranslatef(-sidebarWidth / 4.5f, postHeight / 2, 0f);
        drawFenceSidebar(gl, postWidth, postHeight, postDepth);
        gl.glTranslatef(0f, postHeight / 2 + tipHeight / 2, 0f);
        drawFencePyramid(gl, postWidth, postDepth, tipHeight);
        gl.glPopMatrix();

        // Mid post
        gl.glPushMatrix();
        gl.glTranslatef(0, postHeight / 2, 0f);
        drawFenceSidebar(gl, postWidth, postHeight, postDepth);
        gl.glTranslatef(0f, postHeight / 2 + tipHeight / 2, 0f);
        drawFencePyramid(gl, postWidth, postDepth, tipHeight);
        gl.glPopMatrix();

        // Right post
        gl.glPushMatrix();
        gl.glTranslatef(sidebarWidth / 5f, postHeight / 2, 0f);
        drawFenceSidebar(gl, postWidth, postHeight, postDepth);
        gl.glTranslatef(0f, postHeight / 2 + tipHeight / 2, 0f);
        drawFencePyramid(gl, postWidth, postDepth, tipHeight);
        gl.glPopMatrix();

        // Rightmost post
        gl.glPushMatrix();
        gl.glTranslatef(sidebarWidth/2.5f, postHeight / 2, 0f);
        drawFenceSidebar(gl, postWidth, postHeight, postDepth);
        gl.glTranslatef(0f, postHeight / 2 + tipHeight / 2, 0f);
        drawFencePyramid(gl, postWidth, postDepth, tipHeight);
        gl.glPopMatrix();


        // Lower horizontal bar
        gl.glPushMatrix();
        gl.glTranslatef(0f, postHeight / 6, -sidebarDepth);
        drawFenceSidebar(gl, sidebarWidth, sidebarHeight, sidebarDepth);
        gl.glPopMatrix();

        // Upper horizontal bar
        gl.glPushMatrix();
        gl.glTranslatef(0f, postHeight - sidebarHeight*2, -sidebarDepth);
        drawFenceSidebar(gl, sidebarWidth, sidebarHeight, sidebarDepth);
        gl.glPopMatrix();
    }

    private void drawFenceSidebar(GL2 gl, float width, float height, float depth) {
        float x = width / 2;
        float y = height / 2;
        float z = depth / 2;

        gl.glBegin(GL2.GL_QUADS);

        // Front face
        gl.glVertex3f(-x, -y, z);
        gl.glVertex3f(x, -y, z);
        gl.glVertex3f(x, y, z);
        gl.glVertex3f(-x, y, z);

        // Back face
        gl.glVertex3f(-x, -y, -z);
        gl.glVertex3f(-x, y, -z);
        gl.glVertex3f(x, y, -z);
        gl.glVertex3f(x, -y, -z);

        // Left face
        gl.glVertex3f(-x, -y, -z);
        gl.glVertex3f(-x, -y, z);
        gl.glVertex3f(-x, y, z);
        gl.glVertex3f(-x, y, -z);

        // Right face
        gl.glVertex3f(x, -y, -z);
        gl.glVertex3f(x, y, -z);
        gl.glVertex3f(x, y, z);
        gl.glVertex3f(x, -y, z);

        // Top face
        gl.glVertex3f(-x, y, -z);
        gl.glVertex3f(-x, y, z);
        gl.glVertex3f(x, y, z);
        gl.glVertex3f(x, y, -z);

        // Bottom face
        gl.glVertex3f(-x, -y, -z);
        gl.glVertex3f(x, -y, -z);
        gl.glVertex3f(x, -y, z);
        gl.glVertex3f(-x, -y, z);

        gl.glEnd();
    }

    private void drawFencePyramid(GL2 gl, float baseWidth, float baseDepth, float height) {
        float halfW = baseWidth / 2f;
        float halfD = baseDepth / 2f;

        gl.glBegin(GL2.GL_TRIANGLES);

        // Front face
        gl.glVertex3f(0, height / 2f, 0);               // Apex
        gl.glVertex3f(-halfW, -height / 2f, halfD);     // Bottom left
        gl.glVertex3f(halfW, -height / 2f, halfD);      // Bottom right

        // Right face
        gl.glVertex3f(0, height / 2f, 0);
        gl.glVertex3f(halfW, -height / 2f, halfD);
        gl.glVertex3f(halfW, -height / 2f, -halfD);

        // Back face
        gl.glVertex3f(0, height / 2f, 0);
        gl.glVertex3f(halfW, -height / 2f, -halfD);
        gl.glVertex3f(-halfW, -height / 2f, -halfD);

        // Left face
        gl.glVertex3f(0, height / 2f, 0);
        gl.glVertex3f(-halfW, -height / 2f, -halfD);
        gl.glVertex3f(-halfW, -height / 2f, halfD);

        gl.glEnd();
    }

    private void drawWheat(GL2 gl, float grainSize, float width, float height) {
        gl.glColor3f(1.0f, 0.8f, 0.0f); // yellow-orange
        float stemHeight = 1f;
        float stemWidth = 0.05f;

        float grainWidth = grainSize/5;
        float grainHeight = grainSize*1.5f;

        //draw 3D grain
        gl.glPushMatrix();
        gl.glTranslatef(0f, stemHeight, 0f);
//        drawPyramid(gl, grainSize * 2f, grainSize/4);
        drawWheatCube(gl, grainWidth, grainHeight, grainWidth);
        gl.glTranslatef(0f, grainHeight, 0f);
        drawWheatPyramid(gl, grainWidth, grainWidth);
        gl.glTranslatef(0f, -grainHeight, 0f);
        gl.glRotatef(-180, 0f, 0f, 1f); // opposite tilt
        drawWheatPyramid(gl, grainWidth, grainWidth);
        gl.glPopMatrix();

        // draw 3D stem
        gl.glPushMatrix();
        gl.glTranslatef(0f, 0f, 0f);  // base at origin
        drawWheatStem(gl, stemWidth, stemHeight, stemWidth);  // width, height, depth
        gl.glPopMatrix();

        //draw 2D leafs
        drawWheatLeafs(gl, width, height);
    }

    private void drawWheatLeafs(GL2 gl, float width, float height){
        float leafWidth = 0.3f;

        // draw leaf left
        gl.glBegin(GL2.GL_QUADS);
        gl.glVertex3f(0, 0, 0.0f);         // bottom leaf
        gl.glVertex3f(-width, height/2, 0.0f);         // left leaf
        gl.glVertex3f(-width, height, 0.0f);         // top leaf
        gl.glVertex3f(-width + leafWidth, height/2, 0.4f);         // right leaf
        gl.glEnd();

        // draw leaf right
        gl.glBegin(GL2.GL_QUADS);
        gl.glVertex3f(0, 0, 0.0f);         // bottom leaf
        gl.glVertex3f(width - leafWidth, height/2, -0.4f);         // left leaf
        gl.glVertex3f(width + leafWidth, height, 0.0f);         // top leaf
        gl.glVertex3f(width, height/2, 0.0f);         // right leaf
        gl.glEnd();
    }

    private void drawWheatPyramid(GL2 gl, float size, float sideLength) {
        float h = size;       // height
        float hs = sideLength;  // half side length

        gl.glBegin(GL2.GL_TRIANGLES);
        // Front face
        gl.glVertex3f(0, h, 0);
        gl.glVertex3f(-hs, 0, hs);
        gl.glVertex3f(hs, 0, hs);

        // Right face
        gl.glVertex3f(0, h, 0);
        gl.glVertex3f(hs, 0, hs);
        gl.glVertex3f(hs, 0, -hs);

        // Back face
        gl.glVertex3f(0, h, 0);
        gl.glVertex3f(hs, 0, -hs);
        gl.glVertex3f(-hs, 0, -hs);

        // Left face
        gl.glVertex3f(0, h, 0);
        gl.glVertex3f(-hs, 0, -hs);
        gl.glVertex3f(-hs, 0, hs);
        gl.glEnd();
    }

    private void drawWheatCube(GL2 gl, float width, float height, float depth) {
        float hw = width;
        float hh = height;
        float hd = depth;

        gl.glBegin(GL2.GL_QUADS);

        // Front face
        gl.glVertex3f(-hw, 0, hd);
        gl.glVertex3f(hw, 0, hd);
        gl.glVertex3f(hw, hh, hd);
        gl.glVertex3f(-hw, hh, hd);

        // Back face
        gl.glVertex3f(-hw, 0, -hd);
        gl.glVertex3f(-hw, hh, -hd);
        gl.glVertex3f(hw, hh, -hd);
        gl.glVertex3f(hw, 0, -hd);

        // Left face
        gl.glVertex3f(-hw, 0, -hd);
        gl.glVertex3f(-hw, 0, hd);
        gl.glVertex3f(-hw, hh, hd);
        gl.glVertex3f(-hw, hh, -hd);

        // Right face
        gl.glVertex3f(hw, 0, -hd);
        gl.glVertex3f(hw, hh, -hd);
        gl.glVertex3f(hw, hh, hd);
        gl.glVertex3f(hw, 0, hd);

        // Top face
        gl.glVertex3f(-hw, hh, -hd);
        gl.glVertex3f(-hw, hh, hd);
        gl.glVertex3f(hw, hh, hd);
        gl.glVertex3f(hw, hh, -hd);

        // Bottom face
        gl.glVertex3f(-hw, 0, -hd);
        gl.glVertex3f(hw, 0, -hd);
        gl.glVertex3f(hw, 0, hd);
        gl.glVertex3f(-hw, 0, hd);

        gl.glEnd();
    }

    private void drawWheatStem(GL2 gl, float width, float height, float depth) {
        float hw = width / 2;
        float hh = height;
        float hd = depth / 2;

        gl.glColor3f(0.0f, 0.6f, 0.0f); // green

        gl.glBegin(GL2.GL_QUADS);

        // Front face
        gl.glVertex3f(-hw, 0, hd);
        gl.glVertex3f(hw, 0, hd);
        gl.glVertex3f(hw, hh, hd);
        gl.glVertex3f(-hw, hh, hd);

        // Back face
        gl.glVertex3f(-hw, 0, -hd);
        gl.glVertex3f(-hw, hh, -hd);
        gl.glVertex3f(hw, hh, -hd);
        gl.glVertex3f(hw, 0, -hd);

        // Left face
        gl.glVertex3f(-hw, 0, -hd);
        gl.glVertex3f(-hw, 0, hd);
        gl.glVertex3f(-hw, hh, hd);
        gl.glVertex3f(-hw, hh, -hd);

        // Right face
        gl.glVertex3f(hw, 0, -hd);
        gl.glVertex3f(hw, hh, -hd);
        gl.glVertex3f(hw, hh, hd);
        gl.glVertex3f(hw, 0, hd);

        // Top face
        gl.glVertex3f(-hw, hh, -hd);
        gl.glVertex3f(-hw, hh, hd);
        gl.glVertex3f(hw, hh, hd);
        gl.glVertex3f(hw, hh, -hd);

        // Bottom face
        gl.glVertex3f(-hw, 0, -hd);
        gl.glVertex3f(hw, 0, -hd);
        gl.glVertex3f(hw, 0, hd);
        gl.glVertex3f(-hw, 0, hd);

        gl.glEnd();
    }

    private void drawBarn(GL2 gl, float width, float height, float depth) {
        float hx = width / 2;
        float hy = height / 2;
        float hz = depth / 2;
        float roofHeight = 0.25f; // extra height for the peak

        float peakY = hy + roofHeight;

        gl.glColor3f(1.0f, 0.0f, 0.0f); //red

        // Front face
        gl.glBegin(GL2.GL_POLYGON);
        gl.glVertex3f(-hx, hy, hz);         // bottom left roof
        gl.glVertex3f(-hx, -hy, hz);        // bottom left
        gl.glVertex3f(hx, -hy, hz);         // bottom right
        gl.glVertex3f(hx, hy, hz);          // bottom right roof
        gl.glVertex3f(0, peakY, hz);        // roof peak
        gl.glEnd();

        // Back face
        gl.glBegin(GL2.GL_POLYGON);
        gl.glVertex3f(-hx, hy, -hz);        // bottom left roof
        gl.glVertex3f(-hx, -hy, -hz);       // bottom left
        gl.glVertex3f(hx, -hy, -hz);        // bottom right
        gl.glVertex3f(hx, hy, -hz);         // bottom right roof
        gl.glVertex3f(0, peakY, -hz);       // roof peak
        gl.glEnd();

        // Left face
        gl.glBegin(GL2.GL_QUADS);
        gl.glVertex3f(-hx, hy, hz);         // front top left
        gl.glVertex3f(-hx, -hy, hz);        // front bottom left
        gl.glVertex3f(-hx, -hy, -hz);       // back bottom left
        gl.glVertex3f(-hx, hy, -hz);        // back top left
        gl.glEnd();

        // Right face
        gl.glBegin(GL2.GL_QUADS);
        gl.glVertex3f(hx, hy, hz);          // front top right
        gl.glVertex3f(hx, -hy, hz);         // front bottom right
        gl.glVertex3f(hx, -hy, -hz);        // back bottom right
        gl.glVertex3f(hx, hy, -hz);         // back top right
        gl.glEnd();

        // Roof left slope
        gl.glBegin(GL2.GL_QUADS);
        gl.glVertex3f(-hx, hy, hz);         // front left roof
        gl.glVertex3f(-hx, hy, -hz);        // back left roof
        gl.glVertex3f(0, peakY, -hz);       // back peak
        gl.glVertex3f(0, peakY, hz);        // front peak
        gl.glEnd();

        // Roof right slope
        gl.glBegin(GL2.GL_QUADS);
        gl.glVertex3f(hx, hy, hz);          // front right roof
        gl.glVertex3f(hx, hy, -hz);         // back right roof
        gl.glVertex3f(0, peakY, -hz);       // back peak
        gl.glVertex3f(0, peakY, hz);        // front peak
        gl.glEnd();

        // Bottom face
        gl.glBegin(GL2.GL_QUADS);
        gl.glVertex3f(-hx, -hy, hz);        // front bottom left
        gl.glVertex3f(hx, -hy, hz);         // front bottom right
        gl.glVertex3f(hx, -hy, -hz);        // back bottom right
        gl.glVertex3f(-hx, -hy, -hz);       // back bottom left
        gl.glEnd();
    }

    private void drawRoof(GL2 gl, float width, float height, float depth) {
        float hx = width / 2;
        float hy = height / 2;
        float hz = depth / 2;
        float rh = 0.15f; //roof height

        gl.glColor3f(0.76f, 0.60f, 0.42f);

        //front left
        gl.glBegin(GL2.GL_QUADS);
        gl.glVertex3f(-hx, hy/2, hz); // bot left
        gl.glVertex3f(-hx, (hy/2)+rh, hz); // top left
        gl.glVertex3f(0, (hy)+rh, hz); // top right
        gl.glVertex3f(0, (hy), hz); // bot right
        gl.glEnd();

        //front right
        gl.glBegin(GL2.GL_QUADS);
        gl.glVertex3f(hx, (hy/2), hz); // bot right
        gl.glVertex3f(hx, (hy/2)+rh, hz); // top right
        gl.glVertex3f(0, (hy)+rh, hz); // top left
        gl.glVertex3f(0, hy, hz); // bot left
        gl.glEnd();

        // back left
        gl.glBegin(GL2.GL_QUADS);
        gl.glVertex3f(-hx, hy/2, -hz); // bot left
        gl.glVertex3f(-hx, (hy/2)+rh, -hz); // top left
        gl.glVertex3f(0, (hy)+rh, -hz); // top right
        gl.glVertex3f(0, hy, -hz); // bot right
        gl.glEnd();

        // back right
        gl.glBegin(GL2.GL_QUADS);
        gl.glVertex3f(hx, (hy/2), -hz); // bot right
        gl.glVertex3f(hx, (hy/2)+rh, -hz); // top right
        gl.glVertex3f(0, (hy)+rh, -hz); // top left
        gl.glVertex3f(0, hy, -hz); // bot left
        gl.glEnd();

        // left roof (connect front and back)
        gl.glBegin(GL2.GL_QUADS);
        gl.glVertex3f(-hx, hy/2, hz);       // front bottom
        gl.glVertex3f(-hx, (hy/2)+rh, hz);  // front top
        gl.glVertex3f(-hx, (hy/2)+rh, -hz); // back top
        gl.glVertex3f(-hx, hy/2, -hz);      // back bottom
        gl.glEnd();

        // right roof (connect front and back)
        gl.glBegin(GL2.GL_QUADS);
        gl.glVertex3f(hx, hy/2, hz);       // front bottom
        gl.glVertex3f(hx, (hy/2)+rh, hz);  // front top
        gl.glVertex3f(hx, (hy/2)+rh, -hz); // back top
        gl.glVertex3f(hx, hy/2, -hz);      // back bottom
        gl.glEnd();

        // top ridge (connect the peaks front to back)
        gl.glBegin(GL2.GL_QUADS);
        gl.glVertex3f(0, hy + rh, hz);  // front peak
        gl.glVertex3f(0, hy + rh, -hz); // back peak
        gl.glVertex3f(0, hy, -hz);      // back base of ridge
        gl.glVertex3f(0, hy, hz);       // front base of ridge
        gl.glEnd();

        //left slope top
        gl.glBegin(GL2.GL_QUADS);
        gl.glVertex3f(-hx, (hy/2)+rh, hz);   // front top edge
        gl.glVertex3f(0, hy+rh, hz);         // front ridge peak
        gl.glVertex3f(0, hy+rh, -hz);        // back ridge peak
        gl.glVertex3f(-hx, (hy/2)+rh, -hz);  // back top edge
        gl.glEnd();

        //right slope top
        gl.glBegin(GL2.GL_QUADS);
        gl.glVertex3f(hx, (hy/2)+rh, hz);    // front top edge
        gl.glVertex3f(0, hy+rh, hz);         // front ridge peak
        gl.glVertex3f(0, hy+rh, -hz);        // back ridge peak
        gl.glVertex3f(hx, (hy/2)+rh, -hz);   // back top edge
        gl.glEnd();
    }

    private void drawWindow2D(GL2 gl, float width, float height, float hy) {
        float windowWidth = width;
        float windowHeight = height;
        float zOffset = 1.01f;  // Slightly in front of the barn wall

        float xLeft = -windowWidth / 2;
        float xRight = windowWidth / 2;

        // Place the window near the top of the barn (adjust y offset if needed)
        float yTop = hy - 0.2f;
        float yBottom = yTop - windowHeight;

        gl.glColor3f(1.0f, 1.0f, 1.0f);
        gl.glBegin(GL2.GL_QUADS);
        gl.glVertex3f(xLeft, yTop, zOffset);     // Top Left
        gl.glVertex3f(xRight, yTop, zOffset);    // Top Right
        gl.glVertex3f(xRight, yBottom, zOffset); // Bottom Right
        gl.glVertex3f(xLeft, yBottom, zOffset);  // Bottom Left
        gl.glEnd();

        // Draw window inside (darker red) on window
        xLeft += 0.05f;
        xRight -= 0.05f;
        yTop -= 0.05f;
        yBottom += 0.05f;
        zOffset = 1.02f;
        gl.glColor3f(0.5f, 0.0f, 0.0f);
        gl.glBegin(GL2.GL_QUADS);
        // inside window
        gl.glVertex3f(xLeft, yTop, zOffset);     // Top Left
        gl.glVertex3f(xRight, yTop, zOffset);    // Top Right
        gl.glVertex3f(xRight, yBottom, zOffset); // Bottom Right
        gl.glVertex3f(xLeft, yBottom, zOffset);  // Bottom Left
        gl.glEnd();
    }

    private void drawDoor2D(GL2 gl, float width, float height, float hy) {
        float doorWidth = width;
        float doorHeight = height;
        float zOffset = 1.01f;  // Slightly in front of the barn wall

        float xLeft = -doorWidth / 2;
        float xRight = doorWidth / 2;

        // Place the door near the bottom of the barn (adjust y offset if needed)
        float yTop = hy - 0.25f;
        float yBottom = yTop - doorHeight;

        gl.glColor3f(1.0f, 1.0f, 1.0f);
        gl.glBegin(GL2.GL_QUADS);
        gl.glVertex3f(xLeft, yTop, zOffset);     // Top Left
        gl.glVertex3f(xRight, yTop, zOffset);    // Top Right
        gl.glVertex3f(xRight, yBottom, zOffset); // Bottom Right
        gl.glVertex3f(xLeft, yBottom, zOffset);  // Bottom Left
        gl.glEnd();

        // Draw door inside (darker red) on door
        xLeft += 0.025f;
        xRight -= 0.025f;
        yTop -= 0.025f;
        yBottom += 0.025f;
        zOffset = 1.02f;
        gl.glColor3f(0.8f, 0.0f, 0.0f);
        gl.glBegin(GL2.GL_QUADS);
        // inside door
        gl.glVertex3f(xLeft, yTop, zOffset);     // Top Left
        gl.glVertex3f(xRight, yTop, zOffset);    // Top Right
        gl.glVertex3f(xRight, yBottom, zOffset); // Bottom Right
        gl.glVertex3f(xLeft, yBottom, zOffset);  // Bottom Left
        gl.glEnd();

        drawDoorX2D(gl, width, height, yBottom+ (yTop-yBottom)/2);
    }

    private void drawDoorX2D(GL2 gl, float doorWidth, float doorHeight, float centerY) {
        float zOffset = 1.03f; // Slightly in front of door
        float thickness = 0.1f; // Thickness of the X beams

        float halfW = doorWidth / 2;
        float halfH = doorHeight / 2;

        // First diagonal: bottom-left to top-right
        gl.glColor3f(1.0f, 1.0f, 1.0f);
        gl.glBegin(GL2.GL_POLYGON);
        gl.glVertex3f(-halfW, centerY - halfH, zOffset);
        gl.glVertex3f(-halfW + thickness, centerY - halfH, zOffset);
        gl.glVertex3f(halfW, centerY + halfH, zOffset);
        gl.glVertex3f(halfW - thickness, centerY + halfH, zOffset);
        gl.glEnd();

        // Second diagonal: top-left to bottom-right
        gl.glBegin(GL2.GL_QUADS);
        gl.glVertex3f(-halfW, centerY + halfH, zOffset);
        gl.glVertex3f(-halfW + thickness, centerY + halfH, zOffset);
        gl.glVertex3f(halfW, centerY - halfH, zOffset);
        gl.glVertex3f(halfW - thickness, centerY - halfH, zOffset);
        gl.glEnd();
    }

    private void drawLeftWindows(GL2 gl, float barnHeight, float barnDepth, float windowWidth, float windowHeight) {
        float zOffsetStart = barnDepth / 2.75f;  // front to back placement
        float zStep = barnDepth / 4;               // spacing between windows
        float yStart = (barnHeight / 2);       // from near top
        float xLeftWall = 0.01f;                   // slightly outside the left wall (x = -hx - epsilon)

        for (int i = 0; i < 4; i++) {
            float z = zOffsetStart - i * zStep;

            // Push matrix so we can transform without affecting global space
            gl.glPushMatrix();
            gl.glTranslatef(xLeftWall, 0f, z);      // move to left wall
            gl.glRotatef(90f, 0f, 1f, 0f);          // rotate to face left wall
            drawWindow2D(gl, windowWidth, windowHeight, yStart);
            gl.glPopMatrix();
        }
    }

    private void drawRightWindows(GL2 gl, float barnHeight, float barnDepth, float windowWidth, float windowHeight) {
        float zOffsetStart = barnDepth / 2.75f;       // front to back placement
        float zStep = barnDepth / 4;                  // spacing between windows
        float yStart = barnHeight / 2;                // near top
        float xRightWall = -0.01f;     // slightly outside the right wall (x = +hx + epsilon)

        for (int i = 0; i < 4; i++) {
            float z = zOffsetStart - i * zStep;

            gl.glPushMatrix();
            gl.glTranslatef(xRightWall, 0f, z);       // move to right wall
            gl.glRotatef(-90f, 0f, 1f, 0f);           // rotate to face right wall
            drawWindow2D(gl, windowWidth, windowHeight, yStart);
            gl.glPopMatrix();
        }
    }

    private void drawBackWindows(GL2 gl, float barnHeight, float barnDepth, float windowWidth, float windowHeight) {
        float zBackWall = -barnDepth/6;  // Slightly behind the back face

        // Top window: near the top
        gl.glPushMatrix();
        gl.glTranslatef(0f, barnHeight/2, zBackWall);          // Move to back wall
        gl.glRotatef(180f, 0f, 1f, 0f);              // Rotate to face backwards
        drawWindow2D(gl, windowWidth, windowHeight, barnHeight / 2 - 0.2f);
        gl.glPopMatrix();

        // Bottom window: near the bottom
        gl.glPushMatrix();
        gl.glTranslatef(0f, barnHeight/2 + 0.1f, zBackWall);
        gl.glRotatef(180f, 0f, 1f, 0f);
        drawWindow2D(gl, windowWidth, windowHeight, -barnHeight / 2 + 0.2f);
        gl.glPopMatrix();
    }
    
    private void drawWindmill(GL2 gl) {
    // Save the current matrix
    gl.glPushMatrix();

    // Move the windmill next to the barn (adjust as needed)
    gl.glTranslatef(1.5f, 0.0f, 0.0f);

    // --- Draw the pole ---
    gl.glColor3f(0.3f, 0.3f, 0.3f);  // grey
    float poleWidth = 0.1f;
    float poleHeight = 2f;
    float poleDepth = 0.1f;

    gl.glBegin(GL2.GL_QUADS);
    // Front face
    gl.glVertex3f(-poleWidth, 0f, poleDepth);
    gl.glVertex3f(poleWidth, 0f, poleDepth);
    gl.glVertex3f(poleWidth, poleHeight, poleDepth);
    gl.glVertex3f(-poleWidth, poleHeight, poleDepth);
    // Back face
    gl.glVertex3f(-poleWidth, 0f, -poleDepth);
    gl.glVertex3f(poleWidth, 0f, -poleDepth);
    gl.glVertex3f(poleWidth, poleHeight, -poleDepth);
    gl.glVertex3f(-poleWidth, poleHeight, -poleDepth);
    // Left face
    gl.glVertex3f(-poleWidth, 0f, -poleDepth);
    gl.glVertex3f(-poleWidth, 0f, poleDepth);
    gl.glVertex3f(-poleWidth, poleHeight, poleDepth);
    gl.glVertex3f(-poleWidth, poleHeight, -poleDepth);
    // Right face
    gl.glVertex3f(poleWidth, 0f, -poleDepth);
    gl.glVertex3f(poleWidth, 0f, poleDepth);
    gl.glVertex3f(poleWidth, poleHeight, poleDepth);
    gl.glVertex3f(poleWidth, poleHeight, -poleDepth);
    gl.glEnd();

    // --- Draw the hub ---
    gl.glPushMatrix();
    gl.glTranslatef(0.0f, poleHeight, 0.15f);  // Move to top of pole
    gl.glColor3f(0.2f, 0.2f, 0.2f);  // dark grey

    float radius = 0.1f;
    int slices = 20;
    gl.glBegin(GL2.GL_POLYGON);
    for (int i = 0; i < slices; i++) {
        double angle = 2 * Math.PI * i / slices;
        gl.glVertex3f((float) Math.cos(angle) * radius, (float) Math.sin(angle) * radius, 0.0f);
    }
    gl.glEnd();
    

    // --- Draw the blades ---
    gl.glPushMatrix();

    
    gl.glColor3f(1.0f, 1.0f, 1.0f); // white blades
    int numBlades = 4;
    float bladeLength = 0.6f;
    float bladeWidth = 0.05f;
    float bladeOffset = 0.1f; // Distance from center
   

    for (int i = 0; i < numBlades; i++) {
        float angle = 360.0f / numBlades * i;
        gl.glPushMatrix();
        gl.glRotatef(bladeRotation + (90f*i), 0f, 0f, 1f);
        
        gl.glBegin(GL2.GL_QUADS);
        gl.glVertex3f(-bladeWidth / 2, 0.0f, 0.13f);
        gl.glVertex3f(bladeWidth / 2, 0.0f, 0.13f);
        gl.glVertex3f(bladeWidth / 2, bladeLength, 0.13f);
        gl.glVertex3f(-bladeWidth / 2, bladeLength, 0.13f);
        gl.glEnd();
        gl.glPopMatrix(); 
        
         
}
    
    gl.glPopMatrix(); // End hub & blades transform
    gl.glPopMatrix(); // End windmill transform
   
    }
    

    
    
    //---------------------------Pine Cone---------------------------------------
     
    private void drawPineTreeObject(GL2 gl) {
    gl.glPushMatrix();
    drawTrunk(gl);
    drawLeavesCones(gl);
    drawPineCones(gl);
    gl.glPopMatrix();
}
    
    private void drawTrunk(GL2 gl) {
        GLUquadric quadric = glu.gluNewQuadric();
        gl.glPushMatrix();
        gl.glRotatef(-90, 1, 0, 0);
        gl.glColor3f(0.55f, 0.27f, 0.07f);
        glu.gluCylinder(quadric, 0.3, 0.3, 1.0, 30, 30);
        glu.gluDisk(quadric, 0, 0.3, 30, 1);
        gl.glTranslatef(0, 0, 1.0f);
        glu.gluDisk(quadric, 0, 0.3, 30, 1);
        gl.glPopMatrix();
    }

    private void drawLeavesCones(GL2 gl) {
        GLUquadric cone = glu.gluNewQuadric();

        // Bottom Cone
        gl.glPushMatrix();
        gl.glTranslatef(0, 1.0f, 0);
        drawSingleCone(gl, cone, 0.8f, 1.0f, 30, 30);
        gl.glPopMatrix();

        // Middle Cone
        gl.glPushMatrix();
        gl.glTranslatef(0, 1.6f, 0);
        drawSingleCone(gl, cone, 0.6f, 0.8f, 30, 30);
        gl.glPopMatrix();

        // Top Cone
        gl.glPushMatrix();
        gl.glTranslatef(0, 2.1f, 0);
        drawSingleCone(gl, cone, 0.4f, 0.6f, 30, 30);
        gl.glPopMatrix();
    }

    private void drawSingleCone(GL2 gl, GLUquadric cone, float baseRadius, float height, int slices, int stacks) {
        gl.glRotatef(-90f, 1f, 0f, 0f);

        for (int i = 0; i < stacks; i++) {
            float z0 = (height / stacks) * i;
            float z1 = (height / stacks) * (i + 1);

            float r0 = baseRadius * (1 - ((float) i / stacks));
            float r1 = baseRadius * (1 - ((float) (i + 1) / stacks));

            float green0 = 0.3f + 0.4f * (1 - ((float) i / stacks));
            float green1 = 0.3f + 0.4f * (1 - ((float) (i + 1) / stacks));

            gl.glBegin(GL2.GL_QUAD_STRIP);
            for (int j = 0; j <= slices; j++) {
                double theta = 2 * Math.PI * j / slices;
                float x = (float) Math.cos(theta);
                float y = (float) Math.sin(theta);

                gl.glColor3f(0.0f, green0, 0.0f);
                gl.glVertex3f(x * r0, y * r0, z0);

                gl.glColor3f(0.0f, green1, 0.0f);
                gl.glVertex3f(x * r1, y * r1, z1);
            }
            gl.glEnd();
        }
    }

    private void drawPineCones(GL2 gl) {
        GLUquadric quadric = glu.gluNewQuadric();
        gl.glColor3f(0.6f, 0.4f, 0.2f);

        for (PineCone cone : pineCones) {
            gl.glPushMatrix();
            gl.glTranslated(cone.x, 0, cone.z);
            gl.glRotatef(-90, 1, 0, 0);
            glu.gluCylinder(quadric, 0.05, 0.0, 0.1, 20, 20);
            gl.glPopMatrix();
        }
    }
    
 
    //-----------------------------HayBale-----------------------------
  
    
    private void drawHaybaleObject(GL2 gl) {
    gl.glPushMatrix();
    drawHaybale(gl);
    gl.glPopMatrix();
}

    
    private void drawHaybale(GL2 gl) {
        GLUquadric quadric = glu.gluNewQuadric();

        gl.glPushMatrix();
        gl.glRotatef(90, 0, 1, 0); // Lay horizontally

        // Draw gradient cylinder body
        drawGradientCylinder(gl, 0.5, 1.5, 50);

        // Front disk
        glu.gluDisk(quadric, 0, 0.5, 50, 1);
        drawSwirlOnDisk(gl, false);

        // Back disk
        gl.glTranslated(0, 0, 1.5);
        glu.gluDisk(quadric, 0, 0.5, 50, 1);
        drawSwirlOnDisk(gl, true);

        gl.glPopMatrix();

        // Draw hay sticks (after full cylinder is drawn)
        gl.glPushMatrix();
        gl.glRotatef(90, 0, 1, 0);
        drawHayPieces(gl);
        gl.glPopMatrix();
    }

    private void drawGradientCylinder(GL2 gl, double radius, double height, int slices) {
        for (int i = 0; i < slices; i++) {
            double theta1 = 2 * Math.PI * i / slices;
            double theta2 = 2 * Math.PI * (i + 1) / slices;

            double x1 = radius * Math.cos(theta1);
            double y1 = radius * Math.sin(theta1);
            double x2 = radius * Math.cos(theta2);
            double y2 = radius * Math.sin(theta2);

            gl.glBegin(GL2.GL_QUADS);

            // Bottom color (darker)
            gl.glColor3f(0.85f, 0.65f, 0.15f);
            gl.glVertex3d(x1, y1, 0);
            gl.glVertex3d(x2, y2, 0);

            // Top color (lighter)
            gl.glColor3f(0.95f, 0.75f, 0.25f);
            gl.glVertex3d(x2, y2, height);
            gl.glVertex3d(x1, y1, height);
            gl.glEnd();
        }
    }

    private void drawSwirlOnDisk(GL2 gl, boolean mirror) {
        gl.glColor3f(0.7f, 0.5f, 0.1f);
        gl.glLineWidth(3.0f);

        gl.glBegin(GL2.GL_LINE_STRIP);
        for (double t = 0; t < 8 * Math.PI; t += 0.1) {
            double r = 0.5 * t / (8 * Math.PI);
            double x = r * Math.cos(t);
            double y = r * Math.sin(t);

            if (mirror) x = -x;
            gl.glVertex2d(x, y);
        }
        gl.glEnd();
    }

    private void drawHayPieces(GL2 gl) {
        gl.glColor3f(0.9f, 0.8f, 0.3f);
        gl.glLineWidth(3.0f);  // slightly thicker sticks

        for (HayStick stick : haySticks) {
            gl.glBegin(GL2.GL_LINES);
            gl.glVertex3d(stick.x, stick.y, stick.z);
            gl.glVertex3d(stick.dx, stick.dy, stick.z);
            gl.glEnd();
        }
    }
//------------------------------Silo--------------------------------
 
    private void drawSiloBands(GL2 gl, double radius, double height, int bands) {
    gl.glColor3f(0.4f, 0.4f, 0.4f);  // dark metal band color
    gl.glLineWidth(3.0f);

    for (int i = 1; i <= bands; i++) {
        double z = (height / (bands + 1)) * i;
        gl.glBegin(GL2.GL_LINE_LOOP);
        for (int j = 0; j < 50; j++) {
            double theta = 2 * Math.PI * j / 50;
            double x = radius * Math.cos(theta);
            double y = radius * Math.sin(theta);
            gl.glVertex3d(x, y, z);
        }
        gl.glEnd();
    }
}
private void drawSiloGradientCylinder(GL2 gl, double radius, double height, int slices) {
    for (int i = 0; i < slices; i++) {
        double theta1 = 2 * Math.PI * i / slices;
        double theta2 = 2 * Math.PI * (i + 1) / slices;

        double x1 = radius * Math.cos(theta1);
        double y1 = radius * Math.sin(theta1);
        double x2 = radius * Math.cos(theta2);
        double y2 = radius * Math.sin(theta2);

        gl.glBegin(GL2.GL_QUADS);

        // Bottom dirty gray
        gl.glColor3f(0.5f, 0.5f, 0.5f);
        gl.glVertex3d(x1, y1, 0);
        gl.glVertex3d(x2, y2, 0);

        // Top shiny silver
        gl.glColor3f(0.85f, 0.85f, 0.85f);
        gl.glVertex3d(x2, y2, height);
        gl.glVertex3d(x1, y1, height);
        gl.glEnd();
    }
}
   private void drawSilo(GL2 gl) {
    GLUquadric quadric = glu.gluNewQuadric();

    // Rotate cylinder upright
    gl.glPushMatrix();
    gl.glRotatef(-90, 1, 0, 0);

    // Custom cylinder with gradient (bottom dirty, top shiny)
    drawSiloGradientCylinder(gl, 0.6, 3.0, 50);

    // Bottom disk
    glu.gluDisk(quadric, 0, 0.6, 50, 1);
    gl.glPopMatrix();
    gl.glPushMatrix();
    gl.glRotatef(-90, 1, 0, 0);
    drawSiloBands(gl, 0.6, 3.0, 5);  // 5 bands evenly spaced
    gl.glPopMatrix();
    // Roof cone (bright metallic)
    gl.glPushMatrix();
    gl.glTranslatef(0, 3.0f, 0);
    gl.glRotatef(-90, 1, 0, 0);
    gl.glColor3f(0.9f, 0.9f, 0.9f);  // shiny roof
    glu.gluCylinder(quadric, 0.6, 0.0, 0.5, 50, 50);
    gl.glPopMatrix();
}
   
   
   //------------------------------Animal--------------------------------
   private void Pig(GL2 gl, float x, float y, float z, float size){
        gl.glPushMatrix();
        gl.glTranslatef(x, y, z);
        gl.glScalef(size, size, size);
        
        //body
        gl.glPushMatrix();
        gl.glScalef(1.5f, 0.7f, 1.0f);
        Body(gl, 0, 0.5f, 0, 0.8f);
        gl.glPopMatrix();
        
        //head
        gl.glPushMatrix();
        Body(gl, 1.2f, 0.4f, 0, 0.5f);
        gl.glPopMatrix();
        
        //nose 
        gl.glPushMatrix();
        gl.glRotatef(90.0f, 0.0f, 1.0f, 0.0f);
        PigLeg(gl, 0.0f, 0.4f, 1.6f, 0.2f, 90.0f);
        gl.glPopMatrix();
        
        //ear
        gl.glPushMatrix();
        gl.glRotatef(90.0f, 0.0f, 1.0f, 0.0f);
        PigLeg(gl, -0.3f, 0.75f, 1.1f, 0.05f, 90.0f);
        PigLeg(gl, 0.3f, 0.75f, 1.1f, 0.05f, 90.0f);
        gl.glPopMatrix();
        

        
        PigLeg(gl, 0.6f, 0.1f, -0.4f, 0.7f, 0.0f);//left front
        PigLeg(gl, 0.6f, 0.1f, 0.4f,0.7f, 0.0f);//right front
        PigLeg(gl, -0.6f, 0.1f, -0.4f, 0.7f, 0.0f);
        PigLeg(gl, -0.6f, 0.1f, 0.4f, 0.7f, 0.0f);
        
        Eyes(gl,1.5f, 0.7f, 0.3f);
        Eyes(gl,1.5f, 0.7f, -0.3f);
        
        gl.glPopMatrix();
    }
    
    private void Body(GL2 gl, float x, float y, float z, float radius){
        gl.glPushMatrix();
        gl.glTranslatef(x, y, z);
        
        boolean pigBodyEnabled = false;
        if (pig_body_texture != null) {
            pig_body_texture.enable(gl);
            pig_body_texture.bind(gl);
            gl.glEnable(GL2.GL_TEXTURE_2D);
            pigBodyEnabled = true;
            gl.glColor3f(1.0f, 1.0f, 1.0f);
            
        }else gl.glColor3f(1.0f, 0.75f, 0.8f);
        
        Sphere(gl,radius,32,32);
        
        if(pigBodyEnabled){
            gl.glDisable(GL2.GL_TEXTURE_2D);
            pig_body_texture.disable(gl);
        }
        gl.glPopMatrix();
    }
    
    private void Eyes(GL2 gl, float x, float y, float z){
        gl.glPushMatrix();
        gl.glTranslatef(x, y, -z);
        gl.glColor3f(0.0f,0.0f,0.0f);
        glut.glutSolidSphere(0.05f, 16, 16);
        gl.glPopMatrix();
    }
    
    private void PigLeg(GL2 gl, float x, float y, float z, float height, float rotation) {
        gl.glPushMatrix();
        gl.glTranslatef(x, y, z);
        gl.glRotatef(rotation, 1.0f, 0.0f, 0.0f);

        boolean pigLegEnabled = false;
        if (pig_leg_texture != null) {
            pig_leg_texture.enable(gl);
            pig_leg_texture.bind(gl);
            gl.glEnable(GL2.GL_TEXTURE_2D);
            pigLegEnabled = true;
        } else {
            gl.glColor3f(1.0f, 0.75f, 0.8f); // Pink if no texture
        }

        Cylinder(gl, 0.15f, height, 32);

        if (pigLegEnabled) {
            gl.glDisable(GL2.GL_TEXTURE_2D);
            pig_leg_texture.disable(gl);
        }

        gl.glPopMatrix();
    }
    
    //----------------------------POND------------------------------
    private void FullPond(GL2 gl, float x, float y, float z){
        gl.glTranslatef(x, y, z);
        
        Pond(gl,0.0f,0.0f,1.5f);
        Pond(gl,0.0f,1.0f,1.0f);
        Pond(gl,-0.5f,-1.1f,1.2f);
        
        LilyPads(gl, -1f, -1f, 0.3f);
        LilyPads(gl, 0.5f, 1.0f, 0.25f);
        
        Reeds(gl, -1.6f, -0.6f, 0.7f); //(x, z, height)
        Reeds(gl, -1.55f, -0.7f,1.0f);
        //Reeds(gl, -1.5f, -0.9f,0.5f);
        
        Reeds(gl, 1.0f, -1.0f, 0.5f);
        Reeds(gl, 1.1f, -1.1f, 1.0f);
        Reeds(gl, 1.05f, -0.9f, 0.7f);
        Reeds(gl, 0.5f, -1.6f, 1.0f);
        Reeds(gl, 0.3f, -1.6f, 0.5f);
        
        Reeds(gl, 1.2f, 1.0f, 0.6f);
        
        Rock(gl, 1.1f, 0.0f, -0.9f,0.2f);
        Rock(gl, 1.0f, 0.0f, 1.2f, 0.35f); 
        Rock(gl, 0.7f, 0.0f, 1.5f, 0.2f);
        
        Rock(gl, 0.7f, 0.0f, -1.5f, 0.4f);
        Rock(gl, 0.9f, 0.0f, -1.3f, 0.25f);
        
        Rock(gl, -1.5f, 0.0f, -0.5f, 0.3f);
        Rock(gl, -1.5f, 0.0f, 0.0f,0.2f);
        Rock(gl, -1.5f, 0.0f, 0.4f, 0.2f);
        Rock(gl, -1.25f, 0.0f, 0.75f, 0.2f);
        Rock(gl, -1.0f, 0.0f, 1.0f,0.25f);
    }
    
    private void Pond(GL2 gl, float x, float z, float radius){
        
        boolean waterEnabled = false;
        if(water_texture != null){
            water_texture.enable(gl);
            water_texture.bind(gl);
            gl.glEnable(GL2.GL_TEXTURE_2D);
            waterEnabled = true;
            gl.glColor3f(1.0f, 1.0f, 1.0f);
        } else {
            setColor(gl,BLUE);
        }
        
        gl.glBegin(GL2.GL_TRIANGLE_FAN);
        if(waterEnabled){
            gl.glTexCoord2f(0.5f,0.5f);
        }
        gl.glVertex3f(x, 0.001f, z);
        
        int segments = 100;
        for (int i = 0; i <= segments; i++) {
            double angle = 2 * Math.PI * i / segments;
            float px = x + (float) Math.cos(angle) * radius;
            float pz = z + (float) Math.sin(angle) * radius;
            
            float u = 0.5f + 0.5f * (float)Math.cos(angle);
            float v = 0.5f + 0.5f * (float)Math.sin(angle);
            
            if(waterEnabled){
                gl.glTexCoord2f(u,v);
            }
            gl.glVertex3f(px, 0.001f, pz);
        }
        gl.glEnd();
        
        if (waterEnabled){
            gl.glDisable(GL2.GL_TEXTURE_2D);
            water_texture.disable(gl);
        }
    }
    
    
    private void LilyPads(GL2 gl, float x, float z, float radius){
        setColor(gl, DARK_GREEN);
        gl.glBegin(GL2.GL_TRIANGLE_FAN);
        gl.glVertex3f(x, 0.01f, z);
        for (int i = 0; i < 360; i += 10) {
            double angle = Math.toRadians(i);
            float px = x + (float)Math.cos(angle) * radius;
            float pz = z + (float)Math.sin(angle) * radius;
            gl.glVertex3f(px, 0.01f, pz);
        }
        gl.glEnd();
    }
    
     private Map<String, float[][]> reedParameters = new HashMap<>();
    private Random reedRandom = new Random(42); 

    private void Reeds(GL2 gl, float x, float z, float h) {
        int leaf = 4;
        float base = 0.05f;

        String key = x + "_" + z + "_" + h;

        
        float[][] parameters = reedParameters.get(key);
        if (parameters == null) {
            parameters = new float[leaf][7];
            for (int i = 0; i < leaf; i++) {
                float angle = (float)(i * 2 * Math.PI / leaf);
                parameters[i][0] = (float)(reedRandom.nextDouble() * 0.1f - 0.05f); 
                parameters[i][1] = (float)(reedRandom.nextDouble() * 0.1f - 0.05f); 
                parameters[i][2] = (float)(reedRandom.nextDouble() * 0.3f + 0.1f);  
                parameters[i][3] = LIGHT_BROWN[0] + (float)(reedRandom.nextDouble() * 0.1f - 0.05f); 
                parameters[i][4] = LIGHT_BROWN[1] + (float)(reedRandom.nextDouble() * 0.1f - 0.05f); 
                parameters[i][5] = LIGHT_BROWN[2] + (float)(reedRandom.nextDouble() * 0.1f - 0.05f); 
                parameters[i][6] = angle; 
            }
            reedParameters.put(key, parameters);
        }

        
        for (int i = 0; i < leaf; i++) {
            float offsetX = parameters[i][0];
            float offsetZ = parameters[i][1];
            float curve = parameters[i][2];
            float[] leaf_color = {parameters[i][3], parameters[i][4], parameters[i][5]};
            float angle = parameters[i][6];

            Reed_Leef(gl, x + offsetX, z + offsetZ, h, angle, curve, base, leaf_color);
        }
    }

    private void Reed_Leef(GL2 gl, float x, float z, float height, float angle, float curve, float base, float[] leaf_color) {
        int segments = 8;

        setColor(gl, leaf_color);

        gl.glBegin(GL2.GL_TRIANGLE_STRIP);
        for (int i = 0; i <= segments; i++) {
            float l = (float) i / segments;
            float y = l * height;

            float width = base * (1 - l * 0.7f);
            float curveOffset = curve * (l * l) * height;

            float curveX = (float)(curveOffset * Math.cos(angle));
            float curveZ = (float)(curveOffset * Math.sin(angle));

            
            float edgeX = (float)(width * Math.cos(angle + Math.PI/2));
            float edgeZ = (float)(width * Math.sin(angle + Math.PI/2));

            
            gl.glVertex3f(x + curveX - edgeX, 0.01f + y, z + curveZ - edgeZ);
            gl.glVertex3f(x + curveX + edgeX, 0.01f + y, z + curveZ + edgeZ);
        }
        gl.glEnd();

        
        setColor(gl, DARK_GREEN);
        gl.glLineWidth(1.5f);
        gl.glBegin(GL2.GL_LINE_STRIP);
        for (int i = 0; i <= segments; i++) {
            float l = (float) i/segments;
            float y = l * height;
            float curveOffset = curve * (l * l) * height;
            float curveX = (float)(curveOffset * Math.cos(angle));
            float curveZ = (float)(curveOffset * Math.sin(angle));

            gl.glVertex3f(x + curveX, 0.01f + y, z + curveZ);
        }
        gl.glEnd();
        gl.glLineWidth(1.0f);
    }
    
    
    
    private void Rock(GL2 gl, float x, float y, float z, float radius){
        int stacks = 10;
        int slices = 10;
        
        boolean rockEnabled = false;
        if (rock_texture != null) {
            rock_texture.enable(gl);
            rock_texture.bind(gl);
            gl.glEnable(GL2.GL_TEXTURE_2D);
            rockEnabled = true;
            gl.glColor3f(1.0f, 1.0f, 1.0f);
        } else{
            setColor(gl, GREY);
        }
        
        gl.glPushMatrix();
        gl.glTranslatef(x, y, z);
        gl.glScalef(1.0f, 0.6f, 0.7f);
        
        
        for (int i = 0; i < stacks; ++i) {
            double lat0 = Math.PI * (-0.5 + (double) i / stacks);
            double z0 = Math.sin(lat0);
            double zr0 = Math.cos(lat0);
            
            double lat1 = Math.PI * (-0.5 + (double) (i+1) / stacks);
            double z1 = Math.sin(lat1);
            double zr1 = Math.cos(lat1);
            
            gl.glBegin(GL2.GL_QUAD_STRIP);
            for (int j = 0; j < slices; ++j) {
                double lng = 2 * Math.PI * (double) (j) / slices;
                double x1 = Math.cos(lng);
                double y1 = Math.sin(lng);
                
                float u = (float) j / slices;
                float v0 = (float) i / stacks;
                float v1 = (float) (i+1) / stacks; 
                
                if (rockEnabled){
                    gl.glTexCoord2f(u, v0);
                }
                gl.glVertex3f((float)(x1 * zr0 * radius), (float)(y1 * zr0 * radius), (float)(z0 *radius));
                
                if (rockEnabled){
                    gl.glTexCoord2f(u, v1);
                }
                gl.glVertex3f((float)(x1 * zr1 * radius), (float)(y1 * zr1 * radius), (float)(z1 *radius));
            }
            gl.glEnd();
        }
        gl.glPopMatrix();
        
        if (rockEnabled){
            gl.glDisable(GL2.GL_TEXTURE_2D);
            rock_texture.disable(gl);
        }
    }
    
    private void setColor(GL2 gl, float[] color){
        gl.glColor3f(color[0], color[1], color[2]);
    }
    
    //----------------------------scarecrow-------------------------
    
    private void drawScarecrow(GL2 gl, float x, float y, float z, float scale) {
    gl.glPushMatrix();
    gl.glTranslatef(x, y, z);
    gl.glScalef(scale, scale, scale); // Uniform scaling
    
    // Draw components (relative to center)
    Body(gl, 0.0f, 0.0f, 0.0f);
    Head(gl, 0.0f, 1.6f, 0.0f);
    Hat(gl, 0.0f, 2.2f, 0.0f);
    Stick(gl, -1.0f, 0.8f, 0.0f, 1.5f, 45.0f);  // Left arm
    Stick(gl, 1.0f, 0.8f, 0.0f, 1.5f, -45.0f);  // Right arm
    Stick(gl, -0.3f, -0.8f, 0.0f, 1.5f, 0.0f);  // Left leg
    Stick(gl, 0.3f, -0.8f, 0.0f, 1.5f, 0.0f);   // Right leg
    
    gl.glPopMatrix();
}
    
    
    private void Body(GL2 gl, float x, float y, float z) {
    gl.glTranslatef(x, y, z);
    float bottomY = -0.8f;
    float topY = 0.8f;

    float[] bottomA = {-0.7f, bottomY, -0.5f};
    float[] bottomB = {0.7f, bottomY, -0.5f};
    float[] bottomC = {0.5f, bottomY, 0.5f};
    float[] bottomD = {-0.5f, bottomY, 0.5f};

    float[] topE = {-1.0f, topY, -0.8f};
    float[] topF = {1.0f, topY, -0.8f};
    float[] topG = {0.8f, topY, 0.8f};
    float[] topH = {-0.8f, topY, 0.8f};
    

    gl.glPushMatrix();

    boolean BodyEnabled = false;
        if (body_texture != null) {
            body_texture.enable(gl);
            body_texture.bind(gl);
            gl.glEnable(GL2.GL_TEXTURE_2D);
            BodyEnabled = true;
            gl.glColor3f(1.0f, 1.0f, 1.0f);
            
        }else gl.glColor3f(1.0f, 0.75f, 0.8f);

    // Bottom face
    gl.glBegin(GL2.GL_QUADS);
    gl.glNormal3f(0, -1, 0);
    gl.glTexCoord2f(0, 0); gl.glVertex3f(bottomA[0], bottomA[1], bottomA[2]);
    gl.glTexCoord2f(1, 0); gl.glVertex3f(bottomB[0], bottomB[1], bottomB[2]);
    gl.glTexCoord2f(1, 1); gl.glVertex3f(bottomC[0], bottomC[1], bottomC[2]);
    gl.glTexCoord2f(0, 1); gl.glVertex3f(bottomD[0], bottomD[1], bottomD[2]);
    gl.glEnd();

    // Front face
    gl.glBegin(GL2.GL_QUADS);
    gl.glNormal3f(0, 0, 1);
    gl.glTexCoord2f(0, 0); gl.glVertex3f(bottomD[0], bottomD[1], bottomD[2]);
    gl.glTexCoord2f(1, 0); gl.glVertex3f(bottomC[0], bottomC[1], bottomC[2]);
    gl.glTexCoord2f(1, 1); gl.glVertex3f(topG[0], topG[1], topG[2]);
    gl.glTexCoord2f(0, 1); gl.glVertex3f(topH[0], topH[1], topH[2]);
    gl.glEnd();

    // Back face
    gl.glBegin(GL2.GL_QUADS);
    gl.glNormal3f(0, 0, -1);
    gl.glTexCoord2f(0, 0); gl.glVertex3f(bottomA[0], bottomA[1], bottomA[2]);
    gl.glTexCoord2f(1, 0); gl.glVertex3f(bottomB[0], bottomB[1], bottomB[2]);
    gl.glTexCoord2f(1, 1); gl.glVertex3f(topF[0], topF[1], topF[2]);
    gl.glTexCoord2f(0, 1); gl.glVertex3f(topE[0], topE[1], topE[2]);
    gl.glEnd();

    // Left face
    gl.glBegin(GL2.GL_QUADS);
    gl.glNormal3f(-1, 0, 0);
    gl.glTexCoord2f(0, 0); gl.glVertex3f(bottomA[0], bottomA[1], bottomA[2]);
    gl.glTexCoord2f(1, 0); gl.glVertex3f(bottomD[0], bottomD[1], bottomD[2]);
    gl.glTexCoord2f(1, 1); gl.glVertex3f(topH[0], topH[1], topH[2]);
    gl.glTexCoord2f(0, 1); gl.glVertex3f(topE[0], topE[1], topE[2]);
    gl.glEnd();

    // Right face
    gl.glBegin(GL2.GL_QUADS);
    gl.glNormal3f(1, 0, 0);
    gl.glTexCoord2f(0, 0); gl.glVertex3f(bottomB[0], bottomB[1], bottomB[2]);
    gl.glTexCoord2f(1, 0); gl.glVertex3f(bottomC[0], bottomC[1], bottomC[2]);
    gl.glTexCoord2f(1, 1); gl.glVertex3f(topG[0], topG[1], topG[2]);
    gl.glTexCoord2f(0, 1); gl.glVertex3f(topF[0], topF[1], topF[2]);
    gl.glEnd();

    // Top face
    gl.glBegin(GL2.GL_QUADS);
    gl.glNormal3f(0, 1, 0);
    gl.glTexCoord2f(0, 0); gl.glVertex3f(topE[0], topE[1], topE[2]);
    gl.glTexCoord2f(1, 0); gl.glVertex3f(topF[0], topF[1], topF[2]);
    gl.glTexCoord2f(1, 1); gl.glVertex3f(topG[0], topG[1], topG[2]);
    gl.glTexCoord2f(0, 1); gl.glVertex3f(topH[0], topH[1], topH[2]);
    gl.glEnd();

    if (BodyEnabled) {
        gl.glDisable(GL2.GL_TEXTURE_2D);
        }
        gl.glPopMatrix();
    }

    private void Head(GL2 gl, float x, float y, float z){
        gl.glPushMatrix();
        gl.glTranslatef(x, y, z);

        boolean headEnabled = false;
        if (straw_texture != null) {
            straw_texture.enable(gl);
            straw_texture.bind(gl);
            gl.glEnable(GL2.GL_TEXTURE_2D);
            headEnabled = true;
        } else {
            gl.glColor3f(0, 0, 0); // white if no texture
        }

        Sphere(gl,0.6f,32,32);

        if (headEnabled) {
            gl.glDisable(GL2.GL_TEXTURE_2D);
            straw_texture.disable(gl);
        }

        gl.glPopMatrix();
    }
    
    private void Hat(GL2 gl, float x, float y, float z) {
        gl.glPushMatrix();
        gl.glTranslatef(x, y, z);

        boolean hatEnabled = false;
        if (body_texture != null) {
            body_texture.enable(gl);
            body_texture.bind(gl);
            gl.glEnable(GL2.GL_TEXTURE_2D);
            hatEnabled = true;
        } else {
            gl.glColor3f(0.4f, 0.2f, 0.1f);
        }

        Cylinder(gl, 1.0f, 0.05f, 32);

        gl.glTranslatef(0, 0.3f, 0);
        Cylinder(gl, 0.5f, 0.8f, 32);

        if (hatEnabled) {
            gl.glDisable(GL2.GL_TEXTURE_2D);
            body_texture.disable(gl);
        }

        gl.glPopMatrix();
    }
    
    private void Stick(GL2 gl, float x, float y, float z, float height, float rotation) {
        gl.glPushMatrix();
        gl.glTranslatef(x, y, z);
        gl.glRotatef(rotation, 1.0f, 0.0f, 0.0f);

        boolean pigLegEnabled = false;
        if (straw_texture != null) {
            straw_texture.enable(gl);
            straw_texture.bind(gl);
            gl.glEnable(GL2.GL_TEXTURE_2D);
            pigLegEnabled = true;
        } else {
            gl.glColor3f(1.0f, 0.75f, 0.8f); // Pink if no texture
        }

        Cylinder(gl, 0.15f, height, 32);

        if (pigLegEnabled) {
            gl.glDisable(GL2.GL_TEXTURE_2D);
            straw_texture.disable(gl);
        }

        gl.glPopMatrix();
    }
    
    
    private void drawClouds(GL2 gl) {
    gl.glPushMatrix();

    float[] cloudY = {3.5f, 4.0f, 3.6f, 4.3f, 3.8f, 3.9f, 4.2f, 3.7f, 4.1f, 3.55f};
      float[] cloudZ = {-18f, -14f, -10f, -6f, -2f, 2f, 6f, 10f, 14f, 18f};

    for (int i = 0; i < 10; i++) {
        drawSingleCloud(gl, cloudOffsets[i], cloudY[i], cloudZ[i]);
    }

    gl.glPopMatrix();
}


private void drawSingleCloud(GL2 gl, float offsetX, float offsetY, float offsetZ) {
    gl.glPushMatrix();
    gl.glTranslatef(offsetX, offsetY, offsetZ); // Position cloud

    float radius = 0.4f;
    int segments = 20;

    drawCircle(gl, -0.3f, 0f, 0f, radius, segments);
    drawCircle(gl, 0.0f, 0.2f, 0f, radius, segments);
    drawCircle(gl, 0.3f, 0f, 0f, radius, segments);

    gl.glPopMatrix();
}


// Helper function to draw a flat circle (disc)
private void drawCircle(GL2 gl, float centerX, float centerY, float centerZ, float radius, int segments) {
    gl.glBegin(GL2.GL_POLYGON);
    for (int i = 0; i < segments; i++) {
        double angle = 2.0 * Math.PI * i / segments;
        float x = centerX + (float) Math.cos(angle) * radius;
        float y = centerY + (float) Math.sin(angle) * radius;
        gl.glVertex3f(x, y, centerZ);
    }
    gl.glEnd();
}

    
    
   
    
    
    private void Sphere(GL2 gl, float radius, int slices, int stacks) {
    float x, y, z;
    
    for (int i = 0; i < stacks; i++) {
        float phi1 = (float) (Math.PI * i / stacks);
        float phi2 = (float) (Math.PI * (i + 1) / stacks);
        
        gl.glBegin(GL2.GL_QUAD_STRIP);
        for (int j = 0; j <= slices; j++) {
            float theta = (float) (2.0 * Math.PI * j / slices);
            
            float u = (float) j / slices;
            float v1 = (float) i / stacks;
            float v2 = (float) (i + 1) / stacks;
            
            x = (float) (Math.sin(phi1) * Math.cos(theta));
            y = (float) (Math.cos(phi1));
            z = (float) (Math.sin(phi1) * Math.sin(theta));
            
            gl.glNormal3f(x, y, z);
            gl.glTexCoord2f(u, v1);
            gl.glVertex3f(x * radius, y * radius, z * radius);
            
            x = (float) (Math.sin(phi2) * Math.cos(theta));
            y = (float) (Math.cos(phi2));
            z = (float) (Math.sin(phi2) * Math.sin(theta));
            
            gl.glNormal3f(x, y, z);
            gl.glTexCoord2f(u, v2);
            gl.glVertex3f(x * radius, y * radius, z * radius);
            
            }
        gl.glEnd();
        }
    }

    private void Cylinder(GL2 gl, float radius, float height, int segments) {
        float halfHeight = height / 2.0f;

        gl.glBegin(GL2.GL_QUAD_STRIP);
        for (int i = 0; i <= segments; i++) {
            float angle = (float) (2.0 * Math.PI * i / segments);
            float x = radius * (float) Math.cos(angle);
            float z = radius * (float) Math.sin(angle);

            float u = (float) i / segments;
            float vTop = 1.0f;
            float vBottom = 0.0f;

            gl.glNormal3f(x, 0, z);

            gl.glTexCoord2f(u, vBottom);
            gl.glVertex3f(x, -halfHeight, z);

            gl.glTexCoord2f(u, vTop);
            gl.glVertex3f(x, halfHeight, z);
        }
        gl.glEnd();

        gl.glBegin(GL2.GL_TRIANGLE_FAN);
        gl.glNormal3f(0, 1, 0); 
        gl.glTexCoord2f(0.5f, 0.5f);
        gl.glVertex3f(0, halfHeight, 0); 

        for (int i = 0; i <= segments; i++) {
            float angle = (float) (2.0 * Math.PI * i / segments);
            float x = radius * (float) Math.cos(angle);
            float z = radius * (float) Math.sin(angle);

            float u = 0.5f + 0.5f * (float) Math.cos(angle);
            float v = 0.5f + 0.5f * (float) Math.sin(angle);

            gl.glTexCoord2f(u, v);
            gl.glVertex3f(x, halfHeight, z);
        }
        gl.glEnd();

        gl.glBegin(GL2.GL_TRIANGLE_FAN);
        gl.glNormal3f(0, -1, 0); 
        gl.glTexCoord2f(0.5f, 0.5f);
        gl.glVertex3f(0, -halfHeight, 0);

        for (int i = segments; i >= 0; i--) {
            float angle = (float) (2.0 * Math.PI * i / segments);
            float x = radius * (float) Math.cos(angle);
            float z = radius * (float) Math.sin(angle);

            float u = 0.5f + 0.5f * (float) Math.cos(angle);
            float v = 0.5f + 0.5f * (float) Math.sin(angle);

            gl.glTexCoord2f(u, v);
            gl.glVertex3f(x, -halfHeight, z);
        }
        gl.glEnd();
    }
   


   private void drawGroundWithTexture(GL2 gl) {
    if (grassTexture != null) {
        grassTexture.bind(gl);
        grassTexture.enable(gl);

        gl.glPushMatrix();
        gl.glColor3f(1f, 1f, 1f); // Set color to white to avoid tinting texture

        gl.glBegin(GL2.GL_QUADS);
        gl.glTexCoord2f(0f, 0f); gl.glVertex3f(-10f, 0f, -10f);
        gl.glTexCoord2f(0f, 5f); gl.glVertex3f(-10f, 0f, 10f);
        gl.glTexCoord2f(5f, 5f); gl.glVertex3f(10f, 0f, 10f);
        gl.glTexCoord2f(5f, 0f); gl.glVertex3f(10f, 0f, -10f);
        gl.glEnd();

        gl.glPopMatrix();

        grassTexture.disable(gl);
    }
}
   
   
   
  
   //view of the camera------------------------------------------------------
    @Override
    public void reshape(GLAutoDrawable drawable, int x, int y, int width, int height) {
        GL2 gl = drawable.getGL().getGL2();

        float aspect = (float) width / height;
        gl.glMatrixMode(GL2.GL_PROJECTION);
        gl.glLoadIdentity();
        //make view wider the first value
        new GLU().gluPerspective(60.0, aspect, 1.0, 100.0);
    }

    @Override
    public void dispose(GLAutoDrawable drawable) {
    }

    // Basic WASD & Arrow Key Navigation
    @Override
    public void keyPressed(KeyEvent e) {
        switch (e.getKeyCode()) {
            case KeyEvent.VK_W -> camZ -= 0.1f;
            case KeyEvent.VK_S -> camZ += 0.1f;
            case KeyEvent.VK_A -> camX -= 0.1f;
            case KeyEvent.VK_D -> camX += 0.1f;
            case KeyEvent.VK_LEFT -> rotY -= 2.0f;
            case KeyEvent.VK_RIGHT -> rotY += 2.0f;
        }
    }

    @Override
    public void mousePressed(MouseEvent e) {
        dragging = true;
        lastMouseX = e.getX();
        lastMouseY = e.getY();
    }

    @Override
    public void mouseReleased(MouseEvent e) {
        dragging = false;
    }

    @Override
    public void mouseDragged(MouseEvent e) {
        if (dragging) {
            float dx = e.getX() - lastMouseX;
            float dy = e.getY() - lastMouseY;

            rotY += dx * 0.5f;
            rotX += dy * 0.5f;

            lastMouseX = e.getX();
            lastMouseY = e.getY();
        }
    }

    @Override public void mouseMoved(MouseEvent e) {}
    @Override public void mouseClicked(MouseEvent e) {}
    @Override public void mouseEntered(MouseEvent e) {}
    @Override public void mouseExited(MouseEvent e) {}
    @Override public void keyReleased(KeyEvent e) {}
    @Override public void keyTyped(KeyEvent e) {}

}