package com.UnCypher.repo;

import com.UnCypher.models.AuthCred;
import com.UnCypher.models.User;
import org.springframework.stereotype.Repository;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedClient;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbTable;
import software.amazon.awssdk.enhanced.dynamodb.TableSchema;

@Repository
public class UserRepo{
    private final DynamoDbEnhancedClient awsDb;
    private final String tableName = "user";
    public UserRepo(DynamoDbEnhancedClient awsDb){
        this.awsDb = awsDb;
        System.out.println("User repoo was successfully initialized!");
    }

    public void saveUser(User user){
        DynamoDbTable<User> userTable = awsDb.table(tableName, TableSchema.fromBean(User.class));
        userTable.putItem(user);
    }

    public User findByEmail(String email){
        DynamoDbTable<User> userTable = awsDb.table(tableName, TableSchema.fromBean(User.class));
        User user = userTable.getItem(r -> r.key(k -> k.partitionValue(email)));
        return user;
    }
}