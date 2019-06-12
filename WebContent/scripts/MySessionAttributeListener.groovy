@SessionAttributeListener
class MySessionAttributeListener {
          
     def onAdd() {
        println "session attribute added {name : $name, value = $value}"    
        println session                                          
     }

     def onRemove() {
         println "session attribute removed {name : $name, value = $value}"	                                                  
     }

     void onReplace() {
         println "session attribute replaced {name : $name, value = $value}"                                                
     }

}