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

import com.mongodb.client.MongoCollection;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ejb.EJB;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import org.bson.Document;


public class MCon {

    @EJB
    MongoProvider mongoClientProvider;


    public MCon() {
        try {
            InitialContext ctx = new InitialContext();
            mongoClientProvider = (MongoProvider) ctx.lookup("java:global/SwarmMQP/MongoProvider");
        } catch (NamingException ex) {
            Logger.getLogger(MCon.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public MongoCollection<Document> getCollection(String collectionName){
        return this.mongoClientProvider.getMongoClient().getDatabase(MongoProvider.APP_DATABASE).getCollection(collectionName);
    }
    
}
