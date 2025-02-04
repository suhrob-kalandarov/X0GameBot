package org.exp.botservice.commands;
import com.pengrad.telegrambot.request.EditMessageText;
import com.pengrad.telegrambot.response.SendResponse;
import org.exp.botservice.utils.Constant;
import org.exp.entity.State;
import org.exp.entity.TgUser;
import static org.exp.Main.telegramBot;
import static org.exp.botservice.service.BotButtonService.chooseSymbolButtons;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class SelectionSymbolCmd implements BotCommand {
    private final TgUser tgUser;

    @Override
    public void process() {
        EditMessageText editMessageText = new EditMessageText(
                tgUser.getChatId(),
                tgUser.getMessageId(),
                Constant.CHOOSE_SYMBOL_MSG
        );
        editMessageText.replyMarkup(chooseSymbolButtons());
        tgUser.setState(State.SYMBOL_CHOOSING);
        SendResponse response = (SendResponse) telegramBot.execute(editMessageText);
        tgUser.setMessageId(response.message().messageId());
    }
}