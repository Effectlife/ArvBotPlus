package be.effectlife.arvbotplus.services;

public interface GiveawayService {
    boolean enterName(String name);
    boolean lastCall();
    String drawWinner();
    boolean setup(String keyword);
    String getKeyword();
    String getWinPercentage();

}
