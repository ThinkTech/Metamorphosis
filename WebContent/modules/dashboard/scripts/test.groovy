@Controller
class ModuleAction extends ActionSupport {
                              
      @Get(url="test",page="test")
      def test() {
         println "this is a test"      
         SUCCESS  
      }

}
