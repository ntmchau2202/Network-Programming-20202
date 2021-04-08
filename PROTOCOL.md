# LOGIN
## CLIENT -> SERVER
- POST
    ```
    {
        "command_code": "LOGIN",
        "info": {
            "username": "duongdz"
        }
    }
    ```

## SERVER -> CLIENT
- METHOD: GET

- Success: 

    ```
    {
        "command_code":"LOGIN",
        "info":{
            
        },
        "status_code":"success",
        "error":""
    }
    ```

- Error: 

    ```
    {
        "command_code":"LOGIN",
        "info":{
            
        },
        "status_code":"error",
        "error":"Username has been taken"
    }
    ```

# MATCH-MAKING
## SERVER -> CLIENT
- GET METHOD 

    ```
    {
        "command_code":"MATCH_FOUND",
        "info":{
            "opponent":"phuc"
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
            "move_player": "duongdz",
            "move_position": {
            "x": "6",
            "y": "9"
            }
        },
        "result": "win",
    }
    ```

## SERVER -> CLIENT (client hears from server)
- GET
    ```
    {
        "command_code": "MOVE",
        "info": {
            "move_player": "duongdz",
            "move_position": {
            "x": "6",
            "y": "9"
            }
        },
        "result": "lose"
    }
    ```
