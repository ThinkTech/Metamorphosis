@SessionListener
class MySessionListener {

   def onCreate() {
       println "session created"                               
   }

   def onDestroy() {
       println "session destroyed"	                                      
   }
          
}
