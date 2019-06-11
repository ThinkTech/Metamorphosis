@Controller("dashboard")
class ModuleAction {
                               
      def String execute() {
         SUCCESS        
      }
      
      @GET("hello")
      def hello() {
         println "hello world"        
      }

}
