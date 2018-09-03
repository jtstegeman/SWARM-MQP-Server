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

import com.mongodb.MongoClientSettings;
import com.mongodb.MongoCredential;
import com.mongodb.ServerAddress;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import java.util.Arrays;
import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.ejb.ConcurrencyManagement;
import javax.ejb.ConcurrencyManagementType;
import javax.ejb.Singleton;
import javax.ejb.Lock;
import javax.ejb.LockType;

@Singleton
@ConcurrencyManagement(ConcurrencyManagementType.CONTAINER)
public class MongoProvider {

    public static final String APP_DATABASE = "swarm";
    
    private MongoClient mongoClient = null;

    @Lock(LockType.READ)
    public MongoClient getMongoClient() {
        return mongoClient;
    }

    @PostConstruct
    public void init() {
        try {
            mongoClient = MongoClients.create(
                    MongoClientSettings.builder()
                    .applyToClusterSettings(builder ->
                            builder.hosts(Arrays.asList(
                                    new ServerAddress("localhost", 27017)// add more addresses as necessary
                                    )))
                    .credential(MongoCredential.createCredential("swarmApp", APP_DATABASE, "swAppPass".toCharArray()))
                    .build());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @PreDestroy
    public void destroy()
    {
        if (this.mongoClient!=null)
        {
            try {
                mongoClient.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
