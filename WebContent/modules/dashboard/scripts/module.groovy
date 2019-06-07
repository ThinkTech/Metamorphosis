@Controller("dashboard")
class ModuleAction extends ActionSupport {
                               
      def String execute() {
         SUCCESS        
      }
      
      @Get(url="sayHello")
      def hello() {
         println "hello world"        
      }

}
