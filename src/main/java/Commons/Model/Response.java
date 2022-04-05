package Commons.Model;

import java.io.Serializable;

/*
   Object used to tell the client the result of it's request passing optional data

 */
public class Response implements Serializable {
    private ResponseAction responseAction;
    private Object data;

    public Response(ResponseAction responseAction, Object data) {
        this.responseAction = responseAction;
        this.data = data;
    }

    public ResponseAction getResponseAction() {
        return responseAction;
    }

    public void setResponseAction(ResponseAction responseAction) {
        this.responseAction = responseAction;
    }

    public Object getData() {
        return data;
    }

    public void setData(Object data) {
        this.data = data;
    }

    @Override
    public String toString() {
        return "Response{" +
                "responseAction=" + responseAction +
                ", data=" + data +
                '}';
    }
}
