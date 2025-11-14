package project.servlets;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import project.dto.CurrencyDto;
import project.exception.FieldsEmptyException;
import project.exception.FieldsIncorrectException;
import project.exception.InternalServerException;
import project.exception.ObjectNotFoundException;
import project.service.CurrencyService;
import project.util.JsonManager;
import project.validator.CurrencyValidator;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.SQLException;

@WebServlet("/currency/*")
public class CurrencyServlet extends HttpServlet {

    public static CurrencyService currencyService = CurrencyService.getInstance();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        var pathInfo = req.getPathInfo();
        if (pathInfo != null && pathInfo.length() > 1) {
            String type = pathInfo.substring(1);
            if (!CurrencyValidator.isCodeCorrect(type)) {
                resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                String message = "Incorrect code currency";
                resp.getWriter().write(JsonManager.errorToJson(message, new FieldsIncorrectException(message)));
                return;
            }
            displayCurrency(type, req, resp);
        } else {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            PrintWriter out = resp.getWriter();
            String message = "Path is not found";
            out.write(JsonManager.errorToJson(message, new FieldsEmptyException(message)));
        }
    }

    private void displayCurrency(String type, HttpServletRequest req, HttpServletResponse resp) throws IOException {
        try (PrintWriter out = resp.getWriter()) {
            var currencyObject = currencyService.findByCode(type);
            if (currencyObject.isPresent()) {
                resp.setStatus(HttpServletResponse.SC_OK);
                CurrencyDto dto = currencyObject.get();
                String result = JsonManager.dtoToJson(dto);
                out.write(result);
            } else {
                resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
                String message = "Code is not found";
                out.write(JsonManager.errorToJson(message, new ObjectNotFoundException(message)));
            }
        } catch (SQLException e) {
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            String message = "Internal server error";
            resp.getWriter().write(JsonManager.errorToJson(message, new InternalServerException(e.getMessage())));
        }
    }
}
