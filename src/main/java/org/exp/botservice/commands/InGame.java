package org.exp.botservice.commands;
import lombok.RequiredArgsConstructor;
import org.exp.entity.TgUser;

@RequiredArgsConstructor
public class InGame implements BotCommand {
    private final TgUser tgUser;
    private final String data;

    @Override
    public void process() {
        int row = Integer.parseInt(data.split("_")[1]);
        int col = Integer.parseInt(data.split("_")[2]);
        PlayGameCmd playGameCmd = new PlayGameCmd(tgUser, data);
        playGameCmd.handleMove(row, col);
    }
}
