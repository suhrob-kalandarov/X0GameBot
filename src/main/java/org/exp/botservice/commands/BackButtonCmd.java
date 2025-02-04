package org.exp.botservice.commands;
import com.pengrad.telegrambot.request.EditMessageText;
import com.pengrad.telegrambot.response.SendResponse;
import lombok.RequiredArgsConstructor;
import org.exp.botservice.service.BotButtonService;
import org.exp.entity.State;
import org.exp.entity.TgUser;
import static org.exp.Main.telegramBot;
import static org.exp.botservice.utils.Constant.*;

@RequiredArgsConstructor
public class BackButtonCmd implements BotCommand{
    private final TgUser tgUser;
    private final String data;

    @Override
    public void process() {
        if (data.equals("back_to_cabinet")) {
            EditMessageText editMessageText = new EditMessageText(
                    tgUser.getChatId(),
                    tgUser.getMessageId(),
                    START_MSG + "\n\n" + CHOOSE_AN_OPTION
            );
            editMessageText.replyMarkup(BotButtonService.genCabinetButtons(tgUser));
            tgUser.setState(State.CABINET);
            SendResponse response = (SendResponse) telegramBot.execute(editMessageText);
            tgUser.setMessageId(response.message().messageId());
        }
    }
}
