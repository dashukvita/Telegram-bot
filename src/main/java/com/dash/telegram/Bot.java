package com.dash.telegram;

import com.dash.telegram.entity.Currency;
import com.dash.telegram.service.CurrencyConversionService;
import com.dash.telegram.service.CurrencyModeService;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageReplyMarkup;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.MessageEntity;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@EqualsAndHashCode(callSuper = true)
@Data
@Component
public class Bot extends TelegramLongPollingBot {

    @Value("${bot.name}")
    private String botUsername="@ForScheduling";

    @Value("${bot.token}")
    private String botToken="5539086041:AAFwpm-8MriszV48UGU5jASf-IGeCaXz_X8";

    private final CurrencyModeService currencyModeService = CurrencyModeService.getInstance();
    private final CurrencyConversionService currencyConversionService = CurrencyConversionService.getInstance();

    @SneakyThrows
    @Override
    public void onUpdateReceived(Update update) {
        if (update.hasCallbackQuery()) {
            handleCallback(update.getCallbackQuery());
        }
        if (update.hasMessage()) {
            handleMessage(update.getMessage());
        }
    }

    @SneakyThrows
    private void handleCallback(CallbackQuery callbackQuery) {
        Message message = callbackQuery.getMessage();
        String[] param = callbackQuery.getData().split(":");
        String action = param[0];
        Currency newCurrency = Currency.valueOf(param[1]);
        switch (action) {
            case "ORIGINAL":
                currencyModeService.setOriginalCurrency(message.getChatId(), newCurrency);
                break;
            case "TARGET":
                currencyModeService.setTargetCurrency(message.getChatId(), newCurrency);
                break;
        }

        List<List<InlineKeyboardButton>> buttons = new ArrayList<>();
        Currency originalCurrency = currencyModeService.getOriginalCurrency(message.getChatId());
        Currency targetCurrency = currencyModeService.getTargetCurrency(message.getChatId());
        for (Currency currency: Currency.values()) {
            buttons.add(
                    Arrays.asList(
                            InlineKeyboardButton.builder()
                                    .text(getCurrencyButton(originalCurrency, currency))
                                    .callbackData("ORIGINAL:" + currency)
                                    .build(),
                            InlineKeyboardButton.builder()
                                    .text(getCurrencyButton(targetCurrency, currency))
                                    .callbackData("TARGET:" + currency)
                                    .build()));
        }
        execute(EditMessageReplyMarkup.builder()
                .chatId(message.getChatId().toString())
                .messageId(message.getMessageId())
                .replyMarkup(InlineKeyboardMarkup.builder().keyboard(buttons).build())
                .build());
    }

    @SneakyThrows
    private void handleMessage(Message message) {
        if (message.hasText() && message.hasEntities()) {
            Optional<MessageEntity> commandEntity=
            message.getEntities().stream().filter(mes -> "bot_command".equals(mes.getType())).findFirst();

            if (commandEntity.isPresent()) {
                String command =
                        message
                                .getText()
                                .substring(commandEntity.get().getOffset(), commandEntity.get().getLength());

                switch (command) {
                    case "/set_currency":
                    List<List<InlineKeyboardButton>> buttons = new ArrayList<>();
                    Currency originalCurrency = currencyModeService.getOriginalCurrency(message.getChatId());
                    Currency targetCurrency = currencyModeService.getTargetCurrency(message.getChatId());

                    for (Currency currency: Currency.values()) {
                        buttons.add(
                                Arrays.asList(
                                        InlineKeyboardButton.builder()
                                                .text(getCurrencyButton(originalCurrency, currency))
                                                .callbackData("ORIGINAL:" + currency)
                                                .build(),
                                        InlineKeyboardButton.builder()
                                                .text(getCurrencyButton(targetCurrency, currency))
                                                .callbackData("TARGET:" + currency)
                                                .build()
                                ));
                    }

                    execute(SendMessage.builder()
                            .text("Please choose Original and Target currencies")
                            .chatId(message.getChatId().toString())
                            .replyMarkup(InlineKeyboardMarkup.builder().keyboard(buttons).build())
                            .build());
                    return;
                }
            }
        }
        if (message.hasText()) {
            String messageText = message.getText();
            Optional<Double> value = parseDouble(messageText);
            Currency originalCurrency = currencyModeService.getOriginalCurrency(message.getChatId());
            Currency targetCurrency = currencyModeService.getTargetCurrency(message.getChatId());
            double ratio = currencyConversionService.getConversionRatio(originalCurrency, targetCurrency);
            if (value.isPresent()) {
                execute(
                        SendMessage.builder()
                                .chatId(message.getChatId().toString())
                                .text(
                                        String.format(
                                                "%4.2f %s is %4.2f %s",
                                                value.get(), originalCurrency, (value.get() * ratio), targetCurrency))
                                .build());
            }
        }
    }

    private Optional<Double> parseDouble(String messageText) {

        try{
            return Optional.of(Double.parseDouble(messageText));
        }catch (Exception e) {
         return  Optional.empty();
        }
    }

    private String getCurrencyButton(Currency saved, Currency current) {
        return saved == current ? current + "✅": current.name();
    }

    @SneakyThrows
    public static void main(String[] args) {
        Bot bot = new Bot();
        TelegramBotsApi telegramBotsApi = new TelegramBotsApi(DefaultBotSession.class);
        telegramBotsApi.registerBot(bot);
    }
}
