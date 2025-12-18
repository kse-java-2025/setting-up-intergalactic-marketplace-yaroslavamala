package com.cosmocats.cosmomarket;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.EnableAspectJAutoProxy;

@SpringBootApplication
@EnableAspectJAutoProxy
public class CosmoMarketApplication {

    public static void main(String[] args) {
        SpringApplication.run(CosmoMarketApplication.class, args);
    }

}
