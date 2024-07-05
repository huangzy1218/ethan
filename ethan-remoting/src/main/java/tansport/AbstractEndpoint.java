package tansport;

import com.ethan.common.URL;
import com.ethan.rpc.Constants;
import com.ethan.rpc.model.FrameworkModel;
import com.ethan.rpc.protocol.codec.Codec;
import lombok.Getter;

/**
 * @author Huang Z.Y.
 */
public class AbstractEndpoint {

    @Getter
    private Codec codec;

    @Getter
    private int connectTimeout;

    @Getter
    private URL url;

    public AbstractEndpoint(URL url) {
        this.url = url;
        this.codec = getChannelCodec(url);
        this.connectTimeout =
                url.getPositiveParameter(Constants.CONNECT_TIMEOUT_KEY, Constants.DEFAULT_CONNECT_TIMEOUT);
    }

    protected static Codec getChannelCodec(URL url) {
        return FrameworkModel.defaultModel().getExtensionLoader(Codec.class).getExtension("ethan-codec");
    }

}
    