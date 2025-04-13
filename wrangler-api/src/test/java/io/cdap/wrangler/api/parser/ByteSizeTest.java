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

 import org.junit.Assert;
 import org.junit.Test;
 
 public class ByteSizeTest {
 
     @Test
     public void testByteSizeParsing() {
         // Test basic byte values
         Assert.assertEquals(1024L, new ByteSize("1024").getBytes());
         Assert.assertEquals(2048L, new ByteSize("2048").getBytes());
         
         // Test different units (case insensitive)
         Assert.assertEquals(1024L, new ByteSize("1KB").getBytes());
         Assert.assertEquals(1024L, new ByteSize("1kb").getBytes());
         Assert.assertEquals(1024L * 1024, new ByteSize("1MB").getBytes());
         Assert.assertEquals(1024L * 1024 * 1024, new ByteSize("1GB").getBytes());
         Assert.assertEquals(1024L * 1024 * 1024 * 1024, new ByteSize("1TB").getBytes());
         
         // Test decimal values
         Assert.assertEquals(1536L, new ByteSize("1.5KB").getBytes());
         Assert.assertEquals(1024L * 1024 * 1.5, new ByteSize("1.5MB").getBytes(), 0.001);
     }
 
     @Test(expected = NumberFormatException.class)
     public void testInvalidNumberFormat() {
         new ByteSize("invalid");
     }
 
     @Test(expected = IllegalArgumentException.class)
     public void testNegativeValue() {
         new ByteSize("-1MB");
     }
 
     @Test
     public void testToString() {
         ByteSize byteSize = new ByteSize("1.5MB");
         Assert.assertTrue(byteSize.toString().contains("1.5MB"));
     }
 
     @Test
     public void testToJson() {
         ByteSize byteSize = new ByteSize("1KB");
         Assert.assertEquals(TokenType.BYTE_SIZE.name(), byteSize.toJson().getAsJsonObject().get("type").getAsString());
         Assert.assertEquals("1KB", byteSize.toJson().getAsJsonObject().get("original").getAsString());
         Assert.assertEquals(1024L, byteSize.toJson().getAsJsonObject().get("bytes").getAsLong());
     }
 }
 