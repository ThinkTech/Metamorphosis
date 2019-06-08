@Controller
class UserAction extends ActionSupport {
	
	@POST("login")
	def login()  {
	    session.setAttribute("user","user")
		def module = moduleManager.getMainModule("back-end")
		def url = module ? request.contextPath+"/"+module.url: request.contextPath+"/"
		response.sendRedirect(url)
	}
	
	@GET("logout")
	def logout() {
	    session.invalidate()
		response.sendRedirect(request.contextPath+"/")
	}
	
	
}