package org.example;

import org.json.JSONObject;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.meta.generics.TelegramBot;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.TimerTask;
import java.util.Timer;

public class TelegramBot extends TelegramLongPollingBot {

    private static final String BOT_USERNAME = "BOT_USERNAME";
    private static final String BOT_TOKEN = "BOT_TOKEN";
    private static final String CHAT_ID = "CHAT_ID";

    @Override
    public String getBotUsername() {
        return BOT_USERNAME;
    }

    @Override
    public String getBotToken() {
        return BOT_TOKEN;
    }

    public void sendMessage(String text) {
        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(CHAT_ID);
        sendMessage.setText(text);

        try {
            Message message = execute(sendMessage); 
            int messageId = message.getMessageId();
            Timer timer = new Timer();

            TimerTask task = new TimerTask() {
                @Override
                public void run() {
                    try {
                        URL obj = new URL("https://www.binance.com/fapi/v1/premiumIndex?symbol=BTCUSDT");
                        HttpURLConnection con = (HttpURLConnection) obj.openConnection();
                        con.setRequestMethod("GET");
                        int responseCode = con.getResponseCode();
                        BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
                        String inputLine;
                        StringBuffer response = new StringBuffer();
                        while ((inputLine = in.readLine()) != null) {
                            response.append(inputLine);
                        }
                        in.close();

                        JSONObject jsonObject = new JSONObject(response.toString());
                        String markPrice = jsonObject.optString("markPrice");

                        EditMessageText editMessage = new EditMessageText();
                        editMessage.setChatId(CHAT_ID);
                        editMessage.setMessageId(messageId);
                        editMessage.setText("BTC/USDT\nMark Price: " + markPrice);
                        execute(editMessage);
                        System.out.println("Message sent to channel ");
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            };

            task.run();

            // Запускаем поток, который будет выполняться каждую секунду
            timer.scheduleAtFixedRate(task, 0, 1000);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) throws TelegramApiException {
        TelegramBotsApi botsApi = new TelegramBotsApi(DefaultBotSession.class);

        try {
            TelegramBot bot = new TelegramBot();
            botsApi.registerBot(bot);
            try {
                URL obj = new URL("https://www.binance.com/fapi/v1/premiumIndex?symbol=BTCUSDT");
                HttpURLConnection con = (HttpURLConnection) obj.openConnection();
                con.setRequestMethod("GET");
                int responseCode = con.getResponseCode();
                BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
                String inputLine;
                StringBuffer response = new StringBuffer();
                while ((inputLine = in.readLine()) != null) {
                    response.append(inputLine);
                }
                in.close();

                JSONObject jsonObject = new JSONObject(response.toString());
                String markPrice = jsonObject.optString("markPrice");

                bot.sendMessage("BTC/USDT\nMark Price: " + markPrice);
            } catch (Exception e) {
                e.printStackTrace();
            }
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onUpdateReceived(Update update) {

    }
}
