package org.exp.botservice.commands;
import com.pengrad.telegrambot.request.EditMessageText;
import com.pengrad.telegrambot.request.SendMessage;
import com.pengrad.telegrambot.response.SendResponse;
import lombok.RequiredArgsConstructor;
import org.exp.Main;
import org.exp.botservice.gamelogic.GameLogic;
import org.exp.botservice.service.BotButtonService;
import org.exp.botservice.utils.Constant;
import org.exp.entity.State;
import org.exp.entity.TgUser;
import static org.exp.Main.telegramBot;
import static org.exp.botservice.utils.Constant.*;

@RequiredArgsConstructor
public class PlayGameCmd implements BotCommand {
    private final TgUser tgUser;
    private final String data;

    @Override
    public void process() {
        getAndSetChosenSymbol();
        tgUser.initializeBoard();

        // Agar foydalanuvchi ⭕ ni tanlagan bo'lsa, bot birinchi yurishi kerak
        if (tgUser.getPlayerSymbol().equals(O_SIGN)) {
            int[] botMove = findBestMove(tgUser.getGameBoard());
            tgUser.getGameBoard()[botMove[0]][botMove[1]] = 2; // Bot yurishi
        }

        // O'yin boshlanish xabarini yuborish
        EditMessageText editMessage = new EditMessageText(
                tgUser.getChatId(),
                tgUser.getMessageId(),
                formatGameStartMessage()
        );
        editMessage.replyMarkup(BotButtonService.genGameBoard(tgUser.getGameBoard(), tgUser.getPlayerSymbol()));
        SendResponse response = (SendResponse) telegramBot.execute(editMessage);
        tgUser.setMessageId(response.message().messageId());
        tgUser.setState(State.IN_GAME);
    }

    public void handleMove(int row, int col) {
        // Katakcha band bo'lsa
        if (tgUser.getGameBoard()[row][col] != 0) {
            return;
        }

        // Foydalanuvchi harakati
        tgUser.getGameBoard()[row][col] = 1;

        // Foydalanuvchi yutganligini tekshirish
        if (checkWin(tgUser.getGameBoard(), 1)) {
            sendResult(YOU_WIN_MSG);
            return;
        }

        // Doska to'liqligini tekshirish (foydalanuvchi yurishidan keyin)
        if (isBoardFull(tgUser.getGameBoard())) {
            sendResult(DRAW_MSG);
            return;
        }

        // Bot harakati
        int[] botMove = new GameLogic().findBestMove(tgUser.getGameBoard());
        tgUser.getGameBoard()[botMove[0]][botMove[1]] = 2;

        // Bot yutganligini tekshirish
        if (checkWin(tgUser.getGameBoard(), 2)) {
            sendResult(YOU_LOSE_MSG);
            return;
        }

        // Doska to'liqligini tekshirish (bot yurishidan keyin)
        if (isBoardFull(tgUser.getGameBoard())) {
            sendResult(DRAW_MSG);
            return;
        }
        updateGameBoard();
    }

    // Doskani yangilash
    private void updateGameBoard() {
        EditMessageText editMessage = new EditMessageText(
                tgUser.getChatId(),
                tgUser.getMessageId(),
                formatGameStartMessage()
        );
        editMessage.replyMarkup(BotButtonService.genGameBoard(tgUser.getGameBoard(), tgUser.getPlayerSymbol()));
        Main.telegramBot.execute(editMessage);
    }

    // Bot uchun eng yaxshi yurishni topish
    private int[] findBestMove(int[][] board) {
        GameLogic gameLogic = new GameLogic();
        return gameLogic.findBestMove(board);
    }

    // Yutishni tekshirish
    private boolean checkWin(int[][] board, int player) {
        int size = board.length;

        // Gorizontal va vertikal
        for (int i = 0; i < size; i++) {
            boolean rowWin = true;
            boolean colWin = true;
            for (int j = 0; j < size; j++) {
                if (board[i][j] != player) rowWin = false;
                if (board[j][i] != player) colWin = false;
            }
            if (rowWin || colWin) return true;
        }

        // Diagonal
        boolean diag1 = true;
        boolean diag2 = true;
        for (int i = 0; i < size; i++) {
            if (board[i][i] != player) diag1 = false;
            if (board[i][size - 1 - i] != player) diag2 = false;
        }
        return diag1 || diag2;
    }

    // Doska to'liq to'ldirilganligini tekshirish
    private boolean isBoardFull(int[][] board) {
        for (int[] row : board) {
            for (int cell : row) {
                if (cell == 0) return false;
            }
        }
        return true;
    }

    // Natijani yuborish
    private void sendResult(String resultMessage) {
        String boardState = formatBoard(tgUser.getGameBoard());
        EditMessageText editMessage = new EditMessageText(
                tgUser.getChatId(), tgUser.getMessageId(),
                RESULT_MSG + "\n" + boardState + "\n" + resultMessage
        );
        Main.telegramBot.execute(editMessage);

        SendMessage sendMainMessage = new SendMessage(
                tgUser.getChatId(),
                Constant.START_MSG
        );
        sendMainMessage.replyMarkup(BotButtonService.genCabinetButtons(tgUser));
        SendResponse sendResponse = telegramBot.execute(sendMainMessage);
        tgUser.setMessageId(sendResponse.message().messageId());
        tgUser.setState(State.CABINET);

        // Doskani reset qilish
        tgUser.initializeBoard();
    }

    // Doskani formatlash
    private String formatBoard(int[][] board) {
        StringBuilder sb = new StringBuilder();
        for (int[] row : board) {
            for (int cell : row) {
                String symbol;
                if (cell == 1) {
                    symbol = tgUser.getPlayerSymbol();
                } else if (cell == 2) {
                    symbol = tgUser.getBotSymbol();
                } else {
                    symbol = EMPTY_SIGN; // Bo‘sh joy
                }
                sb.append(symbol);
            }
            sb.append("\n");
        }
        return sb.toString();
    }


    private String formatGameStartMessage() {
        return GAME_MENU_MSG.formatted(
                tgUser.getPlayerSymbol(),
                tgUser.getBotSymbol()
        );
    }

    private void getAndSetChosenSymbol() {
        if (data.equals("CHOOSE_X")) {
            tgUser.setPlayerSymbol(X_SIGN);
            tgUser.setBotSymbol(O_SIGN);
        } else if (data.equals("CHOOSE_O")) {
            tgUser.setPlayerSymbol(O_SIGN);
            tgUser.setBotSymbol(X_SIGN);
        } else {
            SendMessage sendMessage = new SendMessage(tgUser.getChatId(), "Xato: Belgilanmagan belgi!");
            sendMessage.replyMarkup(BotButtonService.genCabinetButtons(tgUser));
            SendResponse sendResponse = telegramBot.execute(sendMessage);
            tgUser.setMessageId(sendResponse.message().messageId());
        }
    }
}