var webSocket = new WebSocket("ws://localhost:8081/chat");

    var msgField = document.getElementById("messageField");

    var divMsg = document.getElementById("msg-box");

    var currentUser = null;

function showMessage(msg) {

        console.log(msg)

        switch (msg.user.name) {

          case "Server":

            divMsg.innerHTML += "<div style='color:green'>Server> : " + msg.message + "</div>"

            break;

          case currentUser.name:

            divMsg.innerHTML += "<div style='color:red'>"+ currentUser.name + "> " + msg.message +

                              "</div>"

            break;



          default:

            divMsg.innerHTML += "<div style='color:blue'>"+ msg.user.name + "> " + msg.message +

                            "</div>"



        }

    }

    function sendMsg(msg) {

        webSocket.send(JSON.stringify(msg));

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



        }



    }



    webSocket.onopen = function() {

        console.log("connection opened");

    };



    webSocket.onclose = function() {

        console.log("connection closed");

    };



    webSocket.onerror = function wserror(message) {

        console.log("error: " + message);

    }
