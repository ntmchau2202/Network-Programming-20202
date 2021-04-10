# NETWORK PROGRAMMING 20202

Final project for Network Programming Course of semester 20202

Topic: Gomoku game

* Team member:

- [20176732] Tran Thai Duong
- [20176845] Nguyen Manh Phuc
- [20184238] Nguyen Thi Minh Chau

## Basic flow
### Introduction about the game
- https://gamevh.net/news/3449

### Main gameplay: client - server - client model
- For each turn, when player places a move, client will send a command with information about coordinate to server
- The server then forwards the information to the opponent
- The game ends if there is a winner, or both accepts a draw, or the board is fully filled.
- The client decides if the player wins. If one of the player wins, the client will send the message to server, and the server will broadcast it to the other opponent
=> The server plays the role of a 3rd party. If the server is down...?

### Register, login and ranking system
- Register: 
+ Players must register an account with username, email and password if he wants to record his result
+ If playing as a guest, he will be assigned a temporary id for displaying and result will not be recorded
+ Information will be stored in a database for further reference

- Login: Player enters username and password for server to authenticate. Server returns if the login successes or fails

- Ranking system: each won gives pts and lose takes pts

- Matching mechanism: 
+ Player can choose to wait/invite a friend to a room
+ If he choose to be randomly placed into a room, the system will find a match based on his elo

### Other features
- Chat: client - server - client model
+ The server plays as a forwarder when receiving message from one player and send it to the target player.

- Players can offer a draw or resign the game
