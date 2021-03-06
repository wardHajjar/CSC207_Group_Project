package com.example.dungeonescape.platformer;

import android.graphics.Color;
import android.graphics.Rect;
import android.graphics.RectF;

import java.util.Random;


public class Coin extends Level3Object {

    Coin(int x, int y, int size, PlatformerManager manager) {
        super(x,y,size,manager);
        paint.setColor(Color.YELLOW);
        this.rect = new Rect(x - size / 2, (y - size / 2),
                x + size / 2, y + size / 2);

    }

    /** Alternate constructor for Coin that is used in Maze. */
    public Coin(int x, int y) {
        super(x,y);
        paint.setColor(Color.YELLOW);
    }

    /** Deletes a Coin that the Character has already collected.
     * Creates a new Coin at a new y value.
     */
    void gotCoin() {
        this.y = 0;
        Random r = new Random();
        this.x = r.nextInt(1080- 150);
        this.rect = new Rect((x - size / 2), (y - size / 2), (x + size / 2), (y + size / 2));
        this.oval = new RectF(x - size / 2, y - size / 2,
                x + size / 2, y + size / 2);
    }

    /** Moves the Platforms down. */
    void update(int down) {
        coinDown(down);
        this.rect = new Rect((x - size / 2), (y - size /2), (x + size / 2), (y + size / 2));
        this.oval = new RectF(x - size / 2, y - size / 2,
                x + size / 2, y + size / 2);
    }

    /** Moves the Coin down when the Character jumps up. */
    private void coinDown(int down) {
        if (this.y + down > manager.getGridHeight()) {
            /* Moves coin up if the Character moves down without collection the Coin. */
            int diff = Math.abs(this.y + down - manager.getGridHeight());
            if (diff > 400) {
                this.y = 0;
            } else if (diff > 200) {
                this.y = 200;
            } else {
                this.y = diff; // moves Coin down
            }
            Random r = new Random();
            this.x = r.nextInt(1080- 150);
        } else {
            this.y = (this.y + down);
        }
    }

    /** Returns the Rect object of the Coin.
     *
     * @return the Rect object of the Coin.
     */
    Rect getRect() {
        return rect;
    }
}
