package org.example;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.events.session.ReadyEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.json.JSONObject;
import java.util.Timer;

import java.awt.*;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.TimerTask;


public class DiscordBoT {
    public static void main(String[] args) {
        String token = "TOKEN";
        String channelId = "channelId";
        long serverId = serverId;

        try {
            JDABuilder builder = JDABuilder.createDefault(token);
            builder.setAutoReconnect(true);

            builder.addEventListeners(new MyListener(channelId,serverId));
            jda = builder.build();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    static JDA jda;

    static class MyListener extends ListenerAdapter {
        private String channelId;
        private long serverId;
        private TextChannel channel;

        public MyListener(String channelId, long serverId) {
            this.channelId = channelId;// Отправляем сообщение каждую секунду
            this.serverId = serverId;// Отправляем сообщение каждую секунду
        }

        @Override
        public void onReady(ReadyEvent event) {
            String text = "";
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
                text = (markPrice);
            } catch (Exception e) {
                e.printStackTrace();
            }
            Guild server = jda.getGuildById(serverId);
            TextChannel channel = server.getTextChannelById(channelId);
            EmbedBuilder embed = new EmbedBuilder()
                    .setTitle("BTC/USDT")
                    .setColor(Color.CYAN)
                    .addField("Mark Price:", "```" + text + "```", true);

            Message message = channel.sendMessage("").setEmbeds(embed.build()).complete();
            System.out.println("Message sent to channel " + channel.getName());

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
                        EmbedBuilder embed = new EmbedBuilder()
                                .setTitle("BTC/USDT")
                                .setColor(Color.CYAN)
                                .addField("Mark Price:", "```" + markPrice + "```", true);

                        message.editMessage("").setEmbeds(embed.build()).complete();
                        System.out.println("Message sent to channel " + channel.getName());
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            };

            task.run();

            // Запускаем поток, который будет выполняться каждую секунду
            timer.scheduleAtFixedRate(task, 0, 1000);
        }
    }
}
