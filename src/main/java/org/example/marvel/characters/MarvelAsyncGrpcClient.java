package org.example.marvel.characters;

import io.grpc.ManagedChannel;
import io.grpc.netty.GrpcSslContexts;
import io.grpc.netty.NettyChannelBuilder;
import io.netty.handler.ssl.SslProvider;
import org.example.marvel.universe.protbuf.CharacterInfo;
import org.example.marvel.universe.protbuf.CharacterPublisherGrpc;
import javax.net.ssl.SSLException;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;

public class MarvelAsyncGrpcClient {
    public ManagedChannel channel;
    public CharacterPublisherGrpc.CharacterPublisherStub AsyncStub;
    private final Logger LOGGER = Logger.getLogger(MarvelAsyncGrpcClient.class.getName());

    public MarvelAsyncGrpcClient(String host, int port, int ssl) throws SSLException {
        this.channel = createChannel(host, port, ssl);
        this.AsyncStub = createStub();
    }

    public ManagedChannel createChannel(String host, int port, int ssl) throws SSLException {
        if(ssl == 1){
            channel = NettyChannelBuilder.forAddress(host, port)
                    .sslContext(GrpcSslContexts.forClient().sslProvider(SslProvider.OPENSSL).build())
                    .build();
        }else {
            channel = NettyChannelBuilder.forAddress(host, port).usePlaintext().build();
        }
        return channel;
    }

    public CharacterPublisherGrpc.CharacterPublisherStub createStub() {
        AsyncStub = CharacterPublisherGrpc.newStub(this.channel);
        return AsyncStub;
    }

    public boolean isActiveChannel() {
        return channel != null;
    }

    public void getCharacterInfoByAlias() {

        try{

            CharacterInfo CharData = CharacterInfo.newBuilder()
                    .setCharacterId("A1111")
                    .setAlias("Iron Man")
                    .setMarvelCharacter(CharacterInfo.MarvelCharacter.IRON_MAN)
                    .setRealName("Tony Stark")
                    .setHometown("New York")
                    .setSuperpowerLevel(9)
                    .setSpecialAbilities(1, "Genius")
                    .setSpecialAbilities(2, "Billionaire")
                    .setNemesis("Thanos")
                    .setSuperheroTeam("Avengers")
                    .setCatchphrase("Sometimes you've gotta run before you can walk")
                    .build();

            this.AsyncStub.publishCharacterInfo(CharData, new MessageCallBack());

        } catch (Exception ex){
            ex.printStackTrace();
            LOGGER.info("Error in Message Formation: " + ex);
        }

    }

    public void shutdown() throws InterruptedException {
        channel.shutdown().awaitTermination(5, TimeUnit.SECONDS);
    }

}
