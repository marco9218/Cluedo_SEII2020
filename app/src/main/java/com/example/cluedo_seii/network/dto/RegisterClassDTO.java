package com.example.cluedo_seii.network.dto;

public class RegisterClassDTO extends RequestDTO {
    private Class<Class> classToRegister;

    public Class getClassToRegister() {
        return classToRegister;
    }

    public void setClassToRegister(Class classToRegister) {
        this.classToRegister = classToRegister;
    }
}
