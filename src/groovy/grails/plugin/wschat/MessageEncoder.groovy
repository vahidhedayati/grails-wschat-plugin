package grails.plugin.wschat

import grails.converters.JSON

import javax.websocket.EncodeException
import javax.websocket.Encoder
import javax.websocket.EndpointConfig

abstract class MessageEncoder implements Encoder.Text<ChatMessage>{
	
	@Override
	public void destroy() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void init(EndpointConfig arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public String encode(ChatMessage message) throws EncodeException {
		def myMsg=[:]
		myMsg.put("message", message.getName()+":"+message.getMessage())
		def myMsgj=myMsg as JSON
		return myMsgj as String
	}

}
