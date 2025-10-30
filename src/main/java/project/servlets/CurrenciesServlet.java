package project.servlets;


import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import project.dto.CurrencyDto;
import project.service.CurrencyService;
import project.validate.CodeValidator;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.SQLException;
import java.util.Optional;

@WebServlet("/currencies")
public class CurrenciesServlet extends HttpServlet {

    public static CurrencyService currencyService = CurrencyService.getINSTANCE();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setContentType("application/json");
        resp.setCharacterEncoding("UTF-8");

        try (PrintWriter printWriter = resp.getWriter()) {
            resp.setStatus(HttpServletResponse.SC_OK);
            currencyService.findAll()
                    .forEach(currencyDto -> printWriter.println(currencyDto.toString()));
        } catch (SQLException e) {
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            throw new RuntimeException(e);
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        var code = req.getParameter("code");
        var fullname = req.getParameter("fullname");
        var sign = req.getParameter("sign");

        if (code.isEmpty() || fullname.isEmpty() || sign.isEmpty()) {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            resp.getWriter().write("error: " + "One of the fields is empty");
            return;
        }
        if (!CodeValidator.isCodeCorrect(code)) {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            resp.getWriter().write("{\"error\": \"Incorrect code\"}");
            return;
        }
        if (!CodeValidator.isUniqueCode(code)) {
            resp.setStatus(HttpServletResponse.SC_CONFLICT);
            resp.getWriter().write("{\"error\": \"Code is not unique\"}");
            return;
        }

        try {
            currencyService.createNewCurrency(code, fullname, sign);
            resp.setStatus(HttpServletResponse.SC_CREATED);
            PrintWriter printWriter = resp.getWriter();
            Optional<CurrencyDto> dto = currencyService.findByCode(code);
            printWriter.write(dto.get().toString());
        } catch (SQLException e) {
            throw new RuntimeException("Database error: ", e);
        }
    }
}
