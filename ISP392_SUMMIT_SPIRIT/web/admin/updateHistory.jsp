<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@page import="java.util.List"%>
<%@page import="dto.UpdateHistoryDTO"%>
<%
    List<UpdateHistoryDTO> logs = (List<UpdateHistoryDTO>) request.getAttribute("logs");
%>
<div class="container mt-4">
    <h2>Update History</h2>
    <table class="table table-bordered mt-3">
        <thead class="table-light">
        <tr>
            <th>LogID</th>
            <th>Action</th>
            <th>Table</th>
            <th>RecordID</th>
            <th>Old Value</th>
            <th>New Value</th>
            <th>By</th>
            <th>At</th>
        </tr>
        </thead>
        <tbody>
        <% if (logs != null) for (UpdateHistoryDTO log : logs) { %>
            <tr>
                <td><%= log.getLogID() %></td>
                <td><%= log.getActionType() %></td>
                <td><%= log.getTableName() %></td>
                <td><%= log.getRecordID() %></td>
                <td><%= log.getOldValue() %></td>
                <td><%= log.getNewValue() %></td>
                <td><%= log.getPerformedBy() %></td>
                <td><%= log.getPerformedAt() %></td>
            </tr>
        <% } %>
        </tbody>
    </table>
</div>
