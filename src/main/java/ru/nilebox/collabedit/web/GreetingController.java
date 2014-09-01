/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ru.nilebox.collabedit.web;

import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;

/**
 *
 * @author nile
 */
@Controller
public class GreetingController {


    @MessageMapping("/hello")
    @SendTo("/topic/greetings")
    public Greeting greeting(HelloMessage message) throws Exception {
        Thread.sleep(3000); // simulated delay
        return new Greeting("Hello, " + message.getName() + "!");
    }
	
	@MessageMapping("/collab")
    @SendTo("/topic/diff")
    public Greeting diff(HelloMessage message) throws Exception {
        return new Greeting(message.getName().toUpperCase());
    }

}
