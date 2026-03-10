package xyz.neonetwork.neolib.api;

import okhttp3.*;
import xyz.neonetwork.neolib.NeoLib;

import java.io.IOException;
import java.util.HashMap;

public class APIRequest {
    private static final OkHttpClient client = new OkHttpClient().newBuilder().build();

    public static APIResponse apiRequest(String url, HashMap<String, String> parameters) {
        if (url == null || url.isEmpty()) {
            NeoLib.LOGGER.trace("NeoLib.api.APIRequest#apiRequest called with empty url");
            return new APIResponse(null);
        }
        if (parameters == null || parameters.isEmpty()) {
            NeoLib.LOGGER.trace("NeoLib.api.APIRequest#apiRequest called without parameters");
            return new APIResponse(null);
        }

        NeoLib.LOGGER.info("[PARAMETER BUILDER] New request: {}", url);
        MultipartBody.Builder bodyBuilder = new MultipartBody.Builder().setType(MultipartBody.FORM);
        parameters.forEach((key, value) -> {
//            NeoLib.LOGGER.info("[PARAMETER BUILDER] {} : {}", key, value);
            if (value == null || value.isEmpty()) {
                NeoLib.LOGGER.trace("NeoLib.api.APIRequest#apiRequest Pair contains empty/null value");
                return;
            }
            bodyBuilder.addFormDataPart(key, value);
        });

        RequestBody body =  bodyBuilder.build();
        Request request = new Request.Builder()
                .url(url)
                .method("POST", body)
                .build();

        try {
            Response response = client.newCall(request).execute();
            APIResponse apiResponse = new APIResponse(response.body().string());
            response.close();
            return apiResponse;
        } catch (IOException e) {
            NeoLib.LOGGER.trace("NeoLib.api.APIRequest#apiRequest request failure");
        }
        return new APIResponse(null);
    }
}
