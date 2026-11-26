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
package org.kie.kogito.examples.quarkus.grpc;

import org.kie.kogito.Model;
import org.kie.kogito.process.Process;
import org.kie.kogito.process.ProcessInstanceReadMode;

import io.quarkus.grpc.GrpcService;
import io.smallrye.common.annotation.Blocking;
import io.smallrye.mutiny.Uni;

import jakarta.inject.Inject;
import jakarta.inject.Named;

/** Minimal gRPC service; its presence makes the gRPC server (and its health service) start. */
@GrpcService
public class OrderStatusServiceImpl implements OrderStatusService {

    @Inject
    @Named("demo.orders")
    Process<? extends Model> orderProcess;

    @Override
    @Blocking
    public Uni<OrderStatusResponse> getStatus(OrderStatusRequest request) {
        boolean found = orderProcess.instances().findById(request.getOrderId(), ProcessInstanceReadMode.READ_ONLY).isPresent();
        return Uni.createFrom().item(OrderStatusResponse.newBuilder()
                .setOrderId(request.getOrderId()).setStatus(found ? "FOUND" : "NOT_FOUND").build());
    }
}
