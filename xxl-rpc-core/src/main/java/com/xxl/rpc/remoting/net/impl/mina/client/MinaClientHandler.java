package com.xxl.rpc.remoting.net.impl.mina.client;

import com.xxl.rpc.remoting.invoker.XxlRpcInvokerFactory;
import com.xxl.rpc.remoting.net.params.XxlRpcRequest;
import com.xxl.rpc.remoting.net.params.XxlRpcResponse;
import org.apache.mina.core.service.IoHandlerAdapter;
import org.apache.mina.core.session.IdleStatus;
import org.apache.mina.core.session.IoSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static com.xxl.rpc.remoting.net.common.Beat.BEAT_ID;

/**
 * rpc mina handler
 * @author xuxueli 2015-11-14 18:55:19
 */
public class MinaClientHandler extends IoHandlerAdapter {
	private static final Logger logger = LoggerFactory.getLogger(MinaClientHandler.class);


	private XxlRpcInvokerFactory xxlRpcInvokerFactory;
	public MinaClientHandler(final XxlRpcInvokerFactory xxlRpcInvokerFactory) {
		this.xxlRpcInvokerFactory = xxlRpcInvokerFactory;
	}


	@Override
	public void messageReceived(IoSession session, Object message) throws Exception {
		if (message instanceof XxlRpcResponse){
			XxlRpcResponse xxlRpcResponse = (XxlRpcResponse) message;
			if (BEAT_ID.equalsIgnoreCase(xxlRpcResponse.getRequestId())){
				return;
			}

			// notify response
			xxlRpcInvokerFactory.notifyInvokerFuture(xxlRpcResponse.getRequestId(), xxlRpcResponse);
		}
		else if (message instanceof XxlRpcRequest){
			final XxlRpcRequest xxlRpcRequest = (XxlRpcRequest) message;
			if (BEAT_ID.equalsIgnoreCase(xxlRpcRequest.getRequestId())){
				return;
			}
			throw new IllegalArgumentException("package IllegalArgument");
		}
		else{
			throw new IllegalArgumentException("package IllegalArgument");
		}
	}

	@Override
	public void exceptionCaught(IoSession session, Throwable cause) throws Exception {
		//super.exceptionCaught(session, cause);
		logger.error(">>>>>>>>>>> xxl-rpc mina client caught exception:", cause);
		session.closeOnFlush();
	}

	@Override
	public void sessionCreated(IoSession session) throws Exception {
		//super.sessionCreated(session);
		session.getConfig().setIdleTime(IdleStatus.BOTH_IDLE, 10*60);
	}

	@Override
	public void sessionIdle(IoSession session, IdleStatus status) throws Exception {
		//super.sessionIdle(session, status);
		if(status == IdleStatus.BOTH_IDLE){
			session.closeOnFlush();
			logger.debug(">>>>>>>>>>> xxl-rpc mina client close an idle session.");
		}
	}

}
