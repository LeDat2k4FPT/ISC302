<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@page import="java.util.List"%>
<%@page import="dto.VoucherDTO"%>
<%
    List<VoucherDTO> vouchers = (List<VoucherDTO>) request.getAttribute("vouchers");
    String msg = request.getParameter("msg");
    if (msg == null) msg = (String) request.getAttribute("msg");
    String type = request.getParameter("type") != null ? request.getParameter("type") : "success";
    if (type == null) type = (String) request.getAttribute("type");
%>

<div class="container-fluid mt-3">
    <h2 style="color:#234C45;font-weight:600;">Manage Vouchers</h2>
    <% if (msg != null) { %>
    <div class="alert alert-<%=type%> mt-2"><%=msg%></div>
    <% } %>

    <form method="post" action="<%=request.getContextPath()%>/ManageVoucherController" class="row g-2 align-items-center mt-3 mb-4">
        <input type="hidden" name="action" value="add">
        <div class="col-md-2">
            <input class="form-control" name="voucherCode" placeholder="Code" required>
        </div>
        <div class="col-md-2">
            <input class="form-control" name="discountValue" placeholder="Discount" type="number" step="0.01" required>
        </div>
        <div class="col-md-3">
            <input class="form-control" name="expiryDate" type="date" required>
        </div>
       <input type="hidden" name="status" value="Active">
        <div class="col-md-1">
            <button class="btn btn-success" style="background:#234C45;" type="submit">Add</button>
        </div>
    </form>

    <div class="table-responsive">
        <table class="table table-bordered table-hover align-middle bg-white">
            <thead class="table-light">
                <tr>
                    <th>ID</th>
                    <th>Code</th>
                    <th>Discount</th>
                    <th>Expiry Date</th>
                    <th>Status</th>
                    <th>Action</th>
                </tr>
            </thead>
            <tbody>
                <% if (vouchers != null) for (VoucherDTO v : vouchers) { %>
                    <tr>
                        <td><%=v.getVoucherID()%></td>
                        <td><%=v.getVoucherCode()%></td>
                        <td><%=v.getDiscountValue()%></td>
                        <td><%=v.getExpiryDate()%></td>
                        <td><%=v.getStatus()%></td>
                        <td>
                            <form method="post" action="<%=request.getContextPath()%>/ManageVoucherController" style="display:inline;">
                                <input type="hidden" name="voucherID" value="<%=v.getVoucherID()%>">
                                <input type="hidden" name="action" value="delete">
                                <button class="btn btn-danger btn-sm" type="submit"
                                        onclick="return confirm('Are you sure you want to delete (deactivate) voucher <%=v.getVoucherCode()%>?');">
                                    Delete
                                </button>
                            </form>
                        </td>
                    </tr>
                <% } %>
            </tbody>
        </table>
    </div>
</div>

<script>
document.addEventListener("DOMContentLoaded", () => {
    const addForm = document.querySelector('form[action="ManageVoucherController"][method="post"]');
    if (addForm) {
        addForm.addEventListener('submit', function(e) {
            e.preventDefault();
            const formData = new FormData(addForm);
            fetch('ManageVoucherController', {
                method: 'POST',
                body: formData
            })
            .then(res => res.text())
            .then(html => {
                const match = html.match(/<div class="alert alert-(.*?) mt-2">(.*?)<\/div>/);
                let msg = '', type = 'success';
                if (match) {
                    type = match[1];
                    msg = match[2];
                }
                if (typeof loadContent === 'function') {
                    loadContent('ManageVoucherController', msg, type);
                } else {
                    location.reload();
                }
            });
        });
    }

    const table = document.querySelector('table');
    if (table) {
        table.addEventListener('submit', function(e) {
            if (e.target && e.target.matches('form[action="ManageVoucherController"]')) {
                e.preventDefault();
                if (!confirm('Are you sure you want to delete (deactivate) this voucher?')) return;
                const formData = new FormData(e.target);
                fetch('ManageVoucherController', {
                    method: 'POST',
                    body: formData
                })
                .then(res => res.text())
                .then(html => {
                    const match = html.match(/<div class="alert alert-(.*?) mt-2">(.*?)<\/div>/);
                    let msg = '', type = 'success';
                    if (match) {
                        type = match[1];
                        msg = match[2];
                    }
                    if (typeof loadContent === 'function') {
                        loadContent('ManageVoucherController', msg, type);
                    } else {
                        location.reload();
                    }
                });
            }
        });
    }
});
</script>
