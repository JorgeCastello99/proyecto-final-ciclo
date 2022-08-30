package com.example.toletaxifinal.cloudmesagging;

import java.util.Map;

public class FCMBody {
    private String to;//Token del usuario al qu eenviamos notificacion
    private String priority;//Prioridad que va a tener la notificacion
    private String ttl;
    Map<String, String> data;//Informacion que vamos a mandar

    public FCMBody(String to, String priority, Map<String, String> data) {
        this.to = to;
        this.priority = priority;
        this.data = data;
        this.ttl = ttl;
    }

    public Map<String, String> getData() {
        return data;
    }

    public void setData(Map<String, String> data) {
        this.data = data;
    }


    // Getter Methods

    public String getTo() {
        return to;
    }

    public String getPriority() {
        return priority;
    }



    // Setter Methods

    public void setTo( String to ) {
        this.to = to;
    }

    public void setPriority( String priority ) {
        this.priority = priority;
    }

    public String getTtl() {
        return ttl;
    }

    public void setTtl(String ttl) {
        this.ttl = ttl;
    }
}
