package dev.partin.james.jellyfinlibrarymanager.api.controller;

import org.springframework.web.bind.annotation.*;

@RestController
public class TestController {
     @GetMapping("/test")
     public String test(@RequestParam int thisIsATestInteger, @RequestParam String thisIsATestString) {
     	return "Test integer: " + thisIsATestInteger + "Test string: " + thisIsATestString;
     }
}
