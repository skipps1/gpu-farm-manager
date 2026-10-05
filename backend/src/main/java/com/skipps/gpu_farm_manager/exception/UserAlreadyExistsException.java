package com.skipps.gpu_farm_manager.exception;

public class UserAlreadyExistsException extends ResourceAlreadyExistsException {
    public UserAlreadyExistsException(String message) {
        super(message);
    }

    public static UserAlreadyExistsException withUsername(String username) {
        return new UserAlreadyExistsException("User already exists with username: " + username);
    }
}
