package com.gunmarket.repository.basicRepo.queryBuilder;

import com.gunmarket.web.HttpParameter;
import com.gunmarket.web.ParameterValue;

import java.util.List;
import java.util.Map;

import static com.gunmarket.repository.basicRepo.repoUtils.RepoUtils.*;
import static com.gunmarket.web.HttpParameter.COMPLEX_PARAM_TYPE;
import static com.gunmarket.web.HttpParameter.OBJECTSIMPLE_PARAM_TYPE;

public class QueryBuilder {

    private static final String EQUALLY_KEYWORD = " = ";
    private static final String OR_KEYWORD = " OR ";
    private static final String SELECT_KEYWORD = "SELECT ";
    private static final String FROM_KEYWORD = "FROM ";
    private static final String WHERE_KEYWORD = " WHERE ";
    private static final String IN_KEYWORD = " IN ";
    private static final String AND_KEYWORD = " AND ";
    private static final String AS_KEYWORD = " AS ";
    private static final String LEFT_JOIN_KEYWORD = " LEFT JOIN ";
    private static final String ID_LOWER_PARAMETER_ADDITION = "_id";
    private static final String ID_UPPER_PARAMETER_ADDITION = "_Id";
    private static final String PLURAL_ENDING_S = "s";
    private static final String COMMA = ".";
    private static final String SPACE = " ";
    private static final String CLOSING_BRACKET_REGEX = "\\)";
    private static final String CLOSING_BRACKET = ")";
    private static final String OPENING_BRACKET = "(";

    public String build(String entityName, Map<HttpParameter, List<ParameterValue>> params) {

        String resultHqlQuery = "";
        String connectorLine = createConnectorLine(entityName);

        for (Map.Entry<HttpParameter, List<ParameterValue>> paramEntry : sortParamsMap(params).entrySet()) {
            String paramName = paramEntry.getKey().getParamName();
            String paramType = paramEntry.getKey().getParamType();
            List<ParameterValue> paramValues = paramEntry.getValue();

            if (paramType.equals(COMPLEX_PARAM_TYPE)) {
                resultHqlQuery = getComplexParamQueryPart(entityName, paramName, paramValues, resultHqlQuery, connectorLine);
            } else {
                resultHqlQuery = getSimpleParamQueryPart(entityName, paramName, paramValues, resultHqlQuery, connectorLine, paramType);
            }
        }

        //ToDo Удалить вывод
        System.out.println("Вывод текущей части " + resultHqlQuery);

        resultHqlQuery = resultHqlQuery.replaceFirst(CLOSING_BRACKET_REGEX, "");
        //ToDo Удалить вывод
        System.out.println("Вывод результата без закр.скобки " + resultHqlQuery);
        return resultHqlQuery.substring(0, resultHqlQuery.length() - connectorLine.length() + 1);

    }

    //Построение простой части
    private String getSimpleParamQueryPart(String entityName, String paramName, List<ParameterValue> paramValues, String resultHqlQuery, String connectorLine, String paramType) {
        if (paramType.equals(OBJECTSIMPLE_PARAM_TYPE)) {
            paramName = paramName + ID_LOWER_PARAMETER_ADDITION;
        }
        return createCleaningPartOfQuery(resultHqlQuery,
                createInitialPartOfSimpleParamQuery(entityName)
                        .append(createParamFillingPartOfSimpleParamQuery(entityName, paramName, paramValues)),
                connectorLine);
    }

    private StringBuilder createInitialPartOfSimpleParamQuery(String entityName) {
        return new StringBuilder(FROM_KEYWORD)
                .append(entityName)
                .append(WHERE_KEYWORD)
                .append(OPENING_BRACKET);
    }

    private String createParamFillingPartOfSimpleParamQuery(String entityName, String paramName, List<ParameterValue> paramValues) {
        StringBuilder currentQPArt = new StringBuilder();
        for (ParameterValue paramValue : paramValues) {
            currentQPArt
                    .append(paramName)
                    .append(EQUALLY_KEYWORD)
                    .append(paramValue.getValueMarker())
                    .append(OR_KEYWORD);
        }
        currentQPArt.append(CLOSING_BRACKET);
        return currentQPArt.toString();
    }

    //Построение составной части
    private String getComplexParamQueryPart(String entityName, String paramName, List<ParameterValue> paramValues, String resultHqlQuery, String connectorLine) {
        return createCleaningPartOfQuery(resultHqlQuery,
                createInitialPartOfComplexParamQuery(entityName, paramName)
                        .append(createParamFillingPartOfComplexParamQuery(paramName, paramValues)),
                connectorLine);
    }

    private StringBuilder createInitialPartOfComplexParamQuery(String entityName, String paramName) {
        return new StringBuilder(SELECT_KEYWORD)
                .append(entityName)
                .append(PLURAL_ENDING_S)
                .append(COMMA)
                .append(entityName.toLowerCase())
                .append(ID_UPPER_PARAMETER_ADDITION)
                .append(SPACE)
                .append(FROM_KEYWORD)
                .append(SPACE)
                .append(firstUpperCase(replaceLastChar(paramName)))
                .append(AS_KEYWORD)
                .append(firstUpperCase(replaceLastChar(paramName)))
                .append(LEFT_JOIN_KEYWORD)
                .append(firstUpperCase(replaceLastChar(paramName)))
                .append(COMMA)
                .append(entityName.toLowerCase())
                .append(PLURAL_ENDING_S)
                .append(SPACE)
                .append(entityName)
                .append(PLURAL_ENDING_S)
                .append(WHERE_KEYWORD); //SELECT Products.product_Id FROM Shop AS Shop LEFT JOIN Shop.products Products WHERE
    }

    private String createParamFillingPartOfComplexParamQuery(String paramName, List<ParameterValue> paramValues) {
        StringBuilder currentQPArt = new StringBuilder();
        for (ParameterValue paramValue : paramValues) {
            currentQPArt
                    .append(firstUpperCase(replaceLastChar(paramName))) // Shop
                    .append(COMMA) // .
                    .append(replaceLastChar(paramName)) // shop
                    .append(ID_UPPER_PARAMETER_ADDITION) // _Id
                    .append(EQUALLY_KEYWORD) // =
                    .append(paramValue.getValueMarker())
                    .append(OR_KEYWORD);
        }
        return currentQPArt.toString();
    }

    //Общие методы для все параметров
    private String createCleaningPartOfQuery(String resultQuery, StringBuilder currentQPArt, String connectorLine) {
        return resultQuery +
                currentQPArt.delete(currentQPArt.length() - 4, currentQPArt.length() - 1).toString() +
                connectorLine;
    }

    private String createConnectorLine(String entityName) {
        return new StringBuilder(CLOSING_BRACKET) //)
                .append(AND_KEYWORD) // AND
                .append(entityName.toLowerCase())// product
                .append(ID_UPPER_PARAMETER_ADDITION) // _Id
                .append(IN_KEYWORD) // IN
                .append(OPENING_BRACKET)// (
                .toString();
    }

}