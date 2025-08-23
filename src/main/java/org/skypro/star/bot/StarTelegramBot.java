package org.skypro.star.bot;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.UpdatesListener;
import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.request.SendMessage;
import org.skypro.star.configuration.BotProperties;
import org.skypro.star.model.Recommendation;
import org.skypro.star.model.RecommendationAnswerUser;
import org.skypro.star.service.RecommendationRuleSetImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.event.EventListener;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class StarTelegramBot {

    private final TelegramBot bot;
    private final BotProperties botProperties;
    private final RecommendationRuleSetImpl recommendationService;
    private final Logger log = LoggerFactory.getLogger(StarTelegramBot.class);

    public StarTelegramBot(BotProperties botProperties,
                           RecommendationRuleSetImpl recommendationService) {
        this.botProperties = botProperties;
        this.recommendationService = recommendationService;
        this.bot = new TelegramBot(botProperties.getToken());
    }

    @EventListener(ApplicationReadyEvent.class)
    public void init() {
        bot.setUpdatesListener(updates -> {
            updates.forEach(this::processUpdate);
            return UpdatesListener.CONFIRMED_UPDATES_ALL;
        });
        log.info("Telegram bot initialized successfully");
    }

    private void processUpdate(Update update) {
        if (update.message() == null || update.message().text() == null) {
            return;
        }

        Long chatId = update.message().chat().id();
        String text = update.message().text();

        try {
            if ("/start".equals(text)) {
                sendWelcomeMessage(chatId);
            } else if (text.startsWith("/recommend")) {
                processRecommendCommand(chatId, text);
            } else {
                sendHelpMessage(chatId);
            }
        } catch (Exception e) {
            log.error("Error processing Telegram message", e);
            sendMessage(chatId, "Произошла ошибка при обработке запроса");
        }
    }

    private void sendWelcomeMessage(Long chatId) {
        String message = """
            🌟 Добро пожаловать в START BankBot! 🌟
            
            Я помогу вам получить персональные рекомендации по банковским продуктам.
            
            Для получения рекомендаций используйте команду:
            /recommend ИмяФамилия
            
            Например: /recommend ИванИванов
            """;
        sendMessage(chatId, message);
    }

    private void sendHelpMessage(Long chatId) {
        String message = """
            ❓ Доступные команды:
            
            /start - Начать работу с ботом
            /recommend ИмяФамилия - Получить рекомендации для пользователя
            
            Пример: /recommend ПетрПетров
            """;
        sendMessage(chatId, message);
    }

    private void processRecommendCommand(Long chatId, String text) {
        String[] parts = text.split(" ", 2);
        if (parts.length < 2) {
            sendMessage(chatId, "⚠️ Пожалуйста, укажите имя пользователя после команды.\nПример: /recommend Ivanov_Ivan");
            return;
        }

        String username = parts[1].trim();
        getRecommendationsForUser(chatId, username);
    }

    private void getRecommendationsForUser(Long chatId, String username) {
        try {
            RecommendationAnswerUser recommendations = recommendationService.getRecommendationsByUsername(username);

            if (recommendations.getRecommendations().isEmpty()) {
                sendMessage(chatId, "Для пользователя " + username + " нет доступных рекомендаций");
            } else {
                String message = formatRecommendationsMessage(username, recommendations);
                sendMessage(chatId, message);
            }

        } catch (Exception e) {
            log.error("Error getting recommendations for user: {}", username, e);
            sendMessage(chatId, "❌ Пользователь '" + username + "' не найден");
        }
    }

    private String formatRecommendationsMessage(String username, RecommendationAnswerUser recommendations) {
        StringBuilder sb = new StringBuilder();
        sb.append("✨ Здравствуйте, ").append(username).append("!\n\n");
        sb.append("Мы подобрали для вас следующие рекомендации:\n\n");

        List<Recommendation> recs = recommendations.getRecommendations();
        if (recs.isEmpty()) {
            sb.append("На данный момент для вас нет рекомендаций.");
        } else {
            for (int i = 0; i < recs.size(); i++) {
                Recommendation rec = recs.get(i);
                sb.append(i + 1).append(". ").append(rec.getName()).append("\n");
                sb.append("   ").append(rec.getText()).append("\n\n");
            }
        }

        return sb.toString();
    }

    private void sendMessage(Long chatId, String message) {
        SendMessage request = new SendMessage(chatId, message);
        bot.execute(request);
    }
}