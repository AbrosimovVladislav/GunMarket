package io.gunmarket.demo.marketApp.repo.querybuilder;

import lombok.Builder;

import java.util.List;


@Builder
class QBParam {
    String paramName;
    String paramValue;
    Operation operation;
    List<String> entities;
}