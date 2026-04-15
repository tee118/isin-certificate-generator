# ISIN Certificate Generator

A multi-threaded Java application that generates financial certificate updates with valid ISIN codes.

## What It Does

Generates realistic financial certificate data with:
- Valid ISIN codes (using Luhn algorithm check digits)
- Random bid/ask prices and volumes
- Multi-threaded processing for performance

## Quick Start

```bash
# Run tests
mvn clean test

# Generate 100 certificates using 4 threads
java -cp target/classes com.solvians.showcase.App 4 100
```

## Example Output

```
1776247194978,DXZ2AR1K14N5,110.00,3657,151.99,7054
1776247194978,LL2Y0VWYUY58,171.30,1112,121.58,9298
```

Each line: `timestamp,ISIN,bidPrice,bidSize,askPrice,askSize`

## Requirements

- Java 8+
- Maven 3.x
