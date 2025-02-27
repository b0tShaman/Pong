package DQN;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;


public class Pong extends JFrame implements Runnable{
    static int WIDTH = 800;
    static int HEIGHT = 600;
    //screen size variables.
    int gWidth = WIDTH;
    int gHeight = HEIGHT;
    Dimension screenSize = new Dimension(gWidth, gHeight);

    Image dbImage;
    Graphics dbGraphics;

    //ball object
    public Ball b = new Ball(WIDTH/2, (int)(HEIGHT * 0.5));

    //constructor for window
    public Pong() {

    }

    public void construct(){
        this.setTitle("Pong!");
        this.setSize(screenSize);
        this.setResizable(false);
        this.setVisible(true);
        this.setBackground(Color.BLACK);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.addKeyListener(new AL());
    }

    public void init() {
        //create and start threads.
        Thread ball = new Thread(b);
        ball.start();
        Thread p1 = new Thread(b.p1);
        Thread p2 = new Thread(b.p2);
        p2.start();
        p1.start();
    }

    @Override
    public void paint(Graphics g) {
        dbImage = createImage(getWidth(), getHeight());
        dbGraphics = dbImage.getGraphics();
        draw(dbGraphics);
        g.drawImage(dbImage, 0, 0, this);
    }

    public void draw(Graphics g) {
        b.draw(g);
        b.p1.draw(g);
        b.p2.draw(g);

        g.setColor(Color.WHITE);
        g.setFont(new Font(Font.DIALOG, Font.BOLD, 40));
        g.drawString(String.valueOf(b.p1score), getWidth()/2 - 40, getHeight()/10);
        g.drawString(":"  , getWidth()/2, gHeight/10);
        g.drawString(String.valueOf(b.p2score), getWidth()/2 + 40, gHeight/10);

        if( b.p1score >= 10) {
            g.setFont(new Font(Font.DIALOG, Font.BOLD, 50));
            g.drawString("RED WINS", getWidth() / 2 - 100, getHeight() / 2);
        }
        else if (b.p2score >= 10){
            g.setFont(new Font(Font.DIALOG, Font.BOLD, 50));
            g.drawString("BLUE WINS", getWidth() / 2 - 100, getHeight() / 2);
        }

        //repaint();
    }

    @Override
    public void run() {
        while(true) {
            repaint();
        }
    }

    public void AI_KeyPressed(Paddle.AI_KeyEvent e){
        b.p1.keyPressed(e);
    }

    public class AL extends KeyAdapter {
        @Override
        public void keyPressed(KeyEvent e) {
            b.p1.keyPressed(e);
            b.p2.keyPressed(e);
        }
        @Override
        public void keyReleased(KeyEvent e) {
            b.p1.keyReleased(e);
            b.p2.keyReleased(e);
        }

    }
}