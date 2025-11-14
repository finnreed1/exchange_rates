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
import project.validator.ExchangeRatesValidator;
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
        var pathInfo = req.getPathInfo();
        if (pathInfo != null && pathInfo.length() > 1) {
            String type = pathInfo.substring(1);
            if (!ExchangeRatesValidator.isCodeCorrect(type)) {
                resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                String message = "Incorrect code currency";
                resp.getWriter().write(JsonManager.errorToJson(message, new FieldsIncorrectException(message)));
                return;
            }
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
        } catch (SQLException e) {
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            String message = "Internal server error";
            resp.getWriter().write(JsonManager.errorToJson(message, new InternalServerException(e.getMessage())));
        }
    }

    public void doPatch(HttpServletRequest req, HttpServletResponse resp) throws IOException {
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
        if (!ExchangeRatesValidator.isRateCorrect(rate)) {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            String message = "Rate is not correct";
            resp.getWriter().write(JsonManager.errorToJson(message, new FieldsIncorrectException(message)));
            return;
        }

        try {
            //404 - Одна (или обе) валюта из валютной пары не существует в БД
            if (!ExchangeRatesValidator.isExistsCode(type)) {
                resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
                String message = "Exchange rate currency on this type is not found";
                resp.getWriter().write(JsonManager.errorToJson(message, new ObjectNotFoundException(message)));
                return;
            }
            Double rateDouble = Double.parseDouble(rate);
            exchangeRatesService.updateRate(type, rateDouble);
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
