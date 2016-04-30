package lib.persistence.dao;

import lib.persistence.DataAccessException;
import lib.persistence.entities.Item;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;

class ItemDAO implements Item.DAO {

    private final SQLiteDAO dao;

    ItemDAO(SQLiteDAO dao) {
        this.dao = dao;
    }

    @Override
    public List<Item> allItems() throws DataAccessException {
        String query = "SELECT * FROM Item WHERE 1 = 1";
        List<Item> items = new LinkedList<>();
        dao.lock();
        try {
            PreparedStatement statement = dao.connection.prepareStatement(query);
            statement.setQueryTimeout(30);
            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                items.add(fromResultSet(resultSet));
            }
        } catch (SQLException e) {
            throw new DataAccessException(e.getMessage());
        } finally {
            dao.unlock();
        }
        return items;
    }

    @Override
    public Item create(Item entity) throws DataAccessException {
        String query = "INSERT INTO Item (Name, Description, PriceDollars, PriceCents) VALUES(?, ?, ?, ?)";
        dao.lock();
        try {
            PreparedStatement statement = dao.connection.prepareStatement(query, Statement.RETURN_GENERATED_KEYS);
            statement.setString(1, entity.getName());
            statement.setString(2, entity.getDescription());
            statement.setInt(3, entity.getCostDollar());
            statement.setInt(4, entity.getCostCents());
            statement.setQueryTimeout(30);
            statement.execute();
            ResultSet key = statement.getGeneratedKeys();
            if (key.next())
                dao.setId(entity, key.getLong(1));
            else throw new DataAccessException("No ID returned for new Item");
        } catch (SQLException e) {
            throw new DataAccessException(e.getMessage());
        } finally {
            dao.unlock();
        }
        return entity;
    }

    @Override
    public Optional<Item> get(long id) throws DataAccessException {
        String query = "SELECT * FROM Item WHERE id = ?";
        dao.lock();
        try {
            PreparedStatement statement = dao.connection.prepareStatement(query);
            statement.setLong(1, id);
            statement.setQueryTimeout(30);
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                Item item = fromResultSet(resultSet);
                dao.setId(item, id);
                return Optional.of(item);
            }
        } catch (SQLException e) {
            throw new DataAccessException(e.getMessage());
        } finally {
            dao.unlock();
        }
        return Optional.empty();
    }

    @Override
    public Item update(Item entity) throws DataAccessException {
        String query = "UPDATE Item SET Name = ?, Description = ?, PriceDollars = ?, PriceCents = ? WHERE id = ?";
        dao.lock();
        try {
            PreparedStatement statement = dao.connection.prepareStatement(query);
            statement.setString(1, entity.getName());
            statement.setString(2, entity.getDescription());
            statement.setInt(3, entity.getCostDollar());
            statement.setInt(4, entity.getCostCents());
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
    public void delete(Item entity) throws DataAccessException {
        String query = "DELETE FROM Item WHERE id = ?";
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

    //helper method for inflating Items
    private static Item fromResultSet(ResultSet resultSet) throws SQLException {
        String name = resultSet.getString("Name");
        String description = resultSet.getString("Description");
        int dollars = resultSet.getInt("PriceDollars");
        int cents = resultSet.getInt("PriceCents");
        return new Item(name, description, dollars, cents);
    }
}
