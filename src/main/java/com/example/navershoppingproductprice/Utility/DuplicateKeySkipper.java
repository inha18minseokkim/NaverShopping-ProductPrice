package com.example.navershoppingproductprice.Utility;

import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.step.skip.SkipLimitExceededException;

import org.springframework.batch.core.step.skip.SkipPolicy;
import org.springframework.dao.DuplicateKeyException;

@Slf4j
public class DuplicateKeySkipper implements SkipPolicy {
    @Override
    public boolean shouldSkip(Throwable throwable, long skipCount) throws SkipLimitExceededException {
        if (throwable instanceof DuplicateKeyException) {
            if (skipCount >= 0) {
                log.warn("skip!");
            }
            return true;
        }
        return false;
    }
}
