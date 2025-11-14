package project.dao;

import project.entity.Currency;
import project.util.ConnectionManager;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import static java.sql.Statement.RETURN_GENERATED_KEYS;

public class CurrencyDao implements Dao<Long, Currency> {
    private static final CurrencyDao INSTANCE = new CurrencyDao();

    String FIND_ALL_SQL = """
            SELECT id, code, fullname, sign 
            FROM Currencies
            """;

    String FIND_BY_ID_SQL = """
            SELECT id, code, fullname, sign
            FROM Currencies
            WHERE code = ?
            """;

    String CREATE_NEW_CURRENCY = """
            INSERT INTO Currencies (code, fullname, sign)
            VALUES (?, ?, ?);
            """;


    @Override
    public List<Currency> findAll() throws SQLException {
        try (var connection = ConnectionManager.get();
            var prepareStatement = connection.prepareStatement(FIND_ALL_SQL)) {
            List<Currency> list = new ArrayList<>();
            var statementResult = prepareStatement.executeQuery();
            while (statementResult.next()) {
                list.add(new Currency(
                        statementResult.getInt("id"),
                        statementResult.getString("code"),
                        statementResult.getString("fullname"),
                        statementResult.getString("sign")
                ));
            }
            return list;
        }
    }

    public Optional<Currency> findByCode(String code) throws SQLException {
        try (var connection = ConnectionManager.get();
        var prepareStatement = connection.prepareStatement(FIND_BY_ID_SQL)) {
            prepareStatement.setString(1, code);
            var resultSet = prepareStatement.executeQuery();
            if (resultSet.next()) {
                return Optional.of(new Currency(
                        resultSet.getInt("id"),
                        resultSet.getString("code"),
                        resultSet.getString("fullname"),
                        resultSet.getString("sign")));
            }
            else {
                return Optional.empty();
            }
        }
    }

    public void createNewCurrency(String code, String fullname, String sign) throws SQLException {
        try (var connection = ConnectionManager.get();
             var prepareStatement = connection.prepareStatement(CREATE_NEW_CURRENCY)) {
            prepareStatement.setString(1, code);
            prepareStatement.setString(2, fullname);
            prepareStatement.setString(3, sign);
            prepareStatement.executeUpdate();
        }
    }


    private CurrencyDao() {}

    public static CurrencyDao getInstance() {
        return INSTANCE;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CurrencyDao that = (CurrencyDao) o;
        return Objects.equals(FIND_ALL_SQL, that.FIND_ALL_SQL);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(FIND_ALL_SQL);
    }

    @Override
    public String toString() {
        return "CurrencyDao{" +
                "SQL='" + FIND_ALL_SQL + '\'' +
                '}';
    }
}
