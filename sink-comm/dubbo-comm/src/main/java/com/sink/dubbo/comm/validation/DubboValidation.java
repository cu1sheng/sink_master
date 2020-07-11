package com.sink.dubbo.comm.validation;

import com.alibaba.dubbo.common.URL;
import com.alibaba.dubbo.validation.Validation;
import com.alibaba.dubbo.validation.Validator;

/**
 *
 * @author Iven
 * @date 2015-02-11
 */
public class DubboValidation implements Validation {

    @Override
    public Validator getValidator(URL url) {
        return new ServiceParamValidator(url);
    }
}
