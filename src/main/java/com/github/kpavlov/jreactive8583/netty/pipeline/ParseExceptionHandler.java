package com.github.kpavlov.jreactive8583.netty.pipeline;

import com.github.kpavlov.jreactive8583.iso.MessageClass;
import com.github.kpavlov.jreactive8583.iso.MessageFactory;
import com.github.kpavlov.jreactive8583.iso.MessageFunction;
import com.github.kpavlov.jreactive8583.iso.MessageOrigin;
import com.solab.iso8583.IsoMessage;
import com.solab.iso8583.IsoType;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

import javax.annotation.Nonnull;
import java.text.ParseException;

/**
 * Handles {@link ParseException}s and responds with administrative message
 *
 * @see <a href="http://stackoverflow.com/questions/28275677/how-to-answer-an-invalid-iso8583-message">StackOverflow: How to answer an invalid ISO8583 message</a>
 */
@ChannelHandler.Sharable
public class ParseExceptionHandler extends ChannelInboundHandlerAdapter {

    private final MessageFactory<IsoMessage> isoMessageFactory;

    private final boolean includeErrorDetails;

    public ParseExceptionHandler(@Nonnull final MessageFactory<IsoMessage> isoMessageFactory,
                                 final boolean includeErrorDetails) {
        this.isoMessageFactory = isoMessageFactory;
        this.includeErrorDetails = includeErrorDetails;
    }

    @Override
    public void exceptionCaught(@Nonnull final ChannelHandlerContext ctx,
                                @Nonnull final Throwable cause) throws Exception {
        if (cause instanceof ParseException) {
            final var message = createErrorResponseMessage((ParseException) cause);
            ctx.writeAndFlush(message);
        }
        super.exceptionCaught(ctx, cause);
    }

    protected IsoMessage createErrorResponseMessage(final ParseException cause) {
        final var message = isoMessageFactory.newMessage(
                MessageClass.ADMINISTRATIVE, MessageFunction.NOTIFICATION, MessageOrigin.OTHER);
        message.setValue(24, 650, IsoType.NUMERIC, 3); //650 (Unable to parse message)
        if (includeErrorDetails) {
            var details = cause.getMessage();
            if (details.length() > 25) {
                details = details.substring(0, 22) + "...";
            }
            message.setValue(44, details, IsoType.LLVAR, 25);
        }
        return message;
    }
}
