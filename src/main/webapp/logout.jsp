<%
	session.invalidate();

	HttpSession newsession = request.getSession(false);
	if (newsession != null) {
		newsession.invalidate();
	}

	response.sendRedirect("login.jsp");
%>