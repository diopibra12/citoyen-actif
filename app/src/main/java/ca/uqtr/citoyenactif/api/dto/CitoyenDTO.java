package ca.uqtr.citoyenactif.api.dto;

import com.google.gson.annotations.SerializedName;

public class CitoyenDTO {
    private long id;
    private String courriel;
    private String motDePasse;
    private String prenom;
    private String nom;
    private String adresse;
    private String numeroTel;
    @SerializedName("agent")
    private boolean agent;
    private String fcmToken;
    private String ville;
    private String numeroAgent;

    public CitoyenDTO(long id, String prenom, String nom, String adresse, String courriel, String motDePasse, String numeroTel, boolean agent, String fcmToken, String ville, String numeroAgent) {
        this.id = id;
        this.prenom = prenom;
        this.nom = nom;
        this.adresse = adresse;
        this.courriel = courriel;
        this.motDePasse = motDePasse;
        this.numeroTel = numeroTel;
        this.agent = agent;
        this.fcmToken = fcmToken;
        this.ville = ville;
        this.numeroAgent = numeroAgent;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getPrenom() {
        return prenom;
    }

    public void setPrenom(String prenom) {
        this.prenom = prenom;
    }

    public String getNom() {
        return nom;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }

    public String getAdresse() {
        return adresse;
    }

    public void setAdresse(String adresse) {
        this.adresse = adresse;
    }

    public String getCourriel() {
        return courriel;
    }

    public void setCourriel(String courriel) {
        this.courriel = courriel;
    }

    public String getMotDePasse() {
        return motDePasse;
    }

    public void setMotDePasse(String motDePasse) {
        this.motDePasse = motDePasse;
    }

    public String getNumeroTel() {
        return numeroTel;
    }

    public void setNumeroTel(String numeroTel) {
        this.numeroTel = numeroTel;
    }

    public boolean isAgent() {
        return agent;
    }

    public boolean getAgent() {
        return agent;
    }

    public void setAgent(boolean agent) {
        this.agent = agent;
    }

    public String getFcmToken() {
        return fcmToken;
    }

    public void setFcmToken(String fcmToken) {
        this.fcmToken = fcmToken;
    }

    public String getVille() {
        return ville;
    }

    public void setVille(String ville) {
        this.ville = ville;
    }

    public String getNumeroAgent() {
        return numeroAgent;
    }

    public void setNumeroAgent(String numeroAgent) {
        this.numeroAgent = numeroAgent;
    }
}
