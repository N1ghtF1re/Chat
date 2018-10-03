class Message {
        constructor(user, message, chat, status="ok") {
            this.user = user;
            this.message = message
            this.status = status;
            this.chat_id = chat;
        }
    }
