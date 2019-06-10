@WebFilter("/*")
public class MyFilter implements Filter {

	void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) {
		chain.doFilter(request,response)
	}

	void init(FilterConfig config) {
	}
    
	void destroy() {
		
	}

}