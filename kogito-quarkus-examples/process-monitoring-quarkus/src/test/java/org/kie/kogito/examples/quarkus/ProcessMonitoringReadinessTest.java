/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.kie.kogito.examples.quarkus;

import java.time.Duration;

import org.junit.jupiter.api.Test;
import org.kie.kogito.examples.quarkus.grpc.OrderStatusRequest;
import org.kie.kogito.examples.quarkus.grpc.OrderStatusService;

import io.quarkus.grpc.GrpcClient;
import io.quarkus.test.junit.QuarkusTest;

import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.jupiter.api.Assertions.assertEquals;

@QuarkusTest
public class ProcessMonitoringReadinessTest {

    @GrpcClient("order-status")
    OrderStatusService orderStatusService;

    /** /q/health/ready must return 200 with status UP. */
    @Test
    public void readinessMustBeUp() {
        given().when().get("/q/health/ready").then().statusCode(200).body("status", is("UP"));
    }

    /** The gRPC service must answer; an unknown order id yields NOT_FOUND. */
    @Test
    public void grpcServiceMustRespond() {
        String status = orderStatusService.getStatus(OrderStatusRequest.newBuilder().setOrderId("unknown").build())
                .await().atMost(Duration.ofSeconds(10)).getStatus();
        assertEquals("NOT_FOUND", status);
    }
}
