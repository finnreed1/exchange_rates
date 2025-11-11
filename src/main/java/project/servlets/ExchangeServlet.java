package project.servlets;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import project.dto.ExchangeRatesAmountDto;
import project.exception.FieldsEmptyException;
import project.exception.FieldsIncorrectException;
import project.exception.InternalServerException;
import project.service.ExchangeRatesService;
import project.util.JsonManager;
import project.validator.ExchangeRatesValidator;
import project.validator.SameValidator;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.SQLException;

@WebServlet("/exchange")
public class ExchangeServlet extends HttpServlet {
    ExchangeRatesService exchangeRatesService = ExchangeRatesService.getInstance();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setContentType("application/json");
        resp.setCharacterEncoding("UTF-8");

        String from = req.getParameter("from");
        String to = req.getParameter("to");
        String amount = req.getParameter("amount");


        if (SameValidator.isInputFields(from, to, amount)) {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            String message = "Fields is empty";
            resp.getWriter().write(JsonManager.errorToJson(message, new FieldsEmptyException(message)));
            return;
        }

        if (!ExchangeRatesValidator.isCodeCorrect(from.concat(to))) {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            String message = "Incorrect code currency";
            resp.getWriter().write(JsonManager.errorToJson(message, new FieldsIncorrectException(message)));
            return;
        }

        try (PrintWriter out = resp.getWriter()) {
            if (ExchangeRatesValidator.isExistsCode(from.concat(to))) {
                // прямой курс;
                ExchangeRatesAmountDto exchangeRatesAmountDto = exchangeRatesService.makeDirectExchangeRate(from, to, amount).get();
                out.write(JsonManager.dtoToJson(exchangeRatesAmountDto));
            } else if (ExchangeRatesValidator.isExistsCode(to.concat(from))) {
                // обратный курс;
                ExchangeRatesAmountDto dto = exchangeRatesService.makeReverseExchangeRate(from, to, amount).get();
                out.write(JsonManager.dtoToJson(dto));
            } else if (ExchangeRatesValidator.isCrossRate(from, to)) {
                // кросс курс;
                ExchangeRatesAmountDto dto = exchangeRatesService.makeCrossExchangeRate(from, to, amount).get();
                out.write(JsonManager.dtoToJson(dto));
            }
        } catch (SQLException e) {
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            PrintWriter writer  = resp.getWriter();
            String message = "Internal server error";
            writer.write(JsonManager.errorToJson(message, new InternalServerException(e.getMessage())));
        }
    }
}
