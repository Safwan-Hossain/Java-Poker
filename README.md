# Java Poker

Text-based Poker main.model.game (Texas hold 'em) that can be played by multiple computers via a LAN connection. Currently, this main.model.game
is only playable in the console.

## How To Play

### Start Server
**NOTE:** Once you start the main.server, you will automatically connect as the host player. It is possible to run multiple
instances of this program where one instance is the host main.server (and player) and the other instances are joining players.
1. Run **main.Runner.java**
2. Enter "1" into the console (menu options will be labelled in console)
3. Share your local IP address (displayed in console) with the other players
4. Type in your username
5. type in "**START**" when all the players have joined. 

### Join Server 
**NOTE:** A running main.server is required to join a main.model.game (see above section).
1. Run **main.Runner.java**
2. Enter "2" into the console (menu options will be labelled in console)
3. Type in the IP address of the main.server host (will be displayed in the main.server host's console)
4. Type in your username
5. Wait for the host to start the main.model.game

## Rules
The main.model.game is called *Texas hold 'em Poker*. To find out how to play please see https://fgbradleys.com/rules/Texas_Holdem_Rules.pdf

## Detailed Description
### Overview
This project was created in a group of three for a software design course. We were tasked with finding a publicly
cloneable programming project and proposing a design that would allow us to extend the existing project with our own implementation.

We chose a single-player poker project. The original author implemented a single-player 5-card draw version of Poker. The project 
can be found at https://github.com/andyxhadji/Simple-Poker.

We proposed expanding this application by building a multiplayer texas hold 'em project that makes use of sockets and multithreading.

### My Role
Our duties were divided among the functionalities that needed to be implemented. I created the main.server-client
functionalities as well as the core main.model.game logic. 
I was responsible for developing a method that allowed multiple players
(using different computers) to connect to a single main.server. The main.server drives the main.model.game logic and broadcasts the updated 
state of the main.model.game to its clients. The client waits for the main.server to deliver updates and then acts on them accordingly 
(e.g., a player cannot make a move until the main.server gives him his turn). This method reduces the possibility of players
having different main.model.game states as a result of unexpected anomalies.

## Future Goals
Although a full party main.model.game of poker can easily be played, this project still requires a lot of restructuring and polishing for improvement. 
I plan to implement the following functionalities/changes:

- Better encapsulation
  - public state variables can be converted to private and make use of getters and setters
- Better readability
  - Some classes can be broken down into smaller subclasses
  - More main.util functions can be created
  - Card ranks and suits can be converted into Enums.
- Add timeout function
  - Currently, a player can block the main.model.game by not making a move
- Usage of try-catch blocks instead of manual close
- Implement a GUI and then add logging
- Custom ports (right now only uses 101)
- Fully comment the project
- Convert the main.server main.model.game logic into a state machine
- More threadsafe logic
  - Multi-threading errors can occur under very specific circumstances
