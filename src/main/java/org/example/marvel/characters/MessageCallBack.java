package org.example.marvel.characters;

import io.grpc.stub.StreamObserver;
import org.example.marvel.universe.protbuf.ResponseCode;

import java.util.logging.Logger;

public class MessageCallBack implements StreamObserver<ResponseCode> {
    private static final Logger LOGGER = Logger.getLogger(MessageCallBack.class.getName());

    @Override
    public void onNext(ResponseCode value) {
        LOGGER.info("Message Received");
    }

    @Override
    public void onError(Throwable cause) {
        cause.printStackTrace();
        LOGGER.warning("Error occurred, cause: " + cause);
    }

    @Override
    public void onCompleted() {
        LOGGER.info("Message Stream completed");
    }
}
