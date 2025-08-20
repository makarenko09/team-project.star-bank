package org.skypro.star.listener;

import org.skypro.star.controller.RecommendationController;
import org.skypro.star.repository.RecommendationRepository;
import org.skypro.star.repository.TransactionRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;
import org.telegram.abilitybots.api.bot.AbilityBot;
import org.telegram.abilitybots.api.objects.Ability;
import org.telegram.abilitybots.api.objects.Locality;
import org.telegram.abilitybots.api.objects.Privacy;
import org.telegram.telegrambots.meta.api.methods.commands.SetMyCommands;
import org.telegram.telegrambots.meta.api.objects.commands.BotCommand;
import org.telegram.telegrambots.meta.api.objects.commands.scope.BotCommandScopeDefault;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Component("ruleBot")
public class RuleBot extends AbilityBot {
    private static final Logger logger = LoggerFactory.getLogger(RuleBot.class);
    private final ResponseHandler responseHandler;
    private final TransactionRepository transactionRepository; // Для поиска пользователя по username
    private final RecommendationRepository recommendationRepository; // Для рекомендаций
    private final RecommendationController recommendationController;

    @Autowired
    public RuleBot(Environment env, TransactionRepository transactionRepository, RecommendationRepository recommendationRepository, RecommendationController recommendationController) {
        super(env.getProperty("telegram.bot.token"), env.getProperty("telegram.bot.username"));
        this.recommendationController = recommendationController;
        this.responseHandler = new ResponseHandler(this.silent);
        this.transactionRepository = transactionRepository;
        this.recommendationRepository = recommendationRepository;
        logger.info("TelegramBot initialized with username: {}", getBotUsername());
        logger.info("Token: {}, Username: {}", env.getProperty("telegram.bot.token"), env.getProperty("telegram.bot.username"));

        List<BotCommand> commands = new ArrayList<>();
        commands.add(new BotCommand("/start", "Start the bot"));
        commands.add(new BotCommand("/recommend", "Get the recommendation"));
        try {
            this.execute(new SetMyCommands(commands, new BotCommandScopeDefault(), null));
        } catch (TelegramApiException e) {
            logger.error("Error setting bot commands", e);
        }
    }

    @Override
    public long creatorId() {
        return 1L; // Ваш Telegram ID
    }

    public Ability startBot() {
        return Ability.builder()
                .name("start")
                .info("Starts the bot")
                .locality(Locality.USER)
                .privacy(Privacy.PUBLIC)
                .action(ctx -> silent.send("""
                        Добро пожаловать в бота!
                        
                        Справка - список команд:
                        - /start: Приветствие и справка.
                        - /recommend username: Получить рекомендации для пользователя по username.
                        """, ctx.chatId()))
                .build();
    }

    public Ability recommendBot() {
        return Ability.builder()
                .name("recommend")
                .info("Get the recommendation")
                .locality(Locality.USER)
                .privacy(Privacy.PUBLIC)
                .input(1) // Ожидать 1 аргумент (username)
                .action(ctx -> {
                    String username = ctx.firstArg(); // Парсинг username
                    if (username == null || username.isEmpty()) {
                        silent.send("Использование: /recommend username", ctx.chatId());
                        return;
                    }

                    // Поиск пользователя по username через репозиторий (предполагаем метод findByUsername)
                    String[] fullNameByUsername = transactionRepository.findFullNameByUsername(username);// Или аналогичный метод
//                    Optional<String[]> optionalStrings = Optional.ofNullable(fullNameByUsername);
//
//                    List<String> validArr = Arrays.asList(fullNameByUsername);
//                    if (Optional.ofNullable(fullNameByUsername).isEmpty()) {
                    if (fullNameByUsername[0] == null || fullNameByUsername[1] == null) {
                        silent.send("Пользователь не найден", ctx.chatId());
                        return;
                    }
                    UUID userId = transactionRepository.findUserUUIDByUsername(username);
                    //ResponseEntity<RecommendationAnswerUser> recommendation= new ResponseEntity<>(recommendationController.getRecommendation(userId), HttpStatus.OK);
                    String recommendation = recommendationController.getRecommendation(userId).toString();
                    String response = "Здравствуйте, " + fullNameByUsername[0] + " " + fullNameByUsername[1] + "\n\nНовые продукты для вас:\n" + recommendation;
                    silent.send(response, ctx.chatId());
                })
                .build();
    }

    @Override
    public Map<String, Ability> abilities() {
        Map<String, Ability> abilities = super.abilities();
        abilities.keySet().removeIf(key -> !key.equals("start") && !key.equals("recommend")); // Сохраняем start и recommend
        return abilities;
    }
}