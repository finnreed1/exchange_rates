package project.servlets;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import project.dto.CurrencyDto;
import project.exception.FieldsEmptyException;
import project.exception.InternalServerException;
import project.exception.ObjectNotFoundException;
import project.service.CurrencyService;
import project.util.JsonManager;

import java.io.IOException;
import java.io.PrintWriter;

@WebServlet("/currency/*")
public class CurrencyServlet extends HttpServlet {

    public static CurrencyService currencyService = CurrencyService.getINSTANCE();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setContentType("application/json");
        resp.setCharacterEncoding("UTF-8");
        var pathInfo = req.getPathInfo();
        if (pathInfo != null && pathInfo.length() > 1) {
            String type = pathInfo.substring(1);
            displayCurrency(type, req, resp);
        } else {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            PrintWriter out = resp.getWriter();
            String message = "Path is not find";
            out.write(JsonManager.errorToJson(message, new FieldsEmptyException(message)));
        }
    }

    private void displayCurrency(String type, HttpServletRequest req, HttpServletResponse resp) throws IOException {
        try (PrintWriter out = resp.getWriter()) {
            var currencyObject = currencyService.findByCode(type);
            if (currencyObject.isPresent()) {
                resp.setStatus(HttpServletResponse.SC_OK);
                CurrencyDto dto = currencyObject.get();
                out.write(JsonManager.dtoToJson(dto));
            } else {
                resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
                String message = "Code is empty";
                out.write(JsonManager.errorToJson(message, new ObjectNotFoundException(message)));
            }
        } catch (IOException e) {
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            String message = "Internal server error";
            resp.getWriter().write(JsonManager.errorToJson(message, new InternalServerException(e.getMessage())));
        }
    }
}
