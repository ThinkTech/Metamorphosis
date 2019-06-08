@Controller("dashboard")
class ModuleAction extends ActionSupport {
                               
      def String execute() {
         SUCCESS        
      }
      
      @Get("sayHello")
      def hello() {
         println "hello world"        
      }

}
