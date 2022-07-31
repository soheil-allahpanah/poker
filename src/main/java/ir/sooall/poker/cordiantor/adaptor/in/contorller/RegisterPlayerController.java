package ir.sooall.poker.cordiantor.adaptor.in.contorller;

import ir.sooall.poker.common.message.PokerMessage;
import ir.sooall.poker.common.message.PokerRegisterMessage;
import ir.sooall.poker.cordiantor.handler.PokerRegistrationProcessMessageMapper;
import ir.sooall.poker.cordiantor.usecase.registerplayer.RegisterPlayerUseCase;

public class RegisterPlayerController {
    private final RegisterPlayerUseCase registerPlayerUseCase;
    private final PokerRegistrationProcessMessageMapper.RegisterMessage2ObjectMapper message2ObjectMapper;
    private final PokerRegistrationProcessMessageMapper.Player2AckRegisterMessageMapper ackRegisterMessageMapper;
    private final PokerRegistrationProcessMessageMapper.Player2NAckRegisterMessageMapper nAckRegisterMessageMapper;

    public RegisterPlayerController(RegisterPlayerUseCase registerPlayerUseCase) {
        this.registerPlayerUseCase = registerPlayerUseCase;
        message2ObjectMapper = new PokerRegistrationProcessMessageMapper.RegisterMessage2ObjectMapper();
        ackRegisterMessageMapper = new PokerRegistrationProcessMessageMapper.Player2AckRegisterMessageMapper();
        nAckRegisterMessageMapper = new PokerRegistrationProcessMessageMapper.Player2NAckRegisterMessageMapper();
    }

    public PokerMessage register(PokerMessage registerPokerMessage) {
        var player = message2ObjectMapper.map((PokerRegisterMessage) registerPokerMessage);
        try {
            return ackRegisterMessageMapper.map(registerPlayerUseCase.register(player));
        } catch (Exception exception) {
            return nAckRegisterMessageMapper.map(player);
        }
    }

}
