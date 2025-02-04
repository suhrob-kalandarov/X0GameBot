package org.exp.entity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TgUser {
    private Long chatId;
    private Integer messageId;
    private State state;
    private String playerSymbol;
    private String botSymbol;
    private int[][] gameBoard;

    public void initializeBoard() {
        this.gameBoard = new int[3][3];
    }
}