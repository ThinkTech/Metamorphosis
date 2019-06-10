@WebServlet("/test.html")
class MyServlet extends Servlet {

	def get() {		
		println "hello world from a groovy servlet"
		json([status:1])
	}
		
}