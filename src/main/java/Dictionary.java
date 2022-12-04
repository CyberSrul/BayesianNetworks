// util class for parsing strings into integers which are easier to work with

import java.security.KeyException;
import java.util.HashMap;

public class Dictionary {

     private final HashMap<String, Integer> map;


     public Dictionary(){ this.map = new HashMap<>(); }

     public int Size(){ return this.map.size(); }

     public Dictionary log(String name){
         if (! this.map.containsKey(name)){ this.map.put(name, this.Size()); }
         return this;
     }

     public int translate(String name) throws KeyException {

         Integer translation = this.map.get(name);
         if (translation == null){ throw new KeyException("No such word in this dictionary"); }
         return translation;
     }
}