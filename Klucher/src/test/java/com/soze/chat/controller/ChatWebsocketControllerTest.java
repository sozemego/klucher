package com.soze.chat.controller;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertThat;

import java.nio.charset.Charset;
import java.security.Principal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.AbstractSubscribableChannel;
import org.springframework.messaging.support.ChannelInterceptorAdapter;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.util.AntPathMatcher;
import org.springframework.util.PathMatcher;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.soze.chat.model.ChatMessage;
import com.soze.chat.model.ChatResponse;
import com.soze.chat.model.NewUserMessage;
import com.soze.chat.model.OutboundSocketMessage;
import com.soze.chat.model.RequestUserList;
import com.soze.chat.model.UserListMessage;
import com.soze.chat.service.ChatService;
import com.soze.common.exceptions.ChatRoomDoesNotExistException;

@RunWith(SpringRunner.class)
@SpringBootTest
@ActiveProfiles("test")
public class ChatWebsocketControllerTest {

	@Autowired
  private AbstractSubscribableChannel clientInboundChannel;

	@Autowired
	private AbstractSubscribableChannel clientOutboundChannel;

	@Autowired
	private AbstractSubscribableChannel brokerChannel;
	
	@Autowired
	private ChatService service;
	
	private TestChannelInterceptor testBrokerInterceptor;
	
	private TestChannelInterceptor testOutboundInterceptor;
	
	@Before
  public void setUp() throws Exception {
    this.testBrokerInterceptor = new TestChannelInterceptor();
    this.testOutboundInterceptor = new TestChannelInterceptor();
		this.brokerChannel.addInterceptor(testBrokerInterceptor);
		this.clientOutboundChannel.addInterceptor(testOutboundInterceptor);
		if(service.isChatRoomOpen("doge")) {
			service.removeChatRoom("doge");
		}
		service.addChatRoom("doge");
  }
	
	@Test
  public void testSubscribe() throws Exception {
		String username = "one";
		String roomName = "doge";
  	StompHeaderAccessor headers = StompHeaderAccessor.create(StompCommand.SUBSCRIBE);
  	headers.setSubscriptionId("0");
  	headers.setDestination("/chat/in/" + roomName);
  	headers.setSessionId("0");
  	headers.setUser(new TestPrincipal(username));
  	headers.setSessionAttributes(new HashMap<>());
  	Message<byte[]> message = MessageBuilder.createMessage(new byte[0], headers.getMessageHeaders());
  	
  	int usersInRoomBefore = service.getNumberOfUsers(roomName);
  	this.clientInboundChannel.send(message);
  	
  	Message<?> reply = testBrokerInterceptor.awaitMessage(5);
  	
  	OutboundSocketMessage response = new ObjectMapper().readValue(getStringFromMessage(reply), OutboundSocketMessage.class);
  	assertThat(response.getClass(), equalTo(NewUserMessage.class));
  	
  	assertThat(service.getNumberOfUsers(roomName), equalTo(usersInRoomBefore + 1));
  }
	
	@Test
	public void testSendChatMessage() throws Exception {
		
		String username = "two";
		String roomName = "doge";
		
		StompHeaderAccessor headers = StompHeaderAccessor.create(StompCommand.SUBSCRIBE);
  	headers.setSubscriptionId("0");
  	headers.setDestination("/chat/in/" + roomName);
  	headers.setSessionId("0");
  	headers.setUser(new TestPrincipal(username));
  	headers.setSessionAttributes(new HashMap<>());
  	Message<byte[]> message = MessageBuilder.createMessage(new byte[0], headers.getMessageHeaders());
  	int usersInRoomBefore = service.getNumberOfUsers(roomName);
  	this.clientInboundChannel.send(message);
  	Message<?> reply = testBrokerInterceptor.awaitMessage(5);
  	
  	OutboundSocketMessage response = new ObjectMapper().readValue(getStringFromMessage(reply), OutboundSocketMessage.class);
  	assertThat(response.getClass(), equalTo(NewUserMessage.class));
  	assertThat(service.getNumberOfUsers(roomName), equalTo(usersInRoomBefore + 1));
		
		headers = StompHeaderAccessor.create(StompCommand.SEND);
  	headers.setSubscriptionId("0");
  	headers.setDestination("/chat/in/" + roomName);
  	headers.setSessionId("0");
  	headers.setUser(new TestPrincipal(username));
  	headers.setSessionAttributes(new HashMap<>());
  	
  	byte[] payload = new ObjectMapper().writeValueAsBytes(new ChatMessage("message!"));
  	message = MessageBuilder.createMessage(payload, headers.getMessageHeaders());
  	
  	this.clientInboundChannel.send(message);
  	reply = testBrokerInterceptor.awaitMessage(5);
  	
  	response = new ObjectMapper().readValue(getStringFromMessage(reply), OutboundSocketMessage.class);
  	assertThat(response.getClass(), equalTo(ChatResponse.class));
  	
	}
	
	@Test
	public void testRequestUserList() throws Exception {
		String username = "three";
		String roomName = "doge";
		
		StompHeaderAccessor headers = StompHeaderAccessor.create(StompCommand.SUBSCRIBE);
  	headers.setSubscriptionId("0");
  	headers.setDestination("/chat/in/" + roomName);
  	headers.setSessionId("0");
  	headers.setUser(new TestPrincipal(username));
  	headers.setSessionAttributes(new HashMap<>());
  	Message<byte[]> message = MessageBuilder.createMessage(new byte[0], headers.getMessageHeaders());
  	int usersInRoomBefore = service.getNumberOfUsers(roomName);
  	this.clientInboundChannel.send(message);
  	Message<?> reply = testBrokerInterceptor.awaitMessage(5);
  	
  	OutboundSocketMessage response = new ObjectMapper().readValue(getStringFromMessage(reply), OutboundSocketMessage.class);
  	assertThat(response.getClass(), equalTo(NewUserMessage.class));
  	assertThat(service.getNumberOfUsers(roomName), equalTo(usersInRoomBefore + 1));
  	
  	headers = StompHeaderAccessor.create(StompCommand.SEND);
  	headers.setSubscriptionId("0");
  	headers.setDestination("/chat/in/" + roomName);
  	headers.setSessionId("0");
  	headers.setUser(new TestPrincipal(username));
  	headers.setSessionAttributes(new HashMap<>());
  	
  	byte[] payload = new ObjectMapper().writeValueAsBytes(new RequestUserList());
  	message = MessageBuilder.createMessage(payload, headers.getMessageHeaders());
  	
  	this.clientInboundChannel.send(message);
  	reply = testBrokerInterceptor.awaitMessage(5);
  	
  	response = new ObjectMapper().readValue(getStringFromMessage(reply), OutboundSocketMessage.class);
  	assertThat(response.getClass(), equalTo(UserListMessage.class));
  	
	}
	
	@Test
	public void testSubscribeRoomDoesNotExist() throws Exception {
		String username = "four";
		String roomName = "doesNotExist";
		
		StompHeaderAccessor headers = StompHeaderAccessor.create(StompCommand.SUBSCRIBE);
  	headers.setSubscriptionId("0");
  	headers.setDestination("/chat/in/" + roomName);
  	headers.setSessionId("0");
  	headers.setUser(new TestPrincipal(username));
  	headers.setSessionAttributes(new HashMap<>());
  	Message<byte[]> message = MessageBuilder.createMessage(new byte[0], headers.getMessageHeaders());
  	try {
  		this.clientInboundChannel.send(message);
  	} catch (Exception e) {
			assertThat(e.getCause().getClass(), equalTo(ChatRoomDoesNotExistException.class));
		}
	}
	
	private String getStringFromMessage(Message<?> message) {
		return new String((byte[]) message.getPayload(), Charset.forName("UTF-8"));
	}
	
  private static class TestPrincipal implements Principal {
  	
  	private final String username;
  	
  	public TestPrincipal(String username) {
  		this.username = username;
  	}

		@Override
		public String getName() {
			return username;
		}
		
  } 
  
  public static class TestChannelInterceptor extends ChannelInterceptorAdapter {

  	private final BlockingQueue<Message<?>> messages = new ArrayBlockingQueue<>(100);

  	private final List<String> destinationPatterns = new ArrayList<>();

  	private final PathMatcher matcher = new AntPathMatcher();


  	public void setIncludedDestinations(String... patterns) {
  		this.destinationPatterns.addAll(Arrays.asList(patterns));
  	}

  	/**
  	 * @return the next received message or {@code null} if the specified time elapses
  	 */
  	public Message<?> awaitMessage(long timeoutInSeconds) throws InterruptedException {
  		return this.messages.poll(timeoutInSeconds, TimeUnit.SECONDS);
  	}

  	@Override
  	public Message<?> preSend(Message<?> message, MessageChannel channel) {
  		if (this.destinationPatterns.isEmpty()) {
  			this.messages.add(message);
  		}
  		else {
  			StompHeaderAccessor headers = StompHeaderAccessor.wrap(message);
  			if (headers.getDestination() != null) {
  				for (String pattern : this.destinationPatterns) {
  					if (this.matcher.match(pattern, headers.getDestination())) {
  						this.messages.add(message);
  						break;
  					}
  				}
  			}
  		}
  		return message;
  	}

  }

}
