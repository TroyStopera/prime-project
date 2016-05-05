package lib.persistence.entities;

import lib.persistence.DataAccessException;
import lib.persistence.Entity;

import java.util.List;
import java.util.Optional;

//TODO hash passwords for security?
public class Account extends Entity {

    public static final int TYPE_USER = 0;
    public static final int TYPE_ADMIN = 1;

    /* Columns */
    private String username, email, password;
    private int type;

    public Account(int type, String username, String email, String password) {
        this.type = type;
        this.username = username;
        this.email = email;
        this.password = password;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
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

    public boolean equals(Account account) {
        return username.equals(account.username)
                && email.equals(account.email)
                && password.equals(account.password)
                && type == account.type;
    }

    public interface DAO extends Entity.DAO<Account> {

	    List<Account> allAccounts() throws DataAccessException;

        Optional<Account> get(String email) throws DataAccessException;

    }

}
