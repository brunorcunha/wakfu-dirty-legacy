package com.akfu.common.network

import com.akfu.auth.network.protocol.message.OpCode
import scala.annotation.StaticAnnotation
import com.akfu.common.network.protocol.message.FrameMessage
import scala.reflect.runtime.universe._
import scala.reflect.runtime.{currentMirror => cm}
import org.slf4j.Logger
import java.lang.reflect.Method

abstract class FrameBase[TClient <: AnyRef, TMessage <: FrameMessage] {  
  var handlers: Map[Int, Method] = Map()
   
  try {
    var methods = getClass().getDeclaredMethods()
    for (method <- methods) {
      var annotations = method.getAnnotations
      for(annotation <- annotations) {
        if(annotation.isInstanceOf[FrameHandler]) {
          handlers += (annotation.asInstanceOf[FrameHandler].opCode -> method)
        }
      }
    }
  }
  catch {
    case e: Exception => println(e)
  }
  
  def processMessage(client: TClient, message: TMessage) : Boolean = {    
    val handler = handlers getOrElse(message.getOpCode(), null)
    if(handler == null) 
      return false
      
    handler.invoke(this, client, message)
    return true
  }
}