package DQN;

import java.awt.*;
import java.awt.event.KeyEvent;


public class Paddle implements Runnable{
    static int WIDTH = 10;
    static int HEIGHT = 50;
    enum AI_KeyEvent{ UP, DOWN, RELEASED}
    int x, y, yDirection, id;

    Rectangle paddle;

    public Paddle(int x, int y, int id){
        this.x = x;
        this.y = y;
        this.id = id;
        paddle = new Rectangle(x, y, WIDTH, HEIGHT);
    }

    public void keyPressed(AI_KeyEvent e) {
        if(e == AI_KeyEvent.UP) {
            setYDirection(-1);
        }
        if(e == AI_KeyEvent.DOWN) {
            setYDirection(1);
        }
        if(e == AI_KeyEvent.RELEASED) {
            setYDirection(0);
        }
    }

    public void keyReleased() {
        setYDirection(0);
    }

    public void keyPressed(KeyEvent e) {
        switch(id) {
            case 1:
                if(e.getKeyCode() == KeyEvent.VK_W) {
                    setYDirection(-1);
                }if(e.getKeyCode() == KeyEvent.VK_S) {
                        setYDirection(1);
                    }
                    break;

            case 2:
                if(e.getKeyCode() == KeyEvent.VK_UP) {
                    setYDirection(-1);
                }
                if(e.getKeyCode() == KeyEvent.VK_DOWN) {
                    setYDirection(1);
                }
                break;
            default:
                System.out.println("Please enter a Valid ID in paddle contructor");
                break;
        }
    }

    public void keyReleased(KeyEvent e) {
        switch(id) {
            case 2:
                if(e.getKeyCode() == e.VK_UP) {
                    setYDirection(0);
                }
                if(e.getKeyCode() == e.VK_DOWN) {
                    setYDirection(0);
                }
                break;
            case 1:

                if(e.getKeyCode() == e.VK_W) {
                    setYDirection(0);
                }
                if(e.getKeyCode() == e.VK_S) {
                    setYDirection(0);
                }
                break;
            default:
                System.out.println("Please enter a Valid ID in paddle contructor");
                break;
        }
    }
    public void setYDirection(int yDir) {
        yDirection = yDir;
    }

    public void move() {
        paddle.y += yDirection;
        if (paddle.y <= HEIGHT/2)
            paddle.y = HEIGHT/2;
        if (paddle.y >= (Pong.HEIGHT - (HEIGHT)))
            paddle.y = Pong.HEIGHT - (HEIGHT);
    }
    public void draw(Graphics g) {
        switch(id) {
            case 1:
                g.setColor(Color.RED);
                g.fillRect(paddle.x, paddle.y, paddle.width, paddle.height);
                break;
            case 2:
                g.setColor(Color.BLUE);
                g.fillRect(paddle.x, paddle.y, paddle.width, paddle.height);
                break;
            default:
                System.out.println("Please enter a Valid ID in paddle contructor");
                break;
        }
    }
    @Override
    public void run() {
        try {
            while(true) {
                //move();
                //Thread.sleep(5);
            }
        } catch(Exception e) { System.err.println(e.getMessage()); }
    }




}