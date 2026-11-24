# cpu-disk-scheduling-simulator
Code was developed locally via Google Drive and committed to the repository at the end of the project. See the contribution matrix (Appendix A).

# CPU and Disk Scheduling Simulator

**Course:** ICS26012 – Operating Systems
**Group:** Bernie Bears (Group No. 9)

A Java Swing app that simulates two CPU scheduling and two disk scheduling algorithms.

## Algorithms and Owners
| Algorithm | Type | Owner |
|---|---|---|
| Shortest Job First (SJF) | CPU, non-preemptive | Regina Dela Llarte |
| Round Robin | CPU, preemptive | Matthew Aguirre |
| SCAN | Disk | AJ Valenzuela |
| C-LOOK | Disk | Maxine Basada |

## Requirements
Java 11 or newer (JDK/JRE). No other libraries needed.

## How to Run
```
java -jar SchedulingSimulator.jar
```

## How to Build
```
javac -d build src/*.java
```

## Using the App
1. Pick an algorithm from the main menu.
2. Enter the inputs, or click **Load Sample Data**.
3. Click **Run** to see the results. **Reset** clears the inputs.

## Input Limits
- **CPU:** 10 to 20 processes, arrival time ≥ 0, burst time ≥ 1, quantum ≥ 1 (Round Robin only).
- **Disk:** 5 to 20 requests, disk size ≥ 2, head and requests between 0 and disk size − 1.
