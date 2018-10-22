package men.brakh.server.controllers;


import java.util.*;
;

import com.google.gson.Gson;
import men.brakh.chat.Message;
import men.brakh.chat.User;
import men.brakh.chat.UsersTypes;
import men.brakh.server.BeanConfiguration;
import men.brakh.server.Server;
import men.brakh.server.data.ExtendUser;
import men.brakh.server.data.TwoPersonChat;
import men.brakh.server.handlers.impl.AgentsHandler;
import men.brakh.server.handlers.impl.CustomersHandler;
import men.brakh.server.senders.impl.JsonSender;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("api")
public class MainController {


    @Autowired
    private static Server server = BeanConfiguration.getServer();

    Map<Integer, String> customersNames = new HashMap<>();

    class RequestChat {
        User customer;
        User agent;
        int id;
        RequestChat(User customer, User agent, int id) {
            this.customer = customer;
            this.agent = agent;
            this.id = id;
        }
        RequestChat(TwoPersonChat chat) {
            customer = chat.getCustomer().getUser();
            ExtendUser extendAgent = chat.getAgent();
            if(extendAgent != null) {
                agent = extendAgent.getUser();
            }
            id = chat.getId();
        }
    }

    @GetMapping("/agents")
    public List<User> getAllAgents(@RequestParam(value="page", defaultValue="0") int page, @RequestParam(value="pagesize", defaultValue="10") int pageSize){
        List<User> users = new LinkedList<>();
        synchronized (server.agentsQueue) {
            synchronized (server.customerChatQueue) {
                for (ExtendUser agent: server.agentsQueue.getAll()) {
                    users.add(agent.getUser());
                }

                for(TwoPersonChat chat: server.customerChatQueue.getAll()) {
                    if(chat.getAgent() != null) {
                        User agent = chat.getAgent().getUser();
                        users.add(agent);
                    }
                }
            }
        }
        int startIndex = page*pageSize > users.size() ? users.size() : pageSize*page;
        int endIndex = page*pageSize + pageSize > users.size() ? users.size() : pageSize*page + pageSize;
        return users.subList(startIndex, endIndex);
    }


    @GetMapping("/agents/free")
    public List<User> getFreeAgents(@RequestParam(value="page", defaultValue="0") int page, @RequestParam(value="pagesize", defaultValue="10") int pageSize) {
        List<User> users = new LinkedList<>();
        synchronized (server.agentsQueue) {
            synchronized (server.customerChatQueue) {
                for (ExtendUser agent: server.agentsQueue.getAll()) {
                    users.add(agent.getUser());
                }
            }
        }

        int startIndex = page*pageSize > users.size() ? users.size() : pageSize*page;
        int endIndex = page*pageSize + pageSize > users.size() ? users.size() : pageSize*page + pageSize;
        return users.subList(startIndex, endIndex);
    }

    @GetMapping("agent")
    public User getAgent(@RequestParam(value="id") int id) {
        User agent;
        agent = server.agentsQueue.searchAgent(id);
        synchronized (server.agentsQueue) {
            synchronized (server.customerChatQueue) {
                if (agent != null) {
                    return agent;
                }
                TwoPersonChat user = server.customerChatQueue.searchAgent(id);
                if (user != null) {
                    return user.getAgent().getUser();
                }
            }
        }
        return null;
    }

    @GetMapping("/agents/free/count")
    public int getFreeAgentsCount() {
        return server.agentsQueue.getAll().length;
    }

    @GetMapping("/chats")
    public String getChats(@RequestParam(value="page", defaultValue="0") int page, @RequestParam(value="pagesize", defaultValue="10") int pageSize) {
        List<RequestChat> chats = new LinkedList<>();

        synchronized (server.customerChatQueue) {
            for(TwoPersonChat chat: server.customerChatQueue.getAll()) {
                chats.add( new RequestChat(chat) );
            }
        }
        Gson gson = new Gson();

        int startIndex = page*pageSize > chats.size() ? chats.size() : pageSize*page;
        int endIndex = page*pageSize + pageSize > chats.size() ? chats.size() : pageSize*page + pageSize;

        return gson.toJson(chats.subList(startIndex, endIndex));
    }

    @GetMapping("/chat")
    public String getChat(@RequestParam(value="id") int id) {
        TwoPersonChat chat = server.customerChatQueue.getById(id);
        if(chat == null) {
            return null;
        }
        RequestChat requestChat = new RequestChat(chat);
        Gson gson = new Gson();


        return gson.toJson(requestChat);
    }

    @GetMapping("customers/free")
    public List<User> getFreeCustomers(@RequestParam(value="page", defaultValue="0") int page, @RequestParam(value="pagesize", defaultValue="10") int pageSize) {
        List<User> chats = new LinkedList<>();
        synchronized (server.customerChatQueue) {
            for(TwoPersonChat chat: server.customerChatQueue.getAll()) {
                ExtendUser agent = chat.getAgent();
                if(agent == null) {
                    chats.add(chat.getCustomer().getUser());
                }
            }
        }
        int startIndex = page*pageSize > chats.size() ? chats.size() : pageSize*page;
        int endIndex = page*pageSize + pageSize > chats.size() ? chats.size() : pageSize*page + pageSize;
        return chats.subList(startIndex, endIndex);

    }

    @GetMapping("/customer")
    public User getCustomer(@RequestParam(value="id") int id) {
        TwoPersonChat chat = server.customerChatQueue.searchCustomer(id);
        if(chat != null) {
            return chat.getCustomer().getUser();
        }
        return null;
    }

    @PostMapping("/agent/add")
    public int addAgent(@RequestParam(value="name") String name) {
        User user = new User(name);
        JsonSender jsonSender = new JsonSender();
        AgentsHandler agentsHandler = new AgentsHandler(server, jsonSender);
        Message message = new Message(user, "", "reg");

        agentsHandler.handle(message);
        return Integer.parseInt(jsonSender.getLast().getMessage());
    }

    @PostMapping("/customer/add")
    public int addCustomer(@RequestParam(value="name") String name) {
        User user = new User(name);



        JsonSender jsonSender = new JsonSender();
        CustomersHandler customersHandler= new CustomersHandler(server, jsonSender);
        Message message = new Message(user, "", "reg");

        customersHandler.handle(message);
        int customer_id = Integer.parseInt(jsonSender.getLast().getMessage());
        customersNames.put(customer_id, name);
        return customer_id;
    }

    @PostMapping("agent/send")
    public void agentSendMessage(@RequestParam(value="id") int id, @RequestParam(value="message") String msg) {
        TwoPersonChat chat = server.customerChatQueue.searchAgent(id);

        User agent = chat.getAgent().getUser();
        agent.setUserType(UsersTypes.AGENT);

        Message message = new Message(agent, msg, chat.getId());
        JsonSender jsonSender = new JsonSender();
        AgentsHandler agentsHandler = new AgentsHandler(server, jsonSender);
        agentsHandler.handle(message);
    }

    @PostMapping("customer/send")
    public void customerSendMessage(@RequestParam(value="id") int id, @RequestParam(value="message") String msg) {
        TwoPersonChat chat = server.customerChatQueue.searchCustomer(id);
        User customer;
        int chat_id;

        // Если пользователь уже писал в чат - просто получаем его "комнату"
        if (chat != null){
            customer = chat.getCustomer().getUser();
            customer.setUserType(UsersTypes.CUSTOMER);
            chat_id = chat.getId();
        } else { // Иначе - будем создавать новую :)
            String name = customersNames.get(id);
            if(!customersNames.containsKey(id)) {
                return;
            }
            customer = new User(name, id);
            customer.setUserType(UsersTypes.CUSTOMER);
            chat_id = -1;
        }


        Message message = new Message(customer, msg, chat_id);
        JsonSender jsonSender = new JsonSender();
        CustomersHandler customersHandler = new CustomersHandler(server, jsonSender);
        customersHandler.handle(message);
    }

    @GetMapping("agent/messages")
    public List<Message> getAgentMessages(@RequestParam(value="id") int id) {
        TwoPersonChat chat = server.customerChatQueue.searchAgent(id);
        if (chat == null) {
            return new ArrayList<>();
        }
        return chat.getMessages();
    }

    @GetMapping("customer/messages")
    public List<Message> getCustomersMessages(@RequestParam(value="id") int id) {
        TwoPersonChat chat = server.customerChatQueue.searchCustomer(id);
        if (chat == null) {
            return new ArrayList<>();
        }
        return chat.getMessages();
    }

    @PostMapping("agent/exit")
    public void exitAgent(@RequestParam(value="id") int id) {
        User agent;
        JsonSender jsonSender = new JsonSender();
        AgentsHandler agentsHandler = new AgentsHandler(server, jsonSender);
        synchronized (server.agentsQueue) {
            synchronized (server.customerChatQueue) {
                User user = server.agentsQueue.searchAgent(id);
                if(user != null) {
                    Message message = new Message(user, "", "exit");
                    agentsHandler.handle(message);
                    return;
                }
                TwoPersonChat chat = server.customerChatQueue.searchAgent(id);
                if(chat != null) {
                    Message message = new Message(chat.getAgent().getUser(), "", "exit");
                    agentsHandler.handle(message);
                }
            }
        }
    }

    @PostMapping("customer/exit")
    public void exitCustomer(@RequestParam(value="id") int id) {
        TwoPersonChat chat = server.customerChatQueue.searchCustomer(id);
        if(chat == null) {
            return;
        }
        User customer = chat.getCustomer().getUser();
        Message message = new Message(customer, "", "exit");

        JsonSender jsonSender = new JsonSender();
        CustomersHandler customersHandler= new CustomersHandler(server, jsonSender);

        customersHandler.handle(message);
    }
}