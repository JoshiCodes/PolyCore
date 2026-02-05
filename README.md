# 🎮 PolyCore

<div align="center">

**A Simple and Small Multiplayer Tetris Server in Java using WebSockets**

[![Java](https://img.shields.io/badge/Java-21-orange.svg)](https://openjdk.org/projects/jdk/21/)
[![Maven](https://img.shields.io/badge/Maven-3.x-blue.svg)](https://maven.apache.org/)
[![WebSocket](https://img.shields.io/badge/WebSocket-Jakarta-green.svg)](https://jakarta.ee/specifications/websocket/)
[![License](https://img.shields.io/badge/License-MIT-yellow.svg)](LICENSE)

</div>

> [!WARNING]  
>
> This Project is currently in early development and is not yet ready for production use. The core server functionality is being built, but many features are still in progress. Stay tuned for updates!

## 📖 Overview

PolyCore is a lightweight, real-time multiplayer Tetris server built with Java and WebSockets. It allows multiple players to connect, create or join game rooms, and compete against each other in classic Tetris gameplay.

### ✨ Features

- 🎯 **Real-time Multiplayer** - Play Tetris with friends in real-time
- 🏠 **Room System** - Create and join game rooms with configurable player limits
- 💬 **In-game Chat** - Communicate with other players via chat
- 🔧 **Command System** - Execute commands via chat (prefix with `/`)
- ⚙️ **Configurable** - Flexible YAML-based configuration
- 🔌 **Extensible Packet System** - Easy-to-extend annotation-based packet handling
- 🎮 **Full Tetris Mechanics** - Rotation, hold piece, hard drop, line clearing, and more

### Clients
#### Currently, there are no official clients for PolyCore.
#### However, you can create your own client using any WebSocket library that follows the packet structure defined in the documentation below.
#### I do plan on creating a simple web-based client in the future (or even a Java-based one), as soon as the Server is working as intended.

---

## 🚀 Getting Started

### Installation

#### TODO

---

## ⚙️ Configuration

On first run, a `config.yml` file will be created. Here are the available options:

```yaml
application:
  host: 0.0.0.0       # Bind address (0.0.0.0 for all interfaces)
  port: 3091          # Server port

# Some other settings
# which will not work yet, but will be implemented in the future.
```

---

## 📡 WebSocket API

### Connection

Connect to the server via WebSocket:
```
ws://<host>:<port>/tetris/game
```

### Packet Format

All packets follow this JSON structure:
```json
{
  "type": "PACKET_TYPE",
  "payload": { ... }
}
```

### Available Packets

#### Client → Server

| Packet Type | Payload | Description |
|------------|---------|-------------|
| `AUTH` | `{"username": "string"}` | Authenticate with the server (required first) |
| `CREATE_ROOM` | `{"room_name": "string", "max_players": int}` | Create a new game room |
| `JOIN_ROOM` | `{"room_id": "string"}` | Join an existing room |
| `SHOW_ROOMS` | `/` | List all available rooms |
| `CHAT` | `{"content": "string"}` | Send a chat message or command |
| `PING` | `any` | Ping the server |
| `LEFT` | - | Move piece left |
| `RIGHT` | - | Move piece right |
| `DOWN` | - | Soft drop piece |
| `DROP` | - | Hard drop piece |
| `ROTATE` | - | Rotate piece |
| `HOLD` | - | Hold current piece |

#### Server → Client

| Packet Type | Payload | Description |
|------------|---------|-------------|
| `AUTH_SUCCESS` | `{"message": "...", "username": "..."}` | Authentication successful |
| `AUTH_ERROR` | `string` | Authentication failed |
| `CREATE_SUCCESS` | `string (room_id)` | Room created successfully |
| `JOIN_SUCCESS` | `{"room_id": "...", "currentPlayers": int, "maxPlayers": int}` | Joined room successfully |
| `SHOW_ROOMS` | `{"rooms": [...]}` | List of available rooms |
| `UPDATE` | `{"states": {...}}` | Game state update |
| `START` | `string` | Game started |
| `DEATH` | `string (player_name)` | Player died |
| `WINNER` | `string (player_name)` | Game winner |
| `JOIN` | `string (player_name)` | Player joined room |
| `LEAVE` | `string (player_id)` | Player left room |
| `MESSAGE` | `{"text": "...", "raw": "..."}` | Chat message |
| `PONG` | `any` | Ping response |

---

## 🎮 Game Controls

Once in a game room, send these packet types (no payload needed) to control your piece:

| Action | Packet Type |
|--------|-------------|
| Move Left | `LEFT` |
| Move Right | `RIGHT` |
| Soft Drop | `DOWN` |
| Hard Drop | `DROP` |
| Rotate | `ROTATE` |
| Hold Piece | `HOLD` |

---

## 💬 Commands

Commands are sent via the `CHAT` packet with content prefixed by `/`:

| Command | Description |
|---------|-------------|
| `/help` | Show available commands |
| `/stop` | Stop the server (admin only) |
| `/start` | Start the game in current room |
##### More to follow!

---

## 🛠️ Tech Stack

- **Java 21** - Modern Java features
- **Maven** - Build and dependency management
- **Jakarta WebSocket API** - WebSocket standard
- **Tyrus** - WebSocket server implementation (Grizzly container)
- **Gson** - JSON serialization
- **BoostedYAML** - YAML configuration
- **Reflections** - Runtime annotation scanning

---

## 📝 License
This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.

---

## 👤 Author

**JoshiCodes** (aka Joroshi)

- GitHub: [@JoshiCodes](https://github.com/JoshiCodes)

---

## 🤝 Contributing

Contributions, issues, and feature requests are welcome! Feel free to check the [issues page](https://github.com/JoshiCodes/PolyCore/issues).

1. Fork the project
2. Create your feature branch (`git checkout -b feature/AmazingFeature`)
3. Commit your changes (`git commit -m 'Add some AmazingFeature'`)
4. Push to the branch (`git push origin feature/AmazingFeature`)
5. Open a Pull Request

---

<div align="center">

**Made with ❤️ and ☕ by JoshiCodes**

</div>
