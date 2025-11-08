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

                Time →
 ────────────────────────────────────────────────────────────────────────────────────────────────────────────

P0 (Main) │  ● Start
           │      ● send WORDs → P1..P6
           │                         ─────────── 15 s wait ───────────
           │                                              ● send COLLECT → P1..P6
           │                                                              ● receive RETURN_WORDs
           │                                                                                 ● print
           │                                                                                       ● send DONE → P1..P6
           │                                                                                                     ● terminate
           │──────────────────────────────────────────────────────────────────────────────────────────────────────

P1         │             ● receive WORD (from P0)
           │                 ● deliver causally
           │                       ● store
           │                                                  ● receive COLLECT
           │                                                        ● send RETURN_WORD → P0
           │                                                                 ● receive DONE → terminate
           │──────────────────────────────────────────────────────────────────────────────────────────────────────

P2         │                   ● receive WORD
           │                       ● deliver causally
           │                             ● store
           │                                                  ● receive COLLECT
           │                                                        ● send RETURN_WORD → P0
           │                                                                 ● receive DONE → terminate
           │──────────────────────────────────────────────────────────────────────────────────────────────────────

P3         │                         ● receive WORD
           │                             ● deliver causally
           │                                   ● store
           │                                                  ● receive COLLECT
           │                                                        ● send RETURN_WORD → P0
           │                                                                 ● receive DONE → terminate
           │──────────────────────────────────────────────────────────────────────────────────────────────────────

P4         │                               ● receive WORD
           │                                   ● deliver causally
           │                                         ● store
           │                                                  ● receive COLLECT
           │                                                        ● send RETURN_WORD → P0
           │                                                                 ● receive DONE → terminate
           │──────────────────────────────────────────────────────────────────────────────────────────────────────

P5         │                                     ● receive WORD
           │                                         ● deliver causally
           │                                               ● store
           │                                                  ● receive COLLECT
           │                                                        ● send RETURN_WORD → P0
           │                                                                 ● receive DONE → terminate
           │──────────────────────────────────────────────────────────────────────────────────────────────────────

P6         │                                           ● receive WORD
           │                                               ● deliver causally
           │                                                     ● store
           │                                                  ● receive COLLECT
           │                                                        ● send RETURN_WORD → P0
           │                                                                 ● receive DONE → terminate
           │──────────────────────────────────────────────────────────────────────────────────────────────────────


explain each file ::

1-Message.java:

🧩 FILE: Message.java
🎯 Purpose

This class defines the message structure that every process (Main or Worker) sends across the network.
Think of it as the “envelope” that carries data between distributed processes — including the payload (word or command) and metadata (IDs, vector clock, type).

🧱 Class Breakdown
public class Message implements Serializable


Implements Serializable → allows the object to be easily sent over sockets using Java’s built-in serialization (convert object → byte stream).

Required so that ObjectOutputStream and ObjectInputStream in your network code can transmit complete Java objects.

📦 Enum: Message.Type
public enum Type { WORD, DONE, COLLECT, RETURN_WORD }


Each type tells the receiver what kind of message it is:

Type	Sent By	Purpose
WORD	MainProcess → Worker	Contains a word from the paragraph to be stored.
COLLECT	MainProcess → Worker	Tells workers to send back their stored words.
RETURN_WORD	Worker → MainProcess	Contains the word being returned to P₀.
DONE	MainProcess → Worker	Tells workers to stop execution (cleanup).
🧩 Fields (Message contents)
Field	Type	Meaning
type	Type	Which action this message represents (WORD, COLLECT, etc.)
fromId	int	ID of the sending process (0 for Main, 1–6 for workers).
toId	int	ID of the target process.
vc	int[]	Vector clock snapshot at send time → for causal ordering.
wordIndex	int	Original position of the word in the input paragraph (for reconstruction).
payload	String	The actual word or command text (“flush”, “bye”, etc.).
⚙️ Constructor
public Message(Type type, int fromId, int toId, int[] vc, int wordIndex, String payload)


When creating a new message:

The process defines who sends it (fromId),

who should receive it (toId),

what kind of message (type),

includes a snapshot of its current vector clock,

and optionally attaches a payload (word or command).

Example:

new Message(Type.WORD, 0, 3, vc, 5, "Hello");


means

“From process P0 to P3, here’s the word ‘Hello’, which was the 6th word (index 5) in the original paragraph.”

🧠 Vector Clock Field (vc)

This array encodes causal time — it shows how far each process has progressed.

When you send or receive a message, processes update or merge their vector clocks.

This ensures causal order delivery (no message is delivered before its causal predecessor), as required by your SE355 assignment.

Example (for 7 processes):

vc = [1,0,0,0,0,0,0]  → means P0 has done one event, others none.


Later after several events:

vc = [3,2,0,1,0,0,0]  → combined causal history from P0, P1, P3

🔍 toString() method
@Override
public String toString() {
    return "Message{" + type + ", from=" + fromId + ", to=" + toId +
           ", idx=" + wordIndex + ", vc=" + Arrays.toString(vc) +
           ", payload='" + payload + "'}";
}


For debugging/logging: prints all the message contents.

Example output:

Message{WORD, from=0, to=4, idx=2, vc=[1,0,0,0,0,0,0], payload='AUIS'}

🧠 Concepts Tied to SE355 Topics
Concept	Relation
Message passing	Each process communicates only by sending Message objects (no shared memory).
Asynchronous network	Messages arrive at unpredictable times; the vc field handles this logically.
Causal ordering	The vector clock ensures send(e₁) → receive(e₂) if e₁ → e₂ (happens-before).
Serialization	Necessary for transmitting structured data objects via sockets.
FIFO delivery (TCP)	TCP already keeps order per sender; the vc ensures global causal order.
🧾 Summary

Message.java is the “packet” class for your distributed system.

It carries data + metadata that maintain both application logic and causal consistency.

Understanding this file means you know exactly what information flows between processes — the foundation for all other files.

2-VectorClock.java:

🎯 Purpose

A Vector Clock is a logical mechanism that maintains causal relationships between events across distributed processes.

It ensures that if:

event A → event B (A happens before B),
then V(A) < V(B) component-wise.

If neither is less nor greater:

events A and B are concurrent.

So, VectorClock.java gives your system the power to decide:

Which message can be delivered now, and

Which one must be held back until its causal dependencies arrive.

📜 Concept Recap (From Lectures)

Based on your slides:

There’s no global clock (Lecture 10 – Physical Clocks).

We need logical time to reason about causality (Lecture 11 – Logical Clocks).

Vector clocks solve the limitations of Lamport clocks, giving strong consistency (Lecture 11 p.53–66).

From Lecture 12 (Message Delivery) and Lecture 6 (Happens-Before):

"x → y means every element of Vx ≤ Vy and at least one is strictly smaller."

That’s the rule your class encodes.

1️⃣ private final int[] clock;

Each process maintains an array:

Size = total number of processes (7 here: P0–P6).

Example for process P3:

clock = [2, 0, 0, 5, 0, 1, 0]


→ means:
P0 did 2 events, P3 did 5, P5 did 1, others 0.

2️⃣ tick(int processId)

Every local event (send, receive, internal) causes the process to increment its own clock entry:

clock[processId]++;


This ensures monotonic time — time always moves forward locally.

Example:

Before sending: [1,0,0,0,0,0,0]
After tick():   [2,0,0,0,0,0,0]

3️⃣ snapshot()

When sending a message, the process includes a copy of its current clock vector in the message (vc in Message.java).

This captures a snapshot of causal history at the time of sending.

4️⃣ update(int[] other)

When a process receives a message, it merges its clock with the sender’s:

for each i:  clock[i] = max(clock[i], other[i])


Then it also increments its own entry (because receiving itself is an event).

Example:

Process	Before Receive	Sender’s Clock	After Merge (update + tick)
P2	[3,1,0,0,0,0,0]	[4,0,2,0,0,0,0]	[4,1,2,0,0,0,0] + tick → [4,1,3,0,0,0,0]

This ensures that the receiver’s clock “jumps ahead” enough to reflect all events it has now causally seen.

5️⃣ lessThanOrEqual(int[] a, int[] b)

Implements the happens-before rule:

a → b ⇔ ∀i: a[i] ≤ b[i] and ∃j: a[j] < b[j]

If this returns true, event A causally precedes event B.

If both a ≤ b and b ≤ a are false, then A and B are concurrent.

Example:

A	B	Relation
[1,0,0]	[2,0,0]	A → B
[2,1,0]	[1,2,0]	Concurrent
🧠 How It Works in Your Project
Step	Action	VectorClock Role
MainProcess sends WORD	tick(0) then send snapshot	Marks the send event.
WorkerProcess receives	update(vc) then tick(workerId)	Merges causal history before storing word.
MainProcess sends COLLECT	New tick + message with updated vector	Keeps causal chain consistent.
WorkerProcess sends RETURN_WORD	Tick + attach vc	Maintains happens-before order.
MainProcess receives RETURN_WORD	update(vc)	Ensures reconstruction doesn’t violate causality.

This ensures the reconstruction happens only after all causal dependencies have been respected (no message is delivered “too early”).

🧩 Connection to SE355 Concepts
Concept	How VectorClock Handles It
No global clock	Each process uses a local vector clock instead.
Happens-before relation (→)	Derived from component-wise comparison.
Causal order delivery	Messages are delivered only if their vector clock ≤ local clock + 1 (per process).
Concurrency detection	If two vectors are incomparable → events are concurrent.
Strong consistency	Vector clocks capture full causal history (unlike Lamport clocks).
🧾 In Summary

VectorClock.java is the causal memory of your distributed system.

It:

Tracks what each process knows about every other process’s progress.

Lets processes decide when it’s safe to deliver a message.

Enforces causal consistency across independent nodes.

Without it, your system could deliver messages in a physically possible but logically impossible order.

3-NetUtil.java"

🧩 FILE: NetUtil.java
🎯 Purpose

NetUtil is a helper (utility) class that hides all the low-level networking details.
It’s responsible for sending and receiving serialized Message objects over TCP sockets.

In simple terms:

It opens a socket connection to the target process.

It sends a Message object through an ObjectOutputStream.

It receives a Message object using an ObjectInputStream.

This makes message-passing reliable, ordered (FIFO), and easy to use across your system.

🧱 Explanation Line-by-Line
1️⃣ BASE_PORT
public static final int BASE_PORT = 5000;


All processes (P0–P6) derive their ports from this base:

P0 → port 5000
P1 → port 5001
P2 → port 5002
...
P6 → port 5006


So every process knows where to send messages.

This is what allows:

sendMsg(3, Type.WORD, ...)


to automatically reach Process 3.

2️⃣ send(Message msg)
Socket socket = new Socket("localhost", port);
ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream());
out.writeObject(msg);
out.flush();


This is the key function used by ProcessNode.sendMsg().

Creates a TCP socket to the destination port.

Wraps the stream in an ObjectOutputStream → allows sending the entire Message object.

Calls flush() to ensure data leaves the buffer immediately.

Uses Java’s built-in serialization (because Message implements Serializable).

After sending, the try-with-resources block automatically closes the socket.

🧠 Why TCP?

TCP guarantees FIFO per-sender, meaning messages from one sender arrive in order — matching your “causal ordered delivery” requirement (Lecture 12).

3️⃣ receive(Socket socket)
ObjectInputStream in = new ObjectInputStream(socket.getInputStream());
return (Message) in.readObject();


Used by the receiver side (in ProcessNode’s listener thread):

Waits until data arrives through the socket.

Reads the byte stream and deserializes it back into a Java Message object.

Returns the fully reconstructed object for further processing.

If no message is available, the socket’s blocking behavior causes the thread to wait.

🔄 Typical Send/Receive Flow
Step	Process	Code Involved	Description
1	P0	sendMsg() → NetUtil.send()	Sends a serialized Message over TCP.
2	P3	Server socket (port 5003) accepts connection.	
3	P3	NetUtil.receive()	Reads and reconstructs the Message.
4	P3	onDeliver()	Decides what to do with the message (store word, return word, etc.).
🧩 Network Reliability and FIFO

You learned in Lecture 12 – Message Delivery that:

“TCP offers FIFO delivery.”

That means if P0 sends messages m1, m2, m3 to P3,
they will always arrive in that same order.

NetUtil uses TCP sockets (Socket / ServerSocket),
so you don’t need to manually enforce FIFO at the network level — you only handle causal ordering (via vector clocks).

🧠 Error Handling

If a destination process isn’t running, this line:

new Socket("localhost", port)


throws a ConnectException.
That’s why higher-level code (like sendMsg()) may include retry logic or simply print an error.

🧾 Summary Table
Function	Role	Used By	Notes
send(Message msg)	Serialize & send a message	ProcessNode.sendMsg()	One TCP connection per send
receive(Socket socket)	Read & deserialize message	Receiver thread	Blocks until message arrives
BASE_PORT	Shared port offset	All processes	Defines the network addressing scheme
🧠 Connection to Distributed System Concepts
Concept	Implementation in NetUtil
Message-passing model	Each message is sent over sockets, not shared memory.
FIFO delivery	Guaranteed by TCP.
Serialization	Converts structured objects into byte streams.
Port mapping	Each process ID maps to a unique port.
Decoupling	High-level logic (ProcessNode, Worker, Main) don’t touch sockets directly.
✅ In Summary

NetUtil.java is your communication backbone:

Converts Java objects into bytes → sends over TCP → receives → converts back to Java objects.

Keeps the message-passing system simple and reliable.

Enforces FIFO, complements causal delivery, and hides network complexity.

4-ProcessNode.java:

🧩 FILE: ProcessNode.java
🎯 Purpose

ProcessNode is an abstract base class that defines what every process can do:

Communicate (send and receive messages over TCP),

Maintain causal order (via vector clocks),

Run continuously as an independent node.

Both MainProcess (P₀) and WorkerProcess (P₁–P₆) extend it.

🧠 Big Idea

Each process has:

A server socket to receive incoming messages.

A receiver thread to handle asynchronous communication.

A vector clock to keep track of logical time.

A message queue (inbox) for pending deliveries.

Utility functions like sendMsg(), startReceiver(), and onDeliver().

🧱 Detailed Explanation
1️⃣ Attributes
Field	Purpose
id	Identifies the process (0 = Main, 1–6 = Workers).
server	A ServerSocket bound to basePort + id, allowing other nodes to connect.
vc	Local vector clock tracking causal history.
inbox	Queue where delivered messages are stored.
holdBack	Temporary list for messages that arrive “too early.”
running	Keeps receiver thread alive until set to false.
2️⃣ sendMsg() — Sending Messages
vc.tick(id);
Message msg = new Message(...);
NetUtil.send(msg);


Every time a process sends a message:

It increments its local clock (new event).

Creates a message with:

Type (WORD, COLLECT, RETURN_WORD…)

Current vector clock snapshot

Sender & receiver IDs

Word index & payload

Calls NetUtil.send() to deliver over TCP.

🧠 Effect: The vector clock now reflects that this process has causally sent a message.

3️⃣ startReceiver() — The Listener Thread

This function spawns a background thread that continuously:

Waits for incoming TCP connections (server.accept()).

Reads a serialized Message (NetUtil.receive()).

Decides whether to deliver or hold back it.

This design reflects the asynchronous network model discussed in Lecture 6 (Happens-Before) and Lecture 12 (Message Delivery):

“Channels are asynchronous; messages can arrive out of order.
We must reorder them based on causality.”

4️⃣ isCausallyReady() — The Safety Check

Implements the rule:

Message from Pₛ with vector Vₘ can be delivered to local process Pᵢ
iff

For every k ≠ s, Vₘ[k] ≤ local[k]

And Vₘ[s] = local[s] + 1

That ensures:

The receiver has already seen all events that the sender had seen when it sent the message.

Therefore, no causal dependency is missing.

If not ready → message goes to holdBack.

5️⃣ deliver() — Deliver & Merge

When a message is ready:

Merge vector clocks → vc.update(m.vc)

Increment local entry → vc.tick(id)

Add to inbox for the app layer.

Trigger the abstract callback → onDeliver(m)

Print debug log.

Re-check holdBack to see if any previously blocked message can now be delivered.

This is causal order delivery in action 🔁

6️⃣ onDeliver(Message m)

Abstract method:

protected abstract void onDeliver(Message m) throws Exception;


Each subclass defines what to do when it receives a message:

MainProcess → store returned words.

WorkerProcess → store received words, respond to COLLECT, etc.

This separation keeps network logic reusable and message logic process-specific.

🧠 Connection to SE355 Concepts
Concept	Implementation
Asynchronous network	Receiver thread with blocking accept()
Message passing	sendMsg() and NetUtil.send()
No global clock	Vector clocks maintain logical time
Causal order delivery	isCausallyReady() and holdBack queue
Happens-Before relation (→)	Derived from vector comparisons
Concurrency	If not causally comparable → messages wait in holdBack
FIFO guarantee	Provided by TCP for each sender
Strong consistency	VectorClock + holdBack ensures no causality violation
🧩 Visualization: Message Flow
┌─────────────┐                ┌──────────────┐
│   P0 (Main) │                │   P3 (Worker)│
│  VectorClock│                │  VectorClock │
│ [1,0,0,...] │                │ [0,0,0,...]  │
└──────┬──────┘                └──────┬───────┘
       │ sendMsg(W3, WORD,"AUIS")    │
       │ ───────────────────────────▶ │
       │                              │
       │   receive + isCausallyReady  │
       │                              │
       │ ◀────── RETURN_WORD back──── │

🧾 In Summary

ProcessNode.java is your distributed OS kernel.

It:

Manages network communication and sockets.

Maintains logical time using vector clocks.

Guarantees causal message delivery.

Provides a generic framework for both Main and Worker processes.

Everything else (MainProcess, WorkerProcess) simply inherits and customizes this behavior.

5-WorkerProcess.java:

🧩 WorkerProcess.java

This file defines how Processes P₁–P₆ behave — the worker nodes in your distributed network.

They receive words, hold them, and respond when the Main process (P₀) tells them to send back their data — all while maintaining causal order using the logic inherited from ProcessNode.

🎯 Purpose

Each WorkerProcess represents one distributed node that:

Listens for incoming messages (from P₀).

Delivers messages causally (thanks to ProcessNode).

Stores words it receives.

Responds when asked to collect (sends them back to P₀).

Stops when told (DONE message).

Essentially, the worker is a reactive process:
It doesn’t act on its own; it reacts to the messages it receives.

🧱 Line-by-Line Explanation
1️⃣ Constructor
public WorkerProcess(int id) throws IOException {
    super(id);
    System.out.println("[P" + id + "] listening on port " + (basePort + id));
}


Calls the ProcessNode constructor, which sets up:

Server socket

Vector clock

Inbox queue

Receiver thread

Prints which port the worker is bound to (e.g., P₁ → 5001).

2️⃣ onDeliver()

This is the core callback from the parent class.
Whenever a causally valid message arrives, ProcessNode calls this method.

protected void onDeliver(Message m) throws Exception {
    switch (m.type) {
        case WORD -> handleWord(m);
        case COLLECT -> handleCollect();
        case DONE -> handleDone();
    }
}


The switch statement ensures each message type triggers the correct behavior.

3️⃣ Handling Each Message Type
🟢 WORD
private void handleWord(Message m) {
    storedWords.add(m);
    System.out.println("[P" + id + "] Stored word: " + m.payload + " (idx=" + m.wordIndex + ")");
}


Stores the received message in memory (storedWords list).

Each message carries its vector clock and original index.

🧠 Conceptually:
This is a local event — a worker receives and stores data sent by the main process.

🟠 COLLECT
private void handleCollect() throws IOException {
    for (Message msg : storedWords) {
        sendMsg(0, Message.Type.RETURN_WORD, msg.wordIndex, msg.payload);
    }
    storedWords.clear();
}


When the main process sends COLLECT:

The worker iterates through all stored messages.

Sends them back to the MainProcess (P₀) as RETURN_WORD messages.

Clears its local buffer afterward.

🧠 Conceptually:
This represents the return phase — similar to how a MapReduce “reduce” step collects partial results.

🔴 DONE
private void handleDone() throws IOException {
    System.out.println("[P" + id + "] Received DONE. Shutting down.");
    running = false;
    server.close();
}


Stops the loop and closes the server socket.

Terminates the receiver thread gracefully.

🧠 This marks the termination phase, ensuring each worker stops listening once the main process is finished.

4️⃣ runWorker()
public void runWorker() throws Exception {
    startReceiver();
    System.out.println("[P" + id + "] Worker started.");
    while (running) {
        Thread.sleep(1000);
    }
}


Starts the background receiver thread (ProcessNode’s listener).

Keeps the process alive until it receives a DONE message.

This method is what you would call in your Main launcher or script to start P₁–P₆.

🧠 Behavior Summary (Event Flow)
Step	Sender	Receiver	Message.Type	Worker Action
1	P₀	P₁–P₆	WORD	Stores the word
2	P₀	P₁–P₆	COLLECT	Sends stored words back as RETURN_WORD
3	P₁–P₆	P₀	RETURN_WORD	Main reconstructs the paragraph
4	P₀	P₁–P₆	DONE	Shuts down worker

This is exactly the “Send → Wait → Collect → Print → Stop” sequence required in your Assignment 1 instructions.

🔗 How It Uses Inherited Logic
Functionality	Source	Effect
startReceiver()	ProcessNode	Starts listening thread
sendMsg()	ProcessNode	Handles vector clock tick + network send
vc (vector clock)	VectorClock	Maintains causality
NetUtil.send()	NetUtil	TCP communication
onDeliver()	WorkerProcess	Application-specific message handling

So WorkerProcess focuses only on the "what", not the "how" —
because ProcessNode already handles all the how (causal delivery, networking, concurrency).

📘 Connection to SE355 Concepts
Distributed Systems Concept	How WorkerProcess Demonstrates It
Decentralized processing	Each worker acts independently, with its own clock and socket.
Message-passing model	No shared memory — only sends and receives messages.
Causal ordering	Inherited from ProcessNode’s vector clock checks.
Concurrency	All workers run in parallel, processing asynchronously.
Synchronization	Achieved through message types (COLLECT, DONE).
Fault tolerance (conceptually)	Each worker is independent; one failure doesn’t crash others.
🧩 In Summary

WorkerProcess.java is a lightweight reactive node that:

Receives messages (causally ordered),

Stores or sends data as needed,

Terminates cleanly when instructed.

It completes the distributed “worker pool” for your Assignment 1 network.

6-MainProcess.java:

.

🧩 FILE: MainProcess.java
🎯 Purpose

MainProcess represents Process P₀, the master controller in your assignment.
It:

Accepts a paragraph input from the user.

Splits it into words.

Randomly distributes each word among the six worker nodes (P₁–P₆).

Waits (asynchronously) for some time (15 seconds).

Sends a COLLECT message to every worker.

Receives the returned words and reassembles them in order.

Prints the reconstructed paragraph.

Sends DONE messages to shut down all workers.

⚙️ Step-by-Step Execution Flow
🪄 1️⃣ Initialization

When new MainProcess(paragraph) is called:

super(0) → sets process ID = 0.

Opens a server socket on port 5000 (using basePort + id).

Initializes its vector clock [0,0,0,0,0,0,0].

Splits the paragraph into an array of words.

💬 2️⃣ Distribution Phase
for (int i = 0; i < words.length; i++) {
    int worker = 1 + ThreadLocalRandom.current().nextInt(6);
    sendMsg(worker, Message.Type.WORD, i, words[i]);
}


Each word (with index i) is randomly sent to one of the six workers.

Each message carries:

Type.WORD

wordIndex = i

payload = word

vc = snapshot() → vector clock snapshot

sendMsg() (inherited from ProcessNode) handles:

Incrementing P₀’s clock (vc.tick(0)),

Creating a Message,

Sending it over TCP via NetUtil.send().

🧠 Effect:
Each worker now has a few words stored, with causality preserved because TCP + vector clocks ensure FIFO + causal order.

⏱ 3️⃣ Wait 15 Seconds
Thread.sleep(15_000);


Simulates asynchronous distributed delay:

Workers are assumed to be “processing” their data.

No synchronization occurs during this period.

🧠 Concept:
Demonstrates the absence of a global clock — P₀ waits on its own local time.

📥 4️⃣ Collection Phase
for (int w = 1; w <= 6; w++)
    sendMsg(w, Message.Type.COLLECT, -1, "flush");


Sends a COLLECT command to all workers.

Each worker reacts by iterating through its stored messages and sending RETURN_WORD back to P₀.

📬 5️⃣ Receiving Returned Words
while (collected.size() < words.length && System.currentTimeMillis() < deadline) {
    Message m = inbox.poll();
    if (m != null) onDeliver(m);
    else Thread.sleep(5);
}


The receiver thread (inherited from ProcessNode) has been running since startReceiver().

Each RETURN_WORD message, when causally ready, triggers:

onDeliver(m);


which stores the returned word in the collected map (collected.put(wordIndex, payload)).

🧠 Causality effect:

Even if messages arrive in different orders, they’re delivered to onDeliver() only when safe (no causal violation).

This maintains causal ordered delivery, one of your assignment’s main requirements.

🧾 6️⃣ Reconstructing the Original Paragraph
for (int i = 0; i < words.length; i++) {
    String w = collected.getOrDefault(i, words[i]);
    sb.append(w).append(' ');
}


Uses the wordIndex metadata to place each word back in the correct order.

Prints the reconstructed paragraph.

✅ Output Example:

Original paragraph (reconstructed):
Hello from AUIS Distributed Systems course!

🔚 7️⃣ Shutdown Phase
for (int w = 1; w <= 6; w++)
    sendMsg(w, Message.Type.DONE, -1, "bye");
running = false;
server.close();


Tells each worker to terminate gracefully.

Closes the main process’s own server socket.

🌐 Space–Time Diagram (Simplified)
Time →
P0(Main) | WORDs→P1..P6 | wait(15s) | COLLECT→P1..P6 | RETURN_WORDs← | DONE→P1..P6
P1–P6    | receive WORD | store | receive COLLECT | send RETURN_WORD | receive DONE


Each process runs its own local timeline (no global synchronization).
Vector clocks + causal checks ensure consistent ordering between these events.

🧠 Concept Mapping to SE355 Lectures
Concept	Where It Appears in Code	Lecture Reference
Message-passing model	sendMsg(), onDeliver()	Lecture 3–4
No global clock	Thread.sleep() delay; vector clocks manage ordering	Lecture 10
Logical time (Lamport / Vector)	vc.tick() and message vc field	Lecture 11
Causal order delivery	isCausallyReady() + hold-back in ProcessNode	Lecture 12
FIFO delivery	TCP channel ensures per-sender order	Lecture 12
Concurrency	Workers operate independently; P₀ collects asynchronously	Lecture 6
Distributed coordination	Main orchestrates via message types (WORD, COLLECT, DONE)	Lecture 7
Fault isolation	Each worker acts independently (failure of one doesn’t block others)	Lecture 8+
🧾 Summary
Phase	Action	Message Type(s)	Direction	Notes
Distribution	Split & send words	WORD	P₀ → P₁–P₆	Random assignment
Wait	15 s pause	–	Local only	Simulates async delay
Collection	Ask for data	COLLECT	P₀ → P₁–P₆	Request return
Return	Send back results	RETURN_WORD	P₁–P₆ → P₀	Delivered causally
Reconstruction	Combine in order	–	Local	Uses wordIndex
Shutdown	Stop all workers	DONE	P₀ → P₁–P₆	Clean exit
🔗 Hierarchy Recap (Whole System)
Message.java        → defines message format
VectorClock.java    → defines causal time
NetUtil.java        → sends/receives messages
ProcessNode.java    → base communication logic
WorkerProcess.java  → worker behavior (receive/store/respond)
MainProcess.java    → coordinator (send/collect/reconstruct)

💡 Final Takeaway

MainProcess is the brain:

It coordinates 6 distributed workers across a causal, asynchronous network.

Demonstrates message-passing, vector clocks, and causal delivery — the three pillars of distributed computing.

Your assignment therefore fulfills all key CLOs of SE355:

CLO	Achieved Through
CLO1 (Design practical DS)	7-process message-passing network
CLO2 (Avoid shared memory/global clock)	Vector clocks + TCP
CLO4 (Obtain concurrency without global clock)	Independent workers + logical ordering
CLO5 (Compare distributed techniques)	Message-passing + causal ordering

✅ Next (Optional Enhancement Ideas)
If you want to improve this project for later labs:

Add timestamps/log files to visualize message ordering.

Implement total order delivery (homework in Lecture 12).

Use threads for each worker from one launcher class to simulate the whole cluster on one machine.

🕒 Space–Time Diagram: Distributed Word Collection System
🧭 Legend

Horizontal axis = Time → (each process’s local clock advances independently)

Vertical lanes = Processes (P₀ main, P₁–P₆ workers)

Solid arrows = Message sent/received (causally related events)

Dashed arrows = Logical happens-before (→) causal links

Colored events = Message types (WORD, COLLECT, RETURN_WORD, DONE)

TIME →
┌────────────────────────────────────────────────────────────────────────────────────────────┐
│                                                                                             │
│     P0 (Main)                                                                               │
│     ─────────────────────────────────────────────────────────────────────────────────────   │
│     │ Input paragraph                                                                       │
│     │ Split into words                                                                      │
│     │                                                                                       │
│     │  send WORD(w0) ───────▶ P1                                                           │
│     │  send WORD(w1) ───────▶ P3                                                           │
│     │  send WORD(w2) ───────▶ P6                                                           │
│     │  send WORD(w3) ───────▶ P4                                                           │
│     │  send WORD(w4) ───────▶ P2                                                           │
│     │  send WORD(w5) ───────▶ P5                                                           │
│     │                                                                                       │
│     │  (15s local wait — simulates async delay, workers act independently)                  │
│     │                                                                                       │
│     │  send COLLECT ───────▶ P1                                                            │
│     │  send COLLECT ───────▶ P2                                                            │
│     │  send COLLECT ───────▶ P3                                                            │
│     │  send COLLECT ───────▶ P4                                                            │
│     │  send COLLECT ───────▶ P5                                                            │
│     │  send COLLECT ───────▶ P6                                                            │
│     │                                                                                       │
│     │  receive RETURN_WORD(wX) ◀────── P1                                                  │
│     │  receive RETURN_WORD(wY) ◀────── P2                                                  │
│     │  receive RETURN_WORD(wZ) ◀────── P3                                                  │
│     │  ... and so on until all words are received causally                                 │
│     │                                                                                       │
│     │  reconstruct paragraph                                                               │
│     │  print final result                                                                  │
│     │                                                                                       │
│     │  send DONE ───────▶ all workers                                                      │
│     │                                                                                       │
│     ─────────────────────────────────────────────────────────────────────────────────────   │
│                                                                                             │
│                                                                                             │
│  P1–P6 (Workers) behave identically but independently:                                     │
│                                                                                             │
│     P1: receive WORD(w0) ◀────── P0                                                        │
│         store locally                                                                      │
│         receive COLLECT ◀────── P0                                                         │
│         send RETURN_WORD(w0) ───────▶ P0                                                   │
│         receive DONE ◀────── P0 → shutdown                                                 │
│                                                                                             │
│     P2: receive WORD(w4) ◀────── P0 … (same pattern)                                       │
│     P3: receive WORD(w1) ◀────── P0 …                                                     │
│     P4: receive WORD(w3) ◀────── P0 …                                                     │
│     P5: receive WORD(w5) ◀────── P0 …                                                     │
│     P6: receive WORD(w2) ◀────── P0 …                                                     │
│                                                                                             │
│     Each worker maintains causal order (VectorClock ensures deliver only when ready)        │
│                                                                                             │
└────────────────────────────────────────────────────────────────────────────────────────────┘

🧩 Causal Links (Happens-Before →)
Relation	Explanation
send(WORD) → receive(WORD)	Each word send event causally precedes its receipt.
receive(WORD) → send(RETURN_WORD)	A worker must receive and store a word before returning it.
send(COLLECT) → send(RETURN_WORD)	P₀’s “collect” command causally triggers the return.
send(RETURN_WORD) → receive(RETURN_WORD)	Return messages follow causal order.
receive(RETURN_WORD) → print()	P₀ reconstructs only after all returns are delivered.
send(DONE) → worker shutdown	Ensures system terminates cleanly after all data is collected.
🧠 How Causal Delivery Works Here

Even if messages arrive out of physical order (e.g., network delays cause P3’s RETURN_WORD to arrive before P1’s):

The ProcessNode checks vector clock conditions before delivering each message.

The system ensures receive(RETURN_WORD) events respect the logical order of send() events.

Thus, no worker’s result is printed before its causal predecessors have been integrated.

So you satisfy Lamport’s “happens-before” model and vector clock-based causal delivery, exactly as required by the SE355 labs (Lectures 6, 7, 11, 12).

📊 Simplified Table View
Process	Role	Receives	Sends	Ends With
P₀ (Main)	Coordinator	RETURN_WORD	WORD, COLLECT, DONE	Reconstructs and terminates
P₁–P₆ (Workers)	Independent agents	WORD, COLLECT, DONE	RETURN_WORD	Shutdown after DONE
🧠 Learning Tie-In
SE355 Concept	Demonstrated By
Message-Passing Programming	TCP sockets + serialized Message objects
Causal Order Delivery	Vector clocks & holdBack mechanism in ProcessNode
Concurrency without Global Clock	All workers run independently, no central timing
Happens-Before Relation	Each send/receive pair respects partial ordering
Logical Clocks (Lamport → Vector)	Each sendMsg() / deliver() updates vector clock
Asynchronous Communication	Messages can arrive anytime; causal logic ensures correctness
Distributed Coordination	Main process orchestrates tasks via message types
🧾 Summary: What the Diagram Shows

P₀ acts like a MapReduce master — distributes, collects, and aggregates.

P₁–P₆ act as workers — passive but consistent participants.

The system maintains:

FIFO order (TCP),

Causal order (Vector clocks),

Event concurrency (no shared clock).

✅ Final Mental Model

🧩 The system is a miniature distributed data collection framework:

7 independent processes

communicating via causal message-passing

coordinated by one main controller (P₀).
It exemplifies how logical clocks, causal delivery, and message ordering combine to form a consistent distributed execution — the key idea behind modern distributed systems like MapReduce, Kafka, and Spark.