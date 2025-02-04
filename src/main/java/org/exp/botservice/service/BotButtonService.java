package org.exp.botservice.service;
import com.pengrad.telegrambot.model.request.InlineKeyboardButton;
import com.pengrad.telegrambot.model.request.InlineKeyboardMarkup;
import org.exp.entity.TgUser;
import static org.exp.botservice.utils.Constant.*;

public interface BotButtonService {
    static InlineKeyboardMarkup genCabinetButtons(TgUser tgUser) {
        return new InlineKeyboardMarkup().
                addRow(
                        new InlineKeyboardButton(PLAY_WITH_BOT_MSG)
                                .callbackData(PLAY_WITH_BOT_MSG)
                );
    }

    static InlineKeyboardMarkup genGameBoard(int[][] board, String playerSymbol) {
        InlineKeyboardMarkup markup = new InlineKeyboardMarkup();
        for (int i = 0; i < board.length; i++) {
            InlineKeyboardButton[] row = new InlineKeyboardButton[board[i].length];
            for (int j = 0; j < board[i].length; j++) {
                String symbol = switch (board[i][j]) {
                    case 1 -> playerSymbol;
                    case 2 -> (playerSymbol.equals(X_SIGN) ? O_SIGN : X_SIGN);
                    default -> EMPTY_SIGN;
                };
                row[j] = new InlineKeyboardButton(symbol)
                        .callbackData("cell_" + i + "_" + j);
            }
            markup.addRow(row);
        }
        return markup;
    }

    static InlineKeyboardMarkup chooseSymbolButtons() {
        return new InlineKeyboardMarkup()
                .addRow(new InlineKeyboardButton(X_SIGN).callbackData("CHOOSE_X"),
                        new InlineKeyboardButton(O_SIGN).callbackData("CHOOSE_O")
                )
                .addRow(new InlineKeyboardButton(BACK_BUTTON_MSG).callbackData("back_to_cabinet"));
    }
}