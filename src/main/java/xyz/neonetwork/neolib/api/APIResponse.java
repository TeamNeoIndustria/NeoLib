package xyz.neonetwork.neolib.api;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import xyz.neonetwork.neolib.NeoLib;

public class APIResponse {
    private JsonObject json;

    public APIResponse(String jsonString) {
        if (jsonString == null) {
            this.json = null;
            return;
        }
        try {
            this.json = JsonParser.parseString(jsonString).getAsJsonObject();
        } catch (Exception e) {
            this.json = null;
            NeoLib.LOGGER.warn("NeoLib.api.APIResponse#APIResponse failed to parse response");
        }
    }

    public boolean getSuccess() {
        if (json == null) return false;
		try {
			return json.get("success").getAsBoolean();
		} catch (Exception ignored) {
			return false;
		}
    }

    public String getStatusCode() {
        if (json == null) return "499"; // 499 = Parse Error / Not Found
		try {
			return String.valueOf(json.get("code").getAsInt());
		} catch (Exception ignored) {
			return "499";
		}
    }

    public String getStatusMessage() {
        if (json == null) return "Unknown API Error";
		try {
			return String.valueOf(json.get("codeMessage").getAsString());
		} catch (Exception ignored) {
			return "Unknown API Error";
		}
    }

    public JsonElement getDataNode() {
        if (json == null) return null;
		try {
			return json.get("data");
		} catch (Exception ignored) {
			return null;
		}
    }
}
