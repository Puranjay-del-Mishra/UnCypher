package com.UnCypher.models;

import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbBean;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbPartitionKey;

@DynamoDbBean
public class User{
    private String id;
    private String Name;
    private String Email;

    @DynamoDbPartitionKey
    public String getId(){
        return this.id;
    }
    public void setId(String id) {
        this.id = id;
    }

    public String getName(){
        return this.Name;
    }
    public void setName(String Name) {
        this.Name = Name;
    }

    public String getEmail(){
        return this.Email;
    }
    public void setEmail(String Email) {
        this.Email = Email;
    }
}