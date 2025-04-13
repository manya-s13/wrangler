/*
 *  Copyright © 2017-2019 Cask Data, Inc.
 *
 *  Licensed under the Apache License, Version 2.0 (the "License"); you may not
 *  use this file except in compliance with the License. You may obtain a copy of
 *  the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 *  WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 *  License for the specific language governing permissions and limitations under
 *  the License.
 */

 package io.cdap.wrangler.api.parser;
 import com.google.gson.JsonObject;
 import org.junit.Assert;
 import org.junit.Test;
 
 
 
 public class TimeDurationTest {
 
     @Test
     public void testTimeDurationParsing() {
         // Test basic time units
         Assert.assertEquals(100L, new TimeDuration("100ns").getNanos());
         Assert.assertEquals(500_000L, new TimeDuration("500us").getNanos());
         Assert.assertEquals(10_000_000L, new TimeDuration("10ms").getNanos());
         Assert.assertEquals(2_000_000_000L, new TimeDuration("2s").getNanos());
         Assert.assertEquals(60_000_000_000L, new TimeDuration("1min").getNanos());
         Assert.assertEquals(3_600_000_000_000L, new TimeDuration("1h").getNanos());
         
         // Test case insensitivity
         Assert.assertEquals(1_000_000L, new TimeDuration("1MS").getNanos());
         Assert.assertEquals(1_000_000_000L, new TimeDuration("1S").getNanos());
         
         // Test decimal values
         Assert.assertEquals(1_500_000_000L, new TimeDuration("1.5s").getNanos());
         Assert.assertEquals(90_000_000_000L, new TimeDuration("1.5min").getNanos());
     }
 
     @Test
     public void testConversionMethods() {
         TimeDuration duration = new TimeDuration("1.5ms");
        
         Assert.assertEquals(1_500_000L, duration.getNanos());     // 1.5ms in nanoseconds
         Assert.assertEquals(1.5, duration.getMillis(), 0.001);    // Should stay 1.5ms
         Assert.assertEquals(0.0015, duration.getSeconds(), 0.0001); // 1.5ms in seconds
     }
     @Test(expected = NumberFormatException.class)
     public void testInvalidNumberFormat() {
         new TimeDuration("invalid");
     }
 
     @Test(expected = IllegalArgumentException.class)
     public void testNegativeValue() {
         new TimeDuration("-1s");
     }
 
     @Test
     public void testToString() {
         TimeDuration duration = new TimeDuration("1.5s");
         Assert.assertTrue(duration.toString().contains("1.5s"));
     }
 
     @Test
     public void testToJson() {  
         TimeDuration duration = new TimeDuration("100ms");
         JsonObject json = duration.toJson().getAsJsonObject();
         Assert.assertEquals(TokenType.TIME_DURATION.name(), json.get("type").getAsString());
         Assert.assertEquals("100ms", duration.toJson().getAsJsonObject().get("original").getAsString());
         Assert.assertEquals(100_000_000L, duration.toJson().getAsJsonObject().get("nanos").getAsLong());
     }
 }
 