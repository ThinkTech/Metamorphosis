@WebFilter("/*")
class MyFilter extends Filter {

	def filter() {
		chain.doFilter(request,response)
	}

}