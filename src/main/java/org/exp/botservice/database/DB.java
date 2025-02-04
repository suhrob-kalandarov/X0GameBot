package org.exp.botservice.database;
import org.exp.entity.TgUser;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public interface DB {

    List<TgUser> TG_USERS_LIST = new ArrayList<>();

    static TgUser getOrCreateUser (Long chatId) {
        Optional<TgUser> optionalTgUser = TG_USERS_LIST.stream()
                .filter(item ->
                        item.getChatId().equals(chatId))
                .findFirst();
        if (optionalTgUser.isPresent()) {
            return optionalTgUser.get();
        }
        else {
            TgUser tgUser = TgUser
                    .builder()
                    .chatId(chatId)
                    .build();
            TG_USERS_LIST.add(tgUser);
            return tgUser;
        }
    }
}