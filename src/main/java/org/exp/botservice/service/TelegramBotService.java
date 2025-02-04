package org.exp.botservice.service;
import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.request.DeleteMessage;
import com.pengrad.telegrambot.request.SendMessage;
import org.exp.botservice.commands.*;
import org.exp.botservice.utils.Constant;
import org.exp.entity.TgUser;

import static java.util.Objects.requireNonNull;
import static org.exp.Main.telegramBot;
import static org.exp.botservice.database.DB.*;
import static org.exp.botservice.utils.Constant.*;

public interface TelegramBotService {
    static void handleUpdate(Update update) {
        try {
            /// processing message/text requests
            if (update.message() != null) {
                Long chatId = update.message().chat().id();
                TgUser tgUser = getOrCreateUser(chatId);
                String text = update.message().text();
                BotCommand command;

                if (text != null && text.equals("/start")) {
                    command = new CabinetCmd(tgUser);
                    if (tgUser.getMessageId()!=null) {
                        telegramBot.execute(new DeleteMessage(
                                tgUser.getChatId(),
                                tgUser.getMessageId())
                        );
                    }
                }
                else {
                    telegramBot.execute(new SendMessage(chatId, Constant.WARNING_MSG));
                    command = new CabinetCmd(tgUser);
                }
                requireNonNull(command).process();
            }

            /// processing callback query/requests
            if (update.callbackQuery() != null) {
                BotCommand command = null;
                Long chatId = update.callbackQuery().from().id();
                TgUser tgUser = getOrCreateUser(chatId);
                String data = update.callbackQuery().data();

                if (data.startsWith("cell_")) {
                    command = new InGame(tgUser, data);
                }
                else if (data.equals(PLAY_WITH_BOT_MSG)) {
                    command = new SelectionSymbolCmd(tgUser);
                }
                else if (data.startsWith("CHOOSE_")) {
                    command = new PlayGameCmd(tgUser, data);
                }
                else if (data.startsWith("back")) {
                    command = new BackButtonCmd(tgUser, data);
                }
                requireNonNull(command).process();
            }
        } catch (Exception e){
            e.printStackTrace();
        }
    }
}