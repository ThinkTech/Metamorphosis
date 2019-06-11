@WebFilter("/*")
class MyFilter {

	def filter() {
		chain.doFilter(request,response)
	}

}