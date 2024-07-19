package com.file.server.fileserver.project.data.model;


import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@RequiredArgsConstructor
public class EmailRequest {

    private String reciepient;
    private String url;

    public EmailRequest(String recipient, String url){
        this.reciepient = recipient;
        this.url = url;
    }
}

