package com.example.todolist.exception;

public class LoginRequiredException extends RuntimeException {

    public LoginRequiredException() {
        super("Login is required");
    }
}