package org.exp.botservice.commands;
import com.pengrad.telegrambot.request.SendMessage;
import com.pengrad.telegrambot.response.SendResponse;
import lombok.RequiredArgsConstructor;
import org.exp.botservice.service.BotButtonService;
import org.exp.botservice.utils.Constant;
import org.exp.entity.State;
import org.exp.entity.TgUser;
import static org.exp.Main.telegramBot;

@RequiredArgsConstructor
public class CabinetCmd implements BotCommand{
    private final TgUser tgUser;

    @Override
    public void process() {
        SendMessage sendMessage = new SendMessage(
                tgUser.getChatId(),
                Constant.START_MSG
        );
        sendMessage.replyMarkup(BotButtonService.genCabinetButtons(tgUser));
        SendResponse response = telegramBot.execute(sendMessage);
        tgUser.setMessageId(response.message().messageId());
        tgUser.setState(State.CABINET);
    }
}
