var webSocket = new WebSocket("ws://localhost:8081/chat");
var msgField = document.getElementById("messageField");
var divMsg = document.getElementById("msg-box");
var currentUser = null;
var currChat = -1;
  function autoris() {
    let userName = document.getElementById('login').value;
    if(userName.length < 3) {
      return false;
    }

    currentUser = new User(userName, TYPE)
    msg = new Message(currentUser, "", -1, "reg")
    showMessage(new Message(new User("Server", "NONE"), "Hello, " + currentUser.name))
    sendMsg(msg)
    document.getElementById('chat').style.display = 'block';
    document.getElementById('autorisation').style.display = 'none';

  }

  function showMessage(msg) {

        console.log(msg.user.userType)

        switch (msg.user.userType) {
          case "NONE":
            divMsg.innerHTML += "<div class='msg server-msg'>" + msg.message + "</div>"
            break;

          case "AGENT":
            divMsg.innerHTML += "<div class='msg agent-msg'><span>"+ msg.user.name + "</span>: " + msg.message + "</div>"
            break;

          case "CUSTOMER":
            divMsg.innerHTML += "<div class='msg customer-msg'><span>"+  msg.user.name + "</span>: " + msg.message + "</div>"
            break;
        }

        document.getElementById("msg-box").scrollTop = 9999;

    }

    function sendMsg(msg) {
        webSocket.send(JSON.stringify(msg));
        console.log(JSON.stringify(msg))
    }


    webSocket.onmessage = function(message) {

        if (message == null) {
          return
        }

        msg = JSON.parse(message.data)
        console.log(msg.status)
        switch (msg.status) {
          case "ok":
            showMessage(msg)
            break;

          case "reg":
            currentUser.setId(msg.message)
            break;
          case "chat":
            currChat = parseInt(msg.message)
            break;
        }
    }



    webSocket.onopen = function() {
        console.log("connection opened");
    };

    webSocket.onclose = function() {
        showMessage(new Message(new User("Server", "NONE"), "Сервер недоступен. Попробуйте позже"))
        document.getElementById("messageField").style.display = 'none';
        document.getElementById("inp").style.display = 'none';
        console.log("connection closed");
    };

    webSocket.onerror = function wserror(message) {
        console.log("error: " + message);
    }
