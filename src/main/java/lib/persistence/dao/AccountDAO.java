package lib.persistence.dao;

import lib.persistence.DataAccessException;
import lib.persistence.entities.Account;

import java.sql.*;
import java.util.Optional;

class AccountDAO implements Account.DAO {

    private final SQLiteDAO dao;

    AccountDAO(SQLiteDAO sqLiteDAO) {
        this.dao = sqLiteDAO;
    }

    @Override
    public Optional<Account> get(String email) throws DataAccessException {
        String query = "SELECT * FROM Account WHERE Email = ?";
        try {
            PreparedStatement statement = dao.connection.prepareStatement(query);
            statement.setString(1, email);
            statement.setQueryTimeout(30);
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                long id = resultSet.getLong("id");
                String type = resultSet.getString("Type");
                String username = resultSet.getString("Username");
                String pass = resultSet.getString("Pass");
                Account account = new Account(type, username, email, pass);
                dao.setId(account, id);
                return Optional.of(account);
            } else {
                return Optional.empty();
            }
        } catch (SQLException e) {
            throw new DataAccessException(e.getMessage());
        }
    }

    @Override
    public Account create(Account entity) throws DataAccessException {
        String query = "INSERT INTO Account (Pass, Type, Username, Email) VALUES(?, ?, ?, ?)";
        try {
            PreparedStatement statement = dao.connection.prepareStatement(query, Statement.RETURN_GENERATED_KEYS);
            statement.setString(1, entity.getPassword());
            statement.setString(2, entity.getType());
            statement.setString(3, entity.getUsername());
            statement.setString(4, entity.getEmail());
            statement.setQueryTimeout(30);
            statement.execute();
            ResultSet key = statement.getGeneratedKeys();
            if (key.next())
                dao.setId(entity, key.getLong(1));
            else throw new DataAccessException("No ID returned for new Account");
        } catch (SQLException e) {
            throw new DataAccessException(e.getMessage());
        }
        return entity;
    }

    @Override
    public Optional<Account> get(long id) throws DataAccessException {
        String query = "SELECT * FROM Account WHERE id = ?";
        try {
            PreparedStatement statement = dao.connection.prepareStatement(query);
            statement.setLong(1, id);
            statement.setQueryTimeout(30);
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                String type = resultSet.getString("Type");
                String username = resultSet.getString("Username");
                String pass = resultSet.getString("Pass");
                String email = resultSet.getString("Email");
                Account account = new Account(type, username, email, pass);
                dao.setId(account, id);
                return Optional.of(account);
            } else {
                return Optional.empty();
            }
        } catch (SQLException e) {
            throw new DataAccessException(e.getMessage());
        }
    }

    @Override
    public Account update(Account entity) throws DataAccessException {
        String query = "UPDATE Account SET Pass = ?, Type = ?, Username = ?, Email = ? WHERE id = ?";
        try {
            PreparedStatement statement = dao.connection.prepareStatement(query);
            statement.setString(1, entity.getPassword());
            statement.setString(2, entity.getType());
            statement.setString(3, entity.getUsername());
            statement.setString(4, entity.getEmail());
            statement.setLong(5, entity.getId());
            statement.setQueryTimeout(30);
            statement.executeUpdate();
        } catch (SQLException e) {
            throw new DataAccessException(e.getMessage());
        }
        return entity;
    }

    @Override
    public void delete(Account entity) throws DataAccessException {
        String query = "DELETE FROM Account WHERE id = ?";
        try {
            PreparedStatement statement = dao.connection.prepareStatement(query);
            statement.setLong(1, entity.getId());
            statement.setQueryTimeout(30);
            statement.executeUpdate();
        } catch (SQLException e) {
            throw new DataAccessException(e.getMessage());
        }
    }
}
