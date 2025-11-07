package project.servlets;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import project.dto.ExchangeRatesAmountDto;
import project.exception.FieldsEmptyException;
import project.exception.InternalServerException;
import project.service.ExchangeRatesService;
import project.util.JsonManager;
import project.validator.SameValidator;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.SQLException;

@WebServlet("/exchange")
public class ExchangeServlet extends HttpServlet {
    ExchangeRatesService exchangeRatesService = ExchangeRatesService.getINSTANCE();

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

        try (PrintWriter out = resp.getWriter()) {
            ExchangeRatesAmountDto exchangeRatesAmountDto = exchangeRatesService.convertAtAmountCurrency(from, to, amount).get();
            out.write(JsonManager.dtoToJson(exchangeRatesAmountDto));
        } catch (SQLException e) {
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            PrintWriter writer  = resp.getWriter();
            String message = "Internal server error";
            writer.write(JsonManager.errorToJson(message, new InternalServerException(e.getMessage())));
        }
    }
}
