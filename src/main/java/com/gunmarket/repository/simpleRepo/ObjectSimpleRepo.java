package com.gunmarket.repository.simpleRepo;

import com.gunmarket.web.HttpParameter;

import java.util.List;
import java.util.Map;

public interface ObjectSimpleRepo {

    List getByParamsDueHql(String entityName, Map<HttpParameter, List<String>> params);

}
