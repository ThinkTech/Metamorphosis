@WebFilter("/*")
class MyFilter extends Filter {
	
	def init() {
	}
	
	def filter() {
		chain.doFilter(request,response)
	}

}