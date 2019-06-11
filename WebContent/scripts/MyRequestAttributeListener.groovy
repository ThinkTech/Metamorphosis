@WebListener
class MyRequestAttributeListener extends RequestAttributeListener {
          
     def onAdd() {
        println "request attribute added {name : $name, value = $value}"                                              
     }

     def onRemove() {
         println "request attribute removed {name : $name, value = $value}"	                                                  
     }

     void onReplace() {
         println "request attribute replaced {name : $name, value = $value}"                                                
     }

}
