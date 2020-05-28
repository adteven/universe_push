/*
 * This file is part of the Wildfire Chat package.
 * (c) Heavyrain2012 <heavyrain.lee@gmail.com>
 *
 * For the full copyright and license information, please view the LICENSE
 * file that was distributed with this source code.
 */

package com.comsince.github.persistence.loader;

import com.comsince.github.context.SpringApplicationContext;
import com.comsince.github.persistence.DatabaseStore;
import com.comsince.github.proto.FSCMessage;
import com.hazelcast.core.MapStore;

import java.util.Collection;
import java.util.Map;

public class ChatroomLoader implements MapStore<String, FSCMessage.ChatroomInfo> {
    private DatabaseStore getDatabaseStore() {
        return (DatabaseStore) SpringApplicationContext.getBean("databaseStore");
    }

    /**
     * Loads the value of a given key. If distributed map doesn't contain the value
     * for the given key then Hazelcast will call implementation's load (key) method
     * to obtain the value. Implementation can use any means of loading the given key;
     * such as an O/R mapping tool, simple SQL or reading a file etc.
     *
     * @param key@return value of the key, value cannot be null
     */
    @Override
    public FSCMessage.ChatroomInfo load(String key) {
        return getDatabaseStore().getPersistChatroomInfo(key);
    }

    /**
     * Loads given keys. This is batch load operation so that implementation can
     * optimize the multiple loads.
     * <p>
     * For any key in the input keys, there should be a single mapping in the resulting map. Also the resulting
     * map should not have any keys that are not part of the input keys.
     * <p>
     * The given collection should not contain any <code>null</code> keys.
     * The returned Map should not contain any <code>null</code> keys or values.
     *
     * @param keys keys of the values entries to load
     * @return map of loaded key-value pairs.
     */
    @Override
    public Map<String, FSCMessage.ChatroomInfo> loadAll(Collection<String> keys) {
        return null;
    }

    @Override
    public Iterable<String> loadAllKeys() {
        return null;
    }

    @Override
    public void store(String s, FSCMessage.ChatroomInfo chatroomInfo) {
        getDatabaseStore().updateChatroomInfo(s, chatroomInfo);
    }

    @Override
    public void storeAll(Map<String, FSCMessage.ChatroomInfo> map) {

    }

    @Override
    public void delete(String s) {
        getDatabaseStore().removeChatroomInfo(s);
    }

    @Override
    public void deleteAll(Collection<String> collection) {

    }
}
