@Controller
class UserAction {
	
	@POST("login")
	def login()  {
	    session.setAttribute("user","user")
		def module = moduleManager.getMainModule("back-end")
		def url = module ? request.contextPath+"/"+module.url: request.contextPath+"/"
		redirect(url)
	}
	
	@GET("logout")
	def logout() {
	    session.invalidate()
		redirect(request.contextPath+"/")
	}
	
}