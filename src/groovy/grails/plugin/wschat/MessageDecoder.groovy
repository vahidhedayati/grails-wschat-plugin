package grails.plugin.wschat

import grails.converters.JSON

import javax.websocket.DecodeException
import javax.websocket.Decoder
import javax.websocket.EndpointConfig

abstract class MessageDecoder implements Decoder.Text<ChatMessage>{

	@Override
	public void destroy() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void init(EndpointConfig arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public ChatMessage decode(String message) throws DecodeException {
		ChatMessage cm=new ChatMessage()
		cm.setMessage(message as JSON)
		// TODO Auto-generated method stub
		return cm;
	}

	@Override
	public boolean willDecode(String arg0) {
		// TODO Auto-generated method stub
		return true;
	}

}
