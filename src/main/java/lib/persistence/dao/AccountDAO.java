package lib.persistence.dao;

import lib.persistence.DataAccessException;
import lib.persistence.entities.Account;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;

class AccountDAO implements Account.DAO {

    private final SQLiteDAO dao;

    AccountDAO(SQLiteDAO sqLiteDAO) {
        this.dao = sqLiteDAO;
    }

	public List<Account> allAccounts() throws DataAccessException {
		String query = "SELECT * FROM Account WHERE 1 = 1";
		List<Account> accounts = new LinkedList<>();
		dao.lock();
		try {
			PreparedStatement statement = dao.connection.prepareStatement(query);
			statement.setQueryTimeout(30);
			ResultSet resultSet = statement.executeQuery();
			while (resultSet.next()) {
				long id = resultSet.getLong("id");
				int type = resultSet.getInt("Type");
				String username = resultSet.getString("Username");
				String email = resultSet.getString("Email");
				String pass = resultSet.getString("Pass");
				Account account = new Account(type, username, email, pass);
				dao.setId(account, id);
				accounts.add(account);
			}
		} catch (SQLException e) {
			throw new DataAccessException(e.getMessage());
		} finally {
			dao.unlock();
		}
		return accounts;
	}

    @Override
    public Optional<Account> get(String email) throws DataAccessException {
        String query = "SELECT * FROM Account WHERE Email = ?";
        dao.lock();
        try {
            PreparedStatement statement = dao.connection.prepareStatement(query);
            statement.setString(1, email);
            statement.setQueryTimeout(30);
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                long id = resultSet.getLong("id");
                int type = resultSet.getInt("Type");
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
        } finally {
            dao.unlock();
        }
    }

    @Override
    public Account create(Account entity) throws DataAccessException {
        String query = "INSERT INTO Account (Pass, Type, Username, Email) VALUES(?, ?, ?, ?)";
        dao.lock();
        try {
            PreparedStatement statement = dao.connection.prepareStatement(query, Statement.RETURN_GENERATED_KEYS);
            statement.setString(1, entity.getPassword());
            statement.setInt(2, entity.getType());
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
        } finally {
            dao.unlock();
        }
        return entity;
    }

    @Override
    public Optional<Account> get(long id) throws DataAccessException {
        String query = "SELECT * FROM Account WHERE id = ?";
        dao.lock();
        try {
            PreparedStatement statement = dao.connection.prepareStatement(query);
            statement.setLong(1, id);
            statement.setQueryTimeout(30);
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                int type = resultSet.getInt("Type");
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
        } finally {
            dao.unlock();
        }
    }

    @Override
    public Account update(Account entity) throws DataAccessException {
        String query = "UPDATE Account SET Pass = ?, Type = ?, Username = ?, Email = ? WHERE id = ?";
        dao.lock();
        try {
            PreparedStatement statement = dao.connection.prepareStatement(query);
            statement.setString(1, entity.getPassword());
            statement.setInt(2, entity.getType());
            statement.setString(3, entity.getUsername());
            statement.setString(4, entity.getEmail());
            statement.setLong(5, entity.getId());
            statement.setQueryTimeout(30);
            statement.executeUpdate();
        } catch (SQLException e) {
            throw new DataAccessException(e.getMessage());
        } finally {
            dao.unlock();
        }
        return entity;
    }

    @Override
    public void delete(Account entity) throws DataAccessException {
        String query = "DELETE FROM Account WHERE id = ?";
        dao.lock();
        try {
            PreparedStatement statement = dao.connection.prepareStatement(query);
            statement.setLong(1, entity.getId());
            statement.setQueryTimeout(30);
            statement.executeUpdate();
        } catch (SQLException e) {
            throw new DataAccessException(e.getMessage());
        } finally {
            dao.unlock();
        }
    }

	//helper method for inflating Accounts
	private Account fromResultSet(ResultSet resultSet) throws SQLException {
		long id = resultSet.getLong("id");
		int type = resultSet.getInt("Type");
		String username = resultSet.getString("Username");
		String email = resultSet.getString("Email");
		String pass = resultSet.getString("Pass");
		Account account = new Account(type, username, email, pass);
		dao.setId(account, id);
		return account;
	}
}
