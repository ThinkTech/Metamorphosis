@WebListener
class MyContextAttributeListener extends ContextAttributeListener {
          
     def onAdd() {
        println "attribute added {name : $name, value = $value}"                                              
     }

     def onRemove() {
         println "attribute removed {name : $name, value = $value}"	                                                  
     }

     void onReplace() {
         println "attribute replaced {name : $name, value = $value}"                                                
     }

}
