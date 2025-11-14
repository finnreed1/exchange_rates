package project.service;

import project.dao.ExchangeRatesDao;
import project.dto.ExchangeRatesAmountDto;
import project.dto.ExchangeRatesDto;
import project.entity.ExchangeRates;

import java.math.BigDecimal;
import java.math.RoundingMode;
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
                exchangeRates.getBaseCurrency(),
                exchangeRates.getTargetCurrency(),
                exchangeRates.getRate()
        )).collect(Collectors.toList());
    }

    public Optional<ExchangeRatesDto> findByCodes(String codes) throws SQLException {
        String baseCurrencyCode = codes.substring(0, 3);
        String targetCurrencyCode = codes.substring(3, 6);

        return exchangeRatesDao.findByCodes(baseCurrencyCode, targetCurrencyCode).map(exchangeRates -> new ExchangeRatesDto(
                exchangeRates.getBaseCurrency(),
                exchangeRates.getTargetCurrency(),
                exchangeRates.getRate()
        ));
    }

    public void createExchangeRates(String baseCurrencyCode, String targetCurrencyCode, double rate) throws SQLException {
        exchangeRatesDao.createExchangeRates(baseCurrencyCode, targetCurrencyCode, rate);
    }

    public void updateRate(String type, Double rateDouble) throws SQLException {
        String baseCurrencyCode = type.substring(0, 3);
        String targetCurrencyCode = type.substring(3, 6);
        exchangeRatesDao.updateRate(baseCurrencyCode, targetCurrencyCode, rateDouble);
    }

    public Optional<ExchangeRatesAmountDto> makeDirectExchangeRate(String from, String to, String amount) throws SQLException {
        Optional<ExchangeRates> object = exchangeRatesDao.convertAtAmountCurrency(from, to);
        return Optional.of(new ExchangeRatesAmountDto(
                object.get().getBaseCurrency(),
                object.get().getTargetCurrency(),
                object.get().getRate(),
                Double.parseDouble(amount),
                convertToMoney(object.get().getRate().doubleValue() * Double.parseDouble(amount))
        ));
    }

    public Optional<ExchangeRatesAmountDto> makeReverseExchangeRate(String from, String to, String amount) throws SQLException {
        Optional<ExchangeRates> object = exchangeRatesDao.convertAtAmountCurrency(to, from);
        return Optional.of(new ExchangeRatesAmountDto(
                object.get().getTargetCurrency(),
                object.get().getBaseCurrency(),
                BigDecimal.valueOf(1 / object.get().getRate().doubleValue()),
                Double.parseDouble(amount),
                convertToMoney(1 / object.get().getRate().doubleValue() * Double.parseDouble(amount))
        ));

    }

    public Optional<ExchangeRatesAmountDto> makeCrossExchangeRate(String from, String to, String amount) throws SQLException {
        String code = findCodesByCrossRates(from, to);

        Optional<ExchangeRates> object1 = exchangeRatesDao.convertAtAmountCurrency(code, from);
        Optional<ExchangeRates> object2 = exchangeRatesDao.convertAtAmountCurrency(code, to);

        double crossRate = object1.get().getRate().doubleValue() / object2.get().getRate().doubleValue();
        return Optional.of(new ExchangeRatesAmountDto(
                object1.get().getTargetCurrency(),
                object2.get().getTargetCurrency(),
                BigDecimal.valueOf(crossRate),
                Double.parseDouble(amount),
                convertToMoney(crossRate * Double.parseDouble(amount))
        ));
    }

    private String findCodesByCrossRates(String from, String to) throws SQLException {
        List<ExchangeRatesDto> rates = findAll();

        Set<String> basesFrom = rates.stream()
                .filter(r -> r.getTargetCurrency().getCode().equals(from))
                .map(r -> r.getBaseCurrency().getCode())
                .collect(Collectors.toSet());

        Set<String> basesTo = rates.stream()
                .filter(r -> r.getTargetCurrency().getCode().equals(to))
                .map(r -> r.getBaseCurrency().getCode())
                .collect(Collectors.toSet());

        for (String fromExample : basesFrom) {
            if (basesTo.contains(fromExample)) {
                return fromExample;
            }
        }
        return null;
    }

    private static BigDecimal convertToMoney(double value) {
        return BigDecimal.valueOf(value).setScale(2, RoundingMode.HALF_UP);
    }

    public static ExchangeRatesService getInstance() {
        return INSTANCE;
    }
}
