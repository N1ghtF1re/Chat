# Java Chat 

Chat is an application for communicating a customer with an agent. When writing a message, the customer becomes in the queue and waits for the agent to respond. In turn, when registering, the agent also becomes a queue of agents and expects the customer.

## Customer Commands
+ **!register UserName** - Register in the client
+ **!leave** - Leave the chat with the agent (when quitting the queue, on the next сutomer message, it returns to the queue)
+ **!exit** - Exit the client 

## Agent Commands
+ **!register UserName** - Register in the client
+ **!skip** - Switch to another client
+ **!exit** - Exit the client

