package lib.persistence;

import com.google.gson.Gson;

import java.util.Optional;

public abstract class Entity {

    protected static final Gson gson = new Gson();

    long id = -1;

    public long getId() {
        return id;
    }

    public boolean extistsInDatabase() {
        return id > 0;
    }

    public final String toJson() {
        return gson.toJson(this);
    }

    /* Simple CRUD interface for accessing underlying data storage; unique to each Entity */
    protected interface DAO<T extends Entity> {

        T create(T entity) throws DataAccessException;

        Optional<T> get(long id) throws DataAccessException;

        T update(T entity) throws DataAccessException;

        void delete(T entity) throws DataAccessException;

    }

}
