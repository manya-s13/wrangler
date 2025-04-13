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

 import io.cdap.cdap.api.annotation.Description;
 import io.cdap.cdap.api.annotation.Name;
 import io.cdap.cdap.api.annotation.Plugin;
 import io.cdap.wrangler.api.Arguments;
 import io.cdap.wrangler.api.Directive;
 import io.cdap.wrangler.api.DirectiveExecutionException;
 import io.cdap.wrangler.api.DirectiveParseException;
 import io.cdap.wrangler.api.ExecutorContext;
 import io.cdap.wrangler.api.Row;
 import io.cdap.wrangler.api.TransientVariableScope;
 import io.cdap.wrangler.api.annotations.Categories;
 import io.cdap.wrangler.api.parser.ByteSize;
 import io.cdap.wrangler.api.parser.ColumnName;
 import io.cdap.wrangler.api.parser.Identifier;
 import io.cdap.wrangler.api.parser.TimeDuration;
 import io.cdap.wrangler.api.parser.TokenType;
 import io.cdap.wrangler.api.parser.UsageDefinition;
 
 import java.util.ArrayList;
 import java.util.Collections;
 import java.util.List;
 
 /**
  * Aggregates byte size and time duration columns into totals or averages.
  */
 @Plugin(type = Directive.TYPE)
 @Name(AggregateStats.NAME)
 @Categories(categories = {"aggregate"})
 @Description("Aggregates byte size and time duration columns into totals or averages.")
 public class AggregateStats implements Directive {
     public static final String NAME = "aggregate-stats";
     private static final String BYTE_TOTAL_KEY = "aggregate-stats-byte-total";
     private static final String TIME_TOTAL_KEY = "aggregate-stats-time-total";
     private static final String ROW_COUNT_KEY = "aggregate-stats-row-count";
 
     private String sizeColumn;
     private String timeColumn;
     private String outputSizeColumn;
     private String outputTimeColumn;
     private String outputSizeUnit = "mb";
     private String outputTimeUnit = "s";
     private String aggregationType = "total";
 
     @Override
     public UsageDefinition define() {
         UsageDefinition.Builder builder = UsageDefinition.builder(NAME);
         builder.define("size-column", TokenType.COLUMN_NAME);
         builder.define("time-column", TokenType.COLUMN_NAME);
         builder.define("output-size-column", TokenType.COLUMN_NAME);
         builder.define("output-time-column", TokenType.COLUMN_NAME);
         builder.define("output-size-unit", TokenType.IDENTIFIER, true);
         builder.define("output-time-unit", TokenType.IDENTIFIER, true);
         builder.define("aggregation-type", TokenType.IDENTIFIER, true);
         return builder.build();
     }
 
     @Override
     public void initialize(Arguments args) throws DirectiveParseException {
         this.sizeColumn = ((ColumnName) args.value("size-column")).value();
         this.timeColumn = ((ColumnName) args.value("time-column")).value();
         this.outputSizeColumn = ((ColumnName) args.value("output-size-column")).value();
         this.outputTimeColumn = ((ColumnName) args.value("output-time-column")).value();
 
         if (args.contains("output-size-unit")) {
             this.outputSizeUnit = ((Identifier) args.value("output-size-unit")).value().toLowerCase();
         }
         if (args.contains("output-time-unit")) {
             this.outputTimeUnit = ((Identifier) args.value("output-time-unit")).value().toLowerCase();
         }
         if (args.contains("aggregation-type")) {
             this.aggregationType = ((Identifier) args.value("aggregation-type")).value().toLowerCase();
             if (!"total".equals(aggregationType) && !"average".equals(aggregationType)) {
                 throw new DirectiveParseException(NAME, 
                     "Invalid aggregation-type: must be 'total' or 'average'");
             }
         }
     }
 
     @Override
     public List<Row> execute(List<Row> rows, ExecutorContext context) throws DirectiveExecutionException {
         Long totalBytes = (Long) context.getTransientStore().get(BYTE_TOTAL_KEY);
         Long totalNanos = (Long) context.getTransientStore().get(TIME_TOTAL_KEY);
         Integer rowCount = (Integer) context.getTransientStore().get(ROW_COUNT_KEY);
 
         if (totalBytes == null) {
             totalBytes = 0L;
         }
         if (totalNanos == null) {
             totalNanos = 0L;
         }
         if (rowCount == null) {
             rowCount = 0;
         }
 
         int validRows = 0;
         for (Row row : rows) {
             try {
                 boolean validRow = true;
                 Object sizeValue = row.getValue(sizeColumn);
                 if (sizeValue instanceof String) {
                     ByteSize byteSize = new ByteSize((String) sizeValue);
                     totalBytes += byteSize.getBytes();
                 } else {
                     validRow = false;
                 }
 
                 Object timeValue = row.getValue(timeColumn);
                 if (timeValue instanceof String) {
                     TimeDuration timeDuration = new TimeDuration((String) timeValue);
                     totalNanos += timeDuration.getNanos();
                 } else {
                     validRow = false;
                 }
 
                 if (validRow) {
                     validRows++;
                 }
             } catch (Exception e) {
                 // Skip invalid rows
             }
         }
         rowCount += validRows;
 
         context.getTransientStore().set(TransientVariableScope.GLOBAL, BYTE_TOTAL_KEY, totalBytes);
         context.getTransientStore().set(TransientVariableScope.GLOBAL, TIME_TOTAL_KEY, totalNanos);
         context.getTransientStore().set(TransientVariableScope.GLOBAL, ROW_COUNT_KEY, rowCount);
 
         // Finalization: return results when input is empty
         if (rows.isEmpty()) {
             Row result = new Row();
             double outputSize = convertBytes(totalBytes, outputSizeUnit);
             double outputTime = convertNanos(totalNanos, outputTimeUnit);
 
             if ("average".equals(aggregationType) && rowCount > 0) {
                 outputSize /= rowCount;
                 outputTime /= rowCount;
             }
 
             result.add(outputSizeColumn, outputSize);
             result.add(outputTimeColumn, outputTime);
             return Collections.singletonList(result);
         }
 
         return new ArrayList<>();
     }
 
     @Override
     public void destroy() {
         // No cleanup needed
     }
 
     private double convertBytes(long bytes, String unit) throws DirectiveExecutionException {
         try {
             return new ByteSize(String.valueOf(bytes)).getValue(unit);
         } catch (IllegalArgumentException e) {
             throw new DirectiveExecutionException(
                 NAME, String.format("Unsupported size unit '%s'. Use b, kb, mb, gb, tb, or pb", unit));
         }
     }
 
     private double convertNanos(long nanos, String unit) throws DirectiveExecutionException {
         try {
             return new TimeDuration(String.valueOf(nanos) + "ns").getValue(unit);
         } catch (IllegalArgumentException e) {
             throw new DirectiveExecutionException(
                 NAME, "Unsupported time unit '" + unit + "'. Use ns, us, ms, s, sec, min, h, hr, d, or day");
         }
     }
 }
