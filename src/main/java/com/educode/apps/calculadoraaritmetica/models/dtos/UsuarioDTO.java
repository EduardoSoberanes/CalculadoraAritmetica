package com.educode.apps.calculadoraaritmetica.models.dtos;

import org.apache.commons.lang3.builder.ToStringBuilder;

import java.sql.Timestamp;

public class UsuarioDTO {
    private String username;
    private String email;
    private Timestamp createAt;

    public UsuarioDTO() {
    }

    public UsuarioDTO(String username, String email, Timestamp createAt) {
        this.username = username;
        this.email = email;
        this.createAt = createAt;
    }

    public UsuarioDTO(String username, String email) {
        this.username = username;
        this.email = email;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Timestamp getCreateAt() {
        return createAt;
    }

    public void setCreateAt(Timestamp createAt) {
        this.createAt = createAt;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .append("username", username)
                .append("email", email)
                .append("createAt", createAt)
                .toString();
    }
}
