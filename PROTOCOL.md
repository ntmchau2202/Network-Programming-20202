# LOGIN
## CLIENT -> SERVER
- POST
    ```
    {
        "command_code": "LOGIN",
        "info": {
            "username": "duongdz",
            "password": "abcdef"
        }
    }
    ```

## SERVER -> CLIENT
- METHOD: GET

    ```
    {
        "command_code":"LOGIN",
        "info":{
            "session_id": "AOSJHBGASLGJB",
	    "username": "duongdz",
	    "elo": 1500/0
        },
        "status_code":"success"/"error"
        "error":""
    }
    ```

# REGISTER

## CLIENT -> SERVER
```
        {
            "command_code": "REGISTER",
            "info": {
                "username": "abc",
                "password": "xyz",
            }
        }
```

## SERVER -> CLIENT
```
        {
            "command_code": "REGISTER",
            "info": {
                "session_id": "AOSJHBGASLGJB",
	    	"username": "duongdz",
	    	"elo": 1500
            },
            "status_code": "success"/"error",
            "error": ""
        }
```
    
# MATCH-MAKING
## CLIENT -> SERVER
- POST METHOD

    ```
    {
        "command_code":"JOIN_QUEUE",
        "info":{
            "mode": "normal"/"ranked",
	    "session_id": "AOSJHBGASLGJB"/"",	// NOTE: if session_id length == 0, then it's a join request from guest
						// Server must return a new session_id and temporary username for guest
        }
    }
    ```
    
## SERVER -> CLIENT

```
    {
        "command_code":"JOIN_QUEUE",
        "info":{
            "session_id": "AOSJHBGASLGJB"/"ASLJGLJSBGEF",
	    "username": "duongdz"/"anon1234",
        }
        "status_code": "success",
        "error": "",
    }
```

```
    {
        "command_code":"MATCH_FOUND",
        "info":{
	    "match_id": 1234,
            "opponent": "phuc"/"anon1234",
            "elo": "1969"/"",
        }
    }
```
# MOVES
## CLIENT -> SERVER
- POST METHOD
    ```
    {
        "command_code": "MOVE",
        "info": {
	    "match_id": 1234,
            "session_id": "AOSJHBGASLGJB",
            "move_player": "duongdz",
            "move_position": {
                "x": "6",
                "y": "9"
            },
            "state": "valid"/"invalid",
            "result": "win"/"lose"/"draw"/""
        }
    }
    ```
   
    ```
    {
        "command_code": "DRAW_REQUEST",
        "info": {
   	    "match_id": 1234,
            "session_id": "AOSJHBGASABCD",
            "move_player": "phuc",
        }
    }
    ```
    
    ```
    {
        "command_code": "DRAW_CONFIRM",
        "info": {
	    "match_id": 1234,
            "session_id": "AOSJHBGASLGJB",
            "move_player": "duongdz",
            "acceptance": true/false
        }
    }
    ```

## SERVER -> CLIENT (client hears from server)
- GET
    ```
    {
        "command_code": "MOVE",
        "info": {
	    "match_id": 1234,
            "move_player": "duongdz",
            "move_position": {
                "x": "6",
                "y": "9"
            },
            "state": "valid"/"invalid",
            "result": "win"/"lose"/"draw"/""
        },
        "status_code": "success",
        "error": "",
    }
    ```
    ```
    {
        "command_code": "ENDGAME",
        "info": {
	    "match_id": 1234,
            "winner":{
                "username": "duongdz",
                "elo": 1510
            },
            "loser":{
                "username": "phuc",
                "elo": 1490
            },
            "draw": true/false
        }
        "status_code": "success"/"error",
        "error": ""
    }
    ```
    ```
    {
        "command_code": "DRAW_REQUEST",
        "info": {
   	    "match_id": 1234,
            "move_player": "phuc",
        },
        "status_code": "success"/"error",
        "error": ""
    }
    ```
    
    ```
    {
        "command_code": "DRAW_CONFIRM",
        "info": {
	    "match_id": 1234,
            "move_player": "duongdz",
            "acceptance": true/false
        },
        "status_code": "success"/"error",
        "error": ""
    }
    ```

# LEADERBOARD 
## CLIENT -> SERVER
    ```
    {
	"command_code": "LEADERBOARD"
        "info": {
            "session_id": "AOSJHBGASLGJB",
	    "username": "duongdz"
        },
    }
    ```

## SERVER -> CLIENT
    ```
    {
        "command_code": "LEADERBOARD",
        "info": {
	    "username": ["duongdz", "phuc", "einh", "lanhonglee"],
 	    "elo": [1390, 1490, 1470, 1460],
	    "rank": [69, 1, 2, 3]
        },
        "status_code": "success"/"error",
        "error": ""
    }
    ```
# CHAT

- Private chat

## CLIENT -> SERVER

   ```
   {
	"command_code": "PRIVMSG",
	"info": {
	     "from_user": "duongdz",
             "to_user": "phuc",
	     "message": "helloooooooooo",
	     "message_id": "PRIV1234"
	}
   }
   ```

## SERVER -> CLIENT

   ```
   {
	"command_code": "PRIVMSG",
	"info": {
	     "from_user": "duongdz",
             "to_user": "phuc",
	     "message": "helloooooooooo",
	     "message_id": "PRIV1234"
	}
	"status_code": "success"/"error",
        "error": ""
   }
   ```

- Normal chat
+ Note: Normal chat will
	+ Send message to user in play room when user is in a game
	+ Boardcast message when user is in a queue or in hall

## CLIENT -> SERVER
   
   ```
   {
	"command_code": "MESSAGE",
	"info": {
	     "from_user": "duongdz",
	     "message_id": "MESS1234",
	     "message": "helloooooooooo",
	}
   }
   ```

## SERVER -> CLIENT

   ```
   {
	"command_code": "MESSAGE",
	"info": {
	     "from_user": "duongdz",
	     "message_id": "PRIV1234",
	     "message": "helloooooooooo",
	}
	"status_code": "success"/"error",
        "error": ""
   }
   ```

- Acknowledgement

## CLIENT -> SERVER 

   ```
   {
  	"command_code": "PRIVMSG"/"MESSAGE",
	"info": {
	     "message_id": "PRIV1234"/"MESS1234",
             "status": "success"/"fail",
	     "error": "",
	}
   }
   ```

  ```
  {
	"command_code": "PRIVMSG"/"MESSAGE",
	"info": {
	     "message_id": "PRIV1234"/"MESS1234",
	}
	"status": "success"/"fail",
	"error": "",
  }
  ```