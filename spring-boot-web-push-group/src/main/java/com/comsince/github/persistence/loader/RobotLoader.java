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

public class RobotLoader implements MapStore<String, FSCMessage.Robot> {
    @Override
    public void store(String s, FSCMessage.Robot robot) {
        getDatabaseStore().updateRobot(robot);
    }

    @Override
    public void storeAll(Map<String, FSCMessage.Robot> map) {

    }

    @Override
    public void delete(String s) {
        getDatabaseStore().deleteRobot(s);
    }

    @Override
    public void deleteAll(Collection<String> collection) {

    }

    private DatabaseStore getDatabaseStore() {
        return (DatabaseStore) SpringApplicationContext.getBean("databaseStore");
    }

    @Override
    public FSCMessage.Robot load(String key) {
        return getDatabaseStore().getRobot(key);
    }

    @Override
    public Map<String, FSCMessage.Robot> loadAll(Collection<String> keys) {
        return null;
    }

    @Override
    public Iterable<String> loadAllKeys() {
        return null;
    }
}
