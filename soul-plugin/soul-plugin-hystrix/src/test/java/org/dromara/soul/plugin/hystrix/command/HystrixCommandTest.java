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

package org.dromara.soul.plugin.hystrix.command;

import com.netflix.hystrix.HystrixCommandGroupKey;
import com.netflix.hystrix.HystrixCommandKey;
import com.netflix.hystrix.HystrixCommandProperties;
import com.netflix.hystrix.HystrixObservableCommand;
import lombok.SneakyThrows;
import org.dromara.soul.common.dto.convert.HystrixHandle;
import org.dromara.soul.plugin.api.SoulPluginChain;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.mock.http.server.reactive.MockServerHttpRequest;
import org.springframework.mock.web.server.MockServerWebExchange;

import java.lang.reflect.Method;
import java.net.InetSocketAddress;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.mock;

/**
 * The Test Case For HystrixCommand.
 *
 * @author nuo-promise
 **/
@RunWith(MockitoJUnitRunner.class)
public final class HystrixCommandTest {

    private HystrixCommand hystrixCommand;

    @SneakyThrows
    @Before
    public void setUp() {
        MockServerWebExchange exchange = MockServerWebExchange.from(MockServerHttpRequest.get("http://localhost:8080/http")
                .remoteAddress(new InetSocketAddress(8092))
                .header("MetaDataCache", "Hello")
                .build());
        HystrixHandle hystrixHandle = new HystrixHandle();
        hystrixHandle.setGroupKey("groupKey");
        hystrixHandle.setCommandKey("commandKey");
        final HystrixCommandProperties.Setter propertiesSetter =
                HystrixCommandProperties.Setter()
                        .withExecutionTimeoutInMilliseconds((int) hystrixHandle.getTimeout())
                        .withCircuitBreakerEnabled(true)
                        .withExecutionIsolationStrategy(HystrixCommandProperties.ExecutionIsolationStrategy.SEMAPHORE)
                        .withExecutionIsolationSemaphoreMaxConcurrentRequests(hystrixHandle.getMaxConcurrentRequests())
                        .withCircuitBreakerErrorThresholdPercentage(hystrixHandle.getErrorThresholdPercentage())
                        .withCircuitBreakerRequestVolumeThreshold(hystrixHandle.getRequestVolumeThreshold())
                        .withCircuitBreakerSleepWindowInMilliseconds(hystrixHandle.getSleepWindowInMilliseconds());
        HystrixObservableCommand.Setter setter = HystrixObservableCommand.Setter
                .withGroupKey(HystrixCommandGroupKey.Factory.asKey(hystrixHandle.getGroupKey()))
                .andCommandKey(HystrixCommandKey.Factory.asKey(hystrixHandle.getCommandKey()))
                .andCommandPropertiesDefaults(propertiesSetter);
        hystrixCommand = new HystrixCommand(setter, exchange, mock(SoulPluginChain.class), "http://callback:8093/test");
    }

    @Test
    public void testFetchObservable() {
        assertNotNull(hystrixCommand.fetchObservable());
    }

    @Test
    public void testGetCallBackUri() {
        assertEquals(hystrixCommand.getCallBackUri().getHost(), "callback");
    }

    @Test
    @SneakyThrows
    public void testConstruct() {
        Class<HystrixCommand> clazz = HystrixCommand.class;
        Method method = clazz.getDeclaredMethod("construct");
        method.setAccessible(true);
        assertNotNull(method.invoke(this.hystrixCommand));
    }
}
