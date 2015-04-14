package com.akfu.auth.network.protocol.message.serverToClient

import com.akfu.common.network.protocol.message.WakfuServerMessage
import com.akfu.auth.network.protocol.message.OpCode
import java.nio.ByteBuffer

object AuthenticationResultsEnum {
  final val SUCCESS: Byte = 0
  final val WRONG_CREDENTIALS: Byte = 2
  final val ALREADY_CONNECTED: Byte = 3
  final val LOCKED: Byte = 9
  final val LOGIN_SERVER_DOWN: Byte = 10
  final val TOO_MANY_CONNECTION: Byte = 11
  final val INVALID_PARTNER: Byte = 12
}

final class ClientAuthenticationResultsMessage(result: Byte) extends WakfuServerMessage {
  
  def getOpCode() = OpCode.SMSG_AUTH_RESULTS
  
  def internalSerialize() = {    
    val out = ByteBuffer allocate(5)
    out put result
    out put byte2Byte(0)
    out put byte2Byte(0)
    out.array
  }
  
}