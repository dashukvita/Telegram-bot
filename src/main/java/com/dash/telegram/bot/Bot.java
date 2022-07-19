package com.dash.telegram.bot;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.DefaultBotOptions;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.MessageEntity;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;

import java.util.Optional;

@EqualsAndHashCode(callSuper = true)
@Data
@Component
public class Bot extends TelegramLongPollingBot {

    @Value("${bot.name}")
    private String botUsername="@ForScheduling";

    @Value("${bot.token}")
    private String botToken="5539086041:AAFwpm-8MriszV48UGU5jASf-IGeCaXz_X8";

    @SneakyThrows
    @Override
    public void onUpdateReceived(Update update) {
        if (update.hasMessage()) {
            
//            handleMessage(update.getMessage());
            Message message = update.getMessage();
            if (message.hasText()) {
                execute(
                        SendMessage.builder()
                                .chatId(message.getChatId().toString())
                                .text("Hello world from Postman2")
                                .build());
            }
            
        }
    }

    @SneakyThrows
    private void handleMessage(Message message) {
        if (message.hasText() && message.hasEntities()) {
            Optional<MessageEntity> commandEntity=
            message.getEntities().stream().filter(mes -> "bot command".equals(mes.getType())).findFirst();

            if (commandEntity.isPresent()) {
                String command = message.getText().substring(commandEntity.get().getOffset(), commandEntity.get().getLength());
                if ("/set_currency".equals(command)) {
                    execute(SendMessage.builder()
                            .text("Hi")
                            .chatId(message.getChatId().toString())
                            .build());
                }
            }
        }
    }

    @SneakyThrows
    public static void main(String[] args) {
        Bot bot = new Bot();
        bot.execute(SendMessage.builder().chatId("227982080").text("Hello world from Postman2").build());
//        TelegramBotsApi telegramBotsApi = new TelegramBotsApi(DefaultBotSession.class);
//        telegramBotsApi.registerBot(bot);
    }
}
