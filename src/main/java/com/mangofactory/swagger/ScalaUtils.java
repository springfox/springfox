package com.mangofactory.swagger;

import scala.None;
import scala.Option;
import scala.collection.Map;
import scala.collection.immutable.List;

import java.util.ArrayList;
import java.util.Collection;

import static scala.collection.JavaConversions.collectionAsScalaIterable;
import static scala.collection.JavaConversions.seqAsJavaList;
import static scala.collection.JavaConversions.mapAsJavaMap;

public class ScalaUtils {

   public static List toScalaList(Collection collection) {
      if(null == collection){
         collection = new ArrayList();
      }
      return collectionAsScalaIterable(collection).toList();
   }

   public static List emptyScalaList() {
      return collectionAsScalaIterable(new ArrayList()).toList();
   }

   public static java.util.List<?> fromScalaList(List<?> list) {
      return seqAsJavaList(list);
   }

   public static java.util.List<?> fromScalaList(None none) {
      return null;
   }
   public static Option toOption(Object o) {
      return Option.apply(o);
   }

   public static <T> T fromOption(Option<T> o) {
      if(!o.isDefined()){
         return null;
      }
      return o.get();
   }

   public static java.util.Map fromScalaMap(Map map){
      return mapAsJavaMap(map);
   }
}
