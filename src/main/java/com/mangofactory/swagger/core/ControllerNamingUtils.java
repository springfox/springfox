package com.mangofactory.swagger.core;

import lombok.extern.slf4j.Slf4j;
import org.springframework.util.Assert;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;

@Slf4j
public class ControllerNamingUtils {

   public static String firstSlashPortion(String requestPattern) {
      Assert.notNull(requestPattern);
      Assert.hasText(requestPattern);
      int idx = requestPattern.indexOf("/", 1);
      if (idx > -1) {
         return requestPattern.substring(0, idx);
      } else {
         return requestPattern;
      }
   }

   public static String firstSlashPortionEncoded(String requestPattern) {
      return encode(firstSlashPortion(requestPattern));
   }

   public static String encode(String s) {
      String encoded = s;
      try {
         encoded = URLEncoder.encode(s, "ISO-8859-1");
      } catch (UnsupportedEncodingException e) {
         ControllerNamingUtils.log.error("Could not encode:" + s);
      }
      return encoded;
   }

   public static String decode(String s) {
      String encoded = s;
      try {
         encoded = URLDecoder.decode(s, "ISO-8859-1");
      } catch (UnsupportedEncodingException e) {
         ControllerNamingUtils.log.error("Could not decode:" + s);
      }
      return encoded;
   }
}
