package org.skypro.star;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.ApplicationContext;
import org.springframework.context.event.EventListener;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.meta.generics.LongPollingBot;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;


@EnableCaching
@SpringBootApplication
public class StarApplication {

	public static void main(String[] args) {
		SpringApplication.run(StarApplication.class, args);
	}
	@EventListener(ApplicationReadyEvent.class)
	public void initTelegramBot(ApplicationReadyEvent event) throws TelegramApiException {
		ApplicationContext ctx = event.getApplicationContext();
		TelegramBotsApi botsApi = new TelegramBotsApi(DefaultBotSession.class);
		botsApi.registerBot(ctx.getBean("ruleBot", LongPollingBot.class));
	}
}
