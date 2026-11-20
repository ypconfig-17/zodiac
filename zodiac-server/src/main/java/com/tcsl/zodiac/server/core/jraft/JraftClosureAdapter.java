package com.tcsl.zodiac.server.core.jraft;

import com.alipay.sofa.jraft.Status;
import com.tcsl.zodiac.server.core.model.JraftOperation;
import com.tcsl.zodiac.server.core.model.response.ErrorRsp;
import lombok.Data;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @program: zodiac
 * @description:
 * @author: Yang Pan
 */
@Data
public class JraftClosureAdapter implements JraftClosure {

    private static final Logger logger = LoggerFactory.getLogger(JraftClosureAdapter.class);

    private JraftClosure done;
    private volatile JraftOperation operation;


    public JraftClosureAdapter(final JraftClosure done, final JraftOperation operation) {
        this.done = done;
        this.operation = operation;
    }

    @Override
    public void run(Status status) {
        if (status.isOk()) {
            setErrorRsp(ErrorRsp.NONE);
        } else {
            logger.error("Fail status: {}.", status);
            if (getErrorRsp() == null) {
                switch (status.getRaftError()) {
                    case SUCCESS:
                        setErrorRsp(ErrorRsp.NONE);
                        break;
                    case EINVAL:
                        setErrorRsp(ErrorRsp.INVALID_REQUEST);
                        break;
                    case EIO:
                        setErrorRsp(ErrorRsp.STORAGE_ERROR);
                        break;
                    default:
                        setErrorRsp(ErrorRsp.LEADER_NOT_AVAILABLE);
                        break;
                }
            }
        }
        if (done != null) {
            done.run(status);
        }
    }

    @Override
    public ErrorRsp getErrorRsp() {
        if (this.done != null) {
            return this.done.getErrorRsp();
        }
        return null;
    }

    @Override
    public void setErrorRsp(ErrorRsp error) {
        if (this.done != null) {
            this.done.setErrorRsp(error);
        }
    }

    @Override
    public Object getData() {
        if (this.done != null) {
            return this.done.getData();
        }
        return null;
    }

    @Override
    public void setData(Object data) {
        if (this.done != null) {
            this.done.setData(data);
        }
    }

}

