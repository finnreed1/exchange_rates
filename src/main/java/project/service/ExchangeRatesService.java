package project.service;

import project.dao.ExchangeRatesDao;
import project.dto.ExchangeRatesAmountDto;
import project.dto.ExchangeRatesDto;
import project.entity.ExchangeRates;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

public class ExchangeRatesService {
    private ExchangeRatesService() {
    }

    private static final ExchangeRatesService INSTANCE = new ExchangeRatesService();

    public static ExchangeRatesDao exchangeRatesDao = ExchangeRatesDao.getInstance();

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

    public Optional<ExchangeRatesAmountDto> makeDirectExchangeRate(String from, String to, String amount) throws SQLException {
        Optional<ExchangeRates> object = exchangeRatesDao.convertAtAmountCurrency(from, to);
        return Optional.of(new ExchangeRatesAmountDto(
                object.get().getBase_currency(),
                object.get().getTarget_currency(),
                object.get().getRate(),
                Double.parseDouble(amount),
                object.get().getRate() * Double.parseDouble(amount)
        ));
    }

    public Optional<ExchangeRatesAmountDto> makeReverseExchangeRate(String from, String to, String amount) throws SQLException {
        Optional<ExchangeRates> object = exchangeRatesDao.convertAtAmountCurrency(to, from);
        return Optional.of(new ExchangeRatesAmountDto(
                object.get().getTarget_currency(),
                object.get().getBase_currency(),
                1 / object.get().getRate(),
                Double.parseDouble(amount),
                (1 / object.get().getRate()) * Double.parseDouble(amount)
        ));

    }

    public Optional<ExchangeRatesAmountDto> makeCrossExchangeRate(String from, String to, String amount) throws SQLException {
        String code = findCodesByCrossRates(from, to);

        Optional<ExchangeRates> object1 = exchangeRatesDao.convertAtAmountCurrency(code, from);
        Optional<ExchangeRates> object2 = exchangeRatesDao.convertAtAmountCurrency(code, to);

        double crossRate = object1.get().getRate() / object2.get().getRate();
        return Optional.of(new ExchangeRatesAmountDto(
                object1.get().getTarget_currency(),
                object2.get().getTarget_currency(),
                crossRate,
                Double.parseDouble(amount),
                crossRate * Double.parseDouble(amount)
        ));
    }

    private String findCodesByCrossRates(String from, String to) throws SQLException {
        String result = "";
        List<ExchangeRatesDto> rates = findAll();

        Set<String> basesFrom = rates.stream()
                .filter(r -> r.getTarget_currency().getCode().equals(from))
                .map(r -> r.getBase_currency().getCode())
                .collect(Collectors.toSet());

        Set<String> basesTo = rates.stream()
                .filter(r -> r.getTarget_currency().getCode().equals(to))
                .map(r -> r.getBase_currency().getCode())
                .collect(Collectors.toSet());

        for (String fromExample : basesFrom) {
            if (basesTo.contains(fromExample)) {
                return fromExample;
            }
        }
        return result;
    }

    public static ExchangeRatesService getInstance() {
        return INSTANCE;
    }
}
