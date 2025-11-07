package project.servlets;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import project.dto.ExchangeRatesDto;
import project.exception.FieldsIncorrectException;
import project.exception.FieldsEmptyException;
import project.exception.InternalServerException;
import project.exception.ObjectNotFoundException;
import project.util.JsonManager;
import project.validator.CurrencyValidator;
import project.validator.SameValidator;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.SQLException;

import static project.servlets.ExchangesRatesServlet.exchangeRatesService;

@WebServlet("/exchangeRates/*")
public class ExchangeRatesServlet extends HttpServlet {

    @Override
    protected void service(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        if ("PATCH".equalsIgnoreCase(req.getMethod())) {
            doPatch(req, resp);
        } else {
            super.service(req, resp);
        }
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setContentType("application/json");
        resp.setCharacterEncoding("UTF-8");

        var pathInfo = req.getPathInfo();
        if (pathInfo != null && pathInfo.length() > 1) {
            String type = pathInfo.substring(1);
            displayExchangeRate(type, req, resp);
        } else {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            PrintWriter printWriter = resp.getWriter();
            String message = "Path is not find";
            printWriter.write(JsonManager.errorToJson(message, new FieldsEmptyException(message)));
        }
    }
    private void displayExchangeRate(String type, HttpServletRequest req, HttpServletResponse resp) throws IOException {
        try (PrintWriter writer = resp.getWriter()) {
            if (exchangeRatesService.findByCodes(type).isPresent()) {
                resp.setStatus(HttpServletResponse.SC_OK);
                ExchangeRatesDto dto = exchangeRatesService.findByCodes(type).get();
                writer.write(JsonManager.dtoToJson(dto));
            } else {
                resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
                PrintWriter printWriter = resp.getWriter();
                String message = "Exchange rates not found";
                printWriter.write(JsonManager.errorToJson(message, new ObjectNotFoundException(message)));
            }
        } catch (IOException e) {
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            String message = "Internal server error";
            resp.getWriter().write(JsonManager.errorToJson(message, new InternalServerException(e.getMessage())));
        }
    }


    public void doPatch(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        resp.setContentType("application/json");
        resp.setCharacterEncoding("UTF-8");

        var pathInfo = req.getPathInfo();
        if (pathInfo != null && pathInfo.length() > 1) {
            String type = pathInfo.substring(1);
            updateExchangeRates(type, req, resp);
        } else {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            PrintWriter printWriter = resp.getWriter();
            String message = "Path is not find";
            printWriter.write(JsonManager.errorToJson(message, new FieldsEmptyException(message)));
        }
    }
    private void updateExchangeRates(String type, HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String rate = req.getParameter("rate");

        //400 - Отсутствует нужное поле формы / введено некорректное поле формы
        if (SameValidator.isInputFields(rate)) {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            String message = "Field is empty";
            resp.getWriter().write(JsonManager.errorToJson(message, new FieldsEmptyException(message)));
            return;
        }

        //404 - Одна (или обе) валюта из валютной пары не существует в БД
        String baseCurrencyCode = type.substring(0, 3);
        String targetCurrencyCode = type.substring(3, 6);
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
            Double rateDouble = Double.parseDouble(rate);
            exchangeRatesService.updateRate(baseCurrencyCode, targetCurrencyCode, rateDouble);
            PrintWriter writer = resp.getWriter();
            ExchangeRatesDto dto = exchangeRatesService.findByCodes(type).get();
            writer.write(JsonManager.dtoToJson(dto));
        } catch (NumberFormatException e){
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
