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

 package io.cdap.directives.aggregates;

 import io.cdap.wrangler.TestingRig;
 import io.cdap.wrangler.api.DirectiveExecutionException;
 import io.cdap.wrangler.api.Row;
 import org.junit.Assert;
 import org.junit.Test;
 
 import java.util.Arrays;
 import java.util.Collections;
 import java.util.List;
 
 /**
  * Tests for AggregateStats directive.
  */
 public class AggregateStatsTest {
 
   @Test
   public void testTotalAggregationDefaultUnits() throws Exception {
     List<Row> rows = Arrays.asList(
         new Row("size", "10kb").add("time", "5ms"),
         new Row("size", "1.5MB").add("time", "2.1s")
     );
     String[] recipe = {
         "aggregate-stats :size :time :total_size :total_time"
     };
     List<Row> results = TestingRig.execute(recipe, rows);
     Assert.assertEquals(1, results.size());
     Assert.assertEquals((10 * 1024 + 1.5 * 1024 * 1024) / (1024.0 * 1024), // mb
         ((Double) results.get(0).getValue("total_size")).doubleValue(), 0.001);
     Assert.assertEquals((5 * 1_000_000 + 2.1 * 1_000_000_000) / 1_000_000_000.0, // s
         ((Double) results.get(0).getValue("total_time")).doubleValue(), 0.001);
   }
 
   @Test
   public void testAverageAggregationCustomUnits() throws Exception {
     List<Row> rows = Arrays.asList(
         new Row("size", "10kb").add("time", "5ms"),
         new Row("size", "1.5MB").add("time", "2.1s")
     );
     String[] recipe = {
         "aggregate-stats :size :time :total_size :total_time kb ms average"
     };
     List<Row> results = TestingRig.execute(recipe, rows);
     Assert.assertEquals(1, results.size());
     Assert.assertEquals(((10 * 1024 + 1.5 * 1024 * 1024) / 1024.0) / 2, // kb
         ((Double) results.get(0).getValue("total_size")).doubleValue(), 0.001);
     Assert.assertEquals(((5 * 1_000_000 + 2.1 * 1_000_000_000) / 1_000_000.0) / 2, // ms
         ((Double) results.get(0).getValue("total_time")).doubleValue(), 0.001);
   }
 
   @Test
   public void testInvalidInput() throws Exception {
     List<Row> rows = Arrays.asList(
         new Row("size", "invalid").add("time", "5ms"),
         new Row("size", "1MB").add("time", "xyz"),
         new Row("size", "10kb").add("time", "1s")
     );
     String[] recipe = {
         "aggregate-stats :size :time :total_size :total_time mb s"
     };
     List<Row> results = TestingRig.execute(recipe, rows);
     Assert.assertEquals(1, results.size());
     Assert.assertEquals((10 * 1024 + 1_024 * 1024) / (1024.0 * 1024), // mb
         ((Double) results.get(0).getValue("total_size")).doubleValue(), 0.001);
     Assert.assertEquals((5 * 1_000_000 + 1_000_000_000) / 1_000_000_000.0, // s
         ((Double) results.get(0).getValue("total_time")).doubleValue(), 0.001);
   }
 
   @Test
   public void testEmptyInput() throws Exception {
     List<Row> rows = Collections.emptyList();
     String[] recipe = {
         "aggregate-stats :size :time :total_size :total_time"
     };
     List<Row> results = TestingRig.execute(recipe, rows);
     Assert.assertEquals(1, results.size());
     Assert.assertEquals(0.0, ((Double) results.get(0).getValue("total_size")).doubleValue(), 0.001);
     Assert.assertEquals(0.0, ((Double) results.get(0).getValue("total_time")).doubleValue(), 0.001);
   }
 
   @Test(expected = DirectiveExecutionException.class)
   public void testInvalidUnit() throws Exception {
     List<Row> rows = Arrays.asList(
         new Row("size", "10kb").add("time", "5ms")
     );
     String[] recipe = {
         "aggregate-stats :size :time :total_size :total_time xb ms"
     };
     TestingRig.execute(recipe, rows);
   }
 }
