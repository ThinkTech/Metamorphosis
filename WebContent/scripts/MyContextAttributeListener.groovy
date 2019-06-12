import org.junit.After

@ContextAttributeListener
class MyContextAttributeListener {
          
     def onAdd() {
        println "context attribute added {name : $name, value = $value}"                             
     }

     def onRemove() {
         println "context attribute removed {name : $name, value = $value}"	                                                  
     }

     void onReplace() {
         println "context attribute replaced {name : $name, value = $value}"                                                
     }

}