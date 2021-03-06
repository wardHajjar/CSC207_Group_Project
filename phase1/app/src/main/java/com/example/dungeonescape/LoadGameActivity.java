package com.example.dungeonescape;

import androidx.appcompat.app.AppCompatActivity;
import com.example.dungeonescape.Maze.MazeActivity;
import com.example.dungeonescape.brickbreaker.BBMainActivity;
import com.example.dungeonescape.platformer.PlatformerMainActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import java.util.ArrayList;

/** Loads the game data. */
public class LoadGameActivity extends AppCompatActivity {
    Player player;
    GameManager gameManager;
    Spinner spinner;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_load_game);
        Intent i = getIntent();
        gameManager = (GameManager) i.getSerializableExtra("Game Manager");
        setSpinner();
        buttons();
    }

    /** Create and set up the drop-down menu. */
    private void setSpinner() {
        spinner = findViewById(R.id.spinner);
        ArrayList<String> names = gameManager.getPlayerNames();
        String[] arr = names.toArray(new String[names.size()]);

        ArrayAdapter<String> myAdapter = new ArrayAdapter<>(LoadGameActivity.this,
                android.R.layout.simple_list_item_1, arr);
        myAdapter.setDropDownViewResource(android.R.layout.simple_list_item_1);
        spinner.setAdapter(myAdapter);
    }

    /** Creates and sets up button functionality. */
    private void buttons() {
        Button enter = findViewById(R.id.Enter);
        enter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String text = spinner.getSelectedItem().toString();
                player = gameManager.getPlayer(text);
                progress();
            }
        });
    }

    /** Loads specific game based on player's level. */
    private void progress() {
        int level = player.getCurrentLevel();
        if (level == 1 || level == 0) {
            Intent intent = new Intent(LoadGameActivity.this, BBMainActivity.class);
            intent.putExtra("Player", player);
            intent.putExtra("Game Manager", gameManager);
            startActivity(intent);
        } else if(level == 2) {
            Intent intent = new Intent(LoadGameActivity.this, MazeActivity.class);
            intent.putExtra("Player", player);
            intent.putExtra("Game Manager", gameManager);
            startActivity(intent);
        } else {
            Intent intent = new Intent(LoadGameActivity.this, PlatformerMainActivity.class);
            intent.putExtra("Player", player);
            intent.putExtra("Game Manager", gameManager);
            startActivity(intent);
        }
    }
}
