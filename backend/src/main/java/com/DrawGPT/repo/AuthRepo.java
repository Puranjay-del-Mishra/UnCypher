package com.DrawGPT.repo;

import com.DrawGPT.models.AuthCred;
import org.springframework.stereotype.Repository;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedClient;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;
import software.amazon.awssdk.services.dynamodb.model.PutItemRequest;
import software.amazon.awssdk.services.dynamodb.model.GetItemRequest;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbTable;
import software.amazon.awssdk.enhanced.dynamodb.TableSchema;


import java.util.HashMap;
import java.util.Map;

@Repository
public class AuthRepo{
    private final DynamoDbEnhancedClient awsDb; //dont wanna change this ever
    private final String tableName = "auth"; //make sure we have an auth table

    public AuthRepo(DynamoDbEnhancedClient awsDb){
        this.awsDb = awsDb;
        System.out.println("Auth Repooooooo initialized successfully.");
    }
    public void saveCredentials(AuthCred cred){
        DynamoDbTable<AuthCred> authTable = awsDb.table(tableName, TableSchema.fromBean(AuthCred.class));
        authTable.putItem(cred);
    }
    public AuthCred findByEmail(String email){
        DynamoDbTable<AuthCred> authTable = awsDb.table(tableName, TableSchema.fromBean(AuthCred.class));
        AuthCred cred = authTable.getItem(r -> r.key(k -> k.partitionValue(email)));
        return cred; // returns null if not found
    }
}