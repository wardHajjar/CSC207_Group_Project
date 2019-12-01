package com.example.dungeonescape.platformer;

import android.content.Intent;
import android.os.Bundle;
import android.os.SystemClock;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.example.dungeonescape.activities.DeadActivity;
import com.example.dungeonescape.activities.EndGameActivity;
import com.example.dungeonescape.activities.MainActivity;
import com.example.dungeonescape.activities.MenuActivity;
import com.example.dungeonescape.player.PlayerManager;
import com.example.dungeonescape.activities.GeneralGameActivity;
import com.example.dungeonescape.player.Player;
import com.example.dungeonescape.R;

import java.util.ArrayList;
import java.util.List;

/**
 * The activity for the main level3 game.
 */
public class PlatformerMainActivity extends GeneralGameActivity{
    private PlatformerView game;
    private boolean running;
    private Player player;
    private long startTime;

    private MenuActivity menuActivity = new MenuActivity();

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        startTime = SystemClock.elapsedRealtime();

        //* Gather saved data. */
        load();
        Intent i = getIntent();
        String name = (String) i.getSerializableExtra("Player Name");
        player = getPlayerManager().getPlayer(name);

        @SuppressWarnings("unchecked")
        ArrayList<Integer> character = (ArrayList<Integer>) i.getSerializableExtra("Character");
        @SuppressWarnings("unchecked")
        ArrayList<List> platformLocations = (ArrayList) i.getSerializableExtra("Platforms");


        setContentView(R.layout.activity_level3_main);
        game = findViewById(R.id.level2);

        if (platformLocations != null && character != null) {
            int score = (int) i.getSerializableExtra("Score");
            game.getManager().setCharacter(character, score);
            game.getManager().setPlatforms(platformLocations);
        }

        // getting player instance from intent
        //pass player into manager
        game.getManager().setPlayer(player);
        // get Resource file for portal
        game.setPortalImage(this.getResources().getDrawable(R.drawable.portal, null));

        setTitle("Level3: Platformer");

        // Set Buttons
        buttons();
        running = true;


        game.setEnterPortalListener(new OnCustomEventListener() {
            public void onEvent() {enterHiddenLevel(savedInstanceState);

            }
        });
        game.setFinishLevelListener(new OnCustomEventListener() {
            public void onEvent() {endGame();
            }
        });
        game.setEndGameListener(new OnCustomEventListener() {
            public void onEvent() {deadPage();
            }
        });
        // Thread code is from the following Youtube Video, body of run() is written myself
        // https://www.youtube.com/watch?v=6sBqeoioCHE&t=193s
        Thread t = new Thread() {
            @Override
            public void run() {
                while (!isInterrupted()) {
                    try {

                        Thread.sleep(1000);
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                if (running) {

                                    // Update the score shown
                                    int score = game.getManager().getCharacterScore();
                                    String scr = String.valueOf(score) ;
                                    String scre = "Score: " + scr;

                                    TextView score1 = (TextView) findViewById(R.id.score);
                                    score1.setText(scre);
                                    int lives = game.getManager().getPlayer().getNumLives();
                                    String life = "Lives: " + String.valueOf(lives);
                                    TextView lifeText = (TextView) findViewById(R.id.lives);
                                    lifeText.setText(life);
                                }
                            }
                        });
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        };
        t.start();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.game_menu, menu);
        return true;
    }



    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.main_menu) {
            save(getPlayerManager());
            Intent intent = menuActivity.createIntent(PlatformerMainActivity.this,
                    MainActivity.class, player.getName());
            startActivity(intent);
            return true;
        } else {
            return super.onContextItemSelected(item);
        }
    }

    /**
     * User has entered the portal to the hidden level.
     */
    private void enterHiddenLevel(Bundle savedInstanceState) {
        long endTime = SystemClock.elapsedRealtime();
        long elapsedMilliSeconds = endTime - startTime;
        player.updateTotalTime(elapsedMilliSeconds);
        Intent intent = new Intent(PlatformerMainActivity.this, PlatformerHiddenActivity.class);
        intent.putExtra("Player Name", player.getName());
        intent.putExtra("Game Manager", getPlayerManager());
        intent.putExtra("Character", game.getManager().getCharacterLocation());
        intent.putExtra("Platforms", game.getManager().getPlatformPositions());
        intent.putExtra("Score", game.getManager().getCharacterScore());

        startActivity(intent);
    }
    /**
     * User has successfully finished Platformer and will now move to the EndGamePage.
     */
    private void endGame() {
        long endTime = SystemClock.elapsedRealtime();
        long elapsedMilliSeconds = endTime - startTime;
        player.updateTotalTime(elapsedMilliSeconds);
        save(getPlayerManager());
        Intent intent = new Intent(PlatformerMainActivity.this, EndGameActivity.class);
        intent.putExtra("Player Name", player.getName());
        startActivity(intent);
    }
    /**
     * User has lost the Game i.e. no more lives left.
     */
    private void deadPage() {
        long endTime = SystemClock.elapsedRealtime();
        long elapsedMilliSeconds = endTime - startTime;
        player.updateTotalTime(elapsedMilliSeconds);
        save(getPlayerManager());
        Intent intent = new Intent(PlatformerMainActivity.this, DeadActivity.class);
        intent.putExtra("Player Name", player.getName());
        startActivity(intent);
    }

    /**
     * Method executes when the player starts the game.
     */
    @Override
    protected void onResume() {
        super.onResume();
        game.resume();
    }

    /**
     * Method for initializing left and right buttons.
     */
    private void buttons() {

        Button left = (Button) findViewById(R.id.left);
        left.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                game.getManager().left_button();
            }
        });

        Button right = (Button) findViewById(R.id.right);
        right.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                game.getManager().right_button();
            }
        });
    }

    @Override
    protected void onPause() {
        super.onPause();
        game.pause();
    }

    @Override
    public void save(PlayerManager playerManager) {
        super.save(playerManager);
        player.setCurrentLevel(3);
    }

}

