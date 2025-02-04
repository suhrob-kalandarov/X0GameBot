package org.exp;
import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.UpdatesListener;
import com.pengrad.telegrambot.model.Update;
import org.exp.botservice.service.TelegramBotService;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Main {
    public static TelegramBot telegramBot = new TelegramBot("your-bot-token");
    public static ExecutorService executorService = Executors.newFixedThreadPool(5);
    public static void main(String[] args) {
        telegramBot.removeGetUpdatesListener();
        telegramBot.setUpdatesListener(updates -> {
            for (Update update : updates) {
                executorService.execute(() -> {
                    TelegramBotService.handleUpdate(update);
                });
            }
            return UpdatesListener.CONFIRMED_UPDATES_ALL;
        });
    }
}