package xyz.neonetwork.neolib.api;

import xyz.neonetwork.neolib.NeoLib;

import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

public class APIRequest {
//    private static final OkHttpClient client = new OkHttpClient().newBuilder().build();
//
//    public static APIResponse apiRequest(String url, HashMap<String, String> parameters) {
//        if (url == null || url.isEmpty()) {
//            NeoLib.LOGGER.trace("NeoLib.api.APIRequest#apiRequest called with empty url");
//            return new APIResponse(null);
//        }
//        if (parameters == null || parameters.isEmpty()) {
//            NeoLib.LOGGER.trace("NeoLib.api.APIRequest#apiRequest called without parameters");
//            return new APIResponse(null);
//        }
//
//        NeoLib.LOGGER.info("[PARAMETER BUILDER] New request: {}", url);
//        MultipartBody.Builder bodyBuilder = new MultipartBody.Builder().setType(MultipartBody.FORM);
//        parameters.forEach((key, value) -> {
////            NeoLib.LOGGER.info("[PARAMETER BUILDER] {} : {}", key, value);
//            if (value == null || value.isEmpty()) {
//                NeoLib.LOGGER.trace("NeoLib.api.APIRequest#apiRequest Pair contains empty/null value");
//                return;
//            }
//            bodyBuilder.addFormDataPart(key, value);
//        });
//
//        RequestBody body =  bodyBuilder.build();
//        Request request = new Request.Builder()
//                .url(url)
//                .method("POST", body)
//                .build();
//
//        try {
//            Response response = client.newCall(request).execute();
//            APIResponse apiResponse = new APIResponse(response.body().string());
//            response.close();
//            return apiResponse;
//        } catch (IOException e) {
//            NeoLib.LOGGER.trace("NeoLib.api.APIRequest#apiRequest request failure");
//        }
//        return new APIResponse(null);
//    }

    private static String getFormDataAsString(Map<String, String> formData) {
        StringBuilder formBodyBuilder = new StringBuilder();
        for (Map.Entry<String, String> entry : formData.entrySet()) {
            if (!formBodyBuilder.isEmpty()) {
                formBodyBuilder.append("&");
            }
            formBodyBuilder.append(URLEncoder.encode(entry.getKey(), StandardCharsets.UTF_8));
            formBodyBuilder.append("=");
            formBodyBuilder.append(URLEncoder.encode(entry.getValue(), StandardCharsets.UTF_8));
        }
        return formBodyBuilder.toString();
    }

    public static APIResponse apiRequest(String url, Map<String, String> parameters) {
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
            .uri(URI.create(url))
            .header("Content-Type", "application/x-www-form-urlencoded")
            .POST(HttpRequest.BodyPublishers.ofString(getFormDataAsString(parameters)))
            .build();

        try {
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            NeoLib.LOGGER.info("Code: {}, Body: {}", response.statusCode(), response.body());
            return new APIResponse(response.body());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return new APIResponse(null);
    }
}
