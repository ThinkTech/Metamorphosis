@Controller
class ModuleAction extends ActionSupport {
                              
      @GET(url="test",page="test")
      def test() {
         println "this is a test"      
         SUCCESS  
      }

}
