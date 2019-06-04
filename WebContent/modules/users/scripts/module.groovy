class UserAction extends ActionSupport {
	
	def login()  {
	    session.setAttribute("user","user")
		def module = moduleManager.getMainModule("back-end")
		def url = module ? request.contextPath+"/"+module.url: request.contextPath+"/"
		response.sendRedirect(url)
	}
	
	def logout() {
	    session.invalidate()
		response.sendRedirect(request.contextPath+"/")
	}
	
	
	def selectTemplate() {
	    def id = request.id;
		def template = templateManager.getTemplate(id)
		if(template && template.backend) session.setAttribute("template",id)
		response.sendRedirect(request.getHeader("referer"))
	}
	
}