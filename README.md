# Safe Learning Communities

A campus safety and facility reporting system developed for SDG 4 (Quality Education), Target 4.a — safe, effective learning environments.

## Overview

Safe Learning Communities allows students, teachers, and staff to report campus safety hazards (e.g. structural damage, electrical faults, flooding). Reports are automatically prioritised using pluggable strategies (hazard type / location), and administrators and maintenance staff are notified of new and updated reports via the Observer pattern. The system also displays live campus weather via the Open-Meteo API to support safety context.

## Requirements

- Java 17 or later
- (Optional, for building from source) Apache Maven 3.8+
- An internet connection is recommended for live weather data. The application runs normally without one — it will display a "Campus Weather Unavailable" message instead.

## Running the Application

### Option 1: Run the prebuilt JAR (recommended)

Make sure Java 17+ is installed:

    java -version

From the project root, run:

    java -jar target/safe-learning-communities-1.0-SNAPSHOT.jar

The application GUI will open automatically.

### Option 2: Build from source

Clone this repository:

    git clone https://github.com/Arafat6666/Safe_learning.git
    cd Safe_learning

Build the project with Maven:

    mvn clean package

Run the generated JAR:

    java -jar target/safe-learning-communities-1.0-SNAPSHOT.jar

## Running the Tests

    mvn test

This runs the full JUnit 5 test suite (36 tests) and generates a JaCoCo coverage report at:

    target/site/jacoco/index.html

Note: one test (submitReport_duplicateReport_shouldRejectSecondSubmission) is intentionally failing. It documents a known limitation — the system currently does not reject duplicate reports — as explained in Section 4.2 of the technical report.

## Main Class

com.safelearning.Main

## Team

Group 5 — CSC61204 Software Construction, Taylor's University