package project.servlets;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import project.dto.ExchangeRatesDto;
import project.exception.*;
import project.service.ExchangeRatesService;
import project.util.JsonManager;
import project.validator.CurrencyValidator;
import project.validator.ExchangeRatesValidator;
import project.validator.SameValidator;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.SQLException;
import java.util.List;

@WebServlet("/exchangeRates")
public class ExchangesRatesServlet extends HttpServlet {
    public static ExchangeRatesService exchangeRatesService = ExchangeRatesService.getInstance();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setContentType("application/json");
        resp.setCharacterEncoding("UTF-8");

        try (PrintWriter printWriter = resp.getWriter()) {
            resp.setStatus(HttpServletResponse.SC_OK);
            List<ExchangeRatesDto> list = exchangeRatesService.findAll();
            for (ExchangeRatesDto dto : list) {
                printWriter.write(JsonManager.dtoToJson(dto));
            }
        } catch (SQLException e) {
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            throw new RuntimeException(e);
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        var baseCurrencyCode = req.getParameter("base_currency");
        var targetCurrencyCode = req.getParameter("target_currency");
        var rate = req.getParameter("rate");

        //400 - Отсутствует нужное поле формы / введено некорректное поле формы
        if (SameValidator.isInputFields(baseCurrencyCode, targetCurrencyCode, rate)) {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            String message = "Fields is empty";
            resp.getWriter().write(JsonManager.errorToJson(message, new FieldsEmptyException(message)));
            return;
        }

        if (!ExchangeRatesValidator.isCodeCorrect(baseCurrencyCode.concat(targetCurrencyCode))) {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            String message = "Incorrect code currency";
            resp.getWriter().write(JsonManager.errorToJson(message, new FieldsIncorrectException(message)));
            return;
        }

        //409 - Валютная пара с таким кодом уже существует
        if (ExchangeRatesValidator.isExistsCode(baseCurrencyCode.concat(targetCurrencyCode))) {
            resp.setStatus(HttpServletResponse.SC_CONFLICT);
            String message = "Code is not unique";
            resp.getWriter().write(JsonManager.errorToJson(message, new CodeExistsException(message)));
            return;
        }

        //404 - Одна (или обе) валюта из валютной пары не существует в БД
        if (CurrencyValidator.isNotExists(baseCurrencyCode)) {
            resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
            String message = "Base currency is not found";
            resp.getWriter().write(JsonManager.errorToJson(message, new ObjectNotFoundException(message)));
        }
        if (CurrencyValidator.isNotExists(targetCurrencyCode)) {
            resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
            String message = "Target currency is not found";
            resp.getWriter().write(JsonManager.errorToJson(message, new ObjectNotFoundException(message)));
        }

        try {
            double rateDouble = Double.parseDouble(rate);
            exchangeRatesService.createExchangeRates(baseCurrencyCode, targetCurrencyCode, rateDouble);
            resp.setStatus(HttpServletResponse.SC_CREATED);
            PrintWriter printWriter = resp.getWriter();
            ExchangeRatesDto dto = exchangeRatesService.findByCodes(baseCurrencyCode.concat(targetCurrencyCode)).get();
            printWriter.write(JsonManager.dtoToJson(dto));
        } catch (NumberFormatException e) {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            String message = "Invalid rate format";
            resp.getWriter().write(JsonManager.errorToJson(message, new FieldsIncorrectException(e.getMessage())));
        } catch (SQLException e) {
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            String message = "Internal server error";
            resp.getWriter().write(JsonManager.errorToJson(message, new InternalServerException(e.getMessage())));
        }
    }
}
