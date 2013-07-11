/*
 * Copyright (c) 2008 - 2013 10gen, Inc. <http://10gen.com>
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.mongodb.connection.impl;

import org.mongodb.MongoCredential;
import org.mongodb.MongoException;
import org.mongodb.codecs.DocumentCodec;
import org.mongodb.codecs.PrimitiveCodecs;
import org.mongodb.command.Command;
import org.mongodb.connection.AsyncConnection;
import org.mongodb.connection.BufferProvider;
import org.mongodb.connection.ClusterDescription;
import org.mongodb.connection.SingleResultCallback;
import org.mongodb.operation.AsyncCommandOperation;
import org.mongodb.operation.CommandResult;

import static org.mongodb.connection.ClusterConnectionMode.Direct;

class NativeAsyncAuthenticator extends AsyncAuthenticator {
    private final BufferProvider bufferProvider;

    NativeAsyncAuthenticator(final MongoCredential credential, final AsyncConnection connection,
                             final BufferProvider bufferProvider) {
        super(credential, connection);
        this.bufferProvider = bufferProvider;
    }

    @Override
    public void authenticate(final SingleResultCallback<CommandResult> callback) {
        new AsyncCommandOperation(getCredential().getSource(),
                new Command(NativeAuthenticationHelper.getNonceCommand()),
                new DocumentCodec(PrimitiveCodecs.createDefault()), new ClusterDescription(Direct), bufferProvider)
                .execute(new ConnectingAsyncServerConnection(getConnection()))
                .register(new SingleResultCallback<CommandResult>() {
                    @Override
                    public void onResult(final CommandResult result, final MongoException e) {
                        if (e != null) {
                            callback.onResult(result, e);
                        }
                        else {
                            new AsyncCommandOperation(getCredential().getSource(),
                                    new Command(NativeAuthenticationHelper.getAuthCommand(getCredential().getUserName(),
                                            getCredential().getPassword(), (String) result.getResponse().get("nonce"))),
                                    new DocumentCodec(PrimitiveCodecs.createDefault()), new ClusterDescription(Direct), bufferProvider)
                                    .execute(new ConnectingAsyncServerConnection(getConnection()))
                                    .register(new SingleResultCallback<CommandResult>() {
                                        @Override
                                        public void onResult(final CommandResult result, final MongoException e) {
                                            callback.onResult(result, e);
                                        }
                                    });
                        }
                    }
                });
    }
}