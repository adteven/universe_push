/*
 * Copyright (c) 2012-2017 The original author or authors
 * ------------------------------------------------------
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * and Apache License v2.0 which accompanies this distribution.
 *
 * The Eclipse Public License is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * The Apache License v2.0 is available at
 * http://www.opensource.org/licenses/apache2.0.php
 *
 * You may elect to redistribute this code under either of these licenses.
 */

package com.comsince.github.persistence;

import com.comsince.github.common.ErrorCode;
import com.comsince.github.persistence.session.ClientSession;
import com.comsince.github.persistence.session.ISessionsStore;
import com.comsince.github.persistence.session.Session;
import com.comsince.github.persistence.session.StoredMessage;
import com.comsince.github.proto.FSCMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.*;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentSkipListSet;

public class MemorySessionStore implements ISessionsStore {
    private static int dumy = 1;
    private static final Logger LOG = LoggerFactory.getLogger(MemorySessionStore.class);

    private final Map<String, Session> sessions = new ConcurrentHashMap<>();
    private final Map<String, ConcurrentSkipListSet<String>> userSessions = new ConcurrentHashMap<>();

    private final DatabaseStore databaseStore;
    public MemorySessionStore(DatabaseStore databaseStore) {
    		this.databaseStore = databaseStore;
    }

    @Override
    public Session getSession(String clientID) {
        Session session = sessions.get(clientID);
        if (session == null) {
            LOG.error("getSession Can't find the session for client <{}>", clientID);
        }
        return session;
    }

    @Override
    public void initStore() {
    }

    @Override
    public boolean contains(String clientID) {
        return sessions.containsKey(clientID);
    }

    @Override
    public void updateSessionToken(Session session, boolean voip) {
        databaseStore.updateSessionToken(session.getUsername(), session.getClientID(), voip ? session.getVoipDeviceToken() : session.getDeviceToken(), voip);
    }

    @Override
    public Session createUserSession(String username, String clientID) {
        LOG.debug("createUserSession for client <{}>, user <{}>", clientID, username);

        ClientSession clientSession = new ClientSession(clientID, this);
        Session session = databaseStore.getSession(username, clientID, clientSession);

        if (session == null) {
            session = databaseStore.createSession(username, clientID, clientSession);
        }

        return session;
    }


    @Override
    public ErrorCode createNewSession(String username, String clientID, boolean cleanSession, boolean createWhenNoExist) {
        LOG.debug("createNewSession for client <{}>", clientID);

        Session session = sessions.get(clientID);
        if (session != null) {
            LOG.error("already exists a session for client <{}>, bad condition", clientID);
            throw new IllegalArgumentException("Can't create a session with the ID of an already existing" + clientID);
        }


        ClientSession clientSession = new ClientSession(clientID, this);
        session = databaseStore.getSession(username, clientID, clientSession);

        if (session == null) {
            if (!createWhenNoExist) {
                return ErrorCode.ERROR_CODE_NOT_EXIST;
            }

            session = databaseStore.createSession(username, clientID, clientSession);
        }

        sessions.put(clientID, session);
        ConcurrentSkipListSet<String> sessionSet = userSessions.get(username);
        if (sessionSet == null) {
			sessionSet = new ConcurrentSkipListSet<>();
			userSessions.put(username, sessionSet);
		}
        sessionSet = userSessions.get(username);
        sessionSet.add(clientID);

        return ErrorCode.ERROR_CODE_SUCCESS;
    }

    @Override
    public ClientSession updateExistSession(String username, String clientID, FSCMessage.RouteRequest endpoint, boolean cleanSession) {
        LOG.debug("updateExistSession for client <{}>", clientID);
        Session session = sessions.get(clientID);
        if (session == null) {
            LOG.error("already exists a session for client <{}>, bad condition", clientID);
            throw new IllegalArgumentException("Can't create a session with the ID of an already existing" + clientID);
        }
        if (!session.getUsername().equals(username)) {
            ConcurrentSkipListSet<String> sessionSet = userSessions.get(session.getUsername());
            if(sessionSet != null) {
                sessionSet.remove(clientID);
            }
        }

        session.setUsername(username);
        sessions.put(clientID, session);

        ConcurrentSkipListSet<String> sessionSet = userSessions.get(username);
        if (sessionSet == null) {
            sessionSet = new ConcurrentSkipListSet<>();
            userSessions.put(username, sessionSet);
        }
        sessionSet = userSessions.get(username);
        sessionSet.add(clientID);

        if (endpoint != null) {
            databaseStore.updateSession(username, clientID, session, endpoint);
        }

        return session.clientSession;
    }

    @Override
    public Session sessionForClientAndUser(String username, String clientID) {
        Session session = sessions.get(clientID);
        if (session != null) {
            if (session.getUsername().equals(username)) {
                return session;
            } else {
                cleanSession(clientID);
            }
        }
        return null;
    }

    @Override
    public ClientSession sessionForClient(String clientID) {
        if (!sessions.containsKey(clientID)) {
            LOG.error("Can't find the session for client <{}>", clientID);
            return null;
        }

        Session session = sessions.get(clientID);

        return session.getClientSession();
    }

    @Override
    public void loadUserSession(String username, String clientID) {
        if (sessions.containsKey(clientID)) {
            return;
        }
        Session session = databaseStore.getSession(username, clientID, new ClientSession(clientID, this));
        LOG.info("put clientId {} username {} session ",clientID,username);
        if (session != null) {
            sessions.put(clientID, session);
        }
    }

    @Override
    public Collection<Session> sessionForUser(String username) {
    	ConcurrentSkipListSet<String> sessionSet = userSessions.get(username);
        if (sessionSet == null) {
			sessionSet = new ConcurrentSkipListSet<String>();
			userSessions.put(username, sessionSet);
		}
        sessionSet = userSessions.get(username);
        ArrayList<Session> out = new ArrayList<>();
        for (String clientId : sessionSet
             ) {
            Session session = sessions.get(clientId);
            if (session != null && session.getUsername().equals(username)) {
                out.add(session);
            }
        }
        return out;
    }

    @Override
    public Collection<ClientSession> getAllSessions() {
        Collection<ClientSession> result = new ArrayList<>();
        for (Session entry : sessions.values()) {
            result.add(new ClientSession(entry.clientID, this));
        }
        return result;
    }

    @Override
    public StoredMessage inFlightAck(String clientID, int messageID) {
        return getSession(clientID).outboundFlightMessages.remove(messageID);
    }

    @Override
    public void inFlight(String clientID, int messageID, StoredMessage msg) {
        Session session = sessions.get(clientID);
        if (session == null) {
            LOG.error("Can't find the session for client <{}>", clientID);
            return;
        }

        session.outboundFlightMessages.put(messageID, msg);
    }

    /**
     * Return the next valid packetIdentifier for the given client session.
     */
    @Override
    public int nextPacketID(String clientID) {
        if (!sessions.containsKey(clientID)) {
            LOG.error("Can't find the session for client <{}>", clientID);
            return -1;
        }

        Map<Integer, StoredMessage> m = sessions.get(clientID).outboundFlightMessages;
        int maxId = m.keySet().isEmpty() ? 0 : Collections.max(m.keySet());
        int nextPacketId = (maxId + 1) % 0xFFFF;
        m.put(nextPacketId, null);
        return nextPacketId;
    }

    @Override
    public BlockingQueue<StoredMessage> queue(String clientID) {
        if (!sessions.containsKey(clientID)) {
            LOG.error("Can't find the session for client <{}>", clientID);
            return null;
        }

        return sessions.get(clientID).queue;
    }

    @Override
    public void dropQueue(String clientID) {
        sessions.get(clientID).queue.clear();
    }

    @Override
    public void moveInFlightToSecondPhaseAckWaiting(String clientID, int messageID, StoredMessage msg) {
        LOG.info("Moving msg inflight second phase store, clientID <{}> messageID {}", clientID, messageID);
        Session session = sessions.get(clientID);
        if (session == null) {
            LOG.error("Can't find the session for client <{}>", clientID);
            return;
        }

        session.secondPhaseStore.put(messageID, msg);
        session.outboundFlightMessages.put(messageID, msg);
    }

    @Override
    public StoredMessage secondPhaseAcknowledged(String clientID, int messageID) {
        LOG.info("Acknowledged message in second phase, clientID <{}> messageID {}", clientID, messageID);
        return getSession(clientID).secondPhaseStore.remove(messageID);
    }

    @Override
    public int getInflightMessagesNo(String clientID) {
        Session session = sessions.get(clientID);
        if (session == null) {
            LOG.error("Can't find the session for client <{}>", clientID);
            return 0;
        }

        return session.inboundFlightMessages.size() + session.secondPhaseStore.size()
            + session.outboundFlightMessages.size();
    }

    @Override
    public StoredMessage inboundInflight(String clientID, int messageID) {
        return getSession(clientID).inboundFlightMessages.get(messageID);
    }

    @Override
    public void markAsInboundInflight(String clientID, int messageID, StoredMessage msg) {
        if (!sessions.containsKey(clientID))
            LOG.error("Can't find the session for client <{}>", clientID);

        sessions.get(clientID).inboundFlightMessages.put(messageID, msg);
    }

    @Override
    public int getPendingPublishMessagesNo(String clientID) {
        if (!sessions.containsKey(clientID)) {
            LOG.error("Can't find the session for client <{}>", clientID);
            return 0;
        }

        return sessions.get(clientID).queue.size();
    }

    @Override
    public int getSecondPhaseAckPendingMessages(String clientID) {
        if (!sessions.containsKey(clientID)) {
            LOG.error("Can't find the session for client <{}>", clientID);
            return 0;
        }

        return sessions.get(clientID).secondPhaseStore.size();
    }

    @Override
    public void cleanSession(String clientID) {
        LOG.info("Fooooooooo <{}>", clientID);

        Session session = sessions.get(clientID);
        if (session == null) {
            LOG.error("Can't find the session for client <{}>", clientID);
            return;
        }
        ConcurrentSkipListSet<String> sessionSet = userSessions.get(session.username);
        if (sessionSet == null) {
			sessionSet = new ConcurrentSkipListSet<>();
			userSessions.put(session.username, sessionSet);
		}
        sessionSet = userSessions.get(session.username);
        sessionSet.remove(clientID);

        // remove also the messages stored of type QoS1/2
        LOG.info("Removing stored messages with QoS 1 and 2. ClientId={}", clientID);

        session.secondPhaseStore.clear();
        session.outboundFlightMessages.clear();
        session.inboundFlightMessages.clear();

        LOG.info("Wiping existing subscriptions. ClientId={}", clientID);

        //remove also the enqueued messages
        dropQueue(clientID);

        // TODO this missing last step breaks the junit test
        sessions.remove(clientID);
    }
}
