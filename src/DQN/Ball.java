package DQN;

import java.awt.*;
import java.util.Random;


public class Ball implements Runnable {

    //global variables
    int x, y, xDirection, yDirection;
    static int HEIGHT = 15;

    public int p1score, p2score;

    public Paddle p1 = new Paddle(10, Pong.HEIGHT/2, 1);
    public Paddle p2 = new Paddle(Pong.WIDTH - Paddle.WIDTH - 10, Pong.HEIGHT/2, 2);

    Rectangle ball;

    public Ball(int x, int y){
        p1score = p2score = 0;
        this.x = x;
        this.y = y;

        int rXDir = 0;
        rXDir--;
        setXDirection(rXDir);

        int rYDir = 0;
        rYDir--;
        setYDirection(rYDir);

        //create "ball"
        ball = new Rectangle(this.x, this.y, HEIGHT, HEIGHT);
    }

    public void setXDirection(int xDir){
        xDirection = xDir;
    }
    public void setYDirection(int yDir){
        yDirection = yDir;
    }

    public void draw(Graphics g) {
        g.setColor(Color.WHITE);
        g.fillRect(ball.x, ball.y, ball.width, ball.height);
    }

    public void collision(){
        if(ball.intersects(p1.paddle)) {
            int xi = +2;
            int yi = 0;
            if( p1.yDirection == 1 && yDirection >= 1){
                yi = (+1);
                xi = (+3);
            }
            if( p1.yDirection == -1 && yDirection <= -1){
                yi = (-1);
                xi = (+3);
            }

            if( p1.yDirection == 1 && yDirection <= -1){
                yi = (1);
                xi = (+2);
            }
            if( p1.yDirection == -1 && yDirection >= 1){
                yi = (-1);
                xi = (+2);
            }
            if( p1.yDirection == 1 && yDirection == 0){
                yi = (+1);
                xi = (+2);
            }
            if( p1.yDirection == -1 && yDirection == 0){
                yi = (-1);
                xi = (+2);
            }
            if( p1.yDirection == 0 && yDirection == 0){
                yi = (0);
            }
            if( p1.yDirection == 0 && yDirection <= -1){
                yi = (0);
            }
            if( p1.yDirection == 0 && yDirection >= 1){
                yi = (0);
            }
            setXDirection(xi);
            setYDirection(yi);
        }
        if(ball.intersects(p2.paddle)) {
            int xi = -2;
            int yi = 0;
            if( p2.yDirection == 1 && yDirection >= 1){
                yi = (+1);
                xi = (-3);
            }
            if( p2.yDirection == -1 && yDirection <= -1){
                yi = (-1);
                xi = (-3);
            }

            if( p2.yDirection == 1 && yDirection <= -1){
                yi = (1);
                xi = (-2);
            }
            if( p2.yDirection == -1 && yDirection >= 1){
                yi = (-1);
                xi = (-2);
            }
            if( p2.yDirection == 1 && yDirection == 0){
                yi = (+1);
                xi = (-2);
            }
            if( p2.yDirection == -1 && yDirection == 0){
                yi = (-1);
                xi = (-2);
            }
            if( p2.yDirection == 0 && yDirection == 0){
                yi = (0);
            }
            if( p2.yDirection == 0 && yDirection <= -1){
                yi = (0);
            }
            if( p2.yDirection == 0 && yDirection >= 1){
                yi = (0);
            }
            setXDirection(xi);
            setYDirection(yi);

        }
    }
    public void move() {
        collision();
        ball.x += xDirection;
        ball.y += yDirection;
        //bounce the ball when it hits the edge of the screen
        if (ball.x <= 0) {
            ball.x = HEIGHT;
            setXDirection(1);
            setYDirection(0);
            p2score++;
            System.out.println(p1score + " : " + p2score );

        }
        if (ball.x >= (Pong.WIDTH - HEIGHT)) {
            ball.x = Pong.WIDTH - HEIGHT;
            setXDirection(-1);
            setYDirection(0);
            p1score++;
            System.out.println(p1score + " : " + p2score );
        }

        if (ball.y <= HEIGHT) {
            setYDirection(+1);
        }

        if (ball.y >= (Pong.HEIGHT- HEIGHT)) {
            setYDirection(-1);
        }
    }

    @Override
    public void run() {
        try {
            while(true) {
                //move();
                //Thread.sleep(10);
            }
        }catch(Exception e) { System.err.println(e.getMessage()); }

    }

}