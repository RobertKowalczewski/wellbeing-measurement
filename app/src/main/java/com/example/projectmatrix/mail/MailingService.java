package com.example.projectmatrix.mail;

import java.io.IOException;
import java.util.Base64;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class MailingService {
    private static final String MAILERSEND_API_KEY = "mlsn.f05b926f735ad5ace29d763b291fa68f8f4dc3d03d827976aa3898dd03317ba8";
    private static final String FROM_EMAIL = "trial-0p7kx4xzk1eg9yjr.mlsender.net";
    private static final String TO_EMAIL = "marcin.leszczynski@student.put.poznan.pl";
    private static final String MAILERSEND_URL = "https://api.mailersend.com/v1/email";

    private final OkHttpClient client = new OkHttpClient();

    public void sendFileByEmail(byte[] fileContent, String filename) throws IOException {
        String base64Content = Base64.getEncoder().encodeToString(fileContent);

        String jsonBody = String.format(
                "{" +
                        "\"from\": {\"email\": \"%s\"}," +
                        "\"to\": [{\"email\": \"%s\"}]," +
                        "\"subject\": \"New wellbeing measured - %s\"," +
                        "\"text\": \"New wellbeing has been measured, here is the data.\"," +
                        "\"attachments\": [{" +
                        "\"content\": \"%s\"," +
                        "\"filename\": \"%s\"," +
                        "\"type\": \"text/csv\"" +
                        "}]" +
                        "}",
                FROM_EMAIL, TO_EMAIL, filename, base64Content, filename);

        RequestBody body = RequestBody.create(
                MediaType.parse("application/json"),
                jsonBody
        );

        Request request = new Request.Builder()
                .url(MAILERSEND_URL)
                .addHeader("Authorization", "Bearer " + MAILERSEND_API_KEY)
                .addHeader("Content-Type", "application/json")
                .post(body)
                .build();

        try (Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                String responseBody = response.body() != null ? response.body().string() : "No response body";
                throw new IOException("Unexpected code " + response + "\nResponse body: " + responseBody);
            }
        }
    }
}