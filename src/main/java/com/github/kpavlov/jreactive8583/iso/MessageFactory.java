package com.github.kpavlov.jreactive8583.iso;

import javax.annotation.Nonnull;
import java.io.UnsupportedEncodingException;
import java.text.ParseException;

public interface MessageFactory<T> {

    T newMessage(int type);

    T newMessage(@Nonnull MessageClass messageClass,
                 @Nonnull MessageFunction messageFunction,
                 @Nonnull MessageOrigin messageOrigin);

    T createResponse(T requestMessage);

    T parseMessage(byte[] buf, int isoHeaderLength) throws UnsupportedEncodingException, ParseException;
}
