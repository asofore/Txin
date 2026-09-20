package com.test.txin;

import org.json.JSONArray;
import org.json.JSONObject;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;

public class GeminiClient {
	
	private final String apiKey;
	private static final String GEMINI_URL = "https://generativelanguage.googleapis.com/v1beta/models/gemini-3.5-flash:generateContent";
	
	public GeminiClient(String apiKey) {
		this.apiKey = apiKey;
	}
	
	public String generateText(String prompt) {
		final String[] result = new String[1];
		
		Thread thread = new Thread(new Runnable() {
			@Override
			public void run() {
				try {
					URL url = new URL(GEMINI_URL);
					HttpURLConnection conn = (HttpURLConnection) url.openConnection();
					conn.setRequestMethod("POST");
					conn.setRequestProperty("Content-Type", "application/json");
					conn.setRequestProperty("x-goog-api-key", apiKey);
					conn.setRequestProperty("Accept", "application/json");
					conn.setDoOutput(true);
					
					// بناء JSON Body باستخدام JSONObject لضمان السلامة
					JSONObject textPart = new JSONObject();
					textPart.put("text", prompt);
					
					JSONArray partsArray = new JSONArray();
					partsArray.put(textPart);
					
					JSONObject contentObject = new JSONObject();
					contentObject.put("parts", partsArray);
					
					JSONArray contentsArray = new JSONArray();
					contentsArray.put(contentObject);
					
					JSONObject mainPayload = new JSONObject();
					mainPayload.put("contents", contentsArray);
					
					// إرسال البيانات
					try (OutputStream os = conn.getOutputStream()) {
						byte[] input = mainPayload.toString().getBytes(StandardCharsets.UTF_8);
						os.write(input, 0, input.length);
					}
					
					int responseCode = conn.getResponseCode();
					
					InputStreamReader isr = (responseCode >= 200 && responseCode < 300)
					? new InputStreamReader(conn.getInputStream(), StandardCharsets.UTF_8)
					: new InputStreamReader(conn.getErrorStream(), StandardCharsets.UTF_8);
					
					try (BufferedReader br = new BufferedReader(isr)) {
						StringBuilder response = new StringBuilder();
						String responseLine;
						while ((responseLine = br.readLine()) != null) {
							response.append(responseLine.trim());
						}
						
						if (responseCode >= 200 && responseCode < 300) {
							result[0] = extractTextFromJson(response.toString());
							} else {
							result[0] = "خطأ: " + response.toString();
						}
					}
					
					conn.disconnect();
					} catch (Exception e) {
					e.printStackTrace();
					result[0] = "خطأ في الاتصال: " + e.getMessage();
				}
			}
		});
		
		thread.start();
		try {
			thread.join();
			} catch (InterruptedException e) {
			e.printStackTrace();
		}
		
		return result[0];
	}
	
	private String extractTextFromJson(String jsonString) {
		try {
			JSONObject responseJson = new JSONObject(jsonString);
			JSONArray candidates = responseJson.getJSONArray("candidates");
			JSONObject firstCandidate = candidates.getJSONObject(0);
			JSONObject content = firstCandidate.getJSONObject("content");
			JSONArray parts = content.getJSONArray("parts");
			JSONObject firstPart = parts.getJSONObject(0);
			
			return firstPart.getString("text");
			} catch (Exception e) {
			e.printStackTrace();
			return jsonString;
		}
	}
}