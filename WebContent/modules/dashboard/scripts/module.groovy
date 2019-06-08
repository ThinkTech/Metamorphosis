@Controller("dashboard")
class ModuleAction extends ActionSupport {
                               
      def String execute() {
         SUCCESS        
      }
      
      @GET("hello")
      def hello() {
         println "hello world"        
      }

}
