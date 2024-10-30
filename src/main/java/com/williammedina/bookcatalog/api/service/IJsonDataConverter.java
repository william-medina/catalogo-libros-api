package com.williammedina.bookcatalog.api.service;

public interface IJsonDataConverter{
    <T> T fromJson(String json, Class<T> clazz);
}
