package org.dawn.serverless;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.support.SpringBootServletInitializer;

@SpringBootApplication
public class AppServerless extends SpringBootServletInitializer implements CommandLineRunner {

    public static void main( String[] args )
    {
        SpringApplication.run(AppServerless.class, args);
    }

    @Override
    protected SpringApplicationBuilder configure(SpringApplicationBuilder builder) {
        //return super.configure(builder);
        return builder.sources(AppServerless.class);
    }

    @Override
    public void run(String... strings) throws Exception {

    }
}
