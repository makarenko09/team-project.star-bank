package org.skypro.star.listener;

import lombok.NonNull;
import org.telegram.abilitybots.api.sender.SilentSender;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.bots.AbsSender;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.HashMap;
import java.util.Map;
public class ResponseHandler {
    private final SilentSender silentSender;
    private final Map<Long, String> chatStates = new HashMap<>();  // Если нужно для будущих состояний

    public ResponseHandler(SilentSender silentSender) {
        this.silentSender = silentSender;
    }

    // Можно добавить методы для состояний, если потребуется
    public void handle(Update update) {
        Long chatId = update.getMessage().getChatId();
        silentSender.send("Ответ от бота", chatId);
    }
}