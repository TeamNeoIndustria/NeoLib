package xyz.neonetwork.neolib.api;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import xyz.neonetwork.neolib.NeoLib;

public class APIResponse {
    private JsonNode json;
    public APIResponse(String jsonString) {
        if (jsonString == null) {
            this.json = null;
            return;
        }
        ObjectMapper mapper = new ObjectMapper();
        try {
//            NeoLib.LOGGER.info(jsonString);
            this.json = mapper.readTree(jsonString);
        } catch (Exception e) {
            this.json = null;
            NeoLib.LOGGER.warn("NeoLib.api.APIResponse#APIResponse failed to parse response");
        }
    }
    public boolean getSuccess() {
        if (json == null) return false;
        if (!json.has("success")) return false;
        return json.get("success").asBoolean();
    }
    public String getStatusCode() {
        if (json == null) return "499"; // 499 = Parse Error / Not Found
        if (!json.has("code")) return "499";
        JsonNode code = json.get("code");
        return code.isTextual() ? code.asText() : "499";
    }
    public String getStatusMessage() {
        if (json == null) return "Unknown API Error";
        if (!json.has("codeMessage")) return "Unknown API Error";
        JsonNode codeMessage = json.get("codeMessage");
        return codeMessage.isTextual() ? codeMessage.asText() : "Unknown API Error";
    }
    public JsonNode getDataNode() {
        if (json == null) return null;
        if (!json.has("data")) return null;
        return json.get("data");
    }
}
