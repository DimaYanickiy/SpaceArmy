package com.army.spacearmy;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

public class PlayActivity extends AppCompatActivity {

    TextView money, shield, speed, gun, price_shield, price_speed, price_gun, fr, click;
    ImageButton buttonStop, button_shield, button_speed, button_gun, nextRocket;
    ImageView red_fraction, yellow_fraction, silver_fraction, mainRocket;
    LinearLayout fr2, back;
    SharedPreferences spreferences;

    private int fraction;

    private int playMoney;
    private int moneyClick;
    private int moneyPerRotation;

    private double coefficient;
    private double coefficientPerClick;
    private double coefficientEveryRotation;

    private double rotationSpeed;
    private double rocketRotation;

    private int priceOfBoosters[] = new int[3];
    private int levels[] = new int[3]; //massive of levels Shield Speed Gun
    private int lv[] = {5, 10, 15, 20, 25, 30, 35, 40, 45, 50};
    private int maxLevelsOfBoosters[] = {50, 50, 50};
    private boolean isShield = false, isSpeed = false, isGun = false, isChange = false;
    private int counter;

    private boolean running;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_play);

        loadFields();

        /*========TextViews========*/
        money = findViewById(R.id.money);
        shield = findViewById(R.id.shield);
        speed = findViewById(R.id.speed);
        gun = findViewById(R.id.gun);
        price_shield = findViewById(R.id.price_shield);
        price_speed = findViewById(R.id.price_speed);
        price_gun = findViewById(R.id.price_gun);
        click = findViewById(R.id.click);

        /*========ImageButtons========*/
        buttonStop = findViewById(R.id.buttonStop);
        button_shield = findViewById(R.id.button_shield);
        button_speed = findViewById(R.id.button_speed);
        button_gun = findViewById(R.id.button_gun);

        /*========ImageViews========*/
        red_fraction = findViewById(R.id.red_fraction);
        yellow_fraction = findViewById(R.id.yellow_fraction);
        silver_fraction = findViewById(R.id.silver_fraction);
        nextRocket = findViewById(R.id.nextRocket);
        mainRocket = findViewById(R.id.mainRocket);

        money.setText(Integer.toString(playMoney));

        fr = findViewById(R.id.fr);
        fr2 = findViewById(R.id.fr2);
        back = findViewById(R.id.back);

        Intent intent = getIntent();
        int newGame = intent.getIntExtra("newGame", 1);
        if(newGame == 1){
            nullAllFields();
        } else{

        }

        checkFractionAndUpdate();
        click.setVisibility(View.GONE);

        red_fraction.setOnClickListener(v -> {
            fraction = 1;
            checkFractionAndUpdate();
        });
        silver_fraction.setOnClickListener(v -> {
            fraction = 2;
            checkFractionAndUpdate();
        });
        yellow_fraction.setOnClickListener(v -> {
            fraction = 3;
            checkFractionAndUpdate();
        });
        mainRocket.setOnClickListener(v -> {
            if(running){
                playMoney += moneyClick * coefficientPerClick;
                money.setText(Integer.toString(playMoney));
            }
        });
        nextRocket.setOnClickListener(v -> {
            checkPriceOfBoosters(4);
        });
        button_shield.setOnClickListener(v -> {
            if (playMoney >= priceOfBoosters[0] && running) {
                coefficientEveryRotation += 0.1;
                playMoney -= priceOfBoosters[0];
                money.setText(Integer.toString(playMoney));
                levels[0]++;
                shield.setText("Shield Lvl." + levels[0]);
                priceOfBoosters[0] *= coefficient;
                price_shield.setText(Integer.toString(priceOfBoosters[0]));
                checkPriceOfBoosters(1);
            }
        });
        button_speed.setOnClickListener(v -> {
            if (playMoney >= priceOfBoosters[1] && running) {
                coefficientEveryRotation += 0.1;
                playMoney -= priceOfBoosters[1];
                money.setText(Integer.toString(playMoney));
                levels[1]++;
                speed.setText("Speed Lvl." + levels[1]);
                priceOfBoosters[1] *= coefficient;
                price_speed.setText(Integer.toString(priceOfBoosters[1]));
                checkPriceOfBoosters(2);
            }
        });
        button_gun.setOnClickListener(v -> {
            if (playMoney >= priceOfBoosters[2] && running) {
                coefficientEveryRotation += 0.1;
                playMoney -= priceOfBoosters[2];
                money.setText(Integer.toString(playMoney));
                levels[2]++;
                gun.setText("Gun Lvl." + levels[2]);
                priceOfBoosters[2] *= coefficient;
                price_gun.setText(Integer.toString(priceOfBoosters[2]));
                checkPriceOfBoosters(3);
            }
        });
        buttonStop.setOnClickListener(v -> {
            if(running){
                running = false;
                buttonStop.setImageDrawable(getResources().getDrawable(R.drawable.btn_stop1));
            } else if(!running){
                running = true;
                buttonStop.setImageDrawable(getResources().getDrawable(R.drawable.btn_stop));
            }
            saveFields();

        });

        time();
    }

    private void checkFractionAndUpdate() {
        if (fraction == 1) {
            mainRocket.setImageDrawable(getResources().getDrawable(R.drawable.r1_red));
            nextRocket.setImageDrawable(getResources().getDrawable(R.drawable.r2_red));
            fr.setVisibility(View.GONE);
            fr2.setVisibility(View.GONE);
            mainRocket.setVisibility(View.VISIBLE);
            running = true;
        } else if (fraction == 2) {
            mainRocket.setImageDrawable(getResources().getDrawable(R.drawable.r1_silver));
            fr.setVisibility(View.GONE);
            fr2.setVisibility(View.GONE);
            mainRocket.setVisibility(View.VISIBLE);
            nextRocket.setImageDrawable(getResources().getDrawable(R.drawable.r2_silver));
            running = true;
        } else if (fraction == 3) {
            mainRocket.setImageDrawable(getResources().getDrawable(R.drawable.r1_yellow));
            nextRocket.setImageDrawable(getResources().getDrawable(R.drawable.r2_yellow));
            fr.setVisibility(View.GONE);
            fr2.setVisibility(View.GONE);
            mainRocket.setVisibility(View.VISIBLE);
            running = true;
        } else if (fraction == 0) {
            fr.setVisibility(View.VISIBLE);
            fr2.setVisibility(View.VISIBLE);
            mainRocket.setVisibility(View.GONE);
            running = false;
        }
    }

    private void time() {
        Handler h = new Handler();
        h.post(new Runnable() {
            @Override
            public void run() {
                if(running){
                    if (rocketRotation >= 360) {
                        rocketRotation = 0;
                        playMoney += moneyPerRotation * coefficientEveryRotation;
                        money.setText(Integer.toString(playMoney));
                    }
                    click.setVisibility(View.GONE);
                    nextRocket.setBackgroundColor(getResources().getColor(R.color.back_disable));
                    if((levels[0] == lv[counter] && levels[1] == lv[counter] && levels[2] == lv[counter]) ||
                            (levels[0] >= lv[counter] && levels[1] == lv[counter] && levels[2] == lv[counter]) ||
                            (levels[0] == lv[counter] && levels[1] >= lv[counter] && levels[2] == lv[counter]) ||
                            (levels[0] == lv[counter] && levels[1] == lv[counter] && levels[2] >= lv[counter]) ||
                            (levels[0] >= lv[counter] && levels[1] >= lv[counter] && levels[2] == lv[counter]) ||
                            (levels[0] == lv[counter] && levels[1] >= lv[counter] && levels[2] >= lv[counter]) ||
                            (levels[0] >= lv[counter] && levels[1] >= lv[counter] && levels[2] >= lv[counter])){
                        nextRocket.setBackgroundColor(getResources().getColor(R.color.back));
                        isChange = true;
                        click.setVisibility(View.VISIBLE);
                    }
                    rocketRotation += rotationSpeed;
                    mainRocket.setRotation((float) rocketRotation);
                }
                h.postDelayed(this, 10);
            }
        });
    }

    private void checkPriceOfBoosters(int n) {
        int k[] = {0,0,0,1}; // k - current level (5, 10, 15, ... , n) to check
        if(n == 1){
            isShield = true;
            for(int i = k[0]; i < lv.length; i++){
                if(levels[0] == lv[i] && levels[0] < maxLevelsOfBoosters[0] && isShield){
                    k[0]++;
                    moneyPerRotation += 10;
                    isShield = false;
                }
            }
        }
        if(n == 1){
            isSpeed = true;
            for(int i = k[1]; i < lv.length; i++){
                if(levels[1] == lv[i] && levels[1] < maxLevelsOfBoosters[1] && isSpeed){
                    k[1]++;
                    rotationSpeed += 0.1;
                    isSpeed = false;
                }
            }
        }
        if(n == 1){
            isGun = true;
            for(int i = k[2]; i < lv.length; i++){
                if(levels[2] == lv[i] && levels[2] < maxLevelsOfBoosters[2] && isGun && !isShield && !isSpeed){
                    k[2]++;
                    priceOfBoosters[0] *= 0.9;
                    priceOfBoosters[1] *= 0.9;
                    price_shield.setText(Integer.toString(priceOfBoosters[0]));
                    price_speed.setText(Integer.toString(priceOfBoosters[1]));
                    isGun = false;
                }
            }
        }
        if(n == 4){
            if(isChange){
                System.out.println(counter);
                counter++;
                changeRoket(counter);
                moneyClick*=5;
                isChange = false;
            }
        }
    }

    private void changeRoket(int n){
        switch(n) {
            case 0:
                if (fraction == 1) {
                    mainRocket.setImageDrawable(getResources().getDrawable(R.drawable.r1_red));
                    nextRocket.setImageDrawable(getResources().getDrawable(R.drawable.r2_red));
                    updatePrices();
                }
                if (fraction == 2) {
                    mainRocket.setImageDrawable(getResources().getDrawable(R.drawable.r1_silver));
                    nextRocket.setImageDrawable(getResources().getDrawable(R.drawable.r2_silver));
                    updatePrices();
                }
                if (fraction == 3) {
                    mainRocket.setImageDrawable(getResources().getDrawable(R.drawable.r1_yellow));
                    nextRocket.setImageDrawable(getResources().getDrawable(R.drawable.r2_yellow));
                    updatePrices();
                }
                break;
            case 1:
                if (fraction == 1) {
                    mainRocket.setImageDrawable(getResources().getDrawable(R.drawable.r2_red));
                    nextRocket.setImageDrawable(getResources().getDrawable(R.drawable.r2_red));
                    updatePrices();
                }
                if (fraction == 2) {
                    mainRocket.setImageDrawable(getResources().getDrawable(R.drawable.r2_silver));
                    nextRocket.setImageDrawable(getResources().getDrawable(R.drawable.r3_silver));
                    updatePrices();
                }
                if (fraction == 3) {
                    mainRocket.setImageDrawable(getResources().getDrawable(R.drawable.r2_yellow));
                    nextRocket.setImageDrawable(getResources().getDrawable(R.drawable.r3_yellow));
                    updatePrices();
                }
                break;
            case 2:
                if (fraction == 1) {
                    mainRocket.setImageDrawable(getResources().getDrawable(R.drawable.r3_red));
                    nextRocket.setImageDrawable(getResources().getDrawable(R.drawable.r4_red));
                    updatePrices();
                }
                if (fraction == 2) {
                    mainRocket.setImageDrawable(getResources().getDrawable(R.drawable.r3_silver));
                    nextRocket.setImageDrawable(getResources().getDrawable(R.drawable.r4_silver));
                    updatePrices();
                }
                if (fraction == 3) {
                    mainRocket.setImageDrawable(getResources().getDrawable(R.drawable.r3_yellow));
                    nextRocket.setImageDrawable(getResources().getDrawable(R.drawable.r4_yellow));
                    updatePrices();
                }
                break;
            case 3:
                if (fraction == 1) {
                    mainRocket.setImageDrawable(getResources().getDrawable(R.drawable.r4_red));
                    nextRocket.setImageDrawable(getResources().getDrawable(R.drawable.r5_red));
                    updatePrices();
                }
                if (fraction == 2) {
                    mainRocket.setImageDrawable(getResources().getDrawable(R.drawable.r4_silver));
                    nextRocket.setImageDrawable(getResources().getDrawable(R.drawable.r5_silver));
                    updatePrices();
                }
                if (fraction == 3) {
                    mainRocket.setImageDrawable(getResources().getDrawable(R.drawable.r4_yellow));
                    nextRocket.setImageDrawable(getResources().getDrawable(R.drawable.r5_yellow));
                    updatePrices();
                }
                break;
            case 4:
                if (fraction == 1) {
                    mainRocket.setImageDrawable(getResources().getDrawable(R.drawable.r5_red));
                    nextRocket.setImageDrawable(getResources().getDrawable(R.drawable.r6_red));
                    updatePrices();
                }
                if (fraction == 2) {
                    mainRocket.setImageDrawable(getResources().getDrawable(R.drawable.r5_silver));
                    nextRocket.setImageDrawable(getResources().getDrawable(R.drawable.r6_silver));
                    updatePrices();
                }
                if (fraction == 3) {
                    mainRocket.setImageDrawable(getResources().getDrawable(R.drawable.r5_yellow));
                    nextRocket.setImageDrawable(getResources().getDrawable(R.drawable.r6_yellow));
                    updatePrices();
                }
                break;
            case 5:
                if (fraction == 1) {
                    mainRocket.setImageDrawable(getResources().getDrawable(R.drawable.r6_red));
                    nextRocket.setImageDrawable(getResources().getDrawable(R.drawable.r7_red));
                    updatePrices();
                }
                if (fraction == 2) {
                    mainRocket.setImageDrawable(getResources().getDrawable(R.drawable.r6_silver));
                    nextRocket.setImageDrawable(getResources().getDrawable(R.drawable.r7_silver));
                    updatePrices();
                }
                if (fraction == 3) {
                    mainRocket.setImageDrawable(getResources().getDrawable(R.drawable.r6_yellow));
                    nextRocket.setImageDrawable(getResources().getDrawable(R.drawable.r7_yellow));
                    updatePrices();
                }
                break;
            case 6:
                if (fraction == 1) {
                    mainRocket.setImageDrawable(getResources().getDrawable(R.drawable.r7_red));
                    nextRocket.setImageDrawable(getResources().getDrawable(R.drawable.r8_red));
                    updatePrices();
                }
                if (fraction == 2) {
                    mainRocket.setImageDrawable(getResources().getDrawable(R.drawable.r7_silver));
                    nextRocket.setImageDrawable(getResources().getDrawable(R.drawable.r8_silver));
                    updatePrices();
                }
                if (fraction == 3) {
                    mainRocket.setImageDrawable(getResources().getDrawable(R.drawable.r7_yellow));
                    nextRocket.setImageDrawable(getResources().getDrawable(R.drawable.btn_stop1));
                    nextRocket.setOnClickListener(v -> {
                        nullAllFields();
                    });
                    updatePrices();
                }
                break;
            case 7:
                if (fraction == 1) {
                    mainRocket.setImageDrawable(getResources().getDrawable(R.drawable.r8_red));
                    nextRocket.setImageDrawable(getResources().getDrawable(R.drawable.r9_red));
                    updatePrices();
                }
                if (fraction == 2) {
                    mainRocket.setImageDrawable(getResources().getDrawable(R.drawable.r8_silver));
                    nextRocket.setImageDrawable(getResources().getDrawable(R.drawable.r9_silver));
                    updatePrices();
                }
                break;
            case 8:
                if (fraction == 1) {
                    mainRocket.setImageDrawable(getResources().getDrawable(R.drawable.r9_red));
                    nextRocket.setImageDrawable(getResources().getDrawable(R.drawable.r10_red));
                    updatePrices();
                }
                if (fraction == 2) {
                    mainRocket.setImageDrawable(getResources().getDrawable(R.drawable.r9_silver));
                    nextRocket.setImageDrawable(getResources().getDrawable(R.drawable.btn_stop1));
                    nextRocket.setOnClickListener(v -> {
                        nullAllFields();
                    });
                    updatePrices();
                }
                break;
            case 9:
                if (fraction == 1) {
                    mainRocket.setImageDrawable(getResources().getDrawable(R.drawable.r10_red));
                    nextRocket.setImageDrawable(getResources().getDrawable(R.drawable.r11_red));
                    updatePrices();
                }
                break;
            case 10:
                if (fraction == 1) {
                    mainRocket.setImageDrawable(getResources().getDrawable(R.drawable.r11_red));
                    nextRocket.setImageDrawable(getResources().getDrawable(R.drawable.btn_stop1));
                    nextRocket.setOnClickListener(v -> {
                        nullAllFields();
                    });
                    updatePrices();
                }
                break;
        }


    }

    private void updatePrices(){
        priceOfBoosters[0] *= 0.5;
        priceOfBoosters[1] *= 0.5;
        priceOfBoosters[2] *= 0.5;
        price_shield.setText(Integer.toString(priceOfBoosters[0]));
        price_speed.setText(Integer.toString(priceOfBoosters[1]));
        price_gun.setText(Integer.toString(priceOfBoosters[2]));
    }

    private void nullAllFields(){
        playMoney = 10;
        money.setText(Integer.toString(playMoney));
        moneyClick = 1;
        priceOfBoosters[0] = 10;
        price_shield.setText(Integer.toString(priceOfBoosters[0]));
        priceOfBoosters[1] = 10;
        price_speed.setText(Integer.toString(priceOfBoosters[1]));
        priceOfBoosters[2] = 10;
        price_gun.setText(Integer.toString(priceOfBoosters[2]));
        moneyPerRotation = 20;
        coefficient = 1.5;
        coefficientPerClick = 1;
        coefficientEveryRotation = 2;
        rotationSpeed = 0.5;
        levels[0] = 1;
        shield.setText("Shield Lvl." + levels[0]);
        levels[1] = 1;
        speed.setText("Speed Lvl." + levels[1]);
        levels[2] = 1;
        gun.setText("Gun Lvl." + levels[2]);
        counter = 0;
        fraction = 0;
        checkFractionAndUpdate();
        changeRoket(counter);
    }

    private void saveFields(){
        spreferences = getPreferences(MODE_PRIVATE);
        SharedPreferences.Editor editor = spreferences.edit();
        editor.putInt("playMoney", playMoney);
        editor.putInt("moneyClick", moneyClick);
        editor.putInt("priceOfBoosters1", priceOfBoosters[0]);
        editor.putInt("priceOfBoosters2", priceOfBoosters[1]);
        editor.putInt("priceOfBoosters3", priceOfBoosters[2]);
        editor.putInt("moneyPerRotation", moneyPerRotation);
        editor.putFloat("coefficient", (float) coefficient);
        editor.putFloat("coefficientPerClick", (float) coefficientPerClick);
        editor.putFloat("coefficientEveryRotation", (float) coefficientEveryRotation);
        editor.putFloat("rotationSpeed", (float) rotationSpeed);
        editor.putInt("levels1", levels[0]);
        editor.putInt("levels2", levels[1]);
        editor.putInt("levels3", levels[2]);
        editor.putInt("counter", counter);
        editor.putInt("fraction", fraction);

        editor.commit();

    }

    public void loadFields(){
        spreferences = getPreferences(MODE_PRIVATE);
        playMoney = spreferences.getInt("playMoney", 10);
        moneyClick = spreferences.getInt("moneyClick", 1);
        priceOfBoosters[0] = spreferences.getInt("priceOfBoosters1", 10);
        priceOfBoosters[1] = spreferences.getInt("priceOfBoosters2", 10);
        priceOfBoosters[2] = spreferences.getInt("priceOfBoosters3", 10);
        moneyPerRotation = spreferences.getInt("moneyPerRotation", 10);
        levels[0] = spreferences.getInt("levels1", 1);
        levels[1] = spreferences.getInt("levels2", 1);
        levels[2] = spreferences.getInt("levels3", 1);
        counter = spreferences.getInt("counter", 0);
        fraction = spreferences.getInt("fraction", 0);

        coefficient = spreferences.getFloat("coefficient", (float) 1.5);
        coefficientPerClick = spreferences.getFloat("coefficientPerClick", 1);
        coefficientEveryRotation = spreferences.getFloat("coefficientEveryRotation", 2);
        rotationSpeed = spreferences.getFloat("rotationSpeed", (float) 0.5);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        saveFields();
        finish();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        saveFields();
    }
}