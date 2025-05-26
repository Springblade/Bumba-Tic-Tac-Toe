# 🕹️ Networked Multiplayer Tic-Tac-Toe Game 

## Overview

This is a **networked multiplayer Tic-Tac-Toe game** built in **Java**, where players can connect over a network and play in real time. It supports multiple simultaneous games, user authentication, and includes an in-game **chat feature** so players can communicate while playing.

The system is built using a **client-server architecture**, with each client running a GUI and the server managing matches and routing messages.

---

## 🎯 Features

- ✅ Real-time multiplayer Tic-Tac-Toe (3x3 grid)
- ✅ Board Expansion (9x9 grid)
- ✅ GUI-based client interface (JavaFX)
- ✅ Server supports multiple simultaneous clients and matches
- ✅ User login system with username and password authentication
- ✅ In-game chat between players and spectators in a match
- ✅ Ranking management system
- ✅ Spectator support
- ✅ Server has a database which store the information of users
- ✅ Has match browsing system

---

## 🗂️ Project Structure

```plaintext
tic_tac_toe/
├── client/                         # Client-side logic and interface
│   ├── Client.java                 # Connects to server, sends/receives data
│
├── server/                         # Server-side logic
│   ├── ClientHandler.java          # Communicates with individual clients
│   └── GamesManager.java           # Manage each game session
│ 
├── enumeration/                    # Some miscellaneous for the logic
│   └── GameState.java              # State for indicating game turn/state
│
│
├── game/                           # Game logic residing in the server
│   ├── TicTacToe.java              # Core logic
│   ├── TicTacToe3x3.java           # Extened logic for 3x3 board
│   └── TicTacToe9x9.java           # Extended logic for 9x9 board
│
├── database/                       # The database module in the server
│   ├── Connect.java                # Connect to the DB server
│   ├── Create.java                 # Registration for new user
│   ├── EloMod.java                 # Elo modification after finish a game
│   ├── LogIn.java                  # User authentication
│   └── Rank.java                   # Global ranking of all users
│
└── resources/                      # External data files
    ├── fxml files                  # Format for the GUI
    ├── img                         # Images
    ├── css                         # Some styling with fxml
    └── sfx                         # Simple sound effects
