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
     public long getMillis() {
         return nanos / 1_000_000;
     }
 
     /**
      * @return duration in seconds
      */
     public double getSeconds() {
         return nanos / 1_000_000_000.0;
     }
 
     /**
      * Parses time duration string into nanoseconds
      * @param value Time duration string (e.g., "100ms", "5s", "1min")
      * @return duration in nanoseconds
      * @throws NumberFormatException if the numeric part cannot be parsed
      * @throws IllegalArgumentException if the time unit is unrecognized
      */
     private long parseTimeDuration(String value) {
         // Extract numeric part
         String numStr = value.replaceAll("[^0-9.]", "");
         if (numStr.isEmpty()) {
             throw new NumberFormatException("No numeric value found in: " + value);
         }
         double number = Double.parseDouble(numStr);
         
         // Extract and normalize unit
         String unit = value.substring(numStr.length()).trim().toLowerCase();
         
         // Convert to nanoseconds
         if (unit.isEmpty() || unit.equals("ms")) {
             return (long) (number * 1_000_000);
         } else if (unit.equals("us") || unit.equals("μs")) {
             return (long) (number * 1_000);
         } else if (unit.equals("ns")) {
             return (long) number;
         } else if (unit.equals("s") || unit.equals("sec")) {
             return (long) (number * 1_000_000_000);
         } else if (unit.equals("min")) {
             return (long) (number * 60 * 1_000_000_000L);
         } else if (unit.equals("h") || unit.equals("hr")) {
             return (long) (number * 60 * 60 * 1_000_000_000L);
         } else if (unit.equals("d") || unit.equals("day")) {
             return (long) (number * 24 * 60 * 60 * 1_000_000_000L);
         }
         throw new IllegalArgumentException("Unrecognized time unit in: " + value);
     }
 }