@Controller
class ModuleAction {
                              
      @GET(url="test",page="test")
      def test() {
         println "this is a test"      
         SUCCESS  
      }

}
