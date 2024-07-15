package com.ethan.serialize.fastjson2;

import com.ethan.common.bean.ScopeBeanFactory;
import com.ethan.rpc.model.ApplicationModel;
import com.ethan.serialize.ObjectInput;
import com.ethan.serialize.ObjectOutput;
import com.ethan.serialize.Serialization;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * FastJson serialization implementation.<br/>
 *
 * <pre>
 *     e.g. &lt;ethan:protocol serialization="fastjson" /&gt;
 * </pre>
 *
 * @author Huang Z.Y.
 */
@Slf4j
public class FastJson2Serialization implements Serialization {

    static {
        ScopeBeanFactory beanFactory = ApplicationModel.getBeanFactory();
        beanFactory.registerBean(new Fastjson2CreatorManager());
    }

    @Override
    public byte getContentTypeId() {
        return 1;
    }

    @Override
    public ObjectOutput serialize(OutputStream output) throws IOException {
        Fastjson2CreatorManager fastjson2CreatorManager = ApplicationModel
                .getBeanFactory().getBean(Fastjson2CreatorManager.class);
        return new FastJson2ObjectOutput(fastjson2CreatorManager, output);
    }

    @Override
    public ObjectInput deserialize(InputStream input) throws IOException {
        Fastjson2CreatorManager fastjson2CreatorManager = ApplicationModel
                .getBeanFactory().getBean(Fastjson2CreatorManager.class);
        return new FastJson2ObjectInput(fastjson2CreatorManager, input);
    }


}

