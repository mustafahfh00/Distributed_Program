# SE355 – Distributed Systems

## Assignment 1: Distributed Word Delivery System (7 Processes)

### **Group Members**

https://github.com/mustafahfh00/Distributed_Program.git

- **Mustafa Haitham Fadhil** — mh22197@auis.edu.krd
- **Hazhin Noori Mahmood ** — hn22045@auis.edu.krd

---

## 📌 **Project Overview**

This assignment implements a **distributed Java application** consisting of **seven processes (P0–P6)** that communicate using **Java sockets** and maintain **causal-order message delivery** using **vector clocks**.

The system workflow:

1. **Main process (P0)** prompts the user for a paragraph.
2. The paragraph is split into words, and each word is sent **randomly** to one of the 6 worker processes (P1–P6).
3. The Main process waits **15 seconds**.
4. P0 sends a **COLLECT** request to all workers.
5. Each worker returns all stored words in **original index order**.
6. P0 reconstructs and prints the original paragraph.

No external libraries are used—everything is built with **Java Sockets**, **Object Streams**, and **VectorClock** logic as covered in class.

---

## 📂 **File Descriptions**

### **Core Classes**

| File                   | Responsibility                                                                 |
| ---------------------- | ------------------------------------------------------------------------------ |
| **MainProcess.java**   | Coordinates the entire workflow: input, distribution, collect, reconstruction. |
| **WorkerProcess.java** | Worker server: receives DATA/COLLECT, stores words, returns responses.         |
| **ProcessNode.java**   | Base class for all processes with ID, port, and vector clock.                  |
| **Message.java**       | Serializable message struct with type, content, index, and vector clock.       |
| **VectorClock.java**   | Implementation of vector clocks for causal-order message delivery.             |
| **NetUtil.java**       | Networking utilities for sending/receiving Message objects.                    |

### **Clean-Architecture Helper Classes**

| File                          | Responsibility                                          |
| ----------------------------- | ------------------------------------------------------- |
| **WordDistributor.java**      | Splits paragraph + selects random worker for each word. |
| **WordCollector.java**        | Stores words in sorted order based on original indices. |
| **WorkerMessageHandler.java** | Handles DATA and COLLECT message logic inside worker.   |

### **Execution**

| File            | Responsibility                                                  |
| --------------- | --------------------------------------------------------------- |
| **RunAll.java** | Starts P1–P6 worker processes + main process for quick testing. |
| **build.sh**    | Compiles all .java files and launches the system.               |

---

## 🧠 **How Causal Order is Preserved**

Causal ordering is guaranteed using **Vector Clocks**, following SE355 theory:

- Each process maintains an array `clock[i]` representing its logical time.
- Before sending a message:  
  `clock[id]++` (local event)
- On receive:
  - Update: `clock[k] = max(clock[k], received[k])`
  - Tick local: `clock[id]++`

This satisfies the correctness rules from:

- SE355 Notebook — _Lecture 17: Vector Clocks_
- Kshemkalyani & Singhal — _Distributed Computing, Ch. 3_

Thus, messages always reflect a consistent **happens-before** relationship.

---

## 💻 **How to Compile and Run**

### **Using build.sh (recommended)**

In the project root:

```bash
chmod +x build.sh
./build.sh

This will:

Clean the previous build

Compile all .java files

Run RunAll.java

Launch P1–P6 workers and then P0

⚠️ Note About build.sh Not Running

If build.sh fails to execute, the cause is usually Windows-style line endings (CRLF).
WSL and Linux require Unix-style line endings (LF) for shell scripts.

To fix this, run:

sed -i 's/\r$//' build.sh
chmod +x build.sh
./build.sh

```

Time → -------------------------------------------------->

P0: |---- send w0 → P3 ----- send w1 → P1 ----- wait ----- COLLECT → | ← responses |
P1: |---- recv w1 ---- store ---- recv COLLECT ---- send RESPONSE → |
P2: |---- (maybe no word) --- recv COLLECT ---- send RESPONSE → |
P3: |---- recv w0 ---- store ---- recv COLLECT ---- send RESPONSE → |
P4: |---- idle ---- recv COLLECT ---- send RESPONSE → |
P5: |---- idle ---- recv COLLECT ---- send RESPONSE → |
P6: |---- idle ---- recv COLLECT ---- send RESPONSE → |

This diagrams:

Sends of DATA messages

15-second delay

COLLECT phase

Return of RESPONSE messages

📜 References
Textbook

Kshemkalyani, A.D. & Singhal, M. Distributed Computing: Principles, Algorithms, and Systems.

Course Materials

SE355 Notebook:

Lecture 7 — Networking

Lecture 11 — Ordering

Lecture 12 — Happens-Before

Lecture 13 — Partial Order

Lecture 15–17 — Logical & Vector Clocks

ITE306 Notebook:

Process creation, shell scripting, system calls

Java Networking

Elliotte Rusty Harold, Java Network Programming, O’Reilly.
