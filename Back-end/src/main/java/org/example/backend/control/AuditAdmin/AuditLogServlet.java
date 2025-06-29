package org.example.backend.control.AuditAdmin;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonSerializer;
import org.example.backend.dao.log.*;
import org.example.backend.model.*;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.jsonwebtoken.Claims;
import jakarta.servlet.http.*;
import org.example.backend.utils.Jwt;



@WebServlet(urlPatterns = {"/api/audit/admin-operations", "/api/audit/password-changes", "/api/audit/phone-changes",
        "/api/audit/login-logs", "/api/audit/customer-queries", "/api/audit/department-operations"})
public class AuditLogServlet extends HttpServlet {

    private final AdminOperationLogDao adminOperationLogDao = new AdminOperationLogDao();
    private final PasswordChangeLogDao passwordChangeLogDao = new PasswordChangeLogDao();
    private final PhoneChangeLogDao phoneChangeLogDao = new PhoneChangeLogDao();
    private final LoginLogDao loginLogDao = new LoginLogDao();
    private final CustomerQueryLogDao customerQueryLogDao = new CustomerQueryLogDao();
    private final DepartmentOperationLogDao departmentOperationLogDao = new DepartmentOperationLogDao();
    private final Gson gson;
    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSSSSS");

    public AuditLogServlet() {
        GsonBuilder gsonBuilder = new GsonBuilder();
        gsonBuilder.registerTypeAdapter(Timestamp.class, (JsonSerializer<Timestamp>) (src, typeOfSrc, context) ->
                context.serialize(src.toLocalDateTime().format(formatter)));
        gsonBuilder.registerTypeAdapter(Timestamp.class, (JsonDeserializer<Timestamp>) (json, typeOfT, context) ->
                Timestamp.valueOf(LocalDateTime.parse(json.getAsString(), formatter)));
        this.gson = gsonBuilder.create();
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setContentType("application/json");
        resp.setCharacterEncoding("UTF-8");
        PrintWriter out = resp.getWriter();

        // Step 1: Extract and validate JWT token from Cookie
        String jwtToken = null;
        if (req.getCookies() != null) {
            for (Cookie c : req.getCookies()) {
                if ("jwtToken".equals(c.getName())) {
                    jwtToken = c.getValue();
                    break;
                }
            }
        }

        if (jwtToken == null || jwtToken.isBlank()) {
            sendError(resp, out, HttpServletResponse.SC_UNAUTHORIZED, "未登录，token 为空");
            return;
        }

        Jwt jwtUtil = new Jwt();
        Claims claims = jwtUtil.validateJwt("jwtToken=" + jwtToken);
        if (claims == null) {
            sendError(resp, out, HttpServletResponse.SC_UNAUTHORIZED, "token 无效或已过期");
            return;
        }

        try {
            String path = req.getServletPath();
            BufferedReader reader = req.getReader();
            Map<String, Object> requestBody = gson.fromJson(reader, Map.class);

            // Validate pagination parameters
            int page = ((Number) requestBody.get("page")).intValue();
            int pageSize = ((Number) requestBody.get("pageSize")).intValue();
            if (page < 1 || pageSize < 1) {
                sendError(resp, out, HttpServletResponse.SC_BAD_REQUEST, "Invalid page or pageSize");
                return;
            }

            switch (path) {
                case "/api/audit/admin-operations":
                    handleAdminOperationLogs(requestBody, page, pageSize, resp, out);
                    break;
                case "/api/audit/password-changes":
                    handlePasswordChangeLogs(requestBody, page, pageSize, resp, out);
                    break;
                case "/api/audit/phone-changes":
                    handlePhoneChangeLogs(requestBody, page, pageSize, resp, out);
                    break;
                case "/api/audit/login-logs":
                    handleLoginLogs(requestBody, page, pageSize, resp, out);
                    break;
                case "/api/audit/customer-queries":
                    handleCustomerQueryLogs(requestBody, page, pageSize, resp, out);
                    break;
                case "/api/audit/department-operations":
                    handleDepartmentOperationLogs(requestBody, page, pageSize, resp, out);
                    break;
                default:
                    sendError(resp, out, HttpServletResponse.SC_NOT_FOUND, "Invalid endpoint");
            }
        } catch (Exception e) {
            sendError(resp, out, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, e.getMessage());
        }
    }

    private void handleAdminOperationLogs(Map<String, Object> requestBody, int page, int pageSize, HttpServletResponse resp, PrintWriter out) throws Exception {
        Timestamp start = requestBody.get("startTime") != null ? gson.fromJson(gson.toJson(requestBody.get("startTime")), Timestamp.class) : null;
        Timestamp end = requestBody.get("endTime") != null ? gson.fromJson(gson.toJson(requestBody.get("endTime")), Timestamp.class) : null;
        Integer operatorId = requestBody.get("operatorId") != null ? ((Number) requestBody.get("operatorId")).intValue() : null;
        Integer targetId = requestBody.get("targetId") != null ? ((Number) requestBody.get("targetId")).intValue() : null;
        Integer operationType = requestBody.get("operationType") != null ? ((Number) requestBody.get("operationType")).intValue() : null;

        List<AdminOperationLog> logs = adminOperationLogDao.queryWithFilters(start, end, operatorId, targetId, operationType, page, pageSize);
        long totalRecords = adminOperationLogDao.countWithFilters(start, end, operatorId, targetId, operationType);
        sendSuccess(resp, out, logs, page, pageSize, totalRecords);
    }

    private void handlePasswordChangeLogs(Map<String, Object> requestBody, int page, int pageSize, HttpServletResponse resp, PrintWriter out) throws Exception {
        Integer adminId = requestBody.get("adminId") != null ? ((Number) requestBody.get("adminId")).intValue() : null;
        Integer operation = requestBody.get("operation") != null ? ((Number) requestBody.get("operation")).intValue() : null;

        List<PasswordChangeLog> logs = passwordChangeLogDao.queryWithFilters(adminId, operation, page, pageSize);
        long totalRecords = passwordChangeLogDao.countWithFilters(adminId, operation);
        sendSuccess(resp, out, logs, page, pageSize, totalRecords);
    }

    private void handlePhoneChangeLogs(Map<String, Object> requestBody, int page, int pageSize, HttpServletResponse resp, PrintWriter out) throws Exception {
        Integer adminId = requestBody.get("adminId") != null ? ((Number) requestBody.get("adminId")).intValue() : null;
        String newPhone = requestBody.get("newPhone") != null ? requestBody.get("newPhone").toString() : null;

        List<PhoneChangeLog> logs = phoneChangeLogDao.queryWithFilters(adminId, newPhone, page, pageSize);
        long totalRecords = phoneChangeLogDao.countWithFilters(adminId, newPhone);
        sendSuccess(resp, out, logs, page, pageSize, totalRecords);
    }

    private void handleLoginLogs(Map<String, Object> requestBody, int page, int pageSize, HttpServletResponse resp, PrintWriter out) throws Exception {
        Integer adminId = requestBody.get("adminId") != null ? ((Number) requestBody.get("adminId")).intValue() : null;
        Timestamp start = requestBody.get("startTime") != null ? gson.fromJson(gson.toJson(requestBody.get("startTime")), Timestamp.class) : null;
        Timestamp end = requestBody.get("endTime") != null ? gson.fromJson(gson.toJson(requestBody.get("endTime")), Timestamp.class) : null;
        Integer loginStatus = requestBody.get("loginStatus") != null ? ((Number) requestBody.get("loginStatus")).intValue() : null;

        List<LoginLog> logs = loginLogDao.queryWithFilters(adminId, start, end, loginStatus, page, pageSize);
        long totalRecords = loginLogDao.countWithFilters(adminId, start, end, loginStatus);
        sendSuccess(resp, out, logs, page, pageSize, totalRecords);
    }

    private void handleCustomerQueryLogs(Map<String, Object> requestBody, int page, int pageSize, HttpServletResponse resp, PrintWriter out) throws Exception {
        String fullName = requestBody.get("fullName") != null ? requestBody.get("fullName").toString() : null;
        String maskedIdNumber = requestBody.get("maskedIdNumber") != null ? requestBody.get("maskedIdNumber").toString() : null;

        List<CustomerQueryLog> logs = customerQueryLogDao.queryWithFilters(fullName, maskedIdNumber, page, pageSize);
        long totalRecords = customerQueryLogDao.countWithFilters(fullName, maskedIdNumber);
        sendSuccess(resp, out, logs, page, pageSize, totalRecords);
    }

    private void handleDepartmentOperationLogs(Map<String, Object> requestBody, int page, int pageSize, HttpServletResponse resp, PrintWriter out) throws Exception {
        Integer operatorId = requestBody.get("operatorId") != null ? ((Number) requestBody.get("operatorId")).intValue() : null;
        Integer targetDepartmentId = requestBody.get("targetDepartmentId") != null ? ((Number) requestBody.get("targetDepartmentId")).intValue() : null;
        Integer operationType = requestBody.get("operationType") != null ? ((Number) requestBody.get("operationType")).intValue() : null;

        List<DepartmentOperationLog> logs = departmentOperationLogDao.queryWithFilters(operatorId, targetDepartmentId, operationType, page, pageSize);
        long totalRecords = departmentOperationLogDao.countWithFilters(operatorId, targetDepartmentId, operationType);
        sendSuccess(resp, out, logs, page, pageSize, totalRecords);
    }

    private void sendSuccess(HttpServletResponse resp, PrintWriter out, List<?> data, int page, int pageSize, long totalRecords) {
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("data", data);
        response.put("pagination", Map.of(
                "currentPage", page,
                "pageSize", pageSize,
                "totalRecords", totalRecords,
                "totalPages", (int) Math.ceil((double) totalRecords / pageSize)
        ));
        out.print(gson.toJson(response));
        resp.setStatus(HttpServletResponse.SC_OK);
    }

    private void sendError(HttpServletResponse resp, PrintWriter out, int status, String message) {
        Map<String, Object> response = new HashMap<>();
        response.put("success", false);
        response.put("error", message);
        out.print(gson.toJson(response));
        resp.setStatus(status);
    }
}