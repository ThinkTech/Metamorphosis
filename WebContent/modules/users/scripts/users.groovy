import org.metamorphosis.core.ActionSupport


class User {}

class UserAction extends ActionSupport {

	
	def login()  {
	    session.setAttribute("user",new User())
		def module = moduleManager.main
		def url = module ? request.contextPath+"/"+module.url: request.contextPath+"/"
		response.sendRedirect(url)
	}
	
	def logout() {
		SUCCESS
	}
	
	def selectTemplate() {
	    def id = request.getParameter("id");
		def template = templateManager.getTemplate(id)
		if(template && template.backend) {
			session.setAttribute("template",id)
		}
		response.sendRedirect(request.getHeader("referer"))
	}
	
}

new UserAction()