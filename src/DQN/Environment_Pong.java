package DQN;

import static DQN.DQN.B0T_Team;
import static DQN.DQN.B0T_Type;

public class Environment_Pong {
    public static class State {
        int ball_x, ball_y, ball_xDirection, ball_yDirection;
        public int paddle_y, paddle_yDirection;
        public int enemy_paddle_y, enemy_paddle_yDirection;

        public double reward;
        public boolean d = false;

        public State( int ball_x, int ball_y, int ball_xDirection, int ball_yDirection, int paddle_y, int paddle_yDirection, int enemy_paddle_y, int enemy_paddle_yDirection, double reward, boolean d) {
            this.ball_x = ball_x;
            this.ball_y = ball_y;
            this.ball_xDirection = ball_xDirection;
            this.ball_yDirection = ball_yDirection;
            this.paddle_y = paddle_y;
            this.paddle_yDirection = paddle_yDirection;
            this.enemy_paddle_y = enemy_paddle_y;
            this.enemy_paddle_yDirection = enemy_paddle_yDirection;
            this.reward = reward;
            this.d = d;
        }

        @Override
        public String toString() {
            return "ball_x = " + ball_x + ", ball_y = " + ball_y + ", ball_xDirection = " + ball_xDirection + ", ball_yDirection = " + ball_yDirection + ", paddle_y = " + paddle_y+ ", paddle_yDirection = " + paddle_yDirection + ", reward = " + reward;
        }
    }
    public Pong pong;
    public int p1_Score;
    public int p2_Score;

    public State getNewStateAndReward(int action) throws InterruptedException {
        //System.out.println("AI_KeyEvent " + action);
        double reward;
        boolean game_over = false;
        DQN.Team normal_bot = DQN.Team.NONE;

        if( B0T_Type.equals(DQN.B0T_Type.DQN)){
            if (B0T_Team.equals(DQN.Team.RED)){
                pong.b.p1.setYDirection(action);
            } else{
                pong.b.p2.setYDirection(action);
            }
        }

        if( B0T_Type.equals(B0T_Type.PROGRAMMED)) {
            if (B0T_Team.equals(DQN.Team.RED)){
                normal_bot = DQN.Team.RED;
            } else{
                normal_bot = DQN.Team.BLUE;
            }
        }

        // bot start
        int x1 = pong.b.ball.x;
        int y1 = pong.b.ball.y;
        // bot end
        if (DQN.Team.RED.equals(normal_bot) &&  pong.b.xDirection > 0 && (DQN.nextIntBetween(-1,2) ==0))
            pong.b.p1.keyReleased();
        if (DQN.Team.BLUE.equals(normal_bot) &&  pong.b.xDirection < 0 && (DQN.nextIntBetween(-1,2) ==0))
            pong.b.p2.keyReleased();

        for (int i = 0; i< 5 ; i++) {
            pong.b.move();
            pong.b.p1.move();
            pong.b.p2.move();
        }

        // Bot start
        int x2 = pong.b.ball.x;
        int y2 = pong.b.ball.y;
        int m  = 0;
        if ( x2 != x1)
          m = (y2 - y1)/(x2 - x1);;

        if( DQN.Team.RED.equals(normal_bot)) {
            int yi = m * (-x1) + y1; /*+ DQN.nextIntBetween(-3 * Paddle.HEIGHT, 150);*/ // Uncomment and adjust to control difficulty

            if (pong.b.p1.paddle.y < yi)
                pong.b.p1.keyPressed(Paddle.AI_KeyEvent.DOWN);
            if (pong.b.p1.paddle.y > yi)
                pong.b.p1.keyPressed(Paddle.AI_KeyEvent.UP);
            if (pong.b.p1.paddle.y == yi)
                pong.b.p1.keyReleased();
        }
        if( DQN.Team.BLUE.equals(normal_bot)) {
            int yi = m * (Pong.WIDTH - x1) + y1 /*+ DQN3.nextIntBetween(-2 * Paddle.HEIGHT, 150)*/;

            if (pong.b.p2.paddle.y < yi)
                pong.b.p2.keyPressed(Paddle.AI_KeyEvent.DOWN);
            if (pong.b.p2.paddle.y > yi)
                pong.b.p2.keyPressed(Paddle.AI_KeyEvent.UP);
            if (pong.b.p2.paddle.y == yi)
                pong.b.p2.keyReleased();
        }
        // bot end
        reward = -1 * Math.abs(y1 - pong.b.p1.y);

/*        if (pong.b.p2score > p2_Score)
            reward = -1;*/
/*        else if( pong.b.p1score > p1_Score)
            reward = 10;*/

        p2_Score = pong.b.p2score;
        p1_Score = pong.b.p1score;

        if ((pong.b.p2score >= DQN.MAX_POINTS) || (pong.b.p1score >= DQN.MAX_POINTS)) game_over = true;

        //Thread.sleep(4);
        return new State(pong.b.ball.x, pong.b.ball.y, pong.b.xDirection, pong.b.yDirection, pong.b.p1.paddle.y, pong.b.p1.yDirection,pong.b.p2.paddle.y, pong.b.p2.yDirection ,reward,game_over);

    }

    public State getState(){
        pong = new Pong();
        pong.b.p1score = 0;
        pong.b.p2score = 0;
        p1_Score = pong.b.p1score;
        p2_Score = pong.b.p2score;
        return new State(pong.b.ball.x, pong.b.ball.y, pong.b.xDirection, pong.b.yDirection, pong.b.p1.paddle.y, pong.b.p1.yDirection,pong.b.p2.paddle.y, pong.b.p2.yDirection ,0,false);
    }

    public void end(){
        pong.dispose();
    }
}
