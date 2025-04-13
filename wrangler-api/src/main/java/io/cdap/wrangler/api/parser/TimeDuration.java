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
  * Token for time duration values with units (e.g., 100ms, 5s, 1min)
  */
 @PublicEvolving
 public class TimeDuration implements Token {
     private final long nanos;
     private final String original;
 
     public TimeDuration(String value) {
         this.original = value.trim();
         this.nanos = parseTimeDuration(this.original);
     }
 
     @Override
     public Object value() {
         return nanos;
     }
 
     @Override
     public TokenType type() {
         return TokenType.TIME_DURATION;
     }
 
     @Override
     public JsonElement toJson() {
         JsonObject object = new JsonObject();
         object.addProperty("type", type().name());
         object.addProperty("original", original);
         object.addProperty("nanos", nanos);
         return object;
     }
 
     /**
      * @return duration in nanoseconds
      */
     public long getNanos() {
         return nanos;
     }
 
     /**
      * @return duration in milliseconds
      */
     public double getMillis() {
         return nanos / 1_000_000.0;
     }
 
     /**
      * @return duration in seconds
      */
     public double getSeconds() {
         return nanos / 1_000_000_000.0;
     }
 
     /**
      * Gets the duration in the specified unit
      * @param unit The unit to convert to (e.g., "ns", "ms", "s", "min", "h", "d")
      * @return The duration in the specified unit
      */
      public double getValue(String unit) {
        unit = unit.toLowerCase();
        switch (unit) {
          case "ns": return nanos;
          case "us": return nanos / 1_000.0;
          case "ms": return nanos / 1_000_000.0;
          case "s":
          case "sec": return nanos / 1_000_000_000.0;
          case "min": return nanos / (60.0 * 1_000_000_000);
          case "h":
          case "hr": return nanos / (3600.0 * 1_000_000_000);
          case "d":
          case "day": return nanos / (24.0 * 3600 * 1_000_000_000);
          default: throw new IllegalArgumentException("Unsupported time unit: " + unit);
        }
      }
 
     @Override
     public String toString() {
         return original;
     }
 
     /**
      * Parses time duration string into nanoseconds
      * @param value Time duration string (e.g., "100ms", "5s", "1min")
      * @return duration in nanoseconds
      * @throws NumberFormatException if the numeric part cannot be parsed
      * @throws IllegalArgumentException if the time unit is unrecognized
      */
     private long parseTimeDuration(String value) {
         String numStr = value.replaceAll("[^0-9.]", "");
         if (numStr.isEmpty()) {
             throw new NumberFormatException("No numeric value found in: " + value);
         }
         if (numStr.contains("..") || numStr.endsWith(".") || numStr.startsWith(".")) {
             throw new NumberFormatException("Invalid numeric format in: " + value);
         }
         double number = Double.parseDouble(numStr);
         String unit = value.substring(numStr.length()).trim().toLowerCase();
         if (unit.isEmpty()) {
             throw new IllegalArgumentException("No time unit specified in: " + value);
         }
         switch (unit) {
             case "ns": return (long) number;
             case "us": return (long) (number * 1_000);
             case "ms": return (long) (number * 1_000_000);
             case "s":
             case "sec": return (long) (number * 1_000_000_000);
             case "min": return (long) (number * 60 * 1_000_000_000);
             case "h":
             case "hr": return (long) (number * 3600 * 1_000_000_000);
             case "d":
             case "day": return (long) (number * 86400 * 1_000_000_000);
             default: throw new IllegalArgumentException("Unrecognized time unit in: " + value);
         }
     }
 }
