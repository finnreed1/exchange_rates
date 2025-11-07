package project.servlets;


import com.fasterxml.jackson.core.JsonProcessingException;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import project.dto.CurrencyDto;
import project.exception.CodeExistsException;
import project.exception.FieldsIncorrectException;
import project.exception.FieldsEmptyException;
import project.exception.InternalServerException;
import project.service.CurrencyService;
import project.util.JsonManager;
import project.validator.CurrencyValidator;
import project.validator.SameValidator;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

@WebServlet("/currencies")
public class CurrenciesServlet extends HttpServlet {

    public static CurrencyService currencyService = CurrencyService.getINSTANCE();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setContentType("application/json");
        resp.setCharacterEncoding("UTF-8");

        try (PrintWriter out = resp.getWriter()) {
            resp.setStatus(HttpServletResponse.SC_OK);
            List<CurrencyDto> list = currencyService.findAll();
            for (CurrencyDto dto : list) {
                out.write(JsonManager.dtoToJson(dto));
            }
        } catch (SQLException e) {
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            String message = "Internal server error";
            resp.getWriter().write(JsonManager.errorToJson(message, new InternalServerException(e.getMessage())));
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        var code = req.getParameter("code");
        var fullname = req.getParameter("fullname");
        var sign = req.getParameter("sign");

        if (SameValidator.isInputFields(code, fullname, sign)) {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            String message = "Fields is empty";
            resp.getWriter().write(JsonManager.errorToJson(message, new FieldsEmptyException(message)));
            return;
        }
        if (!CurrencyValidator.isCodeCorrect(code)) {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            String message = "Incorrect code currency";
            resp.getWriter().write(JsonManager.errorToJson(message, new FieldsIncorrectException(message)));
            return;
        }
        if (!CurrencyValidator.isUniqueCode(code)) {
            resp.setStatus(HttpServletResponse.SC_CONFLICT);
            String message = "Code is not unique";
            resp.getWriter().write(JsonManager.errorToJson(message, new CodeExistsException(message)));
            return;
        }

        try {
            currencyService.createNewCurrency(code, fullname, sign);
            resp.setStatus(HttpServletResponse.SC_CREATED);
            PrintWriter out = resp.getWriter();
            Optional<CurrencyDto> dto = currencyService.findByCode(code);
            out.write(JsonManager.dtoToJson(dto));
        } catch (SQLException e) {
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            String message = "Internal server error";
            resp.getWriter().write(JsonManager.errorToJson(message, new InternalServerException(e.getMessage())));
        }
    }
}
