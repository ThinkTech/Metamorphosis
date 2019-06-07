@WebServlet("/test.html")
class MyServlet extends HttpServlet {

	void doGet(HttpServletRequest request,HttpServletResponse response) {		
		println "wonderful game"
	}
		
}