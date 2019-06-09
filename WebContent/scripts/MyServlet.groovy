@WebServlet("/test.html")
class MyServlet extends HttpServlet {

	def get() {		
		println "hello world from a groovy servlet"
		json([status:1])
	}
		
}