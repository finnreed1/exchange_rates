package project.service;

import project.dao.CurrencyDao;
import project.dto.CurrencyDto;

import java.sql.SQLException;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class CurrencyService {
    private static final CurrencyService INSTANCE = new CurrencyService();

    public CurrencyDao currencyDao = CurrencyDao.getInstance();

    private CurrencyService() {
    }

    public List<CurrencyDto> findAll() throws SQLException {
        return currencyDao.findAll().stream()
                .map(currency -> new CurrencyDto(
                        currency.getCode(),
                        currency.getFullName(),
                        currency.getSign()
                ))
                .collect(Collectors.toList());
    }

    public Optional<CurrencyDto> findByCode(String code) throws SQLException {
        return currencyDao.findByCode(code).map(currency -> new CurrencyDto(
                currency.getCode(),
                currency.getFullName(),
                currency.getSign()
        ));
    }

    public void createNewCurrency(String code, String fullname, String sign) throws SQLException {
        currencyDao.createNewCurrency(code, fullname, sign);
    }

    public static CurrencyService getInstance() {
        return INSTANCE;
    }
}
