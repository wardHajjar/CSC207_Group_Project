package com.example.dungeonescape.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.example.dungeonescape.player.PlayerManager;
import com.example.dungeonescape.R;
import com.example.dungeonescape.game.SaveData;

import java.io.File;

public class MainActivity extends AppCompatActivity{
    private PlayerManager playerManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Intent i = getIntent();
        playerManager = (PlayerManager) i.getSerializableExtra("Game Manager");
        load();
        if (playerManager == null) {
            playerManager = new PlayerManager();
        }
        configureActionButtons();
    }

    private void configureActionButtons() {
        configureNewGameButton();
        configureLoadGameButton();
    }

    private void configureNewGameButton() {
        Button newGame = findViewById(R.id.newGame);
        newGame.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, NewGameActivity.class);
                intent.putExtra("Game Manager", playerManager);
                startActivity(intent);
            }
        });
    }

    private void configureLoadGameButton() {
        Button loadGame = (Button) findViewById(R.id.loadGame);
        loadGame.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, LoadGameActivity.class);
                intent.putExtra("Game Manager", playerManager);
                startActivity(intent);
            }
        });
    }

    private void load() {
        try {
            String filePath = this.getFilesDir().getPath() + "/GameState.txt";
            File f = new File(filePath);
            playerManager = (PlayerManager) SaveData.load(f);
        }
        catch (Exception e) {
            System.out.println("Couldn't load load data: " + e.getMessage());
        }
    }
}
