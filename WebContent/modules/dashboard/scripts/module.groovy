@Controller("dashboard")
class ModuleAction extends ActionSupport {
                               
      def String execute() {
         SUCCESS        
      }
      
      @GET("sayHello")
      def hello() {
         println "hello world"        
      }

}
