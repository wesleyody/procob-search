package br.com.procob.search.model;

import com.google.gson.annotations.SerializedName;

public class ResponseContent {

    @SerializedName( "documento" )
    private String document;

    private String ddd;

    @SerializedName( "telefone" )
    private String phone;

    @SerializedName( "logradouro" )
    private String place;

    @SerializedName( "endereco" )
    private String address;

    @SerializedName( "numero" )
    private String number;

    @SerializedName( "complemento" )
    private String complement;

    @SerializedName( "block" )
    private String block;

    @SerializedName( "bairro" )
    private String district;

    @SerializedName( "cep" )
    private String zipCode;

    @SerializedName( "cidade" )
    private String city;

    @SerializedName( "uf" )
    private String state;

    public String getDocument () {
        return document;
    }

    public void setDocument ( String document ) {
        this.document = document;
    }

    public String getDdd () {
        return ddd;
    }

    public void setDdd ( String ddd ) {
        this.ddd = ddd;
    }

    public String getPhone () {
        return phone;
    }

    public void setPhone ( String phone ) {
        this.phone = phone;
    }

    public String getPlace () {
        return place;
    }

    public void setPlace ( String place ) {
        this.place = place;
    }

    public String getAddress () {
        return address;
    }

    public void setAddress ( String address ) {
        this.address = address;
    }

    public String getNumber () {
        return number;
    }

    public void setNumber ( String number ) {
        this.number = number;
    }

    public String getComplement () {
        return complement;
    }

    public void setComplement ( String complement ) {
        this.complement = complement;
    }

    public String getBlock () {
        return block;
    }

    public void setBlock ( String block ) {
        this.block = block;
    }

    public String getDistrict () {
        return district;
    }

    public void setDistrict ( String district ) {
        this.district = district;
    }

    public String getZipCode () {
        return zipCode;
    }

    public void setZipCode ( String zipCode ) {
        this.zipCode = zipCode;
    }

    public String getCity () {
        return city;
    }

    public void setCity ( String city ) {
        this.city = city;
    }

    public String getState () {
        return state;
    }

    public void setState ( String state ) {
        this.state = state;
    }
}
