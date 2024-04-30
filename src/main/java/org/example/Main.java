package org.example;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class Main {
    public static void main(String[] args) {
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

            System.out.println("Mark Price: " + markPrice);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
