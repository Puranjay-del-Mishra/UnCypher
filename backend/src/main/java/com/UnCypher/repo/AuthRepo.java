package com.UnCypher.repo;

import com.UnCypher.models.AuthCred;
import org.springframework.stereotype.Repository;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedClient;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbTable;
import software.amazon.awssdk.enhanced.dynamodb.TableSchema;
import software.amazon.awssdk.enhanced.dynamodb.Expression;

import java.util.Map;

@Repository
public class AuthRepo {
    private final DynamoDbEnhancedClient awsDb;
    private final String tableName = "auth";

    public AuthRepo(DynamoDbEnhancedClient awsDb) {
        this.awsDb = awsDb;
        System.out.println("✅ AuthRepo initialized successfully.");
    }

    public void saveCredentials(AuthCred cred) {
        DynamoDbTable<AuthCred> authTable = awsDb.table(tableName, TableSchema.fromBean(AuthCred.class));
        authTable.putItem(cred);
    }

    public AuthCred findByEmail(String email) {
        DynamoDbTable<AuthCred> authTable = awsDb.table(tableName, TableSchema.fromBean(AuthCred.class));
        return authTable.getItem(r -> r.key(k -> k.partitionValue(email)));
    }

    public AuthCred findByUserId(String userId) {
        DynamoDbTable<AuthCred> authTable = awsDb.table(tableName, TableSchema.fromBean(AuthCred.class));

        Map<String, AttributeValue> expressionValues = Map.of(
                ":uid", AttributeValue.builder().s(userId).build()
        );

        Expression expression = Expression.builder()
                .expression("user_id = :uid")
                .expressionValues(expressionValues)
                .build();

        return authTable.scan(r -> r
                        .filterExpression(expression)
                        .limit(1)
                )
                .items()
                .stream()
                .findFirst()
                .orElse(null);
    }
}
