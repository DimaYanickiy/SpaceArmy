package com.army.spacearmy;

public interface SaveInterface {

    boolean isFirstGame();
    void setFirstGame(boolean firstGame);
    boolean isFirstAppsFlyer();
    void setFirstAppsFlyer(boolean firstAppsFlyer);
    boolean isFirstRef();
    void setFirstRef(boolean firstRef);
    String getGameUrl();
    void setGameUrl(String gameUrl);
}
