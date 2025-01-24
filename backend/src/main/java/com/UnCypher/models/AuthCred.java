package com.UnCypher.models;

import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbBean;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbPartitionKey;

@DynamoDbBean
public class AuthCred{
    private String Email;
    private String Password;

    @DynamoDbPartitionKey
    public String getEmail(){return this.Email;}
    public void setEmail(String Email){
        this.Email = Email;
    }

    public String getPassword(){return this.Password;}
    public void setPassword(String Password){
        this.Password = Password;
    }
}