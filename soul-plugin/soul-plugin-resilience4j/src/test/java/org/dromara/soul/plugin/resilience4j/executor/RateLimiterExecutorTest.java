/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.dromara.soul.plugin.resilience4j.executor;

import io.github.resilience4j.ratelimiter.RateLimiterConfig;
import org.dromara.soul.plugin.resilience4j.conf.Resilience4JConf;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * RateLimiter executor tests.
 *
 * @author zhanglei
 */
@RunWith(MockitoJUnitRunner.class)
public final class RateLimiterExecutorTest {

    private RateLimiterExecutor ratelimiterExecutor;

    @Before
    public void setUp() {
        ratelimiterExecutor = new RateLimiterExecutor();
    }

    @Test
    public void normalTest() {
        Resilience4JConf conf = mock(Resilience4JConf.class);
        when(conf.getId()).thenReturn("SOUL");
        when(conf.getRateLimiterConfig()).thenReturn(RateLimiterConfig.ofDefaults());
        Mono mono = Mono.just("ERROR");
        StepVerifier.create(ratelimiterExecutor.run(Mono.just("SOUL"), throwable -> {
            return mono;
        }, conf)).expectSubscription().expectNext("SOUL").verifyComplete();

    }

    @Test
    public void errorTest() {
        Resilience4JConf conf = mock(Resilience4JConf.class);
        when(conf.getId()).thenReturn("SOUL");
        when(conf.getRateLimiterConfig()).thenReturn(RateLimiterConfig.ofDefaults());
        StepVerifier.create(ratelimiterExecutor.run(Mono.error(new RuntimeException()), throwable -> {
            return Mono.error(throwable);
        }, conf)).expectSubscription().expectError(RuntimeException.class);
    }
}
