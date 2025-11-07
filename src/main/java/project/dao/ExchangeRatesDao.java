package project.dao;

import lombok.Getter;
import project.entity.Currency;
import project.entity.ExchangeRates;
import project.util.ConnectionManager;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class ExchangeRatesDao implements Dao {
    @Getter
    public static ExchangeRatesDao getInstance =  new ExchangeRatesDao();

    private ExchangeRatesDao() {}

    String SQL_FIND_ALL = """
            SELECT id, base_currency_id, target_currency_id, rate
            FROM ExchangeRates
            """;

    String SQL_SELECT_BASE_CURRENCY = """
            SELECT Currencies.id,
                   Currencies.code,
                   Currencies.fullname,
                   Currencies.sign
            FROM ExchangeRates
            JOIN Currencies ON Currencies.id = base_currency_id
            WHERE base_currency_id = ?
            """;

    String SQL_SELECT_TARGET_CURRENCY = """
            SELECT Currencies.id, 
                   Currencies.code,
                   Currencies.fullname,
                   Currencies.sign
            FROM ExchangeRates
            JOIN Currencies ON Currencies.id = target_currency_id
            WHERE target_currency_id = ?
            """;

    String SQL_FIND_BY_CODES = """
            SELECT  id, base_currency_id, target_currency_id, rate
            FROM ExchangeRates
            WHERE base_currency_id = ? and target_currency_id = ?
            """;

    String SQL_FIND_CURRENCY_CODES = """
            SELECT Currencies.id, Currencies.code, Currencies.fullname, Currencies.sign
            FROM Currencies
            WHERE Currencies.code = ?
            """;

    String SQL_CREATE_NEW_EXCHANGE_RATES = """
            INSERT INTO ExchangeRates (base_currency_id, target_currency_id, rate) 
            VALUES (?, ?, ?)
            """;

    String SQL_UPDATE_RATE = """
            UPDATE ExchangeRates
            SET rate = ?
            WHERE base_currency_id = ? AND target_currency_id = ?
            """;

    @Override
    public List<ExchangeRates> findAll() throws SQLException {
        try (var connection = ConnectionManager.get();
             var prepareStatement = connection.prepareStatement(SQL_FIND_ALL);
             var baseCurrencyStatement = connection.prepareStatement(SQL_SELECT_BASE_CURRENCY);
             var targetCurrencyStatement = connection.prepareStatement(SQL_SELECT_TARGET_CURRENCY)) {
            List<ExchangeRates> list = new ArrayList<>();
            var result = prepareStatement.executeQuery();
            while (result.next()) {
                list.add(new ExchangeRates(
                        result.getInt("id"),
                        compareToCurrency(result.getInt("base_currency_id"), baseCurrencyStatement),
                        compareToCurrency(result.getInt("target_currency_id"), targetCurrencyStatement),
                        result.getDouble("rate")
                ));
            }
            return list;
        }
    }

    private Currency compareToCurrency(int id, PreparedStatement preparedStatement) throws SQLException {
        preparedStatement.setInt(1, id);
        ResultSet resultSet = preparedStatement.executeQuery();
        if (resultSet.next()){
            return new Currency(
                    resultSet.getInt("id"),
                    resultSet.getString("code"),
                    resultSet.getString("fullname"),
                    resultSet.getString("sign")
            );
        }
        else {
            throw new SQLException("ID is not exists");
        }
    }

    public Optional<ExchangeRates> findByCodes(String codes) throws SQLException {
        try (var connection = ConnectionManager.get();
             var prepareStatement = connection.prepareStatement(SQL_FIND_BY_CODES);
             var baseCurrencyStatement = connection.prepareStatement(SQL_FIND_CURRENCY_CODES);
             var targetCurrencyStatement = connection.prepareStatement(SQL_FIND_CURRENCY_CODES)) {

            String code1 = codes.substring(0, 3);
            String code2 = codes.substring(3, 6);
            baseCurrencyStatement.setString(1, code1);
            targetCurrencyStatement.setString(1, code2);

            var baseCurrency = baseCurrencyStatement.executeQuery();
            var targetCurrency = targetCurrencyStatement.executeQuery();
            if (baseCurrency.next() && targetCurrency.next()) {
                prepareStatement.setInt(1, baseCurrency.getInt("id"));
                prepareStatement.setInt(2, targetCurrency.getInt("id"));
                var resultSet = prepareStatement.executeQuery();
                if (resultSet.next()) {
                    return Optional.of(new ExchangeRates(
                            resultSet.getInt("id"),
                            new Currency(
                                    baseCurrency.getInt("id"),
                                    baseCurrency.getString("code"),
                                    baseCurrency.getString("fullname"),
                                    baseCurrency.getString("sign")
                            ),
                            new Currency(
                                    targetCurrency.getInt("id"),
                                    targetCurrency.getString("code"),
                                    targetCurrency.getString("fullname"),
                                    targetCurrency.getString("sign")
                            ),
                            resultSet.getDouble("rate"))
                    );
                }
            }
        }
        return Optional.empty();

    }

    public void createExchangeRates(String baseCurrencyCode, String targetCurrencyCode, double rate) throws SQLException {
        try (var connection = ConnectionManager.get();
            var prepareStatement = connection.prepareStatement(SQL_CREATE_NEW_EXCHANGE_RATES);
            var baseCurrencyStatement = connection.prepareStatement(SQL_FIND_CURRENCY_CODES);
            var targetCurrencyStatement = connection.prepareStatement(SQL_FIND_CURRENCY_CODES)) {

            baseCurrencyStatement.setString(1, baseCurrencyCode);
            targetCurrencyStatement.setString(1, targetCurrencyCode);

            var baseCurrencyObject = baseCurrencyStatement.executeQuery();
            var targetCurrencyObject = targetCurrencyStatement.executeQuery();

            prepareStatement.setInt(1, baseCurrencyObject.getInt("id"));
            prepareStatement.setInt(2, targetCurrencyObject.getInt("id"));
            prepareStatement.setDouble(3, rate);
            prepareStatement.executeUpdate();
        }
    }

    public void updateRate(String baseCurrencyCode, String targetCurrencyCode, Double rateDouble) throws SQLException {
        try (var connection = ConnectionManager.get();
            var prepareStatement = connection.prepareStatement(SQL_UPDATE_RATE);
            var baseCurrencyStatement = connection.prepareStatement(SQL_FIND_CURRENCY_CODES);
            var targetCurrencyStatement = connection.prepareStatement(SQL_FIND_CURRENCY_CODES)) {

            baseCurrencyStatement.setString(1, baseCurrencyCode);
            targetCurrencyStatement.setString(1, targetCurrencyCode);

            var baseCurrencyObject = baseCurrencyStatement.executeQuery();
            var targetCurrencyObject = targetCurrencyStatement.executeQuery();

            prepareStatement.setDouble(1, rateDouble);
            prepareStatement.setInt(2, baseCurrencyObject.getInt("id"));
            prepareStatement.setInt(3, targetCurrencyObject.getInt("id"));
            prepareStatement.executeUpdate();
        }
    }

    public Optional<ExchangeRates> convertAtAmountCurrency(String from, String to) throws SQLException {
        try (var connection = ConnectionManager.get();
            var prepareStatement = connection.prepareStatement(SQL_FIND_BY_CODES);
            var fromStatement = connection.prepareStatement(SQL_FIND_CURRENCY_CODES);
            var toStatement = connection.prepareStatement(SQL_FIND_CURRENCY_CODES)) {

            fromStatement.setString(1, from);
            toStatement.setString(1, to);
            var fromObject = fromStatement.executeQuery();
            var toObject = toStatement.executeQuery();

            prepareStatement.setInt(1, fromObject.getInt("id"));
            prepareStatement.setInt(2, toObject.getInt("id"));
            var resultSet = prepareStatement.executeQuery();
            if (resultSet.next()) {
                return Optional.of(new ExchangeRates(
                        resultSet.getInt("id"),
                        new Currency(
                                fromObject.getInt("id"),
                                fromObject.getString("code"),
                                fromObject.getString("fullname"),
                                fromObject.getString("sign")
                        ),
                        new Currency(
                                toObject.getInt("id"),
                                toObject.getString("code"),
                                toObject.getString("fullname"),
                                toObject.getString("sign")
                        ),
                        resultSet.getDouble("rate")
                ));
            } else {
                return Optional.empty();
            }
        }
    }
}
