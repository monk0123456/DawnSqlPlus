package org.dawnsql.client;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.support.SpringBootServletInitializer;

@SpringBootApplication
public class AppClient extends SpringBootServletInitializer implements CommandLineRunner {

    public static void main( String[] args )
    {
        SpringApplication.run(AppClient.class, args);
    }

    @Override
    public void run(String... strings) throws Exception {

    }

    @Override
    protected SpringApplicationBuilder configure(SpringApplicationBuilder builder) {
        //return super.configure(builder);
        return builder.sources(AppClient.class);
    }
}
