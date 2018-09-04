/*
 * 
 *  Labyrinth Technologies
 *  __________________
 *  
 *   [2016] - [2017] Labyrinth Technologies LLC
 *   All Rights Reserved.
 *  
 *  NOTICE:  All information contained herein is, and remains
 *  the property of Labyrinth Technologies LLC and its suppliers,
 *  if any.  The intellectual and technical concepts contained
 *  herein are proprietary to Labyrinth Technologies LLC
 *  and its suppliers and may be covered by U.S. and Foreign Patents,
 *  patents in process, and are protected by trade secret or copyright law.
 *  Dissemination of this information or reproduction of this material
 *  is strictly forbidden unless prior written permission is obtained
 *  from Labyrinth Technologies LLC.
 * 
 */
package com.wpi.swarm.mongo;

import com.mongodb.client.model.FindOneAndUpdateOptions;
import java.util.HashSet;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.bson.Document;

/**
 *
 * @author John Stegeman <j.stegeman@labyrinth-tech.com>
 */
public class MCounter {

    private static final String COLLECTION_NAME = "counters";

    private static final Set<String> createdCounters = new HashSet<>();
    private static final Object cLock = new Object();
    private MCon con;

    private String name;
    private Document counter;
    private Document counterInc;
    private FindOneAndUpdateOptions opts;

    public MCounter(String counterName, long incCount) {
        try {
            if (counterName == null) {
                counterName = "default";
            }
            this.name = counterName;
            this.counter = new Document();
            this.counter.put("_id", this.name);
            this.counterInc = new Document();
            this.counterInc.put("$inc", new Document().append("count", incCount));
            this.opts = new FindOneAndUpdateOptions().upsert(true);
            con = new MCon();
        } catch (Exception ex) {
            Logger.getLogger(MCounter.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public MCounter(String counterName) {
        this(counterName, 1);
    }

    public synchronized long nextCount(int c) throws Exception {
        if (con == null) {
            throw new Exception("Invalid connection");
        }
        if (c == 0) {
            throw new Exception("Could not increment counter");
        }
        if (!createdCounters.contains(this.name)) {
            synchronized (cLock) {
                if (!createdCounters.contains(this.name)) {
                    Document counterImpl = new Document();
                    counterImpl.put("_id", this.name);
                    counterImpl.put("count", 1L);
                    try {
                        con.getCollection(COLLECTION_NAME).insertOne(counterImpl);
                    } catch (Exception e) {
                    }
                    createdCounters.add(this.name);
                }
            }
        }
        Document count = con.getCollection(COLLECTION_NAME).findOneAndUpdate(counter, counterInc, opts);
        if (count == null) {
            return this.nextCount(c - 1);
        }
        try {
            long cnt = count.getLong("count");
            if (cnt == 0) {
                return this.nextCount(c - 1);
            } else {
                return cnt;
            }
        } catch (Exception e) {
            return this.nextCount(c - 1);
        }
    }

    public synchronized long nextCount() throws Exception {
        return this.nextCount(3);
    }
}
