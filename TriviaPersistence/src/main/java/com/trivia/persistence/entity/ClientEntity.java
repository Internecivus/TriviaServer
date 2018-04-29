package com.trivia.persistence.entity;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.sql.Timestamp;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

@Entity
@Table(name = "client", schema = "Trivia")
public class ClientEntity implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private int id;

    @Basic
    @NotBlank
    @Column(name = "client_id")
    private String clientId;

    @Basic
    @NotBlank
    @Column(name = "api_key")
    private String apiKey;

    @Basic
    @NotBlank
    @Column(name = "api_secret")
    private String apiSecret;

    @Basic
    @NotNull
    @Column(name = "date_created")
    private Timestamp dateCreated;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getClientId() {
        return clientId;
    }

    public void setClientId(String clientId) {
        this.clientId = clientId;
    }

    public String getApiSecret() {
        return apiSecret;
    }

    public void setApiSecret(String apiSecret) {
        this.apiSecret = apiSecret;
    }

    public String getApiKey() {
        return apiKey;
    }

    public void setApiKey(String apiKey) {
        this.apiKey = apiKey;
    }

    public Timestamp getDateCreated() {
        return dateCreated;
    }

    public void setDateCreated(Timestamp dateCreated) {
        this.dateCreated = dateCreated;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ClientEntity that = (ClientEntity) o;
        return id == that.id &&
                Objects.equals(clientId, that.clientId) &&
                Objects.equals(dateCreated, that.dateCreated);
    }

    @Override
    public int hashCode() {

        return Objects.hash(id, clientId, dateCreated);
    }
}