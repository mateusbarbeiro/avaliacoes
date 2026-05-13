package org.adjt.application.services;

import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class GreetingService {
    public String greeting(String name) {
        return "Guten Tag " + name;
    }
}
