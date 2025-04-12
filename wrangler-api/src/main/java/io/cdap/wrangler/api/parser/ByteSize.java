package io.cdap.wrangler.api.parser;

import com.google.gson.JsonObject;
import io.cdap.wrangler.api.annotations.PublicEvolving;
import com.google.gson.JsonElement;

/**
 * Token for byte size values with units (e.g., 10KB, 5MB)
 */
@PublicEvolving
public class ByteSize implements Token {
    private final long bytes;
    private final String original;

    public ByteSize(String value) {
        this.original = value;
        this.bytes = parseByteSize(value);
    }

    @Override
    public Object value() {
        return bytes;
    }

    @Override
    public TokenType type() {
        return TokenType.BYTE_SIZE;
    }

    @Override
    public JsonElement toJson() {
        JsonObject object = new JsonObject();
        object.addProperty("type", type().name());
        object.addProperty("original", original);
        object.addProperty("bytes", bytes);
        return object;
    }

    /**
     * Gets the size in bytes
     */
    public long getBytes() {
        return bytes;
    }

    private long parseByteSize(String value) {
        String numStr = value.replaceAll("[^0-9.]", "");
        double number = Double.parseDouble(numStr);
        
        if (value.matches("(?i).*kb")) {
            return (long) (number * 1024);
        } else if (value.matches("(?i).*mb")) {
            return (long) (number * 1024 * 1024);
        } else if (value.matches("(?i).*gb")) {
            return (long) (number * 1024 * 1024 * 1024);
        } else if (value.matches("(?i).*tb")) {
            return (long) (number * 1024L * 1024 * 1024 * 1024);
        } else if (value.matches("(?i).*pb")) {
            return (long) (number * 1024L * 1024 * 1024 * 1024 * 1024);
        }
        return (long) number; // Default to bytes
    }
}