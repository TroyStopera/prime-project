package lib.persistence.entities;

import lib.persistence.DataAccessException;
import lib.persistence.Entity;

import java.util.Optional;

//TODO hash passwords for security?
public class Account extends Entity {

    /* Columns */
    private String type, username, email, password;

    public Account(String type, String username, String email, String password) {
        this.type = type;
        this.username = username;
        this.email = email;
        this.password = password;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
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

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public static Account fromJson(String json) {
        return gson.fromJson(json, Account.class);
    }

    public interface DAO extends Entity.DAO<Account> {

        Optional<Account> get(String email) throws DataAccessException;

    }

}
