# 🕹️ Networked Multiplayer Tic-Tac-Toe Game with Chat

## Overview

This is a **networked multiplayer Tic-Tac-Toe game** built in **Java**, where players can connect over a network and play in real time. It supports multiple simultaneous games, user authentication, and includes an in-game **chat feature** so players can communicate while playing.

The system is built using a **client-server architecture**, with each client running a GUI and the server managing matches and routing messages.

---

## 🎯 Features

- ✅ Real-time multiplayer Tic-Tac-Toe (3x3 grid)
- ✅ GUI-based client interface (JavaFX/Swing)
- ✅ Server supports multiple simultaneous clients and matches
- ✅ User login system with username and password authentication
- ✅ 🔸 In-game chat between two players in a match 
- ⚡ Bonus-ready: support for larger boards, game rankings, emojis, etc.

---

## 🗂️ Project Structure

### 📁 Project Structure

```plaintext
TicTacToeGame/
├── client/                         # Client-side logic and interface
│   ├── Client.java                 # Connects to server, sends/receives data
│   ├── ClientListener.java         # Listens to server responses (moves/chat)
│   ├── GameWindow.java             # Main GUI frame (board + chat)
│   ├── GamePanel.java              # Draws and handles the Tic-Tac-Toe board
│   └── ChatPanel.java              # 🔸 Chat UI: input field, display area, send button
│
├── server/                         # Server-side logic
│   ├── Server.java                 # Initializes server and handles client connections
│   ├── ClientHandler.java          # Communicates with individual clients
│   ├── GameRoom.java               # Manages a match between two players
│   └── UserDatabase.java           # Simple file-based login validation
│
├── common/                         # Shared constants and protocol formats
│   └── MessageProtocol.java        # Defines standardized message tags like CHAT:, MOVE:
│
└── resources/                      # External data files
    └── db.txt                      # Simulated user database (username:password)


