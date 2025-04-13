# Aggregate Stats Directive

The `aggregate-stats` directive in Wrangler aggregates byte sizes and time durations from input rows, computing either the total or average and outputting results in specified units. This feature is implemented in the `io.cdap.directives.aggregate.AggregateStats` class and supports flexible unit conversions for sizes (e.g., `kb`, `mb`) and times (e.g., `ms`, `s`).

## Table of Contents

- [Aggregate Stats Directive](#aggregate-stats-directive)
  - [Table of Contents](#table-of-contents)
  - [Overview](#overview)
  - [Usage](#usage)
  - [Syntax](#syntax)
  - [Supported Units](#supported-units)
    - [Byte Sizes](#byte-sizes)
    - [Time Durations](#time-durations)
  - [Examples](#examples)
    - [Example 1: Total Aggregation (Default Units)](#example-1-total-aggregation-default-units)
- [Aggregate Stats Directive](#aggregate-stats-directive-1)
  - [Table of Contents](#table-of-contents-1)
  - [Overview](#overview-1)
  - [Usage](#usage-1)
  - [Syntax](#syntax-1)
  - [Supported Units](#supported-units-1)
    - [Byte Sizes](#byte-sizes-1)
    - [Time Durations](#time-durations-1)
  - [Examples](#examples-1)
    - [Example 1: Total Aggregation (Default Units)](#example-1-total-aggregation-default-units-1)
    - [Example 2: Average Aggregation (Custom Units)](#example-2-average-aggregation-custom-units)
    - [Example 3: Invalid Inputs](#example-3-invalid-inputs)
    - [Example 4: Empty Input](#example-4-empty-input)
  - [Setup](#setup)

## Overview

The `aggregate-stats` directive processes rows containing byte size and time duration values, aggregating them into a single output row. It supports:

- **Aggregation Types**: Total or average.
- **Input Columns**: One column for byte sizes, one for time durations.
- **Output Columns**: User-specified columns for aggregated size and time.
- **Unit Conversion**: Converts inputs to a base unit (bytes, nanoseconds) and outputs to user-specified units.
- **Error Handling**: Skips invalid inputs (e.g., `"invalid"`) and throws errors for unsupported units.

This directive is ideal for summarizing data like file sizes or processing times in data pipelines.

## Usage

Add the `aggregate-stats` directive to your Wrangler recipe to aggregate data. Specify input columns, output columns, and optional units/aggregation type. The directive processes all rows and returns one row with the aggregated values.

## Syntax

aggregate-stats :size-column :time-column :output-size-column :output-time-column [output-size-unit] [output-time-unit] [aggregation-type]

- **`:size-column`**: Input column with byte sizes (e.g., `"10kb"`, `"1.5MB"`).
- **`:time-column`**: Input column with time durations (e.g., `"5ms"`, `"2.1s"`).
- **`:output-size-column`**: Output column for aggregated size (e.g., `:total_size`).
- **`:output-time-column`**: Output column for aggregated time (e.g., `:total_time`).
- **`[output-size-unit]`**: Optional size unit for output (default: `mb`).
- **`[output-time-unit]`**: Optional time unit for output (default: `s`).
- **`[aggregation-type]`**: Optional type, either `total` or `average` (default: `total`).

**Note**: Column names must be prefixed with `:` (e.g., `:size`). Units and aggregation type are case-insensitive.

## Supported Units

### Byte Sizes

- `b` (bytes)
- `kb` (kilobytes)
- `mb` (megabytes)
- `gb` (gigabytes)
- `tb` (terabytes)
- `pb` (petabytes)

### Time Durations

- `ns` (nanoseconds)
- `us` (microseconds)
- `ms` (milliseconds)
- `s` or `sec` (seconds)
- `min` (minutes)
- `h` or `hr` (hours)
- `d` or `day` (days)

Invalid units (e.g., `xb`) throw a `DirectiveExecutionException`.

## Examples

### Example 1: Total Aggregation (Default Units)

Sum byte sizes and times, outputting in megabytes (`mb`) and seconds (`s`).

**Recipe**:
My apologies for the misunderstanding. Thank you for clarifying that you want the README file as a standalone file with proper formatting. Since we’ve been working on the aggregate-stats feature in wrangler-core (with AggregateStats.java and AggregateStatsTest.java, avoiding changes to ByteSize.java and TimeDuration.java), I’ll provide the README file exactly as previously shared, ensuring it’s a single, well-formatted Markdown file that instructs users on how to use the aggregate-stats directive. It will include usage instructions, syntax, supported units, examples, setup steps, testing commands, and troubleshooting tips, all tailored to your implementation (internal parsing, double outputs, :column syntax). This will be the complete file, ready to be saved as README.md.

README File: README.md
markdown

Collapse

Wrap

Copy

# Aggregate Stats Directive

The `aggregate-stats` directive in Wrangler aggregates byte sizes and time durations from input rows, computing either the total or average and outputting results in specified units. This feature is implemented in the `io.cdap.directives.aggregate.AggregateStats` class and supports flexible unit conversions for sizes (e.g., `kb`, `mb`) and times (e.g., `ms`, `s`).

## Table of Contents

- [Overview](#overview)
- [Usage](#usage)
- [Syntax](#syntax)
- [Supported Units](#supported-units)
- [Examples](#examples)
- [Setup](#setup)
- [Testing](#testing)
- [Troubleshooting](#troubleshooting)

## Overview

The `aggregate-stats` directive processes rows containing byte size and time duration values, aggregating them into a single output row. It supports:

- **Aggregation Types**: Total or average.
- **Input Columns**: One column for byte sizes, one for time durations.
- **Output Columns**: User-specified columns for aggregated size and time.
- **Unit Conversion**: Converts inputs to a base unit (bytes, nanoseconds) and outputs to user-specified units.
- **Error Handling**: Skips invalid inputs (e.g., `"invalid"`) and throws errors for unsupported units.

This directive is ideal for summarizing data like file sizes or processing times in data pipelines.

## Usage

Add the `aggregate-stats` directive to your Wrangler recipe to aggregate data. Specify input columns, output columns, and optional units/aggregation type. The directive processes all rows and returns one row with the aggregated values.

## Syntax

aggregate-stats :size-column :time-column :output-size-column :output-time-column [output-size-unit] [output-time-unit] [aggregation-type]

text

Collapse

Wrap

Copy

- **`:size-column`**: Input column with byte sizes (e.g., `"10kb"`, `"1.5MB"`).
- **`:time-column`**: Input column with time durations (e.g., `"5ms"`, `"2.1s"`).
- **`:output-size-column`**: Output column for aggregated size (e.g., `:total_size`).
- **`:output-time-column`**: Output column for aggregated time (e.g., `:total_time`).
- **`[output-size-unit]`**: Optional size unit for output (default: `mb`).
- **`[output-time-unit]`**: Optional time unit for output (default: `s`).
- **`[aggregation-type]`**: Optional type, either `total` or `average` (default: `total`).

**Note**: Column names must be prefixed with `:` (e.g., `:size`). Units and aggregation type are case-insensitive.

## Supported Units

### Byte Sizes

- `b` (bytes)
- `kb` (kilobytes)
- `mb` (megabytes)
- `gb` (gigabytes)
- `tb` (terabytes)
- `pb` (petabytes)

### Time Durations

- `ns` (nanoseconds)
- `us` (microseconds)
- `ms` (milliseconds)
- `s` or `sec` (seconds)
- `min` (minutes)
- `h` or `hr` (hours)
- `d` or `day` (days)

Invalid units (e.g., `xb`) throw a `DirectiveExecutionException`.

## Examples

### Example 1: Total Aggregation (Default Units)

Sum byte sizes and times, outputting in megabytes (`mb`) and seconds (`s`).

**Recipe**:
aggregate-stats :size :time :total_size :total_time

**Input**:

| size   | time  |
|--------|-------|
| 10kb   | 5ms   |
| 1.5MB  | 2.1s  |

**Output**:

| total_size | total_time |
|------------|------------|
| 1.527      | 2.105      |

**Explanation**:

- `10kb + 1.5MB = 10,240 + 1,572,864 = 1,583,104 bytes = 1.527 mb`
- `5ms + 2.1s = 5,000,000 + 2,100,000,000 = 2,105,000,000 ns = 2.105 s`

### Example 2: Average Aggregation (Custom Units)

Compute the average size in kilobytes (`kb`) and time in milliseconds (`ms`).

**Recipe**:
aggregate-stats :size :time :avg_size :avg_time kb ms average

**Input**:

| size   | time  |
|--------|-------|
| 10kb   | 5ms   |
| 1.5MB  | 2.1s  |

**Output**:

| avg_size | avg_time |
|----------|----------|
| 791.552  | 1052.5   |

**Explanation**:

- `(10kb + 1.5MB) / 2 = (10,240 + 1,572,864) / 2 = 791,552 bytes = 791.552 kb`
- `(5ms + 2.1s) / 2 = (5,000,000 + 2,100,000,000) / 2 = 1,052,500,000 ns = 1052.5 ms`

### Example 3: Invalid Inputs

Skip invalid values and aggregate valid ones.

**Recipe**:
aggregate-stats :size :time :total_size :total_time mb s

**Input**:

| size   | time  |
|--------|-------|
| invalid| 5ms   |
| 1MB    | xyz   |
| 10kb   | 1s    |

**Output**:

| total_size | total_time |
|------------|------------|
| 1.010      | 1.005      |

**Explanation**:

- Skips `"invalid"`, aggregates `1MB + 10kb = 1,048,576 + 10,240 = 1,058,816 bytes = 1.010 mb`
- Skips `"xyz"`, aggregates `5ms + 1s = 5,000,000 + 1,000,000,000 = 1,005,000,000 ns = 1.005 s`

### Example 4: Empty Input

Handle empty input with zero outputs.

**Recipe**:
aggregate-stats :size :time :total_size :total_time

**Input**:
*(empty)*

**Output**:

| total_size | total_time |
|------------|------------|
| 0.0        | 0.0        |

## Setup

1. **Clone the Repository**:

   ```bash
   git clone <wrangler-repo-url>
   cd wrangler/wrangler-core

2. Build the Project:
  mvn clean compile
  This compiles AggregateStats.java and generates ANTLR parser classes from Directives.g4.

3. Testing this project
  mvn test -Dtest=AggregateStatsTest
  The tests cover:

  Total aggregation with default units (mb, s).
  Average aggregation with custom units (kb, ms).
  Invalid inputs (skipped gracefully).
  Empty input (returns zeros).
  Invalid units (throws DirectiveExecutionException).
  