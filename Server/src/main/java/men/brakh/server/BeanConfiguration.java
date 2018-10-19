package men.brakh.server;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.IOException;


@Configuration
public class BeanConfiguration {
    private static Server server;

    @Bean
    Server server() throws IOException {
        server = new Server(7777);
        return server;
    }

    public static Server getServer() {
        return server;
    }
}