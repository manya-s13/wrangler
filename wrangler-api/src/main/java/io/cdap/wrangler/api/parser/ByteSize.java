/*
 * Copyright © 2017-2019 Cask Data, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */

 package io.cdap.wrangler.api.parser;

 import com.google.gson.JsonElement;
 import com.google.gson.JsonObject;
 import io.cdap.wrangler.api.annotations.PublicEvolving;
 
 /**
  * Token for byte size values with units (e.g., 10KB, 5MB)
  */
 @PublicEvolving
 public class ByteSize implements Token {
     private final long bytes;
     private final String original;
 
     public ByteSize(String value) {
         this.original = value.trim();
         String numStr = value.replaceAll("[^0-9.]", "");
         if (numStr.isEmpty()) {
             throw new NumberFormatException("No numeric value found in: " + value);
         }
         double number = Double.parseDouble(numStr);
         if (number < 0) {
             throw new IllegalArgumentException("Byte size cannot be negative");
         }
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
 
     /**
      * Gets the size in the specified unit
      * @param unit The unit to convert to (e.g., "b", "kb", "mb", "gb", "tb", "pb")
      * @return The size in the specified unit
      */
      public double getValue(String unit) {
        unit = unit.toLowerCase();
        switch (unit) {
          case "b": return bytes;
          case "kb": return bytes / 1024.0;
          case "mb": return bytes / (1024.0 * 1024);
          case "gb": return bytes / (1024.0 * 1024 * 1024);
          case "tb": return bytes / (1024.0 * 1024 * 1024 * 1024);
          case "pb": return bytes / (1024.0 * 1024 * 1024 * 1024 * 1024);
          default: throw new IllegalArgumentException("Unsupported byte size unit: " + unit);
        }
      }
 
     @Override
     public String toString() {
         return original;
     }
 
     private long parseByteSize(String value) {
         String numStr = value.replaceAll("[^0-9.]", "");
         double number = Double.parseDouble(numStr);
         String unit = value.substring(numStr.length()).trim().toLowerCase();
         switch (unit) {
             case "k":
             case "kb": return (long) (number * 1024);
             case "m":
             case "mb": return (long) (number * 1024 * 1024);
             case "g":
             case "gb": return (long) (number * 1024 * 1024 * 1024);
             case "t":
             case "tb": return (long) (number * 1024L * 1024 * 1024 * 1024);
             case "p":
             case "pb": return (long) (number * 1024L * 1024 * 1024 * 1024 * 1024);
             case "":
             case "b": return (long) number;
             default: throw new IllegalArgumentException("Unrecognized byte unit in: " + value);
         }
     }
 }
