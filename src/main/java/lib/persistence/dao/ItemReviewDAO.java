package lib.persistence.dao;

import lib.persistence.DataAccessException;
import lib.persistence.entities.Item;
import lib.persistence.entities.ItemReview;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;

class ItemReviewDAO implements ItemReview.DAO {

    private final SQLiteDAO dao;

    ItemReviewDAO(SQLiteDAO dao) {
        this.dao = dao;
    }

    @Override
    public List<ItemReview> reviewsFor(Item item) throws DataAccessException {
        String query = "SELECT * FROM ItemReview WHERE Item_id = ?";
        List<ItemReview> reviews = new LinkedList<>();
        dao.lock();
        try {
            PreparedStatement statement = dao.connection.prepareStatement(query);
            statement.setLong(1, item.getId());
            statement.setQueryTimeout(30);
            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                long id = resultSet.getLong("id");
                ItemReview review = fromResultSet(resultSet);
                dao.setId(review, id);
                reviews.add(review);
            }
        } catch (SQLException e) {
            throw new DataAccessException(e.getMessage());
        } finally {
            dao.unlock();
        }
        return reviews;
    }

    @Override
    public ItemReview create(ItemReview entity) throws DataAccessException {
        String query = "INSERT INTO ItemReview (Rating, Item_id, Account_id, Description) VALUES(?, ?, ?, ?)";
        dao.lock();
        try {
            PreparedStatement statement = dao.connection.prepareStatement(query, Statement.RETURN_GENERATED_KEYS);
            statement.setInt(1, entity.getRating());
            statement.setLong(2, entity.getItemId());
            statement.setLong(3, entity.getAccountId());
            statement.setString(4, entity.getReview());
            statement.setQueryTimeout(30);
            statement.execute();
            ResultSet key = statement.getGeneratedKeys();
            if (key.next())
                dao.setId(entity, key.getLong(1));
            else throw new DataAccessException("No ID returned for new ItemReview");
        } catch (SQLException e) {
            throw new DataAccessException(e.getMessage());
        } finally {
            dao.unlock();
        }
        return entity;
    }

    @Override
    public Optional<ItemReview> get(long id) throws DataAccessException {
        String query = "SELECT * FROM ItemReview WHERE id = ?";
        dao.lock();
        try {
            PreparedStatement statement = dao.connection.prepareStatement(query);
            statement.setLong(1, id);
            statement.setQueryTimeout(30);
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                ItemReview review = fromResultSet(resultSet);
                dao.setId(review, id);
                return Optional.of(review);
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
    public ItemReview update(ItemReview entity) throws DataAccessException {
        String query = "UPDATE ItemReview SET Rating = ?, Description = ? WHERE id = ?";
        dao.lock();
        try {
            PreparedStatement statement = dao.connection.prepareStatement(query);
            statement.setInt(1, entity.getRating());
            statement.setString(2, entity.getReview());
            statement.setLong(3, entity.getId());
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
    public void delete(ItemReview entity) throws DataAccessException {
        String query = "DELETE FROM ItemReview WHERE id = ?";
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

    private static ItemReview fromResultSet(ResultSet resultSet) throws SQLException {
        long itemId = resultSet.getLong("Item_id");
        int rating = resultSet.getInt("Rating");
        String review = resultSet.getString("Description");
        long accountId = resultSet.getLong("Account_id");
        return new ItemReview(itemId, rating, review, accountId);
    }
}
