/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (c) 2012 Oracle and/or its affiliates. All rights reserved.
 *
 * The contents of this file are subject to the terms of either the GNU
 * General Public License Version 2 only ("GPL") or the Common Development
 * and Distribution License("CDDL") (collectively, the "License").  You
 * may not use this file except in compliance with the License.  You can
 * obtain a copy of the License at
 * http://glassfish.java.net/public/CDDL+GPL_1_1.html
 * or packager/legal/LICENSE.txt.  See the License for the specific
 * language governing permissions and limitations under the License.
 *
 * When distributing the software, include this License Header Notice in each
 * file and include the License file at packager/legal/LICENSE.txt.
 *
 * GPL Classpath Exception:
 * Oracle designates this particular file as subject to the "Classpath"
 * exception as provided by Oracle in the GPL Version 2 section of the License
 * file that accompanied this code.
 *
 * Modifications:
 * If applicable, add the following below the License Header, with the fields
 * enclosed by brackets [] replaced by your own identifying information:
 * "Portions Copyright [year] [name of copyright owner]"
 *
 * Contributor(s):
 * If you wish your version of this file to be governed by only the CDDL or
 * only the GPL Version 2, indicate your decision by adding "[Contributor]
 * elects to include this software in this distribution under the [CDDL or GPL
 * Version 2] license."  If you don't indicate a single choice of license, a
 * recipient has the option to distribute your version of this file under
 * either the CDDL, the GPL Version 2 or to extend the choice of license to
 * its licensees as provided above.  However, if you add GPL Version 2 code
 * and therefore, elected the GPL Version 2 license, then the option applies
 * only if the new code is made subject to such option by the copyright
 * holder.
 */
package org.glassfish.tyrus.test.e2e;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import javax.net.websocket.SendHandler;
import javax.net.websocket.SendResult;
import javax.net.websocket.Session;
import javax.net.websocket.annotations.WebSocketEndpoint;
import javax.net.websocket.annotations.WebSocketMessage;
import javax.net.websocket.annotations.WebSocketOpen;

/**
 * @author Danny Coward (danny.coward at oracle.com)
 */
@WebSocketEndpoint("/hellocompletionhandlerfuture")
public class TextFutureCompletionHandlerServer {
    static Future<SendResult> fsr = null;
    static SendResult sr = null;
    static CountDownLatch messageLatch;

    @WebSocketOpen
    public void init(Session session) {
        System.out.println("HELLOCFSERVER opened");
        //System.out.println(" session container is " + session.getContainer());

        //MyStreamingEndpoint mse = new MyStreamingEndpoint();
        try {
            //URI uri = new URI("/streaming");
            //DefaultServerConfiguration dsc = new DefaultServerConfiguration(uri);

            //((ServerContainer) session.getContainer()).publishServer(mse, dsc);
            //System.out.println("Deployed at " + uri);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @WebSocketMessage
    public void sayHello(String message, Session session) {
        System.out.println("HELLOCFSERVER got  message: " + message + " from session " + session);
        System.out.println("HELLOCFSERVER lets send one back in async mode with a future and completion handler");
        SendHandler sh = new SendHandler() {
            public void setResult(SendResult sr) {
                if (!sr.isOK()) {
                    throw new RuntimeException(sr.getException());
                }
            }
        };

        fsr = session.getRemote().sendString("server hello", sh);
        try {
            sr = fsr.get();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
        messageLatch.countDown();
    }
}