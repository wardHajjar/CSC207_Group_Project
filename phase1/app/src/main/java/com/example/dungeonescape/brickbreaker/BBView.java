package com.example.dungeonescape.brickbreaker;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import java.util.ArrayList;

import android.view.Display;
import android.graphics.Point;
import android.app.Activity;
/*
BBMainActivity and BBView were structured like the following game:
http://gamecodeschool.com/android/building-a-simple-game-engine/
 */

/**
 * Class that controls the logic of the game by handling collisions, updating the objects' stats and
 * drawing the new states onto the canvas.
 */
public class BBView extends SurfaceView implements Runnable {
    /**
     * gameThread - the main game thread that gets executed by the program.
     * holder - contains the canvas on which objects are drawn.
     * playing - indicates whether the game is running i.e. user is playing
     * paused - indicates if the game is paused; starts off as true since the user hasn't started
     *          playing yet.
     * canvas - the Canvas object onto which objects are drawn.
     * fps - frames per second.
     * timeThisFrame - time it takes to execute the draw and update methods in one frame.
     * paint - the Paint object which determines the drawing style.
     * screenX - the width of the screen
     * screenY - the height of the screen
     */
    Thread gameThread = null;
    SurfaceHolder holder;
    boolean playing;
    boolean paused = true;
    Canvas canvas;
    long fps;
    private long timeThisFrame;
    Paint paint;

    int screenX;
    int screenY;
    Ball ball;
    //
    Paddle paddle;
    ArrayList<Brick> bricks;


    /**
     * Initializes the surface in the context environment.
     * @param context the environment
     */
    public BBView(Context context){
        super(context);
        this.holder = getHolder();
        this.paint = new Paint();


        Display display = ((Activity)context).getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        this.screenX = size.x;
        this.screenY = size.y;
        holder = getHolder();
        paint = new Paint();
        this.ball = new Ball(20, screenY - 200, 25, -25);

        // construct paddle and bricks
        paddle = new Paddle(screenX/2 - 75, screenY - 100);
        bricks = new ArrayList<>();
        int brickWidth = screenX / 6;
        int brickHeight = screenY / 20;
        for (int x = 0; x < screenX; x += brickWidth + 1) {
            for (int y = 10; y < 4 * brickHeight; y += brickHeight + 1) {
                bricks.add(new Brick(x, y, brickWidth, brickHeight));
            }
        }
    }

    /**
     * The method that runs the program's while loop.
     */
    @Override
    public void run(){
        while (playing) {

            // Capture the current time in milliseconds in startFrameTime
            long startFrameTime = System.currentTimeMillis();
            try {
                //set time in mili
                Thread.sleep(1);

            }catch (Exception e){
                e.printStackTrace();
            }
//             Updating the frame
            if(!paused){
                update();
            }

            // Draw the frame
            draw();

            timeThisFrame = System.currentTimeMillis() - startFrameTime;
            if (timeThisFrame >= 1) {   // Calculating the fps
                paused = false;
                fps = 1000 / timeThisFrame;
            }

        }
    }

    /**
     *
     */
    // TODO: Detecting collisions and updating objects' positions.
    public void update() {
        char wall_collision = ball.madeWallCollision(screenX, screenY);
        if ( wall_collision == 'x'){
            ball.setX_speed(ball.getX_speed() * -1);
        }
        else if(wall_collision == 'y'){
            ball.setY_speed(ball.getY_speed() * -1);
        }

        for (Brick brick: bricks){
            if (!(brick.getHitStatus())) {
                char brick_collision = ball.madeRectCollision(brick.getRect());
                if (brick_collision == 'x') {
                    ball.setX_speed(ball.getX_speed() * -1);
                    brick.changeHitStatus();
                } else if (brick_collision == 'y') {
                    ball.setY_speed(ball.getY_speed() * -1);
                    brick.changeHitStatus();
                }
            }
        }

        ball.move(fps);
    }

    /**
     *
     */
    // TODO: Draw the balls, bricks and paddle
    public void draw() {

        // Make sure the drawing surface is valid or program crashes
        if (holder.getSurface().isValid()) {
            // Lock the canvas ready to draw
            canvas = holder.lockCanvas();

            // Draw the background color - black
            canvas.drawColor(Color.argb(255,  0, 0, 0));

            // Choose the brush color for drawing - white

            paint.setColor(Color.argb(255,  255, 255, 255));

            // Ball
            ball.draw(canvas);

            // Paddle
            paddle.draw(canvas);
            // while playing/not dead
            if (playing) paddle.checkBounds(screenX);
            if (playing) paddle.updateLocation();

            // Bricks
            for (int i = 0; i < bricks.size(); i++) {
                if (!bricks.get(i).getHitStatus()) {
                    bricks.get(i).draw(canvas);
                }
            }

            // Draws everything to the screen
            holder.unlockCanvasAndPost(canvas);
        }

    }
    // Dictates the movement of the paddle
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if(event.getAction() == MotionEvent.ACTION_DOWN) {
            return true;
        }
        if (event.getAction() == MotionEvent.ACTION_MOVE) {
            float x = event.getX();
            if (x < paddle.x) {
                paddle.movingLeft = true;
            } else if (x > paddle.x) {
                paddle.movingRight = true;
            }
            return true;
        }
        if (event.getAction() == MotionEvent.ACTION_UP) {
            paddle.movingLeft = false;
            paddle.movingRight = false;
        }
        return super.onTouchEvent(event);
    }
    /**
     * If Activity is paused/stopped, the thread must stop as well.
     */
    public void pause() {
        playing = false;
        try {
            gameThread.join();
        } catch (InterruptedException e) {
            Log.e("Error:", "joining thread");
        }

    }

    /**
     * If Activity is started then thread must start as well.
     */
    public void resume() {
        playing = true;
        gameThread = new Thread(this);
        gameThread.start();
    }



}