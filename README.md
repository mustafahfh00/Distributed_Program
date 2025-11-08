📘 README.md
# SE355 – Assignment 1  
**Course:** Distributed Systems (SE 355)  
**Instructor:** Yad Tahir / AUIS  
**Students:**  
- Mustafa Haitham Fadhil – mh22197@auis.edu.krd  
- Hajeen –  

---

## 🧩 Assignment Overview

This project implements a **distributed Java application** that demonstrates **message passing, concurrency, and causal ordered delivery** across multiple processes.

The system launches **seven processes**:

| Process | Role | Description |
|----------|------|-------------|
| **P0** | **Main Process** | Prompts the user for a paragraph, splits it into words, and distributes each word randomly to the six workers. After 15 seconds, it collects the words back and reconstructs the original paragraph. |
| **P1–P6** | **Worker Processes** | Receive words from the main process, store them, and send them back when requested. They preserve **causal order** using **vector clocks** and a **hold-back queue**. |

---

## ⚙️ Features Implemented

- **Inter-process communication** using Java `ServerSocket` / `Socket`.
- **Causal ordered delivery** enforced via **Vector Clocks**.  
  Messages are only delivered when all their causal predecessors have been delivered.
- **Concurrency** – each process runs on its own thread with independent clock and port.
- **Message passing simulation** – all 7 processes communicate over localhost TCP.
- **Space–time diagram** generation (events easily traceable from console logs).
- **Clean modular design** with reusable base class `ProcessNode`.

---

## 🗂️ Project Structure

```markdown
```text
assignment1/
├── src/
│   ├── Message.java          # Serializable message structure
│   ├── VectorClock.java      # Implements vector-clock logic
│   ├── NetUtil.java          # Utility for sending objects via sockets
│   ├── ProcessNode.java      # Abstract base class for all processes
│   ├── WorkerProcess.java    # Logic for worker nodes (P1–P6)
│   ├── MainProcess.java      # Main controller node (P0)
│   └── RunAll.java           # Launches all 7 processes
├── .gitignore
└── README.md



---

## 🚀 How to Run

### 🖥️ Step 1 – Compile
```bash
javac -d out src/assignment1/*.java

🖥️ Step 2 – Run (default paragraph)
java -cp out assignment1.RunAll

🖋️ Step 3 – Run with custom paragraph
java -cp out assignment1.RunAll "Hello from AUIS this is SE355 assignment one"

🔁 How It Works (Summary)

Initialization:
RunAll starts 6 worker threads (P1–P6), each listening on ports 6401–6406.
Then it starts the main process (P0) on port 6400.

Distribution Phase:
Main process splits the user’s paragraph into words and randomly assigns each word to a worker.

Causal Ordering:
Every message carries a vector clock snapshot.
Workers deliver messages only when the causal delivery condition holds:

msgVC[sender] == local[sender] + 1  AND  for all k ≠ sender, msgVC[k] ≤ local[k]


Wait & Collection Phase:
After 15 seconds, P0 sends a COLLECT message to all workers.
Each worker replies with all words it received.

Reconstruction:
P0 sorts returned words by their original index and prints the full reconstructed paragraph.

Termination:
P0 sends DONE messages to all workers to close sockets and exit cleanly.

🧠 Key Concepts Demonstrated
Concept	Explanation
Causal Delivery	Ensures messages are delivered respecting the happens-before relation.
Vector Clocks	Logical timestamps used to capture causality between distributed events.
Sockets	Simulate networked processes communicating via TCP on localhost.
Hold-back Queue	Stores messages until they can be delivered causally.
Concurrency	Multiple Java threads simulate independent nodes in a distributed network.
🕒 Space–Time Diagram (Conceptual)
Time ↓
P0 (Main)  | • send WORDs → P1..P6          | (15 s wait) | • send COLLECT → P1..P6 | • receive RETURN_WORDs | • print
P1–P6      | • receive WORD | • deliver causally | • receive COLLECT | • send RETURN_WORD → P0 | • receive DONE


Each arrow represents a message; vector-clock values evolve as messages are sent and delivered.

📚 References

Ajay (AUIS) – SE355 Lecture Slides:
Happens-Before, Causality, Logical & Vector Clocks, Message Delivery.

Tanenbaum & van Steen – Distributed Systems: Principles and Paradigms, 2nd Ed.

Elliotte Harold, Java Network Programming, 4th Edition (O’Reilly).

Java SE Documentation: ServerSocket
, Socket
.

🏁 Output Example
[P1] listening on port 6401
...
[P0] Connected to P6
Original paragraph (reconstructed):
Hello from AUIS this is SE355 assignment one

🧹 Notes

Only src/, README.md, and .gitignore are version-controlled.

Build artifacts (out/, .class files, .idea/, .vscode/, etc.) are ignored via .gitignore.

Tested on Windows 11 / JDK 17 with PowerShell.

This project demonstrates distributed communication and causal message ordering in Java, applying the theoretical principles of SE355 to a practical multi-process simulation.


---

### ✅ What You Need to Do
1. Replace the two placeholder names/emails at the top.  
2. Save this file as `README.md` in your project root.  
3. Commit & push:
   ```bash
   git add README.md
   git commit -m "Add detailed README"
   git push

   🧭 Add this to your README (under “Project Structure”)
---

## 🧱 System Architecture Diagram


                  ┌──────────────────────────┐
                  │         MAIN (P0)        │
                  │ Port: 6400               │
                  │ Role: Controller         │
                  │ • Prompts user input     │
                  │ • Splits paragraph       │
                  │ • Sends words randomly   │
                  │ • Waits 15s then collects│
                  │ • Rebuilds paragraph     │
                  └────────────┬─────────────┘
                               │
       ┌───────────────────────┼────────────────────────┐
       │                       │                        │
┌──────▼──────┐         ┌──────▼──────┐          ┌──────▼──────┐
│ WORKER (P1) │         │ WORKER (P2) │          │ WORKER (P3) │
│ Port: 6401  │         │ Port: 6402  │          │ Port: 6403  │
│ Receives    │         │ Receives    │          │ Receives    │
│ & stores    │         │ & stores    │          │ & stores    │
│ words       │         │ words       │          │ words       │
│-------------│         │-------------│          │-------------│
│ Returns on  │         │ Returns on  │          │ Returns on  │
│ "COLLECT"   │         │ "COLLECT"   │          │ "COLLECT"   │
└─────────────┘         └─────────────┘          └─────────────┘
       │                       │                        │
       └───────────────────────┼────────────────────────┘
                               │
                               ▼
                  ┌──────────────────────────┐
                  │       WORKER (P4)        │
                  │ Port: 6404               │
                  │ Receives words           │
                  │ Sends back to Main       │
                  └────────────┬─────────────┘
                               │
         ┌─────────────────────┼────────────────────┐
         │                                          │
   ┌─────▼─────┐                              ┌─────▼─────┐
   │ WORKER P5  │                              │ WORKER P6  │
   │ Port: 6405 │                              │ Port: 6406 │
   │ Receives,  │                              │ Receives,  │
   │ stores,    │                              │ stores,    │
   │ returns    │                              │ returns    │
   └────────────┘                              └────────────┘


**Communication Flow**
1. Main (P0) → sends each word → Random Worker (P1–P6)  
2. Workers store words and update vector clocks.  
3. After 15 seconds, Main sends `COLLECT` to all.  
4. Workers send back `RETURN_WORD` to Main.  
5. Main sorts by index and prints reconstructed paragraph.

---

### 🔄 Logical Flow Summary

| Phase | Action | Processes Involved |
|--------|---------|--------------------|
| **1. Initialization** | Start sockets on ports 6400–6406 | P0–P6 |
| **2. Distribution** | Main distributes words | P0 → P1–P6 |
| **3. Wait** | Main sleeps 15 seconds | P0 |
| **4. Collection** | Main sends `COLLECT` requests | P0 → P1–P6 |
| **5. Reconstruction** | Workers reply with stored words | P1–P6 → P0 |
| **6. Output** | Main prints paragraph | P0 |
| **7. Termination** | Main sends `DONE` to all workers | P0 → P1–P6 |

---

💡 **Tip:**  
You can take a screenshot of this diagram (or draw a similar one in draw.io or Lucidchart) for your Moodle PDF report — it looks clear, structured, and professional.

---

Would you like me to create a **digital (graphical) version** of this diagram (in draw.io / mermaid style) so you can embed it visually on GitHub or export it for your report PDF?
