package project.service;

import lombok.Getter;
import project.dao.ExchangeRatesDao;
import project.dto.ExchangeRatesAmountDto;
import project.dto.ExchangeRatesDto;
import project.entity.ExchangeRates;

import java.sql.SQLException;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class ExchangeRatesService {
    private ExchangeRatesService() {}

    private static ExchangeRatesService INSTANCE = new  ExchangeRatesService();

    public static ExchangeRatesDao exchangeRatesDao = ExchangeRatesDao.getInstance;

    public List<ExchangeRatesDto> findAll() throws SQLException {
        return exchangeRatesDao.findAll().stream().map(exchangeRates -> new ExchangeRatesDto(
                exchangeRates.getBase_currency(),
                exchangeRates.getTarget_currency(),
                exchangeRates.getRate()
                )).collect(Collectors.toList());
    }

    public Optional<ExchangeRatesDto> findByCodes(String codes) {
        try {
            return exchangeRatesDao.findByCodes(codes).map(exchangeRates -> new ExchangeRatesDto(
                    exchangeRates.getBase_currency(),
                    exchangeRates.getTarget_currency(),
                    exchangeRates.getRate()
            ));
        } catch (SQLException e) {
            throw new RuntimeException("SQL error: ", e);
        }
    }

    public void createExchangeRates(String baseCurrencyCode, String targetCurrencyCode, double rate) throws SQLException {
        exchangeRatesDao.createExchangeRates(baseCurrencyCode, targetCurrencyCode, rate);
        // здесь нужно реверснуть, чтобы был доступен обратный перевод данных
    }

    public void updateRate(String baseCurrencyCode, String targetCurrencyCode, Double rateDouble) throws SQLException {
        exchangeRatesDao.updateRate(baseCurrencyCode, targetCurrencyCode, rateDouble);
    }

    public Optional<ExchangeRatesAmountDto> convertAtAmountCurrency(String from, String to, String amount) throws SQLException {
        try {
            Optional<ExchangeRates> object = exchangeRatesDao.convertAtAmountCurrency(from, to);
            return Optional.of(new ExchangeRatesAmountDto(
                    object.get().getBase_currency(),
                    object.get().getTarget_currency(),
                    object.get().getRate(),
                    Double.parseDouble(amount),
                    object.get().getRate() * Double.parseDouble(amount)
            ));
        } catch (SQLException e) {
            throw new RuntimeException("SQL error: ", e);
        }
    }

    public static ExchangeRatesService getINSTANCE() {
        return INSTANCE;
    }
}
