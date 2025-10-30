package project.dao;

import project.entity.Currency;

import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

public interface Dao<K, T> {
    List<T> findAll() throws SQLException;
}
