package lib.persistence.dao;

import lib.persistence.DataAccessException;
import lib.persistence.entities.Cart;

import java.sql.*;

class CartDAO implements Cart.DAO {

    private final SQLiteDAO dao;

    CartDAO(SQLiteDAO dao) {
        this.dao = dao;
    }

    @Override
    public Cart get(long accountId) throws DataAccessException {
        String query = "SELECT * FROM CartEntry WHERE Account_id = ?";
        Cart cart = new Cart(accountId);
        dao.setId(cart, accountId);
        dao.lock();
        try {
            PreparedStatement statement = dao.connection.prepareStatement(query);
            statement.setLong(1, accountId);
            statement.setQueryTimeout(30);
            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                long itemId = resultSet.getLong("Item_id");
                int quantity = resultSet.getInt("Quantity");
                cart.updateCart(itemId, quantity);
            }
        } catch (SQLException e) {
            throw new DataAccessException(e.getMessage());
        } finally {
            dao.unlock();
        }
        return cart;
    }

    @Override
    public Cart update(Cart entity) throws DataAccessException {
        //could be improved upon... doesn't NEED to delete all previous entries
        String query = "INSERT INTO CartEntry (Quantity, Item_id, Account_id) VALUES(?, ?, ?)";
        dao.lock();
        try {
            //delete previous cart contents
            PreparedStatement delete = dao.connection.prepareStatement("DELETE FROM CartEntry WHERE Account_id = ?");
            delete.setLong(1, entity.getAccountId());
            delete.setQueryTimeout(30);
            delete.executeUpdate();

            //insert new items
            for (Cart.CartItem item : entity.getItems())
                if (item.getQuantity() > 0) {
                    PreparedStatement statement = dao.connection.prepareStatement(query);
                    statement.setInt(1, item.getQuantity());
                    statement.setLong(2, item.getItemId());
                    statement.setLong(3, entity.getAccountId());
                    statement.setQueryTimeout(30);
                    statement.execute();
                }
        } catch (SQLException e) {
            throw new DataAccessException(e.getMessage());
        } finally {
            dao.unlock();
        }
        return entity;
    }

}
