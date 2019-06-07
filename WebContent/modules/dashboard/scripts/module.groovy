@Controller("dashboard")
class ModuleAction extends ActionSupport {
                               
      def String execute() {
         SUCCESS        
      }
      
      @Get(url="hello")
      def test() {
         println "test"        
      }

}
