package com.example.calculator;

import java.io.IOException;
import java.io.PrintWriter;
import java.text.DecimalFormat;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@WebServlet("/calculate")
public class CalculatorServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private static final DecimalFormat NUMBER_FORMAT = new DecimalFormat("0.##########");

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("text/html;charset=UTF-8");

        String number1Value = request.getParameter("number1");
        String number2Value = request.getParameter("number2");
        String operation = request.getParameter("operation");

        try {
            double number1 = Double.parseDouble(number1Value);
            double number2 = Double.parseDouble(number2Value);
            String answer = calculate(number1, number2, operation);
            writePage(response, number1Value, number2Value, operation, answer, null);
        } catch (NumberFormatException ex) {
            writePage(response, number1Value, number2Value, operation, "", "Please enter valid numbers.");
        } catch (IllegalArgumentException ex) {
            writePage(response, number1Value, number2Value, operation, "", ex.getMessage());
        }
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.sendRedirect("index.html");
    }

    private String calculate(double number1, double number2, String operation) {
        double result;

        switch (operation) {
            case "add":
                result = number1 + number2;
                break;
            case "subtract":
                result = number1 - number2;
                break;
            case "multiply":
                result = number1 * number2;
                break;
            case "divide":
                if (number2 == 0) {
                    throw new IllegalArgumentException("Division by zero is not allowed.");
                }
                result = number1 / number2;
                break;
            default:
                throw new IllegalArgumentException("Please choose a valid operation.");
        }

        return NUMBER_FORMAT.format(result);
    }

    private void writePage(HttpServletResponse response, String number1, String number2, String operation,
            String answer, String error) throws IOException {
        try (PrintWriter out = response.getWriter()) {
            out.println("<!DOCTYPE html>");
            out.println("<html lang=\"en\">");
            out.println("<head>");
            out.println("<meta charset=\"UTF-8\">");
            out.println("<meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0\">");
            out.println("<title>Simple Calculator Result</title>");
            out.println("<style>");
            out.println("body{font-family:Arial,sans-serif;margin:40px;background:#f4f6f8;color:#222}");
            out.println(".calculator{max-width:560px;margin:0 auto;background:#fff;padding:24px;border:1px solid #d8dee4;border-radius:8px}");
            out.println("h1{margin-top:0;font-size:24px;text-align:center}");
            out.println("table{width:100%;border-collapse:collapse}");
            out.println("th,td{padding:12px;border:1px solid #d8dee4;text-align:left}");
            out.println("th{background:#eef2f6}");
            out.println("input,select{width:100%;box-sizing:border-box;padding:8px;font-size:16px}");
            out.println("button,.link{display:block;width:100%;box-sizing:border-box;padding:10px;border:0;border-radius:6px;background:#1f6feb;color:#fff;font-size:16px;text-align:center;text-decoration:none;cursor:pointer}");
            out.println(".error{margin-bottom:16px;padding:10px;background:#fff1f0;border:1px solid #ffccc7;color:#a8071a;border-radius:6px}");
            out.println("</style>");
            out.println("</head>");
            out.println("<body>");
            out.println("<main class=\"calculator\">");
            out.println("<h1>Simple Calculator</h1>");

            if (error != null) {
                out.println("<div class=\"error\">" + escapeHtml(error) + "</div>");
            }

            out.println("<form action=\"calculate\" method=\"post\">");
            out.println("<table>");
            out.println("<thead><tr><th>First Number</th><th>Second Number</th><th>Answer</th></tr></thead>");
            out.println("<tbody>");
            out.println("<tr>");
            out.println("<td><input type=\"number\" name=\"number1\" step=\"any\" value=\"" + escapeHtml(number1) + "\" required></td>");
            out.println("<td><input type=\"number\" name=\"number2\" step=\"any\" value=\"" + escapeHtml(number2) + "\" required></td>");
            out.println("<td><input type=\"text\" value=\"" + escapeHtml(answer) + "\" readonly></td>");
            out.println("</tr>");
            out.println("<tr><td colspan=\"3\">");
            out.println("<select name=\"operation\" required>");
            writeOption(out, "add", "Addition (+)", operation);
            writeOption(out, "subtract", "Subtraction (-)", operation);
            writeOption(out, "multiply", "Multiplication (*)", operation);
            writeOption(out, "divide", "Division (/)", operation);
            out.println("</select>");
            out.println("</td></tr>");
            out.println("<tr><td colspan=\"3\"><button type=\"submit\">Calculate Again</button></td></tr>");
            out.println("</tbody>");
            out.println("</table>");
            out.println("</form>");
            out.println("</main>");
            out.println("</body>");
            out.println("</html>");
        }
    }

    private void writeOption(PrintWriter out, String value, String label, String selectedValue) {
        String selected = value.equals(selectedValue) ? " selected" : "";
        out.println("<option value=\"" + value + "\"" + selected + ">" + label + "</option>");
    }

    private String escapeHtml(String value) {
        if (value == null) {
            return "";
        }

        return value.replace("&", "&amp;")
                .replace("<", "&lt;")
                .replace(">", "&gt;")
                .replace("\"", "&quot;")
                .replace("'", "&#39;");
    }
}
