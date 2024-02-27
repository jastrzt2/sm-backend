package com.example.demo.Session;

@RestController
public class SessionController {

    @GetMapping("/setSession")
    public String setSession(HttpSession session) {
        session.setAttribute("user", "ExampleUser");
        return "Session attribute set";
    }

    @GetMapping("/getSession")
    public String getSession(HttpSession session) {
        return "Session attribute: " + session.getAttribute("user");
    }
}
