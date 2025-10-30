package project.service;

import lombok.Getter;
import project.dao.CurrencyDao;
import project.dto.CurrencyDto;

import java.sql.SQLException;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class CurrencyService {
    @Getter
    private static CurrencyService INSTANCE = new CurrencyService();

    public CurrencyDao currencyDao = CurrencyDao.getInstance();

    private CurrencyService() {}

    public List<CurrencyDto> findAll() throws SQLException {
        return currencyDao.findAll().stream()
                .map(currency -> new CurrencyDto(
                        currency.getCode(),
                        currency.getFullname(),
                        currency.getSign()
                ))
                .collect(Collectors.toList());
    }

    public Optional<CurrencyDto> findByCode(String code) {
        try {
            return currencyDao.findByCode(code).map(currency -> new CurrencyDto(
                    currency.getCode(),
                    currency.getFullname(),
                    currency.getSign()
            ));
        } catch (SQLException e) {
            throw new RuntimeException("SQL error: ", e);
        }
    }

    public void createNewCurrency(String code, String fullname, String sign) throws SQLException {
        currencyDao.createNewCurrency(code, fullname, sign);
    }

}
